package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.AnimatedEntity;
import fuzs.mutantmonsters.world.entity.EntityAnimation;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import fuzs.mutantmonsters.world.entity.ai.goal.AnimationGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.puzzleslib.api.util.v1.DamageSourcesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MutantSkeleton extends AbstractMutantMonster implements AnimatedEntity {
    public static final EntityAnimation MELEE_ANIMATION = new EntityAnimation(14);
    public static final EntityAnimation CONSTRICT_RIBS_ANIMATION = new EntityAnimation(20);
    public static final EntityAnimation SHOOT_ANIMATION = new EntityAnimation(32);
    public static final EntityAnimation MULTI_SHOT_ANIMATION = new EntityAnimation(30);
    private static final EntityAnimation[] ANIMATIONS = new EntityAnimation[]{MELEE_ANIMATION, CONSTRICT_RIBS_ANIMATION, SHOOT_ANIMATION, MULTI_SHOT_ANIMATION};

    private EntityAnimation animation;
    private int animationTick;

    public MutantSkeleton(EntityType<? extends MutantSkeleton> type, Level worldIn) {
        super(type, worldIn);
        this.animation = EntityAnimation.NONE;
        this.xpReward = Enemy.XP_REWARD_HUGE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 150.0).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.FOLLOW_RANGE, 48.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.KNOCKBACK_RESISTANCE, 0.75).add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeGoal(this));
        this.goalSelector.addGoal(0, new ShootGoal(this));
        this.goalSelector.addGoal(0, new MultiShotGoal(this));
        this.goalSelector.addGoal(0, new ConstrictRibsGoal(this));
        this.goalSelector.addGoal(1, new MutantMeleeAttackGoal(this, 1.1).setMaxAttackTick(5));
        this.goalSelector.addGoal(2, new AvoidDamageGoal(this, 1.0));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByNearestTargetGoal(this, WitherBoss.class));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected void updateNoActionTime() {
        // NO-OP
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAnimationPlaying()) {
            ++this.animationTick;
        }

        if (this.level().isNight() && this.tickCount % 100 == 0 && this.isAlive() && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
        }

    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        if (!this.isAnimationPlaying()) {
            if (this.random.nextInt(4) != 0) {
                this.animation = MELEE_ANIMATION;
            } else {
                this.animation = CONSTRICT_RIBS_ANIMATION;
            }
        }

        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        return !(damageSource.getEntity() instanceof MutantSkeleton) && super.hurtServer(serverLevel, damageSource, damageAmount);
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

    @Override
    public EntityAnimation getAnimation() {
        return this.animation;
    }

    @Override
    public void setAnimation(EntityAnimation animation) {
        this.animation = animation;
    }

    @Override
    public EntityAnimation[] getAnimations() {
        return ANIMATIONS;
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.level() instanceof ServerLevel serverLevel) {

            for (LivingEntity livingEntity : serverLevel.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0, 2.0, 3.0))) {
                DamageSource damageSource = DamageSourcesHelper.source(serverLevel,
                        ModRegistry.MUTANT_SKELETON_SHATTER_DAMAGE_TYPE,
                        this);
                livingEntity.hurtServer(serverLevel, damageSource, 7.0F);
            }

            for (MutantSkeletonBodyPart.BodyPart bodyPart : MutantSkeletonBodyPart.BodyPart.values()) {
                MutantSkeletonBodyPart mutantSkeletonBodyPart = new MutantSkeletonBodyPart(serverLevel, this, bodyPart);
                mutantSkeletonBodyPart.setDeltaMovement(mutantSkeletonBodyPart.getDeltaMovement().add(this.random.nextFloat() * 0.8F * 2.0F - 0.8F, this.random.nextFloat() * 0.25F + 0.1F, this.random.nextFloat() * 0.8F * 2.0F - 0.8F));
                serverLevel.addFreshEntity(mutantSkeletonBodyPart);
            }
        }

        this.deathTime = 19;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENTITY_MUTANT_SKELETON_AMBIENT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSoundEvents.ENTITY_MUTANT_SKELETON_HURT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_MUTANT_SKELETON_DEATH_SOUND_EVENT.value();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ModSoundEvents.ENTITY_MUTANT_SKELETON_STEP_SOUND_EVENT.value(), 0.15F, 1.0F);
    }

    static class MultiShotGoal extends AnimationGoal<MutantSkeleton> {
        private final List<MutantArrow> shots = new ArrayList<>();

        public MultiShotGoal(MutantSkeleton mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantSkeleton.MULTI_SHOT_ANIMATION;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && this.mob.tickCount % 3 == 0 && !this.mob.isAnimationPlaying() && (this.mob.onGround() && this.mob.random.nextInt(26) == 0 && this.mob.hasLineOfSight(target) || this.mob.getVehicle() == target);
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getNavigation().stop();
                this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
                if (this.mob.animationTick == 10) {
                    this.mob.stopRiding();
                    double x = target.getX() - this.mob.getX();
                    double z = target.getZ() - this.mob.getZ();
                    float scale = 0.06F + this.mob.random.nextFloat() * 0.03F;
                    if (this.mob.distanceToSqr(target) < 16.0) {
                        x *= -1.0;
                        z *= -1.0;
                        scale = (float) ((double) scale * 5.0);
                    }

                    this.mob.stuckSpeedMultiplier = Vec3.ZERO;
                    this.mob.setDeltaMovement(x * (double) scale, 1.1 * (double) this.mob.getBlockJumpFactor(), z * (double) scale);
                }

                if (this.mob.animationTick == 15) {
                    this.mob.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_3.value(), 1.0F, 1.0F);
                }

                if (this.mob.animationTick == 20) {
                    this.mob.playSound(SoundEvents.CROSSBOW_LOADING_END.value(), 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
                }

                if (this.mob.animationTick >= 24 && this.mob.animationTick < 28) {
                    if (!this.shots.isEmpty()) {

                        for (MutantArrow arrowEntity : this.shots) {
                            this.mob.level().addFreshEntity(arrowEntity);
                        }

                        this.shots.clear();
                    }

                    for (int i = 0; i < 6; ++i) {
                        MutantArrow mutantArrow = new MutantArrow(this.mob.level(), this.mob);
                        mutantArrow.shoot(target, 2.0F - this.mob.random.nextFloat() * 0.1F, 3.0F);
//                        mutantArrow.setClones(2);
//                        mutantArrow.setBaseDamage(5.0 + this.mob.random.nextInt(5));
                        this.shots.add(mutantArrow);
                    }

                    this.mob.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.4F + 1.2F) + 0.25F);
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.shots.clear();
        }
    }

    static class ShootGoal extends AnimationGoal<MutantSkeleton> {

        public ShootGoal(MutantSkeleton mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantSkeleton.SHOOT_ANIMATION;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && !this.mob.isAnimationPlaying() && this.mob.random.nextInt(12) == 0 && this.mob.distanceToSqr(target) > 4.0 && this.mob.hasLineOfSight(target);
        }

        @Override
        public void tick() {

            LivingEntity target = this.mob.getTarget();
            if (target != null) {

                this.mob.getNavigation().stop();
                this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
                if (this.mob.animationTick == 5) {
                    this.mob.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_2.value(), 1.0F, 1.0F);
                }

                if (this.mob.animationTick == 20) {
                    this.mob.playSound(SoundEvents.CROSSBOW_LOADING_END.value(), 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
                }

                if (this.mob.animationTick == 26 && target.isAlive()) {

                    float randomization = (float) this.mob.hurtTime / 2.0F;
                    if (this.mob.hurtTime > 0 && this.mob.lastHurt > 0.0F && this.mob.getLastDamageSource() != null && this.mob.getLastDamageSource().getEntity() != null) {
                        randomization = (float) this.mob.hurtTime / 2.0F;
                    } else if (!this.mob.hasLineOfSight(target)) {
                        randomization = 0.5F + this.mob.random.nextFloat();
                    }

                    MutantArrow mutantArrow = new MutantArrow(this.mob.level(), this.mob);
                    mutantArrow.shoot(target, 2.4F, randomization);
//                    arrowEntity.setClones(10);

                    if (this.mob.random.nextInt(4) == 0) {
                        mutantArrow.addEffect(new MobEffectInstance(MobEffects.POISON, 80 + this.mob.random.nextInt(60), 0));
                    }

                    if (this.mob.random.nextInt(4) == 0) {
                        mutantArrow.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120 + this.mob.random.nextInt(60), 1));
                    }

                    if (this.mob.random.nextInt(4) == 0) {
                        mutantArrow.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120 + this.mob.random.nextInt(60), 1));
                    }

                    this.mob.level().addFreshEntity(mutantArrow);
                    this.mob.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.4F + 1.2F) + 0.25F);
                }
            }
        }
    }

    static class ConstrictRibsGoal extends AnimationGoal<MutantSkeleton> {

        public ConstrictRibsGoal(MutantSkeleton mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantSkeleton.CONSTRICT_RIBS_ANIMATION;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && super.canUse();
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getNavigation().stop();
                if (this.mob.animationTick < 6) {
                    this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
                }

                if (this.mob.animationTick == 5) {
                    target.stopRiding();
                }

                if (this.mob.animationTick == 6) {
                    float attackDamage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    DamageSources damageSources = this.mob.level().damageSources();
                    if (!target.hurtServer((ServerLevel) this.mob.level(), damageSources.mobAttack(this.mob), attackDamage > 0.0F ? attackDamage + 6.0F : 0.0F)) {
                        EntityUtil.disableShield(target, 100);
                    }

                    double motionX = (double) (1.0F + this.mob.random.nextFloat() * 0.4F) * (double) (this.mob.random.nextBoolean() ? 1 : -1);
                    double motionY = 0.4F + this.mob.random.nextFloat() * 0.8F;
                    double motionZ = (double) (1.0F + this.mob.random.nextFloat() * 0.4F) * (double) (this.mob.random.nextBoolean() ? 1 : -1);
                    target.setDeltaMovement(motionX, motionY, motionZ);
                    EntityUtil.sendPlayerVelocityPacket(target);
                    this.mob.playSound(SoundEvents.GENERIC_EXPLODE.value(), 0.5F, 0.8F + this.mob.random.nextFloat() * 0.4F);
                }
            }
        }
    }

    static class MeleeGoal extends AnimationGoal<MutantSkeleton> {
        public MeleeGoal(MutantSkeleton mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantSkeleton.MELEE_ANIMATION;
        }

        @Override
        public void tick() {
            this.mob.getNavigation().stop();
            LivingEntity target = this.mob.getTarget();
            if (target != null && target.isAlive()) {
                this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
            }

            if (this.mob.animationTick == 3) {
                float attackDamage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);

                for (LivingEntity livingEntity : this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(4.0))) {
                    if (!(livingEntity instanceof MutantSkeleton)) {
                        double dist = this.mob.distanceTo(livingEntity);
                        double x = this.mob.getX() - livingEntity.getX();
                        double z = this.mob.getZ() - livingEntity.getZ();
                        if (dist <= 3.0 && EntityUtil.getHeadAngle(this.mob, x, z) < 60.0F) {
                            float power = 1.8F + (float) this.mob.random.nextInt(5) * 0.15F;
                            livingEntity.hurt(this.mob.level().damageSources().mobAttack(this.mob), attackDamage > 0.0F ? attackDamage + (float) this.mob.random.nextInt(2) : 0.0F);
                            livingEntity.setDeltaMovement(-x / dist * (double) power, Math.max(0.2800000011920929, livingEntity.getDeltaMovement().y), -z / dist * (double) power);
                            EntityUtil.sendPlayerVelocityPacket(livingEntity);
                        }
                    }
                }

                this.mob.playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.4F + 1.2F));
            }

        }
    }
}
