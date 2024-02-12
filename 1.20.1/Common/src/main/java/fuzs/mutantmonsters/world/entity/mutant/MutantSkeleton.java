package fuzs.mutantmonsters.world.entity.mutant;

import com.google.common.collect.Lists;
import fuzs.mutantmonsters.animation.AnimatedEntity;
import fuzs.mutantmonsters.animation.Animation;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import fuzs.mutantmonsters.world.entity.ai.goal.AnimationGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.level.pathfinder.MutantGroundPathNavigation;
import fuzs.puzzleslib.api.entity.v1.DamageSourcesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MutantSkeleton extends AbstractMutantMonster implements AnimatedEntity {
    public static final Animation MELEE_ANIMATION = new Animation(14);
    public static final Animation CONSTRICT_RIBS_ANIMATION = new Animation(20);
    public static final Animation SHOOT_ANIMATION = new Animation(32);
    public static final Animation MULTI_SHOT_ANIMATION = new Animation(30);
    private static final Animation[] ANIMATIONS = new Animation[]{MELEE_ANIMATION, CONSTRICT_RIBS_ANIMATION, SHOOT_ANIMATION, MULTI_SHOT_ANIMATION};

    private Animation animation;
    private int animationTick;

    public MutantSkeleton(EntityType<? extends MutantSkeleton> type, Level worldIn) {
        super(type, worldIn);
        this.animation = Animation.NONE;
        this.setMaxUpStep(1.0F);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 150.0).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.FOLLOW_RANGE, 48.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.KNOCKBACK_RESISTANCE, 0.75);
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
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 3.25F;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new MutantGroundPathNavigation(this, worldIn);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return super.createBodyControl();
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void updateNoActionTime() {
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
    public boolean doHurtTarget(Entity entityIn) {
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
    public boolean hurt(DamageSource source, float amount) {
        return !(source.getEntity() instanceof MutantSkeleton) && super.hurt(source, amount);
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
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    @Override
    public Animation[] getAnimations() {
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
        if (!this.level().isClientSide) {

            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0, 2.0, 3.0))) {
                livingEntity.hurt(DamageSourcesHelper.source(this.level(), ModRegistry.ARMOR_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, this), 7.0F);
            }

            for (int i = 0; i < 18; ++i) {

                int j = i;
                if (i >= 3) {
                    j = i + 1;
                }

                if (j >= 4) {
                    ++j;
                }

                if (j >= 5) {
                    ++j;
                }

                if (j >= 6) {
                    ++j;
                }

                if (j >= 9) {
                    ++j;
                }

                if (j >= 10) {
                    ++j;
                }

                if (j >= 11) {
                    ++j;
                }

                if (j >= 12) {
                    ++j;
                }

                if (j >= 15) {
                    ++j;
                }

                if (j >= 16) {
                    ++j;
                }

                if (j >= 17) {
                    ++j;
                }

                if (j >= 18) {
                    ++j;
                }

                if (j >= 20) {
                    ++j;
                }

                MutantSkeletonBodyPart part = new MutantSkeletonBodyPart(this.level(), this, j);
                part.setDeltaMovement(part.getDeltaMovement().add(this.random.nextFloat() * 0.8F * 2.0F - 0.8F, this.random.nextFloat() * 0.25F + 0.1F, this.random.nextFloat() * 0.8F * 2.0F - 0.8F));
                this.level().addFreshEntity(part);
            }
        }

        this.deathTime = 19;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModRegistry.ENTITY_MUTANT_SKELETON_AMBIENT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModRegistry.ENTITY_MUTANT_SKELETON_HURT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModRegistry.ENTITY_MUTANT_SKELETON_DEATH_SOUND_EVENT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ModRegistry.ENTITY_MUTANT_SKELETON_STEP_SOUND_EVENT.get(), 0.15F, 1.0F);
    }

    static class MultiShotGoal extends AnimationGoal<MutantSkeleton> {
        private final List<MutantArrow> shots = new ArrayList<>();

        public MultiShotGoal(MutantSkeleton mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        protected Animation getAnimation() {
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
                    this.mob.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_3, 1.0F, 1.0F);
                }

                if (this.mob.animationTick == 20) {
                    this.mob.playSound(SoundEvents.CROSSBOW_LOADING_END, 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
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
        protected Animation getAnimation() {
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
                    this.mob.playSound(SoundEvents.CROSSBOW_QUICK_CHARGE_2, 1.0F, 1.0F);
                }

                if (this.mob.animationTick == 20) {
                    this.mob.playSound(SoundEvents.CROSSBOW_LOADING_END, 1.0F, 1.0F / (this.mob.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
                }

                if (this.mob.animationTick == 26 && target.isAlive()) {

                    float randomization = (float) this.mob.hurtTime / 2.0F;
                    if (this.mob.hurtTime > 0 && this.mob.lastHurt > 0.0F && this.mob.getLastDamageSource() != null && this.mob.getLastDamageSource().getEntity() != null) {
                        randomization = (float) this.mob.hurtTime / 2.0F;
                    } else if (!this.mob.hasLineOfSight(target)) {
                        randomization = 0.5F + this.mob.random.nextFloat();
                    }

                    MutantArrow arrowEntity = new MutantArrow(this.mob.level(), this.mob);
                    arrowEntity.shoot(target, 2.4F, randomization);
//                    arrowEntity.setClones(10);

                    List<MobEffectInstance> effects = Lists.newArrayList();

                    if (this.mob.random.nextInt(4) == 0) {
                        effects.add(new MobEffectInstance(MobEffects.POISON, 80 + this.mob.random.nextInt(60), 0));
                    }

                    if (this.mob.random.nextInt(4) == 0) {
                        effects.add(new MobEffectInstance(MobEffects.HUNGER, 120 + this.mob.random.nextInt(60), 1));
                    }

                    if (this.mob.random.nextInt(4) == 0) {
                        effects.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120 + this.mob.random.nextInt(60), 1));
                    }

                    if (!effects.isEmpty()) {
                        ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
                        PotionUtils.setCustomEffects(itemStack, effects);
                        arrowEntity.setEffectsFromItem(itemStack);
                    }

                    this.mob.level().addFreshEntity(arrowEntity);
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
        protected Animation getAnimation() {
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
                    if (!target.hurt(this.mob.level().damageSources().mobAttack(this.mob), attackDamage > 0.0F ? attackDamage + 6.0F : 0.0F)) {
                        EntityUtil.disableShield(target, 100);
                    }

                    double motionX = (double) (1.0F + this.mob.random.nextFloat() * 0.4F) * (double) (this.mob.random.nextBoolean() ? 1 : -1);
                    double motionY = 0.4F + this.mob.random.nextFloat() * 0.8F;
                    double motionZ = (double) (1.0F + this.mob.random.nextFloat() * 0.4F) * (double) (this.mob.random.nextBoolean() ? 1 : -1);
                    target.setDeltaMovement(motionX, motionY, motionZ);
                    EntityUtil.sendPlayerVelocityPacket(target);
                    this.mob.playSound(SoundEvents.GENERIC_EXPLODE, 0.5F, 0.8F + this.mob.random.nextFloat() * 0.4F);
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
        protected Animation getAnimation() {
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
