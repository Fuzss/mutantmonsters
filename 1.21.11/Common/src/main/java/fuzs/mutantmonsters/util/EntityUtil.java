package fuzs.mutantmonsters.util;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.network.ClientboundMutantLevelParticlesMessage;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Optional;

public final class EntityUtil {

    private EntityUtil() {
        // NO-OP
    }

    public static float getHeadAngle(LivingEntity livingEntity, double x, double z) {
        return Mth.degreesDifferenceAbs((float) (Math.atan2(z, x) * 180.0 / Math.PI) + 90.0F, livingEntity.yHeadRot);
    }

    public static void spawnLingeringCloud(LivingEntity livingEntity) {
        Collection<MobEffectInstance> collection = livingEntity.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaEffectCloud = new AreaEffectCloud(livingEntity.level(),
                    livingEntity.getX(),
                    livingEntity.getY(),
                    livingEntity.getZ());
            areaEffectCloud.setRadius(1.5F);
            areaEffectCloud.setRadiusOnUse(-0.5F);
            areaEffectCloud.setWaitTime(10);
            areaEffectCloud.setDuration(areaEffectCloud.getDuration() / 2);
            areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float) areaEffectCloud.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaEffectCloud.addEffect(new MobEffectInstance(effectinstance));
            }

            livingEntity.level().addFreshEntity(areaEffectCloud);
        }
    }

    public static void stunRavager(LivingEntity livingEntity) {
        if (livingEntity instanceof Ravager ravager) {
            if (ravager.getStunnedTick() == 0) {
                ravager.handleEntityEvent(EntityEvent.RAVAGER_STUNNED);
                livingEntity.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
                livingEntity.level().broadcastEntityEvent(livingEntity, EntityEvent.RAVAGER_STUNNED);
            }
        }
    }

    public static void disableShield(LivingEntity livingEntity, int ticks) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            ItemStack itemStack = livingEntity.getItemBlockingWith();
            if (itemStack != null && itemStack.has(DataComponents.BLOCKS_ATTACKS)) {
                itemStack.get(DataComponents.BLOCKS_ATTACKS)
                        .disable(serverLevel, livingEntity, ticks / 20.0F, itemStack);
            }
        }
    }

    public static void sendPlayerVelocityPacket(Entity entity) {
        if (entity instanceof ServerPlayer) {
            ((ServerPlayer) entity).connection.send(new ClientboundSetEntityMotionPacket(entity));
        }
    }

    public static boolean isFeline(LivingEntity livingEntity) {
        return livingEntity instanceof Ocelot || livingEntity instanceof Cat;
    }

    public static boolean shouldAttackEntity(LivingEntity target, LivingEntity owner, boolean targetCreepers) {
        if (!(owner instanceof Player)) {
            return true;
        } else if (target instanceof Creeper) {
            return targetCreepers;
        } else if (target instanceof TamableAnimal) {
            return !((TamableAnimal) target).isOwnedBy(owner);
        } else if (target instanceof Player && !((Player) owner).canHarmPlayer((Player) target)) {
            return false;
        } else if (target instanceof AbstractGolem && !(target instanceof Enemy)) {
            return false;
        } else {
            return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTamed();
        }
    }

    public static void spawnEndersoulParticles(Entity entity, RandomSource random, int amount, float speed) {
        for (int i = 0; i < amount; ++i) {
            float f = (random.nextFloat() - 0.5F) * speed;
            float f1 = (random.nextFloat() - 0.5F) * speed;
            float f2 = (random.nextFloat() - 0.5F) * speed;
            double tempX = entity.getX() + (double) ((random.nextFloat() - 0.5F) * entity.getBbWidth());
            double tempY = entity.getY() + (double) ((random.nextFloat() - 0.5F) * entity.getBbHeight()) + 0.5;
            double tempZ = entity.getZ() + (double) ((random.nextFloat() - 0.5F) * entity.getBbWidth());
            entity.level().addParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), tempX, tempY, tempZ, f, f1, f2);
        }
    }

    public static void sendParticlePacket(Entity entity, ParticleOptions particle, int amount) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        MessageSender.broadcast(PlayerSet.nearEntity(entity),
                new ClientboundMutantLevelParticlesMessage(particle,
                        x,
                        y,
                        z,
                        entity.getBbWidth(),
                        entity.getBbHeight(),
                        entity.getBbWidth(),
                        amount));
    }

    public static Vec3 getDirVector(float rotation, float scale) {
        float rad = rotation * 0.017453292F;
        return new Vec3(-Mth.sin(rad) * scale, 0.0, Mth.cos(rad) * scale);
    }

    public static boolean teleportTo(Mob mob, double x, double y, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        boolean success = false;
        if (mob.level().isLoaded(pos)) {
            while (true) {
                pos.move(Direction.DOWN);
                if (pos.getY() <= mob.level().getMinY() || mob.level().getBlockState(pos).blocksMotion()) {
                    pos.move(Direction.UP);
                    AABB bb = mob.getDimensions(Pose.STANDING)
                            .makeBoundingBox((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5);
                    if (mob.level().noCollision(mob, bb) && !mob.level().containsAnyLiquid(bb)) {
                        success = true;
                    }
                    break;
                }
            }
        }

        if (!success) {
            return false;
        } else {
            mob.getNavigation().stop();
            mob.setPos((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5);
            return true;
        }
    }

    public static void divertAttackers(Mob targetedMob, LivingEntity newTarget) {
        for (Mob attacker : targetedMob.level()
                .getEntitiesOfClass(Mob.class, targetedMob.getBoundingBox().inflate(16.0, 10.0, 16.0))) {
            if (attacker != targetedMob && attacker.getTarget() == targetedMob) {
                attacker.setTarget(newTarget);
            }
        }
    }

    public static Optional<Boolean> extractCreeperMinion(CompoundTag tag) {
        if (!tag.isEmpty()) {
            EntityType<?> entityType = tag.read("id", EntityType.CODEC).orElse(null);
            if (entityType == ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value()) {
                return tag.getBoolean("Powered");
            }
        }

        return Optional.empty();
    }
}
