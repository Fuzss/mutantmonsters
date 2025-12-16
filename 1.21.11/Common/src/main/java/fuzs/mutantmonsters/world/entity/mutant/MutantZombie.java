package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.ai.goal.AnimationGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.animation.AdditionalSpawnDataEntity;
import fuzs.mutantmonsters.world.entity.animation.AnimatedEntity;
import fuzs.mutantmonsters.world.entity.animation.EntityAnimation;
import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.mutantmonsters.world.level.ZombieResurrection;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.puzzleslib.api.util.v1.DamageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MutantZombie extends MutantMonster implements AnimatedEntity {
    private static final int VANISH_WHEN_ACTUALLY_DEAD_TIME = 100;
    private static final int REVIVE_AFTER_DEATH_TIME = 140;
    public static final EntityAnimation SLAM_GROUND_ANIMATION = new EntityAnimation("mutant_zombie_slam_ground", 25);
    public static final EntityAnimation THROW_ANIMATION = new EntityAnimation("mutant_zombie_throw", 15);
    public static final EntityAnimation ROAR_ANIMATION = new EntityAnimation("mutant_zombie_roar", 120);
    private static final EntityDataAccessor<Byte> DATA_REMAINING_LIVES = SynchedEntityData.defineId(MutantZombie.class,
            EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_THROW_ATTACK_STATE = SynchedEntityData.defineId(MutantZombie.class,
            EntityDataSerializers.BYTE);
    private static final EntityAnimation[] ANIMATIONS = new EntityAnimation[]{
            SLAM_GROUND_ANIMATION, THROW_ANIMATION, ROAR_ANIMATION
    };
    private final List<SeismicWave> seismicWaveList = new ArrayList<>();
    private final List<ZombieResurrection> resurrectionList = new ArrayList<>();
    public int throwHitTick = -1;
    public int throwFinishTick = -1;
    private int vanishTime;
    private int oldDeathTime;
    private EntityAnimation animation = EntityAnimation.NONE;
    private int animationTick;
    @Nullable
    private DamageSource killedByDamageSource;
    private long killedByMemoryTime;

    public MutantZombie(EntityType<? extends MutantZombie> type, Level level) {
        super(type, level);
        this.xpReward = Enemy.XP_REWARD_HUGE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SlamGroundGoal(this));
        this.goalSelector.addGoal(0, new RoarGoal(this));
        this.goalSelector.addGoal(0, new ThrowAttackGoal(this));
        this.goalSelector.addGoal(1, new MutantMeleeAttackGoal(this, 1.2).setMaxAttackTick(0));
        this.goalSelector.addGoal(2, new AvoidDamageGoal(this, 1.0));
        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 1.0, true, 4, () -> {
            return false;
        }));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByNearestTargetGoal(this, WitherBoss.class).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_LIVES, (byte) 3);
        builder.define(DATA_THROW_ATTACK_STATE, (byte) 0);
    }

    public int getRemainingLives() {
        return this.entityData.get(DATA_REMAINING_LIVES);
    }

    private void setRemainingLives(int lives) {
        this.entityData.set(DATA_REMAINING_LIVES, (byte) lives);
        this.vanishTime = 0;
    }

    public boolean hasThrowAttackHit() {
        return (this.entityData.get(DATA_THROW_ATTACK_STATE) & 1) != 0;
    }

    private void setThrowAttackHit(boolean hit) {
        byte b0 = this.entityData.get(DATA_THROW_ATTACK_STATE);
        this.entityData.set(DATA_THROW_ATTACK_STATE, hit ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public boolean isThrowAttackFinished() {
        return (this.entityData.get(DATA_THROW_ATTACK_STATE) & 2) != 0;
    }

    private void setThrowAttackFinished(boolean finished) {
        byte b0 = this.entityData.get(DATA_THROW_ATTACK_STATE);
        this.entityData.set(DATA_THROW_ATTACK_STATE, finished ? (byte) (b0 | 2) : (byte) (b0 & -3));
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
    protected void tickHeadTurn(float renderYawOffset) {
        if (this.deathTime <= 0) {
            super.tickHeadTurn(renderYawOffset);
        }
    }

    @Override
    public int getMaxFallDistance() {
        return this.getTarget() != null ? (int) this.distanceTo(this.getTarget()) : 3;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand interactionHand) {
        // we cannot use #mobInteract as it does not trigger for dead mobs
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        ItemStack itemInHand = player.getItemInHand(interactionHand);
        ItemStack originalItemInHand = itemInHand.copy();
        InteractionResult interactionResult = this.mobInteractWhenDead(player, interactionHand);
        if (interactionResult.consumesAction()) {
            if (player.getAbilities().instabuild && itemInHand == player.getItemInHand(interactionHand)
                    && itemInHand.getCount() < originalItemInHand.getCount()) {
                itemInHand.setCount(originalItemInHand.getCount());
            }

            if (itemInHand.isEmpty() && !player.getAbilities().instabuild) {
                player.setItemInHand(interactionHand, ItemStack.EMPTY);
            }

            this.gameEvent(GameEvent.ENTITY_INTERACT);
            return interactionResult;
        } else {
            return super.interactAt(player, vec, interactionHand);
        }
    }

    private InteractionResult mobInteractWhenDead(Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (itemInHand.is(ItemTags.CREEPER_IGNITERS) && !this.isAlive() && !this.isOnFire()
                && !this.isInWaterOrRain()) {
            this.level()
                    .playSound(player,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            SoundEvents.FLINTANDSTEEL_USE,
                            this.getSoundSource(),
                            1.0F,
                            this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide()) {
                this.igniteForSeconds(8.0F);
                if (!itemInHand.isDamageableItem()) {
                    itemInHand.shrink(1);
                } else {
                    ItemHelper.hurtAndBreak(itemInHand, 1, player, interactionHand);
                }

                player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void igniteForTicks(int ticks) {
        super.igniteForTicks(ticks);
        if (this.level() instanceof ServerLevel && this.isDeadOrDying()) {
            this.setRemainingLives(0);
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        if (!this.isAnimationPlaying()) {
            if (entity.getVehicle() != this && this.random.nextInt(5) != 0) {
                if (this.onGround() || !this.getInBlockState().getFluidState().isEmpty()) {
                    this.animation = SLAM_GROUND_ANIMATION;
                }
            } else {
                this.animation = THROW_ANIMATION;
            }
        }

        return true;
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        if (!this.isAnimationPlaying() && this.getTarget() != null
                && Math.abs(this.getY() - this.getTarget().getY()) <= 1.0
                && this.distanceToSqr(this.getTarget()) <= 49.0 && this.random.nextInt(20) == 0) {
            this.animation = SLAM_GROUND_ANIMATION;
        }

        super.customServerAiStep(serverLevel);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (this.isInvulnerableTo(serverLevel, damageSource)) {
            return false;
        } else {
            Entity entity = damageSource.getEntity();
            return (entity == null || this.canHarm(entity) && (this.animation != THROW_ANIMATION
                    || entity != this.getTarget())) && super.hurtServer(serverLevel, damageSource, damageAmount);
        }
    }

    @Override
    public void tick() {
        this.oldDeathTime = this.deathTime;
        super.tick();
        this.snapRotation();
        this.updateAnimation();
        if (this.level() instanceof ServerLevel serverLevel) {
            this.updateMeleeGrounds(serverLevel);
        }

        if (this.level().isDarkOutside() && this.tickCount % 100 == 0 && this.isAlive()
                && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
        }

        for (int i = this.resurrectionList.size() - 1; i >= 0; --i) {
            ZombieResurrection zr = this.resurrectionList.get(i);
            if (!zr.update(this)) {
                this.resurrectionList.remove(zr);
            }
        }
    }

    private void snapRotation() {
        float yaw;
        yaw = this.yHeadRot - this.yBodyRot;
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }

        while (yaw >= 180.0F) {
            yaw -= 360.0F;
        }

        float offset = 0.1F;
        if (this.animation == SLAM_GROUND_ANIMATION) {
            offset = 0.2F;
        }

        this.yBodyRot += yaw * offset;
    }

    private void updateAnimation() {
        if (this.isAnimationPlaying()) {
            ++this.animationTick;
        }

        if (this.level().isClientSide()) {
            if (this.animation == THROW_ANIMATION) {
                if (this.hasThrowAttackHit()) {
                    if (this.throwHitTick == -1) {
                        this.throwHitTick = 0;
                    }

                    ++this.throwHitTick;
                }

                if (this.isThrowAttackFinished()) {
                    if (this.throwFinishTick == -1) {
                        this.throwFinishTick = 0;
                    }

                    ++this.throwFinishTick;
                }
            } else {
                this.throwHitTick = -1;
                this.throwFinishTick = -1;
            }
        }

    }

    private void updateMeleeGrounds(ServerLevel serverLevel) {
        if (!this.seismicWaveList.isEmpty()) {
            SeismicWave wave = this.seismicWaveList.remove(0);
            wave.affectBlocks(serverLevel, this);
            AABB box = new AABB(wave.getX(),
                    (double) wave.getY() + 1.0,
                    wave.getZ(),
                    (double) wave.getX() + 1.0,
                    (double) wave.getY() + 2.0,
                    (double) wave.getZ() + 1.0);
            if (wave.isInitial()) {
                double addScale = this.random.nextDouble() * 0.75;
                box = box.inflate(0.25 + addScale, 0.25 + addScale * 0.5, 0.25 + addScale);
            }

            DamageSource damageSource = DamageHelper.damageSource(this.level(),
                    ModRegistry.MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE,
                    this);
            for (Entity entity : this.level()
                    .getEntities(this, box, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(this::canHarm))) {
                float damageAmount =
                        wave.isInitial() ? (float) (9 + this.random.nextInt(4)) : (float) (6 + this.random.nextInt(3));
                if (entity instanceof LivingEntity && entity.hurtServer(serverLevel, damageSource, damageAmount)
                        && this.random.nextInt(5) == 0) {
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.HUNGER, 160, 1));
                }

                double x = entity.getX() - this.getX();
                double z = entity.getZ() - this.getZ();
                double d = Math.sqrt(x * x + z * z);
                entity.setDeltaMovement(x / d * 0.3, 0.04, z / d * 0.3);
                EntityUtil.sendPlayerVelocityPacket(entity);
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isOnFire()) {
                this.setRemainingLives(0);
            }

            this.killedByDamageSource = damageSource;
            this.killedByMemoryTime = serverLevel.getGameTime();
            if (this.getRemainingLives() > 0) {
                this.stopAllGoals();
                this.getNavigation().stop();
                serverLevel.broadcastEntityEvent(this, (byte) 3);
                if (this.lastHurtByPlayerMemoryTime > 0) {
                    this.lastHurtByPlayerMemoryTime += REVIVE_AFTER_DEATH_TIME;
                }
            }
        }
    }

    private void stopAllGoals() {
        for (WrappedGoal goal : this.goalSelector.getAvailableGoals()) {
            if (goal.isRunning()) {
                goal.stop();
            }
        }
    }

    @Override
    protected void tickDeath() {
        if (this.getRemainingLives() > 0) {
            if (++this.deathTime >= REVIVE_AFTER_DEATH_TIME) {
                this.deathTime = 0;
                this.killedByDamageSource = null;
                this.setHealth(this.getMaxHealth() / 4.0F);
                this.setRemainingLives(this.getRemainingLives() - 1);
            }
        } else {
            if (this.deathTime > VANISH_WHEN_ACTUALLY_DEAD_TIME) {
                this.deathTime--;
            } else if (this.deathTime < VANISH_WHEN_ACTUALLY_DEAD_TIME) {
                this.deathTime++;
            }

            if (++this.vanishTime >= VANISH_WHEN_ACTUALLY_DEAD_TIME) {
                DamageSource killedByDamageSource = this.getKilledByDamageSource();
                super.die(killedByDamageSource != null ? killedByDamageSource : this.damageSources().generic());
                super.tickDeath();
            }
        }
    }

    /**
     * @see LivingEntity#getLastDamageSource()
     */
    public @Nullable DamageSource getKilledByDamageSource() {
        if (this.level().getGameTime() - this.killedByMemoryTime > REVIVE_AFTER_DEATH_TIME * 2L) {
            return this.killedByDamageSource = null;
        } else {
            return this.killedByDamageSource;
        }
    }

    public float getDeathTime(float partialTick) {
        return Mth.lerp(partialTick, this.oldDeathTime, this.deathTime);
    }

    public float getVanishingProgress(float partialTick) {
        return this.getRemainingLives() <= 0 ?
                Math.min(1.0F, (this.vanishTime + partialTick) / VANISH_WHEN_ACTUALLY_DEAD_TIME) : 0;
    }

    @Override
    public void makePoofParticles() {
        for (int i = 0; i < 30; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level()
                    .addParticle(this.isOnFire() ? ParticleTypes.FLAME : ParticleTypes.POOF,
                            this.getRandomX(1.0),
                            this.getRandomY() + 0.5,
                            this.getRandomZ(1.0),
                            d0,
                            d1,
                            d2);
        }
    }

    @Override
    public void kill(ServerLevel serverLevel) {
        super.kill(serverLevel);
        this.setRemainingLives(0);
    }

    public boolean canHarm(Entity entity) {
        return !entity.getType().is(EntityTypeTags.ZOMBIES);
    }

    @Override
    public boolean killedEntity(ServerLevel serverLevel, LivingEntity entity, DamageSource damageSource) {
        boolean killedEntity = super.killedEntity(serverLevel, entity, damageSource);
        if ((serverLevel.getDifficulty() == Difficulty.NORMAL || serverLevel.getDifficulty() == Difficulty.HARD)
                && entity instanceof Villager villager) {
            if (serverLevel.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return killedEntity;
            }

            if (this.convertVillagerToZombieVillager(serverLevel, villager)) {
                killedEntity = false;
            }
        }

        return killedEntity;
    }

    private boolean convertVillagerToZombieVillager(ServerLevel serverLevel, Villager villager) {
        ZombieVillager zombieVillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER,
                ConversionParams.single(villager, true, true),
                zombieVillagerx -> {
                    zombieVillagerx.finalizeSpawn(serverLevel,
                            serverLevel.getCurrentDifficultyAt(zombieVillagerx.blockPosition()),
                            EntitySpawnReason.CONVERSION,
                            new Zombie.ZombieGroupData(false, true));
                    zombieVillagerx.setVillagerData(villager.getVillagerData());
                    zombieVillagerx.setGossips(villager.getGossips().copy());
                    zombieVillagerx.setTradeOffers(villager.getOffers().copy());
                    zombieVillagerx.setVillagerXp(villager.getVillagerXp());
                    if (!this.isSilent()) {
                        serverLevel.levelEvent(null, 1026, this.blockPosition(), 0);
                    }
                });
        return zombieVillager != null;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("Lives", this.getRemainingLives());
        valueOutput.putShort("VanishTime", (short) this.vanishTime);
        valueOutput.store("Resurrections", ZombieResurrection.LIST_CODEC, this.resurrectionList);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        valueInput.getInt("Lives").ifPresent(this::setRemainingLives);
        this.vanishTime = valueInput.getShortOr("VanishTime", (short) 0);
        valueInput.read("Resurrections", ZombieResurrection.LIST_CODEC).ifPresent(this.resurrectionList::addAll);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENTITY_MUTANT_ZOMBIE_AMBIENT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSoundEvents.ENTITY_MUTANT_ZOMBIE_HURT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_MUTANT_ZOMBIE_DEATH_SOUND_EVENT.value();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if (this.deathTime == 0) {
            this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
        }
    }

    @Override
    public void writeAdditionalAddEntityData(CompoundTag compoundTag) {
        AnimatedEntity.super.writeAdditionalAddEntityData(compoundTag);
        compoundTag.putInt("death_time", this.deathTime);
        compoundTag.putInt("vanish_time", this.vanishTime);
        compoundTag.putInt("throw_hit_tick", this.throwHitTick);
        compoundTag.putInt("throw_finish_tick", this.throwFinishTick);
    }

    @Override
    public void readAdditionalAddEntityData(CompoundTag compoundTag) {
        AnimatedEntity.super.readAdditionalAddEntityData(compoundTag);
        this.deathTime = compoundTag.getIntOr("death_time", 0);
        this.vanishTime = compoundTag.getIntOr("vanish_time", 0);
        this.throwHitTick = compoundTag.getIntOr("throw_hit_tick", 0);
        this.throwFinishTick = compoundTag.getIntOr("throw_finish_tick", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return AdditionalSpawnDataEntity.getPacket(this, serverEntity);
    }

    static class ThrowAttackGoal extends AnimationGoal<MutantZombie> {
        private int finish = -1;

        public ThrowAttackGoal(MutantZombie mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantZombie.THROW_ANIMATION;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                target.stopRiding();
                double x = target.getX() - this.mob.getX();
                double z = target.getZ() - this.mob.getZ();
                double d = Math.sqrt(x * x + z * z);
                target.setDeltaMovement(x / d * 0.8, 1.6, z / d * 0.8);
                EntityUtil.sendPlayerVelocityPacket(target);
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (this.finish >= 10) {
                return false;
            }
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                return !(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative();
            }
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getNavigation().stop();
                this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
                if (this.mob.animationTick == MutantZombie.THROW_ANIMATION.duration()) {
                    this.mob.stuckSpeedMultiplier = Vec3.ZERO;
                    double d1 = target.getX() - this.mob.getX();
                    double d2 = target.getY() - this.mob.getY();
                    double x = target.getZ() - this.mob.getZ();
                    double z = Math.sqrt(d1 * d1 + d2 * d2 + x * x);
                    this.mob.setDeltaMovement(d1 / z * 3.4, d2 / z * 1.4, x / z * 3.4);
                } else if (this.mob.animationTick > MutantZombie.THROW_ANIMATION.duration()) {
                    double d1 = this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F;
                    double d2 = this.mob.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
                    if (d2 < d1 && !this.mob.hasThrowAttackHit()) {
                        this.mob.setThrowAttackHit(true);
                        DamageSource damageSource = this.mob.level().damageSources().mobAttack(this.mob);
                        if (!target.hurtServer((ServerLevel) this.mob.level(),
                                damageSource,
                                (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                            EntityUtil.disableShield(target, 150);
                        }

                        double x = target.getX() - this.mob.getX();
                        double z = target.getZ() - this.mob.getZ();
                        double d = Math.sqrt(x * x + z * z);
                        target.setDeltaMovement(x / d * 0.6, -1.2, z / d * 0.6);
                        target.invulnerableTime = 10;
                        EntityUtil.sendPlayerVelocityPacket(target);
                        EntityUtil.stunRavager(target);
                        this.mob.playSound(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_GRUNT_SOUND_EVENT.value(),
                                0.3F,
                                0.8F + this.mob.random.nextFloat() * 0.4F);
                    }

                    if ((this.mob.onGround() || !this.mob.getInBlockState().getFluidState().isEmpty())
                            && !this.mob.isThrowAttackFinished()) {
                        this.finish = 0;
                        this.mob.setThrowAttackFinished(true);
                    }

                    if (this.finish >= 0) {
                        ++this.finish;
                    }
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.finish = -1;
            this.mob.setThrowAttackHit(false);
            this.mob.setThrowAttackFinished(false);
        }
    }

    static class RoarGoal extends AnimationGoal<MutantZombie> {

        public RoarGoal(MutantZombie mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantZombie.ROAR_ANIMATION;
        }

        @Override
        public boolean canUse() {
            return this.mob.tickCount % 3 == 0 && !this.mob.isAnimationPlaying() && this.mob.getTarget() != null
                    && this.mob.onGround() && this.mob.resurrectionList.isEmpty()
                    && this.mob.distanceToSqr(this.mob.getTarget()) > 16.0
                    && this.mob.random.nextFloat() * 100.0F < 0.35F;
        }

        @Override
        public void start() {
            super.start();
            this.mob.invulnerableTime = 20;
            this.mob.ambientSoundTime = -this.mob.getAmbientSoundInterval();
        }

        @Override
        public void tick() {
            this.mob.getNavigation().stop();
            if (this.mob.animationTick < 75 && this.mob.getTarget() != null) {
                this.mob.lookControl.setLookAt(this.mob.getTarget(), 30.0F, 30.0F);
            }

            if (this.mob.animationTick == 10) {
                this.mob.playSound(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_ROAR_SOUND_EVENT.value(),
                        3.0F,
                        0.7F + this.mob.random.nextFloat() * 0.2F);

                for (Entity entity : this.mob.level()
                        .getEntities(this.mob,
                                this.mob.getBoundingBox().inflate(12.0, 8.0, 12.0),
                                EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
                    if (this.mob.canHarm(entity) && this.mob.distanceToSqr(entity) <= 196.0) {
                        double x = entity.getX() - this.mob.getX();
                        double z = entity.getZ() - this.mob.getZ();
                        double d = Math.sqrt(x * x + z * z);
                        entity.setDeltaMovement(x / d * 0.7, 0.3, z / d * 0.7);
                        DamageSource damageSource = DamageHelper.damageSource(this.mob.level(),
                                ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE,
                                this.mob);
                        entity.hurtServer((ServerLevel) this.mob.level(),
                                damageSource,
                                (float) (2 + this.mob.random.nextInt(2)));
                        EntityUtil.sendPlayerVelocityPacket(entity);
                    }
                }
            }

            if (this.mob.animationTick >= 20 && this.mob.animationTick < 80 && this.mob.animationTick % 10 == 0) {
                int x = Mth.floor(this.mob.getX());
                int y = Mth.floor(this.mob.getBoundingBox().minY);
                int z = Mth.floor(this.mob.getZ());
                x += (1 + this.mob.random.nextInt(8)) * (this.mob.random.nextBoolean() ? 1 : -1);
                z += (1 + this.mob.random.nextInt(8)) * (this.mob.random.nextBoolean() ? 1 : -1);
                y = ZombieResurrection.getSuitableGround(this.mob.level(), x, y - 1, z);
                if (y != -1) {
                    this.mob.resurrectionList.add(new ZombieResurrection(this.mob.level(), x, y, z));
                }
            }

        }
    }

    static class SlamGroundGoal extends AnimationGoal<MutantZombie> {
        private double dirX = -1.0;
        private double dirZ = -1.0;

        public SlamGroundGoal(MutantZombie mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        protected EntityAnimation getAnimation() {
            return MutantZombie.SLAM_GROUND_ANIMATION;
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.mob.ambientSoundTime = -this.mob.getAmbientSoundInterval();
            this.mob.playSound(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT.value(),
                    0.3F,
                    0.8F + this.mob.random.nextFloat() * 0.4F);
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                this.mob.getNavigation().stop();
                if (this.mob.animationTick < 8) {
                    this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
                }

                if (this.mob.animationTick == 8) {
                    double x = target.getX() - this.mob.getX();
                    double z = target.getZ() - this.mob.getZ();
                    double d = Math.sqrt(x * x + z * z);
                    this.dirX = x / d;
                    this.dirZ = z / d;
                }

                if (this.mob.animationTick == 12) {
                    int x = Mth.floor(this.mob.getX() + this.dirX * 2.0);
                    int y = Mth.floor(this.mob.getBoundingBox().minY);
                    int z = Mth.floor(this.mob.getZ() + this.dirZ * 2.0);
                    int x1 = Mth.floor(this.mob.getX() + this.dirX * 8.0);
                    int z1 = Mth.floor(this.mob.getZ() + this.dirZ * 8.0);
                    SeismicWave.createWaves(this.mob.level(), this.mob.seismicWaveList, x, z, x1, z1, y);
                    this.mob.playSound(SoundEvents.GENERIC_EXPLODE.value(),
                            0.5F,
                            0.8F + this.mob.random.nextFloat() * 0.4F);
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.dirX = -1.0;
            this.dirZ = -1.0;
        }
    }
}
