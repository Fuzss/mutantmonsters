package fuzs.mutantmonsters.entity.mutant;

import fuzs.mutantmonsters.entity.CreeperMinionEggEntity;
import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import fuzs.mutantmonsters.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.entity.ai.goal.MBMeleeAttackGoal;
import fuzs.mutantmonsters.pathfinding.MBGroundPathNavigator;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.util.MutatedExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;

public class MutantCreeperEntity extends Monster {
    private static final EntityDataAccessor<Byte> STATUS;
    public static final int MAX_CHARGE_TIME = 100;
    public static final int MAX_DEATH_TIME = 100;
    private int chargeTime;
    private int chargeHits;
    private int lastJumpTick;
    private int jumpTick;
    private boolean summonLightning;
    private DamageSource deathCause;

    public MutantCreeperEntity(EntityType<? extends MutantCreeperEntity> type, Level worldIn) {
        super(type, worldIn);
        this.chargeHits = 3 + this.random.nextInt(3);
        this.maxUpStep = 1.0F;
        this.xpReward = 30;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new JumpAttackGoal());
        this.goalSelector.addGoal(1, new SpawnMinionsGoal());
        this.goalSelector.addGoal(1, new ChargeAttackGoal());
        this.goalSelector.addGoal(2, new MBMeleeAttackGoal(this, 1.3));
        this.goalSelector.addGoal(3, new AvoidDamageGoal(this, 1.0));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByNearestTargetGoal(this));
        this.targetSelector.addGoal(1, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(200));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Animal.class, 100, true, true, EntityUtil::isFeline));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 120.0).add(Attributes.ATTACK_DAMAGE, 5.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATUS, (byte)0);
    }

    public boolean isCharged() {
        return (this.entityData.get(STATUS) & 1) != 0;
    }

    private void setCharged(boolean charged) {
        byte b0 = this.entityData.get(STATUS);
        this.entityData.set(STATUS, charged ? (byte)(b0 | 1) : (byte)(b0 & -2));
    }

    public boolean isJumpAttacking() {
        return (this.entityData.get(STATUS) & 2) != 0;
    }

    private void setJumpAttacking(boolean jumping) {
        byte b0 = this.entityData.get(STATUS);
        this.entityData.set(STATUS, jumping ? (byte)(b0 | 2) : (byte)(b0 & -3));
    }

    public boolean isCharging() {
        return (this.entityData.get(STATUS) & 4) != 0;
    }

    private void setCharging(boolean flag) {
        byte b0 = this.entityData.get(STATUS);
        this.entityData.set(STATUS, flag ? (byte)(b0 | 4) : (byte)(b0 & -5));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 2.6F;
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new MBGroundPathNavigator(this, worldIn);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return this.isCharged() || super.fireImmune();
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = entityIn.hurt(DamageSource.mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        if (flag) {
            this.doEnchantDamageEffects(this, entityIn);
        }

        double x = entityIn.getX() - this.getX();
        double y = entityIn.getY() - this.getY();
        double z = entityIn.getZ() - this.getZ();
        double d = Math.sqrt(x * x + y * y + z * z);
        entityIn.push(x / d * 0.5, y / d * 0.05000000074505806 + 0.15000000596046448, z / d * 0.5);
        this.swing(InteractionHand.MAIN_HAND);
        return flag;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.isExplosion()) {
            float healAmount = amount / 2.0F;
            if (this.isAlive() && this.getHealth() < this.getMaxHealth() && !(source.getEntity() instanceof MutantCreeperEntity)) {
                this.heal(healAmount);
                EntityUtil.sendParticlePacket(this, ParticleTypes.HEART, (int)(healAmount / 2.0F));
            }

            return false;
        } else {
            boolean takenDamage = super.hurt(source, amount);
            if (this.isCharging()) {
                if (!source.isMagic() && source.getDirectEntity() instanceof LivingEntity) {
                    source.getDirectEntity().hurt(DamageSource.thorns(this), 2.0F);
                }

                if (takenDamage && amount > 0.0F) {
                    --this.chargeHits;
                }
            }

            return takenDamage;
        }
    }

    @Override
    public double getVisibilityPercent(@Nullable Entity lookingEntity) {
        return !(lookingEntity instanceof IronGolem) && !(lookingEntity instanceof Zoglin) ? super.getVisibilityPercent(lookingEntity) : 0.0;
    }

    @Override
    public void thunderHit(ServerLevel serverWorld, LightningBolt lightningBolt) {
        this.setCharged(true);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected void updateNoActionTime() {
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 6) {
            for(int i = 0; i < 15; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(id);
        }

    }

    @Override
    public void tick() {
        super.tick();
        this.lastJumpTick = this.jumpTick;
        if (this.isJumpAttacking()) {
            if (this.jumpTick == 0) {
                this.level.playSound((Player) null, this, MBSoundEvents.ENTITY_MUTANT_CREEPER_PRIMED, this.getSoundSource(), 2.0F, this.getVoicePitch());
            }

            ++this.jumpTick;
            this.stuckSpeedMultiplier = Vec3.ZERO;
            if (!this.level.isClientSide && (this.onGround || !this.getFeetBlockState().getFluidState().isEmpty())) {
                MutatedExplosion.create(this, this.isCharged() ? 6.0F : 4.0F, false, Explosion.BlockInteraction.DESTROY);
                this.setJumpAttacking(false);
            }
        } else if (this.jumpTick > 0) {
            this.jumpTick = 0;
        }

    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void blockedByShield(LivingEntity livingEntity) {
        livingEntity.hurtMarked = true;
    }

    public float getOverlayColor(float partialTicks) {
        if (this.deathTime > 0) {
            return (float)this.deathTime / 100.0F;
        } else if (this.isCharging()) {
            return this.tickCount % 20 < 10 ? 0.6F : 0.0F;
        } else {
            return Mth.lerp(partialTicks, (float)this.lastJumpTick, (float)this.jumpTick) / 28.0F;
        }
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level.isClientSide) {
            this.deathCause = cause;
            this.setCharging(false);
            this.level.broadcastEntityEvent(this, (byte)3);
            this.level.playSound(null, this, MBSoundEvents.ENTITY_MUTANT_CREEPER_DEATH, this.getSoundSource(), 2.0F, 1.0F);
            if (this.lastHurtByPlayerTime > 0) {
                this.lastHurtByPlayerTime += 100;
            }
        }

    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.maxUpStep = 0.0F;
        float power = this.isCharged() ? 12.0F : 8.0F;
        float radius = power * 1.5F;

        for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(radius), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            double x = this.getX() - entity.getX();
            double y = this.getY() - entity.getY();
            double z = this.getZ() - entity.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            float scale = (float) this.deathTime / 100.0F;
            entity.setDeltaMovement(entity.getDeltaMovement().add(x / d * (double) scale * 0.09, y / d * (double) scale * 0.09, z / d * (double) scale * 0.09));
        }

        this.setPosRaw(this.getX() + (double)(this.random.nextFloat() * 0.2F) - 0.10000000149011612, this.getY(), this.getZ() + (double)(this.random.nextFloat() * 0.2F) - 0.10000000149011612);
        if (this.deathTime >= 100) {
            if (!this.level.isClientSide) {
                MutatedExplosion.create(this, power, this.isOnFire(), Explosion.BlockInteraction.DESTROY);
                super.die(this.deathCause != null ? this.deathCause : DamageSource.GENERIC);
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) && this.lastHurtByPlayer != null) {
                    this.level.addFreshEntity(new CreeperMinionEggEntity(this, this.lastHurtByPlayer));
                }
            }

            this.discard();
        }

    }

    @Override
    public float getBlockExplosionResistance(Explosion explosionIn, BlockGetter worldIn, BlockPos pos, BlockState blockStateIn, FluidState fluidState, float resistance) {
        return this.isCharged() && blockStateIn.getDestroySpeed(worldIn, pos) > -1.0F ? Math.min(0.8F, resistance) : resistance;
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null) {
            super.playAmbientSound();
        }

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MBSoundEvents.ENTITY_MUTANT_CREEPER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return MBSoundEvents.ENTITY_MUTANT_CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MBSoundEvents.ENTITY_MUTANT_CREEPER_HURT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("JumpAttacking", this.isJumpAttacking());
        compound.putBoolean("Charging", this.isCharging());
        compound.putInt("ChargeTime", this.chargeTime);
        compound.putInt("ChargeHits", this.chargeHits);
        compound.putBoolean("SummonLightning", this.summonLightning);
        if (this.isCharged()) {
            compound.putBoolean("Powered", true);
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setCharged(compound.getBoolean("powered") || compound.getBoolean("Powered"));
        this.setJumpAttacking(compound.getBoolean("JumpAttacking"));
        this.setCharging(compound.getBoolean("Charging"));
        this.chargeTime = compound.getInt("ChargeTime");
        this.chargeHits = compound.getInt("ChargeHits");
        this.summonLightning = compound.getBoolean("SummonLightning");
    }

    static {
        STATUS = SynchedEntityData.defineId(MutantCreeperEntity.class, EntityDataSerializers.BYTE);
    }

    class JumpAttackGoal extends Goal {
        JumpAttackGoal() {
        }

        @Override
        public boolean canUse() {
            return MutantCreeperEntity.this.getTarget() != null && MutantCreeperEntity.this.onGround && MutantCreeperEntity.this.distanceToSqr(MutantCreeperEntity.this.getTarget()) <= 1024.0 && !MutantCreeperEntity.this.isJumpAttacking() && !MutantCreeperEntity.this.isCharging() && MutantCreeperEntity.this.random.nextFloat() * 100.0F < 0.9F;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            MutantCreeperEntity.this.setJumpAttacking(true);
            MutantCreeperEntity.this.setDeltaMovement((MutantCreeperEntity.this.getTarget().getX() - MutantCreeperEntity.this.getX()) * 0.2, 1.4 * (double)MutantCreeperEntity.this.getBlockJumpFactor(), (MutantCreeperEntity.this.getTarget().getZ() - MutantCreeperEntity.this.getZ()) * 0.2);
        }
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = MutantCreeperEntity.this.getTarget();
            return target != null && !MutantCreeperEntity.this.isJumpAttacking() && !(MutantCreeperEntity.this.getMaxHealth() - MutantCreeperEntity.this.getHealth() < MutantCreeperEntity.this.getMaxHealth() / 6.0F) && MutantCreeperEntity.this.distanceToSqr(target) >= 25.0 && MutantCreeperEntity.this.distanceToSqr(target) <= 1024.0 && MutantCreeperEntity.this.random.nextFloat() * 100.0F < 0.7F || MutantCreeperEntity.this.isCharging();
        }

        @Override
        public boolean canContinueToUse() {
            if (MutantCreeperEntity.this.summonLightning && MutantCreeperEntity.this.getTarget() != null && MutantCreeperEntity.this.distanceToSqr(MutantCreeperEntity.this.getTarget()) < 25.0) {
                return false;
            } else {
                return MutantCreeperEntity.this.chargeTime < 100 && MutantCreeperEntity.this.chargeHits > 0;
            }
        }

        @Override
        public void start() {
            MutantCreeperEntity.this.setCharging(true);
            if (MutantCreeperEntity.this.random.nextInt(MutantCreeperEntity.this.level.isThundering() ? 2 : 6) == 0 && !MutantCreeperEntity.this.isCharged()) {
                MutantCreeperEntity.this.summonLightning = true;
            }

        }

        @Override
        public void tick() {
            MutantCreeperEntity.this.getNavigation().stop();
            int i = MutantCreeperEntity.this.chargeTime % 20;
            if (i == 0 || i == 20) {
                MutantCreeperEntity.this.playSound(MBSoundEvents.ENTITY_MUTANT_CREEPER_CHARGE, 0.6F, 0.7F + MutantCreeperEntity.this.random.nextFloat() * 0.6F);
            }

            ++MutantCreeperEntity.this.chargeTime;
        }

        @Override
        public void stop() {
            if (MutantCreeperEntity.this.summonLightning && MutantCreeperEntity.this.getTarget() != null && MutantCreeperEntity.this.distanceToSqr(MutantCreeperEntity.this.getTarget()) < 25.0 && MutantCreeperEntity.this.level.canSeeSky(MutantCreeperEntity.this.blockPosition())) {
                LightningBolt lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(MutantCreeperEntity.this.level);
                lightningBoltEntity.moveTo(MutantCreeperEntity.this.getX(), MutantCreeperEntity.this.getY(), MutantCreeperEntity.this.getZ());
                MutantCreeperEntity.this.level.addFreshEntity(lightningBoltEntity);
            } else if (MutantCreeperEntity.this.chargeTime >= 100) {
                MutantCreeperEntity.this.heal(30.0F);
                MutantCreeperEntity.this.level.broadcastEntityEvent(MutantCreeperEntity.this, (byte)6);
            }

            MutantCreeperEntity.this.chargeTime = 0;
            MutantCreeperEntity.this.chargeHits = 4 + MutantCreeperEntity.this.random.nextInt(3);
            MutantCreeperEntity.this.setCharging(false);
            MutantCreeperEntity.this.summonLightning = false;
        }
    }

    class SpawnMinionsGoal extends Goal {
        SpawnMinionsGoal() {
        }

        @Override
        public boolean canUse() {
            float chance = MutantCreeperEntity.this.isPathFinding() && (MutantCreeperEntity.this.getLastDamageSource() == null || !MutantCreeperEntity.this.getLastDamageSource().isProjectile()) ? 0.6F : 0.9F;
            return MutantCreeperEntity.this.getTarget() != null && MutantCreeperEntity.this.distanceToSqr(MutantCreeperEntity.this.getTarget()) <= 1024.0 && !MutantCreeperEntity.this.isCharging() && !MutantCreeperEntity.this.isJumpAttacking() && MutantCreeperEntity.this.random.nextFloat() * 100.0F < chance;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            for(int i = (int)Math.ceil((double)(MutantCreeperEntity.this.getHealth() / MutantCreeperEntity.this.getMaxHealth()) * 4.0); i > 0; --i) {
                CreeperMinionEntity minion = (CreeperMinionEntity) MBEntityType.CREEPER_MINION.create(MutantCreeperEntity.this.level);
                double x = MutantCreeperEntity.this.getX() + (double)MutantCreeperEntity.this.random.nextFloat() - (double)MutantCreeperEntity.this.random.nextFloat();
                double y = MutantCreeperEntity.this.getY() + (double)(MutantCreeperEntity.this.random.nextFloat() * 0.5F);
                double z = MutantCreeperEntity.this.getZ() + (double)MutantCreeperEntity.this.random.nextFloat() - (double)MutantCreeperEntity.this.random.nextFloat();
                double xx = MutantCreeperEntity.this.getTarget().getX() - MutantCreeperEntity.this.getX();
                double yy = MutantCreeperEntity.this.getTarget().getY() - MutantCreeperEntity.this.getY();
                double zz = MutantCreeperEntity.this.getTarget().getZ() - MutantCreeperEntity.this.getZ();
                minion.setDeltaMovement(xx * 0.15000000596046448 + (double)(MutantCreeperEntity.this.random.nextFloat() * 0.05F), yy * 0.15000000596046448 + (double)(MutantCreeperEntity.this.random.nextFloat() * 0.05F), zz * 0.15000000596046448 + (double)(MutantCreeperEntity.this.random.nextFloat() * 0.05F));
                minion.setPos(x, y, z);
                minion.setTarget(MutantCreeperEntity.this.getTarget());
                minion.setOwnerUUID(MutantCreeperEntity.this.uuid);
                if (MutantCreeperEntity.this.isCharged()) {
                    minion.setCharged(true);
                }

                MutantCreeperEntity.this.level.addFreshEntity(minion);
            }

        }
    }
}
