package fuzs.mutantmonsters.entity.projectile;

import com.google.common.collect.ImmutableMap;
import fuzs.mutantmonsters.entity.SkullSpiritEntity;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;

public class ChemicalXEntity extends ThrowableItemProjectile {
    public static final Predicate<LivingEntity> IS_APPLICABLE;
    public static final TargetingConditions PREDICATE;
    private static final Map<EntityType<? extends Mob>, EntityType<? extends Mob>> MUTATIONS;

    public ChemicalXEntity(EntityType<? extends ChemicalXEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public ChemicalXEntity(LivingEntity livingEntityIn, Level worldIn) {
        super(MBEntityType.CHEMICAL_X, livingEntityIn, worldIn);
    }

    public ChemicalXEntity(double x, double y, double z, Level worldIn) {
        super(MBEntityType.CHEMICAL_X, x, y, z, worldIn);
    }

    public ChemicalXEntity(PlayMessages.SpawnEntity packet, Level worldIn) {
        super(MBEntityType.CHEMICAL_X, worldIn);
    }

    protected Item getDefaultItem() {
        return MBItems.CHEMICAL_X;
    }

    protected float getGravity() {
        return 0.05F;
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            int i;
            double x;
            double y;
            double z;
            for(i = 5 + this.random.nextInt(3); i >= 0; --i) {
                x = (this.random.nextDouble() - this.random.nextDouble()) * 0.3;
                y = 0.1 + this.random.nextDouble() * 0.1;
                z = (this.random.nextDouble() - this.random.nextDouble()) * 0.3;
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), x, y, z);
            }

            for(i = this.random.nextInt(5); i < 50; ++i) {
                x = (this.random.nextDouble() - 0.5) * 1.2;
                y = this.random.nextDouble() * 0.2;
                z = (this.random.nextDouble() - 0.5) * 1.2;
                this.level.addParticle(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.get(), this.getX(), this.getY(), this.getZ(), x, y, z);
            }
        }

    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            Mob target = null;
            boolean directHit = false;
            if (result.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)result).getEntity();
                if (entity instanceof Mob && PREDICATE.test(null, (Mob)entity)) {
                    target = (Mob) entity;
                    directHit = true;
                }
            }

            if (!directHit) {
                target = this.level.getNearestEntity(Mob.class, PREDICATE, null, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(12.0, 8.0, 12.0));
            }

            if (target != null) {
                SkullSpiritEntity spirit = new SkullSpiritEntity(this.level, target);
                spirit.setPos(this.getX(), this.getY(), this.getZ());
                this.level.addFreshEntity(spirit);
            }

            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

        this.playSound(SoundEvents.SPLASH_POTION_BREAK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Nullable
    public static EntityType<? extends Mob> getMutantOf(LivingEntity target) {
        EntityType<?> targetType = target.getType();
        if (!MUTATIONS.containsKey(targetType)) {
            return null;
        } else if (targetType == EntityType.PIG && (!target.hasEffect(MobEffects.UNLUCK) || target.getEffect(MobEffects.UNLUCK).getAmplifier() != 13)) {
            return null;
        } else {
            return targetType == EntityType.ZOMBIE && target.isBaby() ? null : MUTATIONS.get(targetType);
        }
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static {
        MUTATIONS = ImmutableMap.builder()
            .put(EntityType.CREEPER, MBEntityType.MUTANT_CREEPER)
            .put(EntityType.ENDERMAN, MBEntityType.MUTANT_ENDERMAN)
            .put(EntityType.PIG, MBEntityType.SPIDER_PIG)
            .put(EntityType.SKELETON, MBEntityType.MUTANT_SKELETON)
            .put(EntityType.SNOW_GOLEM, MBEntityType.MUTANT_SNOW_GOLEM)
            .put(EntityType.ZOMBIE, MBEntityType.MUTANT_ZOMBIE).build();
        IS_APPLICABLE = (target) -> {
            EntityType<?> entityType = target.getType();
            return target.canChangeDimensions() && !MUTATIONS.containsValue(entityType) && entityType != MBEntityType.CREEPER_MINION && entityType != MBEntityType.ENDERSOUL_CLONE;
        };
        PREDICATE = TargetingConditions.forNonCombat().selector(IS_APPLICABLE);
    }
}
