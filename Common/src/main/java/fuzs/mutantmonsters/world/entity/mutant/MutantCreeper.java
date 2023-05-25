package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.CreeperMinionEgg;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.level.MutatedExplosion;
import fuzs.mutantmonsters.world.level.pathfinder.MutantGroundPathNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class MutantCreeper extends Monster {
    private static final EntityDataAccessor<Byte> STATUS;
    public static final int MAX_CHARGE_TIME = 100;
    public static final int MAX_DEATH_TIME = 100;

    private int chargeTime;
    private int chargeHits;
    private int lastJumpTick;
    private int jumpTick;
    private boolean summonLightning;
    private DamageSource deathCause;

    public MutantCreeper(EntityType<? extends MutantCreeper> type, Level worldIn) {
        super(type, worldIn);
        this.chargeHits = 3 + this.random.nextInt(3);
        this.setMaxUpStep(1.0F);
        this.xpReward = 30;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new JumpAttackGoal());
        this.goalSelector.addGoal(1, new SpawnMinionsGoal());
        this.goalSelector.addGoal(1, new ChargeAttackGoal());
        this.goalSelector.addGoal(2, new MutantMeleeAttackGoal(this, 1.3));
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
        return new MutantGroundPathNavigation(this, worldIn);
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
        boolean flag = entityIn.hurt(this.level.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
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
        } else if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            float healAmount = amount / 2.0F;
            if (this.isAlive() && this.getHealth() < this.getMaxHealth() && !(source.getEntity() instanceof MutantCreeper)) {
                this.heal(healAmount);
                EntityUtil.sendParticlePacket(this, ParticleTypes.HEART, (int)(healAmount / 2.0F));
            }

            return false;
        } else {
            boolean takenDamage = super.hurt(source, amount);
            if (this.isCharging()) {
                if (!source.is(DamageTypeTags.WITCH_RESISTANT_TO) && source.getDirectEntity() instanceof LivingEntity) {
                    source.getDirectEntity().hurt(this.damageSources().thorns(this), 2.0F);
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
                this.level.playSound(null, this, ModRegistry.ENTITY_MUTANT_CREEPER_PRIMED_SOUND_EVENT.get(), this.getSoundSource(), 2.0F, this.getVoicePitch());
            }

            ++this.jumpTick;
            this.stuckSpeedMultiplier = Vec3.ZERO;
            if (!this.level.isClientSide && (this.onGround || !this.getFeetBlockState().getFluidState().isEmpty())) {
                MutatedExplosion.create(this, this.isCharged() ? 6.0F : 4.0F, false, Level.ExplosionInteraction.MOB);
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
            this.level.playSound(null, this, ModRegistry.ENTITY_MUTANT_CREEPER_DEATH_SOUND_EVENT.get(), this.getSoundSource(), 2.0F, 1.0F);
            if (this.lastHurtByPlayerTime > 0) {
                this.lastHurtByPlayerTime += 100;
            }
        }

    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.setMaxUpStep(0.0F);
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
                MutatedExplosion.create(this, power, this.isOnFire(), Level.ExplosionInteraction.MOB);
                super.die(this.deathCause != null ? this.deathCause : this.damageSources().generic());
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) && this.lastHurtByPlayer != null) {
                    this.level.addFreshEntity(new CreeperMinionEgg(this, this.lastHurtByPlayer));
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
        return ModRegistry.ENTITY_MUTANT_CREEPER_AMBIENT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModRegistry.ENTITY_MUTANT_CREEPER_HURT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModRegistry.ENTITY_MUTANT_CREEPER_HURT_SOUND_EVENT.get();
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
        STATUS = SynchedEntityData.defineId(MutantCreeper.class, EntityDataSerializers.BYTE);
    }

    class JumpAttackGoal extends Goal {
        JumpAttackGoal() {
        }

        @Override
        public boolean canUse() {
            return MutantCreeper.this.getTarget() != null && MutantCreeper.this.onGround && MutantCreeper.this.distanceToSqr(MutantCreeper.this.getTarget()) <= 1024.0 && !MutantCreeper.this.isJumpAttacking() && !MutantCreeper.this.isCharging() && MutantCreeper.this.random.nextFloat() * 100.0F < 0.9F;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            MutantCreeper.this.setJumpAttacking(true);
            MutantCreeper.this.setDeltaMovement((MutantCreeper.this.getTarget().getX() - MutantCreeper.this.getX()) * 0.2, 1.4 * (double) MutantCreeper.this.getBlockJumpFactor(), (MutantCreeper.this.getTarget().getZ() - MutantCreeper.this.getZ()) * 0.2);
        }
    }

    class ChargeAttackGoal extends Goal {
        public ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = MutantCreeper.this.getTarget();
            return target != null && !MutantCreeper.this.isJumpAttacking() && !(MutantCreeper.this.getMaxHealth() - MutantCreeper.this.getHealth() < MutantCreeper.this.getMaxHealth() / 6.0F) && MutantCreeper.this.distanceToSqr(target) >= 25.0 && MutantCreeper.this.distanceToSqr(target) <= 1024.0 && MutantCreeper.this.random.nextFloat() * 100.0F < 0.7F || MutantCreeper.this.isCharging();
        }

        @Override
        public boolean canContinueToUse() {
            if (MutantCreeper.this.summonLightning && MutantCreeper.this.getTarget() != null && MutantCreeper.this.distanceToSqr(MutantCreeper.this.getTarget()) < 25.0) {
                return false;
            } else {
                return MutantCreeper.this.chargeTime < 100 && MutantCreeper.this.chargeHits > 0;
            }
        }

        @Override
        public void start() {
            MutantCreeper.this.setCharging(true);
            if (MutantCreeper.this.random.nextInt(MutantCreeper.this.level.isThundering() ? 2 : 6) == 0 && !MutantCreeper.this.isCharged()) {
                MutantCreeper.this.summonLightning = true;
            }

        }

        @Override
        public void tick() {
            MutantCreeper.this.getNavigation().stop();
            int i = MutantCreeper.this.chargeTime % 20;
            if (i == 0) {
                MutantCreeper.this.playSound(ModRegistry.ENTITY_MUTANT_CREEPER_CHARGE_SOUND_EVENT.get(), 0.6F, 0.7F + MutantCreeper.this.random.nextFloat() * 0.6F);
            }

            ++MutantCreeper.this.chargeTime;
        }

        @Override
        public void stop() {
            if (MutantCreeper.this.summonLightning && MutantCreeper.this.getTarget() != null && MutantCreeper.this.distanceToSqr(MutantCreeper.this.getTarget()) < 25.0 && MutantCreeper.this.level.canSeeSky(MutantCreeper.this.blockPosition())) {
                LightningBolt lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(MutantCreeper.this.level);
                lightningBoltEntity.moveTo(MutantCreeper.this.getX(), MutantCreeper.this.getY(), MutantCreeper.this.getZ());
                MutantCreeper.this.level.addFreshEntity(lightningBoltEntity);
            } else if (MutantCreeper.this.chargeTime >= 100) {
                MutantCreeper.this.heal(30.0F);
                MutantCreeper.this.level.broadcastEntityEvent(MutantCreeper.this, (byte)6);
            }

            MutantCreeper.this.chargeTime = 0;
            MutantCreeper.this.chargeHits = 4 + MutantCreeper.this.random.nextInt(3);
            MutantCreeper.this.setCharging(false);
            MutantCreeper.this.summonLightning = false;
        }
    }

    class SpawnMinionsGoal extends Goal {
        SpawnMinionsGoal() {
        }

        @Override
        public boolean canUse() {
            float chance = MutantCreeper.this.isPathFinding() && (MutantCreeper.this.getLastDamageSource() == null || !MutantCreeper.this.getLastDamageSource().is(DamageTypeTags.IS_PROJECTILE)) ? 0.6F : 0.9F;
            return MutantCreeper.this.getTarget() != null && MutantCreeper.this.distanceToSqr(MutantCreeper.this.getTarget()) <= 1024.0 && !MutantCreeper.this.isCharging() && !MutantCreeper.this.isJumpAttacking() && MutantCreeper.this.random.nextFloat() * 100.0F < chance;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            for(int i = (int)Math.ceil((double)(MutantCreeper.this.getHealth() / MutantCreeper.this.getMaxHealth()) * 4.0); i > 0; --i) {
                CreeperMinion minion = ModRegistry.CREEPER_MINION_ENTITY_TYPE.get().create(MutantCreeper.this.level);
                double x = MutantCreeper.this.getX() + (double) MutantCreeper.this.random.nextFloat() - (double) MutantCreeper.this.random.nextFloat();
                double y = MutantCreeper.this.getY() + (double)(MutantCreeper.this.random.nextFloat() * 0.5F);
                double z = MutantCreeper.this.getZ() + (double) MutantCreeper.this.random.nextFloat() - (double) MutantCreeper.this.random.nextFloat();
                double xx = MutantCreeper.this.getTarget().getX() - MutantCreeper.this.getX();
                double yy = MutantCreeper.this.getTarget().getY() - MutantCreeper.this.getY();
                double zz = MutantCreeper.this.getTarget().getZ() - MutantCreeper.this.getZ();
                minion.setDeltaMovement(xx * 0.15000000596046448 + (double)(MutantCreeper.this.random.nextFloat() * 0.05F), yy * 0.15000000596046448 + (double)(MutantCreeper.this.random.nextFloat() * 0.05F), zz * 0.15000000596046448 + (double)(MutantCreeper.this.random.nextFloat() * 0.05F));
                minion.setPos(x, y, z);
                minion.setTarget(MutantCreeper.this.getTarget());
                minion.setOwnerUUID(MutantCreeper.this.uuid);
                if (MutantCreeper.this.isCharged()) {
                    minion.setCharged(true);
                }

                MutantCreeper.this.level.addFreshEntity(minion);
            }

        }
    }
}
