package fuzs.mutantmonsters.util;

import com.google.common.collect.ImmutableMap;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.mixin.accessor.RavagerAccessor;
import fuzs.mutantmonsters.packet.MBPacketHandler;
import fuzs.mutantmonsters.packet.SpawnParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Map;

public final class EntityUtil {
    private static final Map<EntityType<?>, Item> VANILLA_SKULLS_MAP = ImmutableMap.of(EntityType.CREEPER, Items.CREEPER_HEAD, EntityType.ZOMBIE, Items.ZOMBIE_HEAD, EntityType.SKELETON, Items.SKELETON_SKULL, EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL, EntityType.ENDER_DRAGON, Items.DRAGON_HEAD);

    private EntityUtil() {

    }

    public static float getHeadAngle(LivingEntity livingEntity, double x, double z) {
        return Mth.degreesDifferenceAbs((float)(Math.atan2(z, x) * 180.0 / Math.PI) + 90.0F, livingEntity.yHeadRot);
    }

    public static void spawnLingeringCloud(LivingEntity livingEntity) {
        Collection<MobEffectInstance> collection = livingEntity.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(livingEntity.level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            areaeffectcloudentity.setRadius(1.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new MobEffectInstance(effectinstance));
            }

            livingEntity.level.addFreshEntity(areaeffectcloudentity);
        }

    }

    public static void stunRavager(LivingEntity livingEntity) {
        if (livingEntity instanceof Ravager) {
            if (((RavagerAccessor) livingEntity).mutantmonsters$getStunnedTick() == 0) {
                ((RavagerAccessor) livingEntity).mutantmonsters$setStunnedTick(40);
                livingEntity.playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.0F);
                livingEntity.level.broadcastEntityEvent(livingEntity, (byte)39);
            }
        }

    }

    public static void disableShield(LivingEntity livingEntity, int ticks) {
        if (livingEntity instanceof Player && livingEntity.isBlocking()) {
            ((Player)livingEntity).getCooldowns().addCooldown(livingEntity.getUseItem().getItem(), ticks);
            livingEntity.stopUsingItem();
            livingEntity.level.broadcastEntityEvent(livingEntity, (byte)30);
        }

    }

    public static void sendPlayerVelocityPacket(Entity entity) {
        if (entity instanceof ServerPlayer) {
            ((ServerPlayer)entity).connection.send(new ClientboundSetEntityMotionPacket(entity));
        }

    }

    public static void sendMetadataPacket(Entity entity) {
        if (entity.level instanceof ServerLevel) {
            ((ServerLevel)entity.level).getChunkSource().broadcast(entity, new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), false));
        }

    }

    public static boolean isFeline(LivingEntity livingEntity) {
        return livingEntity instanceof Ocelot || livingEntity instanceof Cat;
    }

    public static boolean shouldAttackEntity(TamableAnimal attacker, LivingEntity target, LivingEntity owner, boolean canTargetCreepers) {
        if (!(owner instanceof Player)) {
            return true;
        } else if (target instanceof Creeper) {
            return canTargetCreepers;
        } else if (target instanceof TamableAnimal) {
            return !((TamableAnimal)target).isOwnedBy(owner);
        } else if (target instanceof Player && !((Player)owner).canHarmPlayer((Player)target)) {
            return false;
        } else if (target instanceof AbstractGolem && !(target instanceof Enemy)) {
            return false;
        } else {
            return !(target instanceof AbstractHorse) || !((AbstractHorse)target).isTamed();
        }
    }

    public static <T extends Mob> T convertMobWithNBT(LivingEntity mobToConvert, EntityType<T> newEntityType, boolean dropInventory) {
        T newMob = newEntityType.create(mobToConvert.level);
        CompoundTag copiedNBT = mobToConvert.saveWithoutId(new CompoundTag());
        copiedNBT.putUUID("UUID", newMob.getUUID());
        copiedNBT.put("Attributes", newMob.getAttributes().save());
        copiedNBT.putFloat("Health", newMob.getHealth());
        if (mobToConvert.getTeam() != null) {
            copiedNBT.putString("Team", mobToConvert.getTeam().getName());
        }

        ListTag handItems;
        if (copiedNBT.contains("ActiveEffects", 9)) {
            handItems = copiedNBT.getList("ActiveEffects", 10);

            for(int i = 0; i < handItems.size(); ++i) {
                CompoundTag compoundnbt = handItems.getCompound(i);
                MobEffectInstance effectInstance = MobEffectInstance.load(compoundnbt);
                if (effectInstance != null && !newMob.canBeAffected(effectInstance)) {
                    handItems.remove(i);
                    --i;
                }
            }
        }

        copiedNBT.putBoolean("CanPickUpLoot", !dropInventory && copiedNBT.getBoolean("CanPickUpLoot"));
        if (dropInventory && mobToConvert.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            ListTag handDropChances;
            int i;
            ItemStack itemStack;
            if (copiedNBT.contains("ArmorItems", 9)) {
                handItems = copiedNBT.getList("ArmorItems", 10);
                handDropChances = copiedNBT.getList("ArmorDropChances", 5);

                for(i = 0; i < handItems.size(); ++i) {
                    itemStack = ItemStack.of(handItems.getCompound(i));
                    if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && handDropChances.getFloat(i) > 1.0F) {
                        mobToConvert.spawnAtLocation(itemStack);
                    }
                }

                handItems.clear();
                handDropChances.clear();
            }

            if (copiedNBT.contains("HandItems", 9)) {
                handItems = copiedNBT.getList("HandItems", 10);
                handDropChances = copiedNBT.getList("HandDropChances", 5);

                for(i = 0; i < handItems.size(); ++i) {
                    itemStack = ItemStack.of(handItems.getCompound(i));
                    if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && handDropChances.getFloat(i) > 1.0F) {
                        mobToConvert.spawnAtLocation(itemStack);
                    }
                }

                handItems.clear();
                handDropChances.clear();
            }

            if (mobToConvert.getType() == EntityType.ENDERMAN && copiedNBT.contains("carriedBlockState", 10)) {
                BlockState blockState = NbtUtils.readBlockState(copiedNBT.getCompound("carriedBlockState"));
                if (!blockState.isAir()) {
                    mobToConvert.spawnAtLocation(blockState.getBlock());
                }
            }
        }

        newMob.load(copiedNBT);
        mobToConvert.level.addFreshEntity(newMob);
        mobToConvert.discard();
        return newMob;
    }

    public static void spawnEndersoulParticles(Entity entity, RandomSource random, int amount, float speed) {
        for(int i = 0; i < amount; ++i) {
            float f = (random.nextFloat() - 0.5F) * speed;
            float f1 = (random.nextFloat() - 0.5F) * speed;
            float f2 = (random.nextFloat() - 0.5F) * speed;
            double tempX = entity.getX() + (double)((random.nextFloat() - 0.5F) * entity.getBbWidth());
            double tempY = entity.getY() + (double)((random.nextFloat() - 0.5F) * entity.getBbHeight()) + 0.5;
            double tempZ = entity.getZ() + (double)((random.nextFloat() - 0.5F) * entity.getBbWidth());
            entity.level.addParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), tempX, tempY, tempZ, (double)f, (double)f1, (double)f2);
        }

    }

    public static void sendParticlePacket(Entity entity, ParticleOptions particleData, int amount) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        MBPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> {
            return new PacketDistributor.TargetPoint(x, y, z, 1024.0, entity.level.dimension());
        }), new SpawnParticlePacket(particleData, x, y, z, (double)entity.getBbWidth(), (double)entity.getBbHeight(), (double)entity.getBbWidth(), amount));
    }

    public static Vec3 getDirVector(float rotation, float scale) {
        float rad = rotation * 0.017453292F;
        return new Vec3((double)(-Mth.sin(rad) * scale), 0.0, (double)(Mth.cos(rad) * scale));
    }

    public static boolean teleportTo(Mob mob, double x, double y, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        boolean success = false;
        if (mob.level.isLoaded(pos)) {
            while(true) {
                pos.move(Direction.DOWN);
                if (pos.getY() <= 0 || mob.level.getBlockState(pos).getMaterial().blocksMotion()) {
                    pos.move(Direction.UP);
                    AABB bb = mob.getDimensions(Pose.STANDING).makeBoundingBox((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
                    if (mob.level.noCollision(mob, bb) && !mob.level.containsAnyLiquid(bb)) {
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
            mob.setPos((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
            return true;
        }
    }

    public static void divertAttackers(Mob targetedMob, LivingEntity newTarget) {

        for (Mob attacker : targetedMob.level.getEntitiesOfClass(Mob.class, targetedMob.getBoundingBox().inflate(16.0, 10.0, 16.0))) {
            if (attacker != targetedMob && attacker.getTarget() == targetedMob) {
                attacker.setTarget(newTarget);
            }
        }

    }

    public static double getKnockResistanceFactor(Entity entity) {
        return entity instanceof LivingEntity ? 1.0 - ((LivingEntity)entity).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 1.0;
    }

    public static ItemStack getSkullDrop(EntityType<?> entityType) {
        if (!VANILLA_SKULLS_MAP.containsKey(entityType)) return ItemStack.EMPTY;
        return new ItemStack(VANILLA_SKULLS_MAP.get(entityType));
    }
}