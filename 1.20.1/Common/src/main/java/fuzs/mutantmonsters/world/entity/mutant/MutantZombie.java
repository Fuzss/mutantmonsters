package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.animation.AnimatedEntity;
import fuzs.mutantmonsters.animation.Animation;
import fuzs.mutantmonsters.core.SeismicWave;
import fuzs.mutantmonsters.core.ZombieResurrection;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.ai.goal.AnimationGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.level.pathfinder.MutantGroundPathNavigation;
import fuzs.puzzleslib.api.entity.v1.AdditionalAddEntityData;
import fuzs.puzzleslib.api.entity.v1.DamageSourcesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MutantZombie extends AbstractMutantMonster implements AnimatedEntity {
    public static final int MAX_VANISH_TIME = 100;
    public static final int MAX_DEATH_TIME = 140;
    public static final Animation SLAM_GROUND_ANIMATION = new Animation(25);
    public static final Animation THROW_ANIMATION = new Animation(15);
    public static final Animation ROAR_ANIMATION = new Animation(120);
    private static final EntityDataAccessor<Integer> LIVES = SynchedEntityData.defineId(MutantZombie.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> THROW_ATTACK_STATE = SynchedEntityData.defineId(MutantZombie.class, EntityDataSerializers.BYTE);
    private static final Animation[] ANIMATIONS = new Animation[]{SLAM_GROUND_ANIMATION, THROW_ANIMATION, ROAR_ANIMATION};
    private final List<SeismicWave> seismicWaveList;
    private final List<ZombieResurrection> resurrectionList;
    public int throwHitTick;
    public int throwFinishTick;
    public int vanishTime;
    private Animation animation;
    private int animationTick;
    private DamageSource deathCause;

    public MutantZombie(EntityType<? extends MutantZombie> type, Level worldIn) {
        super(type, worldIn);
        this.animation = Animation.NONE;
        this.throwHitTick = -1;
        this.throwFinishTick = -1;
        this.seismicWaveList = new ArrayList<>();
        this.resurrectionList = new ArrayList<>();
        this.setMaxUpStep(1.0F);
        this.xpReward = 30;
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 150.0).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SlamGroundGoal(this));
        this.goalSelector.addGoal(0, new RoarGoal(this));
        this.goalSelector.addGoal(0, new ThrowAttackGoal(this));
        this.goalSelector.addGoal(1, (new MutantMeleeAttackGoal(this, 1.2)).setMaxAttackTick(0));
        this.goalSelector.addGoal(2, new AvoidDamageGoal(this, 1.0));
        this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 1.0, true, 4, () -> {
            return false;
        }));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByNearestTargetGoal(this, WitherBoss.class)).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LIVES, 3);
        this.entityData.define(THROW_ATTACK_STATE, (byte) 0);
    }

    public int getLives() {
        return this.entityData.get(LIVES);
    }

    private void setLives(int lives) {
        this.entityData.set(LIVES, lives);
    }

    public boolean hasThrowAttackHit() {
        return (this.entityData.get(THROW_ATTACK_STATE) & 1) != 0;
    }

    private void setThrowAttackHit(boolean hit) {
        byte b0 = this.entityData.get(THROW_ATTACK_STATE);
        this.entityData.set(THROW_ATTACK_STATE, hit ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public boolean isThrowAttackFinished() {
        return (this.entityData.get(THROW_ATTACK_STATE) & 2) != 0;
    }

    private void setThrowAttackFinished(boolean finished) {
        byte b0 = this.entityData.get(THROW_ATTACK_STATE);
        this.entityData.set(THROW_ATTACK_STATE, finished ? (byte) (b0 | 2) : (byte) (b0 & -3));
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
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 2.8F;
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
    protected float tickHeadTurn(float renderYawOffset, float distance) {
        return this.deathTime > 0 ? distance : super.tickHeadTurn(renderYawOffset, distance);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public int getMaxFallDistance() {
        return this.getTarget() != null ? (int) this.distanceTo(this.getTarget()) : 3;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return !this.onClimbable();
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        // we cannot use #mobInteract as it does not trigger for dead mobs
        if (player.isSpectator()) return InteractionResult.PASS;
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack itemInHandCopy = itemInHand.copy();
        InteractionResult interactionResult = this.deadMobInteract(player, hand);
        if (interactionResult.consumesAction()) {
            if (player.getAbilities().instabuild && itemInHand == player.getItemInHand(hand) && itemInHand.getCount() < itemInHandCopy.getCount()) {
                itemInHand.setCount(itemInHandCopy.getCount());
            }
            if (itemInHand.isEmpty() && !player.getAbilities().instabuild) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
            this.gameEvent(GameEvent.ENTITY_INTERACT);

            return interactionResult;
        }

        return super.interactAt(player, vec, hand);
    }

    private InteractionResult deadMobInteract(Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(ItemTags.CREEPER_IGNITERS) && !this.isAlive() && !this.isOnFire() && !this.isInWaterOrRain()) {
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide) {
                this.setSecondsOnFire(8);
                if (!itemInHand.isDamageableItem()) {
                    itemInHand.shrink(1);
                } else {
                    itemInHand.hurtAndBreak(1, player, (livingEntity) -> {
                        livingEntity.broadcastBreakEvent(hand);
                    });
                }
                player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (!this.isAnimationPlaying()) {
            if (entityIn.getVehicle() != this && this.random.nextInt(5) != 0) {
                if (this.onGround() || !this.getFeetBlockState().getFluidState().isEmpty()) {
                    this.animation = SLAM_GROUND_ANIMATION;
                }
            } else {
                this.animation = THROW_ANIMATION;
            }
        }

        return true;
    }

    @Override
    protected void customServerAiStep() {
        if (!this.isAnimationPlaying() && this.getTarget() != null && Math.abs(this.getY() - this.getTarget().getY()) <= 1.0 && this.distanceToSqr(this.getTarget()) <= 49.0 && this.random.nextInt(20) == 0) {
            this.animation = SLAM_GROUND_ANIMATION;
        }

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getEntity();
            return (entity == null || this.canHarm(entity) && (this.animation != THROW_ANIMATION || entity != this.getTarget())) && super.hurt(source, amount);
        }
    }

    @Override
    protected void updateNoActionTime() {

    }

    @Override
    public void tick() {
        super.tick();
        this.fixRotation();
        this.updateAnimation();
        this.updateMeleeGrounds();
        if (this.level().isNight() && this.tickCount % 100 == 0 && this.isAlive() && this.getHealth() < this.getMaxHealth()) {
            this.heal(2.0F);
        }

        for (int i = this.resurrectionList.size() - 1; i >= 0; --i) {
            ZombieResurrection zr = this.resurrectionList.get(i);
            if (!zr.update(this)) {
                this.resurrectionList.remove(zr);
            }
        }

        if (this.getHealth() > 0.0F) {
            this.deathTime = 0;
            this.vanishTime = 0;
        }

    }

    private void fixRotation() {
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

        if (this.level().isClientSide) {
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

    private void updateMeleeGrounds() {
        if (!this.seismicWaveList.isEmpty()) {
            SeismicWave wave = this.seismicWaveList.remove(0);
            wave.affectBlocks(this.level(), this);
            AABB box = new AABB(wave.getX(), (double) wave.getY() + 1.0, wave.getZ(), (double) wave.getX() + 1.0, (double) wave.getY() + 2.0, (double) wave.getZ() + 1.0);
            if (wave.isFirst()) {
                double addScale = this.random.nextDouble() * 0.75;
                box = box.inflate(0.25 + addScale, 0.25 + addScale * 0.5, 0.25 + addScale);
            }

            DamageSource source = DamageSourcesHelper.source(this.level(), ModRegistry.EFFECTS_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, this);

            for (Entity entity : this.level().getEntities(this, box, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(this::canHarm))) {
                if (entity instanceof LivingEntity && entity.hurt(source, wave.isFirst() ? (float) (9 + this.random.nextInt(4)) : (float) (6 + this.random.nextInt(3))) && this.random.nextInt(5) == 0) {
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
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0);
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
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            this.deathCause = cause;
            this.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
            this.setLastHurtMob(this.getLastHurtByMob());
            this.level().broadcastEntityEvent(this, (byte) 3);
            if (this.lastHurtByPlayerTime > 0) {
                this.lastHurtByPlayerTime += 140;
            }
        }
    }

    @Override
    protected void tickDeath() {
        if (this.deathTime <= 25 || !this.isOnFire() || this.deathTime >= 100) {
            ++this.deathTime;
        }

        if (this.isOnFire()) {
            if (this.vanishTime == 0) {
                EntityUtil.sendMetadataPacket(this);
            }

            ++this.vanishTime;
        } else {
            this.vanishTime = Math.max(0, this.vanishTime - 1);
        }

        if (this.deathTime >= 140) {
            this.deathTime = 0;
            this.vanishTime = 0;
            this.deathCause = null;
            this.setLives(this.getLives() - 1);
            if (this.getLastHurtMob() != null) {
                this.getLastHurtMob().setLastHurtByMob(this);
            }

            this.setHealth((float) Math.round(this.getMaxHealth() / 3.75F));
        }

        if (this.vanishTime >= 100 || this.getLives() <= 0 && this.deathTime > 25) {
            if (!this.level().isClientSide) {
                super.die(this.deathCause != null ? this.deathCause : this.level().damageSources().generic());
            }

            for (int i = 0; i < 30; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level().addParticle(this.isOnFire() ? ParticleTypes.FLAME : ParticleTypes.POOF, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }

            this.discard();
        }

    }

    @Override
    public void kill() {
        super.kill();
        this.setLives(0);
    }

    private boolean canHarm(Entity entity) {
        return entity.getType() != EntityType.ZOMBIE && entity.getType() != EntityType.ZOMBIE_VILLAGER && entity.getType() != EntityType.HUSK && entity.getType() != EntityType.DROWNED && !(entity instanceof MutantZombie);
    }

    @Override
    public boolean killedEntity(ServerLevel serverWorld, LivingEntity livingEntity) {
        if ((serverWorld.getDifficulty() == Difficulty.NORMAL && this.random.nextBoolean() || serverWorld.getDifficulty() == Difficulty.HARD) && livingEntity instanceof Villager) {
            EntityUtil.convertMobWithNBT(livingEntity, EntityType.ZOMBIE_VILLAGER, false);
            if (!livingEntity.isSilent()) {
                serverWorld.levelEvent(null, 1026, livingEntity.blockPosition(), 0);
            }
            return false;
        }
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Lives", this.getLives());
        compound.putShort("VanishTime", (short) this.vanishTime);
        if (!this.resurrectionList.isEmpty()) {
            ListTag listnbt = new ListTag();

            for (ZombieResurrection resurrection : this.resurrectionList) {
                CompoundTag compoundNBT = NbtUtils.writeBlockPos(resurrection);
                compoundNBT.putInt("Tick", resurrection.getTick());
                listnbt.add(compoundNBT);
            }

            compound.put("Resurrections", listnbt);
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Lives")) {
            this.setLives(compound.getInt("Lives"));
        }

        this.vanishTime = compound.getShort("VanishTime");
        ListTag listNBT = compound.getList("Resurrections", 10);

        for (int i = 0; i < listNBT.size(); ++i) {
            CompoundTag compoundNBT = listNBT.getCompound(i);
            this.resurrectionList.add(i, new ZombieResurrection(this.level(), NbtUtils.readBlockPos(compoundNBT), compoundNBT.getInt("Tick")));
        }

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModRegistry.ENTITY_MUTANT_ZOMBIE_AMBIENT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModRegistry.ENTITY_MUTANT_ZOMBIE_HURT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModRegistry.ENTITY_MUTANT_ZOMBIE_DEATH_SOUND_EVENT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        if (this.deathTime == 0) {
            this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.0F);
        }

    }

    @Override
    public void writeAdditionalAddEntityData(FriendlyByteBuf buffer) {
        AnimatedEntity.super.writeAdditionalAddEntityData(buffer);
        buffer.writeVarInt(this.deathTime);
        buffer.writeVarInt(this.vanishTime);
        buffer.writeVarInt(this.throwHitTick);
        buffer.writeVarInt(this.throwFinishTick);
    }

    @Override
    public void readAdditionalAddEntityData(FriendlyByteBuf additionalData) {
        AnimatedEntity.super.readAdditionalAddEntityData(additionalData);
        this.deathTime = additionalData.readVarInt();
        this.vanishTime = additionalData.readVarInt();
        this.throwHitTick = additionalData.readVarInt();
        this.throwFinishTick = additionalData.readVarInt();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return AdditionalAddEntityData.getPacket(this);
    }

    static class ThrowAttackGoal extends AnimationGoal<MutantZombie> {
        private int finish = -1;

        public ThrowAttackGoal(MutantZombie mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        protected Animation getAnimation() {
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
                return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
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
                        if (!target.hurt(this.mob.level().damageSources().mobAttack(this.mob), (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                            EntityUtil.disableShield(target, 150);
                        }

                        double x = target.getX() - this.mob.getX();
                        double z = target.getZ() - this.mob.getZ();
                        double d = Math.sqrt(x * x + z * z);
                        target.setDeltaMovement(x / d * 0.6, -1.2, z / d * 0.6);
                        target.invulnerableTime = 10;
                        EntityUtil.sendPlayerVelocityPacket(target);
                        EntityUtil.stunRavager(target);
                        this.mob.playSound(ModRegistry.ENTITY_MUTANT_ZOMBIE_GRUNT_SOUND_EVENT.get(), 0.3F, 0.8F + this.mob.random.nextFloat() * 0.4F);
                    }

                    if ((this.mob.onGround() || !this.mob.getFeetBlockState().getFluidState().isEmpty()) && !this.mob.isThrowAttackFinished()) {
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
        protected Animation getAnimation() {
            return MutantZombie.ROAR_ANIMATION;
        }

        @Override
        public boolean canUse() {
            return this.mob.tickCount % 3 == 0 && !this.mob.isAnimationPlaying() && this.mob.getTarget() != null && this.mob.onGround() && this.mob.resurrectionList.isEmpty() && this.mob.distanceToSqr(this.mob.getTarget()) > 16.0 && this.mob.random.nextFloat() * 100.0F < 0.35F;
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
                this.mob.playSound(ModRegistry.ENTITY_MUTANT_ZOMBIE_ROAR_SOUND_EVENT.get(), 3.0F, 0.7F + this.mob.random.nextFloat() * 0.2F);

                for (Entity entity : this.mob.level().getEntities(this.mob, this.mob.getBoundingBox().inflate(12.0, 8.0, 12.0))) {
                    if (this.mob.canHarm(entity) && this.mob.distanceToSqr(entity) <= 196.0) {
                        double x = entity.getX() - this.mob.getX();
                        double z = entity.getZ() - this.mob.getZ();
                        double d = Math.sqrt(x * x + z * z);
                        entity.setDeltaMovement(x / d * 0.7, 0.3, z / d * 0.7);
                        entity.hurt(DamageSourcesHelper.source(this.mob.level(), ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, this.mob), (float) (2 + this.mob.random.nextInt(2)));
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
        protected Animation getAnimation() {
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
            this.mob.playSound(ModRegistry.ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT.get(), 0.3F, 0.8F + this.mob.random.nextFloat() * 0.4F);
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
                    this.mob.playSound(SoundEvents.GENERIC_EXPLODE, 0.5F, 0.8F + this.mob.random.nextFloat() * 0.4F);
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
