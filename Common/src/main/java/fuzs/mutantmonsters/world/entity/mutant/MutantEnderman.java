package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.animation.AnimatedEntity;
import fuzs.mutantmonsters.animation.Animation;
import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.network.S2CMutantEndermanHeldBlockMessage;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import fuzs.mutantmonsters.world.entity.ai.goal.AnimationGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import fuzs.mutantmonsters.world.level.pathfinder.MutantGroundPathNavigation;
import fuzs.puzzleslib.api.entity.v1.AdditionalAddEntityData;
import fuzs.puzzleslib.api.entity.v1.DamageSourcesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MutantEnderman extends Monster implements NeutralMob, AnimatedEntity {
    private static final EntityDataAccessor<Optional<BlockPos>> TELEPORT_POSITION = SynchedEntityData.defineId(MutantEnderman.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Byte> ACTIVE_ARM = SynchedEntityData.defineId(MutantEnderman.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> CLONE = SynchedEntityData.defineId(MutantEnderman.class, EntityDataSerializers.BOOLEAN);
    public static final Animation MELEE_ANIMATION = new Animation(10);
    public static final Animation THROW_ANIMATION = new Animation(14);
    public static final Animation STARE_ANIMATION = new Animation(100);
    public static final Animation TELEPORT_ANIMATION = new Animation(10);
    public static final Animation SCREAM_ANIMATION = new Animation(165);
    public static final Animation CLONE_ANIMATION = new Animation(600);
    public static final Animation TELESMASH_ANIMATION = new Animation(30);
    public static final Animation DEATH_ANIMATION = new Animation(280);
    private static final Animation[] ANIMATIONS = new Animation[]{MELEE_ANIMATION, THROW_ANIMATION, STARE_ANIMATION, TELEPORT_ANIMATION, SCREAM_ANIMATION, CLONE_ANIMATION, TELESMASH_ANIMATION, DEATH_ANIMATION};

    private Animation animation;
    private int animationTick;
    private int prevArmScale;
    private int armScale;
    public int hasTarget;
    private int screamDelayTick;
    private int[] heldBlock;
    private int[] heldBlockTick;
    private boolean triggerThrowBlock;
    private int blockFrenzy;
    @Nullable
    private List<Entity> capturedEntities;
    private DamageSource deathCause;
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    public MutantEnderman(EntityType<? extends MutantEnderman> type, Level worldIn) {
        super(type, worldIn);
        this.animation = Animation.NONE;
        this.heldBlock = new int[4];
        this.heldBlockTick = new int[4];
        this.xpReward = 40;
        this.setMaxUpStep(1.4F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeGoal(this));
        this.goalSelector.addGoal(1, new ThrowBlockGoal(this));
        this.goalSelector.addGoal(1, new StareGoal(this));
        this.goalSelector.addGoal(1, new TeleportGoal(this));
        this.goalSelector.addGoal(1, new ScreamGoal(this));
        this.goalSelector.addGoal(1, new CloneGoal(this));
        this.goalSelector.addGoal(1, new TeleSmashGoal(this));
        this.goalSelector.addGoal(2, (new MutantMeleeAttackGoal(this, 1.2)).setMaxAttackTick(15));
        this.goalSelector.addGoal(3, new AvoidDamageGoal(this, 1.0));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByNearestTargetGoal(this));
        this.targetSelector.addGoal(1, new FindTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Endermite.class, true));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 200.0).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 96.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TELEPORT_POSITION, Optional.empty());
        this.entityData.define(ACTIVE_ARM, (byte)0);
        this.entityData.define(CLONE, false);
    }

    public Optional<BlockPos> getTeleportPosition() {
        return this.entityData.get(TELEPORT_POSITION);
    }

    private void setTeleportPosition(@Nullable BlockPos pos) {
        this.entityData.set(TELEPORT_POSITION, Optional.ofNullable(pos));
    }

    public int getHeldBlock(int index) {
        return this.heldBlock[index];
    }

    public void setHeldBlock(int index, int blockId, int tick) {
        this.heldBlock[index] = blockId;
        this.heldBlockTick[index] = tick;
        if (!this.level().isClientSide) {
            MutantMonsters.NETWORK.sendToAllTracking(new S2CMutantEndermanHeldBlockMessage(this, blockId, index), this);
        }
    }

    public int getHeldBlockTick(int arm) {
        return this.heldBlockTick[arm];
    }

    public int getActiveArm() {
        return this.entityData.get(ACTIVE_ARM);
    }

    private void setActiveArm(int armId) {
        this.entityData.set(ACTIVE_ARM, (byte)armId);
    }

    public boolean isClone() {
        return this.entityData.get(CLONE);
    }

    private void setClone(boolean isClone) {
        this.entityData.set(CLONE, isClone);
        this.playSound(ModRegistry.ENTITY_MUTANT_ENDERMAN_MORPH_SOUND_EVENT.get(), 2.0F, this.getVoicePitch());
        this.level().broadcastEntityEvent(this, (byte)0);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angerTarget) {
        this.angerTarget = angerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
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
        return this.isClone() ? 2.55F : 3.9F;
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isClone() ? ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get().getDimensions() : super.getDimensions(poseIn);
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
        return this.isClone() ? 3 : super.getMaxFallDistance();
    }

    @Override
    public boolean isPickable() {
        return super.isPickable() && this.animation != TELEPORT_ANIMATION;
    }

    @Override
    protected void updateNoActionTime() {
    }

    @Override
    public boolean isSensitiveToWater() {
        return this.tickCount % 100 == 0 && !this.isClone();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (CLONE.equals(key)) {
            this.refreshDimensions();
        }

        if (TELEPORT_POSITION.equals(key) && this.getTeleportPosition().isPresent() && this.level().isClientSide) {
            this.animation = TELEPORT_ANIMATION;
            this.animationTick = 0;
            this.spawnTeleportParticles();
        }

    }

    @Override
    public void setTarget(@Nullable LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
        this.setAggressive(entitylivingbaseIn != null);
    }

    public float getArmScale(float partialTicks) {
        return Mth.lerp(partialTicks, (float)this.prevArmScale, (float)this.armScale) / 10.0F;
    }

    private void updateTargetTick() {
        this.prevArmScale = this.armScale;
        if (this.isAggressive()) {
            this.hasTarget = 20;
        }

        boolean emptyHanded = true;

        for(int i = 0; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] > 0) {
                emptyHanded = false;
            }

            if (this.hasTarget > 0) {
                if (this.heldBlock[i] > 0) {
                    this.heldBlockTick[i] = Math.min(10, this.heldBlockTick[i] + 1);
                }
            } else {
                this.heldBlockTick[i] = Math.max(0, this.heldBlockTick[i] - 1);
            }
        }

        if (this.hasTarget > 0) {
            this.armScale = Math.min(10, this.armScale + 1);
        } else if (emptyHanded) {
            this.armScale = Math.max(0, this.armScale - 1);
        } else if (!this.level().isClientSide) {
            boolean mobGriefing = CommonAbstractions.INSTANCE.getMobGriefingEvent(this.level(), this);

            for(int i = 0; i < this.heldBlock.length; ++i) {
                if (this.heldBlock[i] > 0 && this.heldBlockTick[i] == 0) {
                    BlockPos startPos = BlockPos.containing(this.getX() - 1.5 + this.random.nextDouble() * 4.0, this.getY() - 0.5 + this.random.nextDouble() * 2.5, this.getZ() - 1.5 + this.random.nextDouble() * 4.0);
                    BlockState heldState = Block.updateFromNeighbourShapes(Block.stateById(this.heldBlock[i]), this.level(), startPos);
                    if (mobGriefing && this.canPlaceBlock(this.level(), startPos, heldState, startPos.below())) {
                        this.level().setBlockAndUpdate(startPos, heldState);
                        SoundType soundType = heldState.getSoundType();
                        this.level().playSound(null, startPos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                        this.setHeldBlock(i, 0, 0);
                    } else if (!mobGriefing || this.random.nextInt(50) == 0) {
                        this.triggerThrowBlock = true;
                    }
                }
            }
        }

        this.hasTarget = Math.max(0, this.hasTarget - 1);
    }

    private boolean canPlaceBlock(Level world, BlockPos startPos, BlockState heldState, BlockPos belowPos) {
        return world.isEmptyBlock(startPos) && !world.isEmptyBlock(belowPos) && world.getBlockState(belowPos).isCollisionShapeFullBlock(world, belowPos) && heldState.canSurvive(world, startPos) && world.getEntities(this, new AABB(startPos)).isEmpty();
    }

    private void updateScreamEntities() {
        this.screamDelayTick = Math.max(0, this.screamDelayTick - 1);
        if (this.animation == SCREAM_ANIMATION && this.animationTick >= 40 && this.animationTick <= 160) {
            if (this.animationTick == 160) {
                this.capturedEntities = null;
            } else if (this.capturedEntities == null) {
                this.capturedEntities = this.level().getEntities(this, this.getBoundingBox().inflate(20.0, 12.0, 20.0), EndersoulFragment.IS_VALID_TARGET);
            } else {
                Iterator<Entity> iterator = this.capturedEntities.iterator();
                while(iterator.hasNext()) {
                    Entity entity = iterator.next();
                    if (!(this.distanceToSqr(entity) > 400.0) && !entity.isSpectator()) {
                        entity.setXRot(entity.getXRot() + (this.random.nextFloat() - 0.3F) * 6.0F);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }

    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 0) {
            this.spawnTeleportParticles();
        } else {
            super.handleEntityEvent(id);
        }

    }

    @Override
    public void aiStep() {
        this.jumping = false;
        super.aiStep();
        if (this.isAnimationPlaying()) {
            ++this.animationTick;
        }

        if (this.animation == DEATH_ANIMATION) {
            this.deathTime = this.animationTick;
        }

        this.updateTargetTick();
        this.updateScreamEntities();
        if (this.level().isClientSide && !this.isClone()) {
            for(int i = 0; i < 3; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5), this.getRandomY() + (double)(this.deathTime > 0 ? 1.0F : 0.0F) - 0.25, this.getRandomZ(0.5), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }

    }

    private void updateBlockFrenzy() {
        this.blockFrenzy = Math.max(0, this.blockFrenzy - 1);
        if (this.getTarget() != null && !this.isAnimationPlaying()) {
            if (this.blockFrenzy == 0 && (this.getLastDamageSource() != null && this.getLastDamageSource().isIndirect() || this.random.nextInt(!this.isPathFinding() ? 300 : 600) == 0)) {
                this.blockFrenzy = 200 + this.random.nextInt(80);
            }

            if (this.blockFrenzy > 0 && this.random.nextInt(8) == 0) {
                int index = this.getFavorableHand();
                BlockPos pos = BlockPos.containing(this.getX() - 2.5 + this.random.nextDouble() * 5.0, this.getY() - 0.5 + this.random.nextDouble() * 3.0, this.getZ() - 2.5 + this.random.nextDouble() * 5.0);
                BlockState blockState = this.level().getBlockState(pos);
                if (index != -1 && canBlockBeHeld(this.level(), pos, blockState, ModRegistry.MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG)) {
                    this.setHeldBlock(index, Block.getId(blockState), 0);
                    if (CommonAbstractions.INSTANCE.getMobGriefingEvent(this.level(), this)) {
                        this.level().removeBlock(pos, false);
                    }
                }
            }
        }

    }

    public static boolean canBlockBeHeld(Level level, BlockPos pos, BlockState state, TagKey<Block> tag) {
        return state.isCollisionShapeFullBlock(level, pos) && !state.hasBlockEntity() && (state.is(Blocks.END_STONE) || !state.is(tag));
    }

    private void updateTeleport() {
        Entity entity = this.getTarget();
        this.teleportByChance(entity == null ? 1600 : 800, entity);
        if (this.isInWater() || this.fallDistance > 3.0F || entity != null && (this.isPassengerOfSameVehicle(entity) || this.distanceToSqr(entity) > 1024.0 || !this.isPathFinding())) {
            this.teleportByChance(10, entity);
        }

    }

    @Override
    protected void customServerAiStep() {
        this.updatePersistentAnger((ServerLevel) this.level(), true);
        this.updateBlockFrenzy();
        this.updateTeleport();
    }

    private int getAvailableHand() {
        List<Integer> list = new ArrayList<>();

        for(int i = 0; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] == 0) {
                list.add(i);
            }
        }

        if (list.isEmpty()) {
            return -1;
        } else {
            return list.get(this.random.nextInt(list.size()));
        }
    }

    private int getFavorableHand() {
        List<Integer> outer = new ArrayList<>();
        List<Integer> inner = new ArrayList<>();

        for(int i = 0; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] == 0) {
                if (i <= 1) {
                    outer.add(i);
                } else {
                    inner.add(i);
                }
            }
        }

        if (outer.isEmpty() && inner.isEmpty()) {
            return -1;
        } else if (!outer.isEmpty()) {
            return outer.get(this.random.nextInt(outer.size()));
        } else {
            return inner.get(this.random.nextInt(inner.size()));
        }
    }

    private int getThrowingHand() {
        List<Integer> outer = new ArrayList<>();
        List<Integer> inner = new ArrayList<>();

        for(int i = 0; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] > 0) {
                if (i <= 1) {
                    outer.add(i);
                } else {
                    inner.add(i);
                }
            }
        }

        if (outer.isEmpty() && inner.isEmpty()) {
            return -1;
        } else if (!inner.isEmpty()) {
            return inner.get(this.random.nextInt(inner.size()));
        } else {
            return outer.get(this.random.nextInt(outer.size()));
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (!this.level().isClientSide && !this.isAnimationPlaying()) {
            int arm = this.getAvailableHand();
            if (!this.teleportByChance(6, entityIn)) {
                if (arm != -1) {
                    boolean allHandsFree = this.heldBlock[0] == 0 && this.heldBlock[1] == 0;
                    if (allHandsFree && entityIn.getType() != EntityType.WITHER && this.random.nextInt(10) == 0) {
                        this.animation = CLONE_ANIMATION;
                    } else if (allHandsFree && this.random.nextInt(7) == 0) {
                        this.animation = TELESMASH_ANIMATION;
                    } else {
                        this.setActiveArm(arm);
                        this.animation = MELEE_ANIMATION;
                    }
                } else {
                    this.triggerThrowBlock = true;
                }
            }
        }

        if (this.isClone()) {
            boolean flag = entityIn.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            if (!this.level().isClientSide && this.random.nextInt(2) == 0) {
                double x = entityIn.getX() + (this.random.nextDouble() - 0.5) * 24.0;
                double y = entityIn.getY() + (double)this.random.nextInt(5) + 4.0;
                double z = entityIn.getZ() + (this.random.nextDouble() - 0.5) * 24.0;
                this.teleportToPosition(x, y, z);
            }

            if (flag) {
                this.heal(2.0F);
                this.doEnchantDamageEffects(this, entityIn);
            }

            this.swing(InteractionHand.MAIN_HAND);
            return flag;
        } else {
            return true;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!(source.getEntity() instanceof EnderDragon) && !(source.getEntity() instanceof MutantEnderman)) {
            if ((this.animation == TELEPORT_ANIMATION || this.animation == SCREAM_ANIMATION) && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                return false;
            } else {
                boolean damaged = super.hurt(source, amount);
                if (damaged && this.animation == STARE_ANIMATION) {
                    this.animation = Animation.NONE;
                    return damaged;
                } else {
                    if (!this.level().isClientSide && !this.isAnimationPlaying() && this.isAlive()) {
                        Entity entity = source.getEntity();
                        boolean betterDodge = entity == null;
                        if (source.is(DamageTypeTags.IS_PROJECTILE) || source.is(DamageTypeTags.IS_EXPLOSION) || source.is(DamageTypes.FALL)) {
                            betterDodge = true;
                        }

                        if (this.teleportByChance(betterDodge ? 3 : 6, entity) && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                            if (entity instanceof LivingEntity) {
                                this.setLastHurtByMob((LivingEntity)entity);
                            }

                            return false;
                        }

                        boolean betterTeleport = source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL);
                        this.teleportByChance(betterTeleport ? 3 : 5, entity);
                    }

                    return damaged;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstanceIn, @Nullable Entity entity) {
        return !this.isClone() && super.addEffect(effectInstanceIn, entity);
    }

    private boolean teleportByChance(int chance, @Nullable Entity entity) {
        if (this.isAnimationPlaying() && !this.isClone()) {
            return false;
        } else if (this.random.nextInt(Math.max(1, chance)) == 0) {
            return entity == null ? this.teleportRandomly() : this.teleportToEntity(entity);
        } else {
            return false;
        }
    }

    private boolean teleportRandomly() {
        if (this.isAnimationPlaying() && !this.isClone()) {
            return false;
        } else {
            double radius = 24.0;
            double x = this.getX() + (this.random.nextDouble() - 0.5) * 2.0 * radius;
            double y = this.getY() + (double)this.random.nextInt((int)radius * 2) - radius;
            double z = this.getZ() + (this.random.nextDouble() - 0.5) * 2.0 * radius;
            return this.teleportToPosition(x, y, z);
        }
    }

    private boolean teleportToEntity(Entity entity) {
        if (this.isAnimationPlaying() && !this.isClone()) {
            return false;
        } else {
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            double radius = 16.0;
            if (this.distanceToSqr(entity) < 100.0) {
                x = entity.getX() + (this.random.nextDouble() - 0.5) * 2.0 * radius;
                y = entity.getY() + this.random.nextDouble() * radius;
                z = entity.getZ() + (this.random.nextDouble() - 0.5) * 2.0 * radius;
            } else {
                Vec3 vec = new Vec3(this.getX() - entity.getX(), this.getY(0.5) - entity.getEyeY(), this.getZ() - entity.getZ());
                vec = vec.normalize();
                x = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - vec.x * radius;
                y = this.getY() + (double)this.random.nextInt(8) - vec.y * radius;
                z = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - vec.z * radius;
            }

            return this.teleportToPosition(x, y, z);
        }
    }

    private boolean teleportToPosition(double x, double y, double z) {
        if (!this.isEffectiveAi()) {
            return false;
        } else if (this.isClone()) {
            boolean flag = EntityUtil.teleportTo(this, x, y, z);
            if (flag) {
                this.stopRiding();
                if (!this.isSilent()) {
                    this.level().playLocalSound(this.xo, this.yo, this.zo, ModRegistry.ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT.get(), this.getSoundSource(), 1.0F, 1.0F, false);
                    this.playSound(ModRegistry.ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT.get(), 1.0F, 1.0F);
                }
            }

            return flag;
        } else if (this.isAnimationPlaying()) {
            return false;
        } else {
            this.animation = TELEPORT_ANIMATION;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
            boolean success = false;
            if (this.level().isLoaded(pos)) {
                do {
                    pos.move(Direction.DOWN);
                } while(pos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(pos).blocksMotion());

                pos.move(Direction.UP);
                AABB bb = this.getType().getAABB((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
                if (this.level().noCollision(this, bb) && !this.level().containsAnyLiquid(bb)) {
                    success = true;
                }
            }

            if (!success) {
                this.animation = Animation.NONE;
                return false;
            } else {
                this.setTeleportPosition(pos);
                return true;
            }
        }
    }

    public static void teleportAttack(LivingEntity attacker) {
        double radius = 3.0;
        int duration = 140 + attacker.getRandom().nextInt(60);
        DamageSource damageSource = attacker.damageSources().mobAttack(attacker);
        if (attacker instanceof Player) {
            radius = 2.0;
            duration = 100;
            damageSource = attacker.damageSources().playerAttack((Player) attacker);
        }

        for (Entity entity : attacker.level().getEntities(attacker, attacker.getBoundingBox().inflate(radius), EndersoulFragment.IS_VALID_TARGET)) {
            if (entity instanceof LivingEntity && entity.hurt(damageSource, 4.0F) && attacker.getRandom().nextInt(3) == 0) {
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration));
            }

            double x = entity.getX() - attacker.getX();
            double z = entity.getZ() - attacker.getZ();
            double signX = x / Math.abs(x);
            double signZ = z / Math.abs(z);
            entity.setDeltaMovement((radius * signX * 2.0 - x) * 0.20000000298023224, 0.20000000298023224, (radius * signZ * 2.0 - z) * 0.20000000298023224);
            EntityUtil.sendPlayerVelocityPacket(entity);
        }

    }

    private void spawnTeleportParticles() {
        BlockPos teleportPos = this.getTeleportPosition().orElse(null);
        int amount = teleportPos != null ? 512 : 256;

        for(int i = 0; i < amount; ++i) {
            boolean useCurrentPos = teleportPos == null || i < amount / 2;
            double tempX = (useCurrentPos ? this.getX() : (double)teleportPos.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth();
            double tempY = (useCurrentPos ? this.getY() : (double)teleportPos.getY()) + (this.random.nextDouble() - 0.5) * (double)this.getBbHeight() + 1.5;
            double tempZ = (useCurrentPos ? this.getZ() : (double)teleportPos.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth();
            this.level().addParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), tempX, tempY, tempZ, (this.random.nextDouble() - 0.5) * 1.8, (this.random.nextDouble() - 0.5) * 1.8, (this.random.nextDouble() - 0.5) * 1.8);
        }

    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0);
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
        if (this.isClone()) {
            super.blockedByShield(livingEntity);
        } else {
            livingEntity.hurtMarked = true;
        }

    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        this.capturedEntities = null;
        if (!this.level().isClientSide) {
            this.deathCause = cause;
            this.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
            AnimatedEntity.sendAnimationPacket(this, DEATH_ANIMATION);
            if (this.lastHurtByPlayerTime > 0) {
                this.lastHurtByPlayerTime += DEATH_ANIMATION.duration();
            }
        }

    }

    @Override
    protected void tickDeath() {
        this.setDeltaMovement(0.0, Math.min(this.getDeltaMovement().y, 0.0), 0.0);
        if (this.deathTime == 80) {
            this.playSound(ModRegistry.ENTITY_MUTANT_ENDERMAN_DEATH_SOUND_EVENT.get(), 5.0F, this.getVoicePitch());
        }

        if (this.deathTime >= 60) {
            if (this.deathTime < 80 && this.capturedEntities == null) {
                this.capturedEntities = this.level().getEntities(this, this.getBoundingBox().inflate(10.0, 8.0, 10.0), EndersoulFragment.IS_VALID_TARGET);
            }

            if (!this.level().isClientSide && this.random.nextInt(3) != 0) {
                EndersoulFragment orb = new EndersoulFragment(this.level(), this);
                orb.setPos(this.getX(), this.getY() + 3.8, this.getZ());
                orb.setDeltaMovement((this.random.nextDouble() - 0.5) * 1.5, (this.random.nextDouble() - 0.5) * 1.5, (this.random.nextDouble() - 0.5) * 1.5);
                this.level().addFreshEntity(orb);
            }
        }

        if (this.deathTime >= 80 && this.deathTime < DEATH_ANIMATION.duration() - 20 && this.capturedEntities != null) {
            Iterator<Entity> iterator = this.capturedEntities.iterator();

            label80:
            while(true) {
                while(true) {
                    Entity entity;
                    do {
                        if (!iterator.hasNext()) {
                            break label80;
                        }

                        entity = iterator.next();
                        if (entity.fallDistance > 4.5F) {
                            entity.fallDistance = 4.5F;
                        }
                    } while(!(this.distanceToSqr(entity) > 64.0));

                    if (!EndersoulFragment.isProtected(entity) && !entity.isSpectator()) {
                        double x = this.getX() - entity.getX();
                        double y = entity.getDeltaMovement().y;
                        double z = this.getZ() - entity.getZ();
                        double d = Math.sqrt(x * x + z * z);
                        if (this.getY() + 4.0 > entity.getY()) {
                            y = Math.max(entity.getDeltaMovement().y, 0.4000000059604645);
                        }

                        entity.setDeltaMovement(0.800000011920929 * x / d, y, 0.800000011920929 * z / d);
                    } else {
                        iterator.remove();
                    }
                }
            }
        }

        if (!this.level().isClientSide && this.deathTime >= 100 && this.deathTime < 150 && this.deathTime % 6 == 0 && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            super.dropFromLootTable(this.deathCause != null ? this.deathCause : this.level().damageSources().generic(), this.lastHurtByPlayerTime > 0);
        }

        if (this.deathTime >= DEATH_ANIMATION.duration()) {
            this.dropExperience();
            this.discard();
        }

    }

    @Override
    protected void dropFromLootTable(DamageSource damageSourceIn, boolean attackedRecently) {
    }

    @Override
    public boolean shouldDropExperience() {
        return this.deathTime > 0;
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack) {
        return this.deathTime > 0 ? this.spawnAtLocation(stack, 3.84F) : super.spawnAtLocation(stack);
    }

    public static boolean canSpawn(EntityType<MutantEnderman> type, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return randomIn.nextInt(3) == 0 && checkMonsterSpawnRules(type, worldIn, reason, pos, randomIn);
    }

    @Override
    protected Component getTypeName() {
        return this.isClone() ? ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get().getDescription() : super.getTypeName();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addPersistentAngerSaveData(compound);
        compound.putInt("BlockFrenzy", this.blockFrenzy);
        compound.putInt("ScreamDelay", this.screamDelayTick);
        ListTag listNBT = new ListTag();

        for(int i = 0; i < this.heldBlock.length; ++i) {
            if (this.heldBlock[i] > 0) {
                CompoundTag compoundNBT = NbtUtils.writeBlockState(Block.stateById(this.heldBlock[i]));
                compound.putByte("Index", (byte)i);
                compoundNBT.putInt("Tick", this.heldBlockTick[i]);
                listNBT.add(compoundNBT);
            }
        }

        if (!listNBT.isEmpty()) {
            compound.put("HeldBlocks", listNBT);
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (this.level() instanceof ServerLevel) {
            this.readPersistentAngerSaveData(this.level(), compound);
        }

        this.blockFrenzy = compound.getInt("BlockFrenzy");
        this.screamDelayTick = compound.getInt("ScreamDelay");
        if (compound.contains("HeldBlocks", 9)) {
            ListTag listNBT = compound.getList("HeldBlocks", 9);

            for(int i = 0; i < listNBT.size(); ++i) {
                CompoundTag compoundNBT = listNBT.getCompound(i);
                BlockState blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compoundNBT);
                this.setHeldBlock(compoundNBT.getByte("Index"), Block.getId(blockState), compound.getInt("Tick"));
            }
        }

        if (this.deathTime > 0) {
            this.animation = DEATH_ANIMATION;
            this.animationTick = this.deathTime;
        }

    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Override
    public void playAmbientSound() {
        if (!this.isClone()) {
            super.playAmbientSound();
        }

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModRegistry.ENTITY_MUTANT_ENDERMAN_AMBIENT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModRegistry.ENTITY_MUTANT_ENDERMAN_HURT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModRegistry.ENTITY_MUTANT_ENDERMAN_HURT_SOUND_EVENT.get();
    }

    private boolean isBeingLookedAtBy(LivingEntity target) {
        if (!(target instanceof Mob)) {
            Vec3 lookVec = target.getViewVector(1.0F).normalize();
            Vec3 targetVec = new Vec3(this.getX() - target.getX(), this.getEyeY() - target.getEyeY(), this.getZ() - target.getZ());
            double length = targetVec.length();
            targetVec = targetVec.normalize();
            double d = lookVec.dot(targetVec);
            return d > 1.0 - 0.08 / length && target.hasLineOfSight(this);
        } else {
            return ((Mob)target).getTarget() == this && target.hasLineOfSight(this);
        }
    }

    @Override
    public void writeAdditionalAddEntityData(FriendlyByteBuf buffer) {
        AnimatedEntity.super.writeAdditionalAddEntityData(buffer);
        buffer.writeVarInt(this.hasTarget);
        buffer.writeVarInt(this.armScale);
        buffer.writeVarIntArray(this.heldBlock);
        buffer.writeVarIntArray(this.heldBlockTick);
    }

    @Override
    public void readAdditionalAddEntityData(FriendlyByteBuf additionalData) {
        AnimatedEntity.super.readAdditionalAddEntityData(additionalData);
        this.hasTarget = additionalData.readVarInt();
        this.armScale = additionalData.readVarInt();
        this.heldBlock = additionalData.readVarIntArray();
        this.heldBlockTick = additionalData.readVarIntArray();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return (Packet<ClientGamePacketListener>) AdditionalAddEntityData.getPacket(this);
    }

    static class ThrowBlockGoal extends AnimationGoal<MutantEnderman> {
        public ThrowBlockGoal(MutantEnderman mob) {
            super(mob);
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.THROW_ANIMATION;
        }

        @Override
        public boolean canUse() {
            if (this.mob.isAnimationPlaying()) {
                return false;
            } else if (!this.mob.triggerThrowBlock && this.mob.getRandom().nextInt(28) != 0) {
                return false;
            } else if (this.mob.getTarget() != null && !this.mob.hasLineOfSight(this.mob.getTarget())) {
                return false;
            } else {
                int id = this.mob.getThrowingHand();
                if (id == -1) {
                    return false;
                } else {
                    this.mob.setActiveArm(id);
                    return true;
                }
            }
        }

        @Override
        public void start() {
            super.start();
            int id = this.mob.getActiveArm();
            this.mob.level().addFreshEntity(new ThrowableBlock(this.mob, id));
            this.mob.setHeldBlock(id, 0, 0);
        }

        @Override
        public void stop() {
            super.stop();
            this.mob.triggerThrowBlock = false;
        }
    }

    static class TeleSmashGoal extends AnimationGoal<MutantEnderman> {
        private LivingEntity attackTarget;

        public TeleSmashGoal(MutantEnderman mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.TELESMASH_ANIMATION;
        }

        @Override
        public boolean canUse() {
            this.attackTarget = this.mob.getTarget();
            return this.attackTarget != null && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.attackTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 5));
            this.attackTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160 + this.attackTarget.getRandom().nextInt(160)));
        }

        @Override
        public void tick() {
            this.mob.getNavigation().stop();
            if (this.mob.animationTick < 20) {
                this.mob.lookControl.setLookAt(this.attackTarget, 30.0F, 30.0F);
            }

            if (this.mob.animationTick == 17) {
                this.attackTarget.stopRiding();
            }

            if (this.mob.animationTick == 18) {
                double x = this.attackTarget.getX() + (this.attackTarget.getRandom().nextDouble() - 0.5) * 14.0;
                double y = this.attackTarget.getY() + this.attackTarget.getRandom().nextDouble() + (this.attackTarget instanceof Player ? 13.0 : 7.0);
                double z = this.attackTarget.getZ() + (this.attackTarget.getRandom().nextDouble() - 0.5) * 14.0;
                EntityUtil.stunRavager(this.attackTarget);
                EntityUtil.sendParticlePacket(this.attackTarget, ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), 256);
                this.attackTarget.teleportTo(x, y, z);
                this.mob.level().playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, this.attackTarget.getSoundSource(), 1.2F, 0.9F + this.attackTarget.getRandom().nextFloat() * 0.2F);
                this.attackTarget.hurt(DamageSourcesHelper.source(this.mob.level(), ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, this.mob), 6.0F);
            }

        }

        @Override
        public void stop() {
            super.stop();
            this.attackTarget = null;
        }
    }

    static class TeleportGoal extends AnimationGoal<MutantEnderman> {
        public TeleportGoal(MutantEnderman mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.TELEPORT_ANIMATION;
        }

        @Override
        public void start() {
            this.mob.animation = MutantEnderman.TELEPORT_ANIMATION;
            this.mob.animationTick = 0;
            MutantEnderman.teleportAttack(this.mob);
            this.mob.getTeleportPosition().ifPresent((pos) -> {
                this.mob.setPos((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
            });
            if (!this.mob.isSilent()) {
                this.mob.level().playSound(null, this.mob.xo, this.mob.yo, this.mob.zo, ModRegistry.ENTITY_MUTANT_ENDERMAN_TELEPORT_SOUND_EVENT.get(), this.mob.getSoundSource(), 1.0F, 1.0F);
                this.mob.playSound(ModRegistry.ENTITY_MUTANT_ENDERMAN_TELEPORT_SOUND_EVENT.get(), 1.0F, 1.0F);
            }

            MutantEnderman.teleportAttack(this.mob);
            this.mob.setPos(this.mob.xo, this.mob.yo, this.mob.zo);
        }

        @Override
        public void tick() {
            this.mob.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.mob.stopRiding();
            this.mob.fallDistance = 0.0F;
            this.mob.getTeleportPosition().ifPresent((pos) -> {
                this.mob.teleportTo((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
            });
            super.stop();
            this.mob.setTeleportPosition(null);
        }
    }

    static class ScreamGoal extends AnimationGoal<MutantEnderman> {
        public ScreamGoal(MutantEnderman mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.SCREAM_ANIMATION;
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTarget() != null && !this.mob.isAnimationPlaying()) {
                return this.mob.screamDelayTick <= 0 && this.mob.distanceToSqr(this.mob.getTarget()) < 400.0 && this.mob.random.nextInt(this.mob.isInWaterOrRain() ? 400 : 1200) == 0;
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            this.mob.getNavigation().stop();
            if (this.mob.animationTick == 40) {
                this.mob.ambientSoundTime = -this.mob.getAmbientSoundInterval();
                this.mob.level().getLevelData().setRaining(false);
                this.mob.level().broadcastEntityEvent(this.mob, (byte)0);
                this.mob.playSound(ModRegistry.ENTITY_MUTANT_ENDERMAN_SCREAM_SOUND_EVENT.get(), 5.0F, 0.7F + this.mob.random.nextFloat() * 0.2F);

                for (Entity entity : this.mob.level().getEntities(this.mob, this.mob.getBoundingBox().inflate(20.0, 12.0, 20.0), EndersoulFragment.IS_VALID_TARGET)) {
                    if (this.mob.distanceToSqr(entity) < 400.0) {
                        entity.hurt(DamageSourcesHelper.source(this.mob.level(), ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, this.mob), 4.0F);
                        if (entity instanceof Mob mobEntity) {
                            mobEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 3));
                            if (this.mob.random.nextInt(2) != 0) {
                                mobEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 120 + this.mob.random.nextInt(180), this.mob.random.nextInt(2)));
                            }

                            if (this.mob.random.nextInt(4) != 0) {
                                mobEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300 + this.mob.random.nextInt(300), this.mob.random.nextInt(2)));
                            }

                            if (this.mob.random.nextInt(3) != 0) {
                                mobEntity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 120 + this.mob.random.nextInt(60), 10 + this.mob.random.nextInt(2)));
                            }

                            if (this.mob.random.nextInt(4) != 0) {
                                mobEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120 + this.mob.random.nextInt(400)));
                            }
                        }
                    }
                }
            }

        }

        @Override
        public void stop() {
            super.stop();
            this.mob.screamDelayTick = 600;
        }
    }

    static class CloneGoal extends AnimationGoal<MutantEnderman> {
        private final List<EndersoulClone> cloneList = new ArrayList<>();
        private LivingEntity attackTarget;

        public CloneGoal(MutantEnderman mob) {
            super(mob);
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.CLONE_ANIMATION;
        }

        @Override
        public boolean canUse() {
            this.attackTarget = this.mob.getTarget();
            if (this.attackTarget == null) {
                return false;
            } else if (this.attackTarget.getType() != EntityType.WITHER && this.mob.heldBlock[0] == 0 && this.mob.heldBlock[1] == 0) {
                return this.mob.hurtTime == 0 && (super.canUse() || !this.mob.isAnimationPlaying() && this.mob.tickCount % 3 == 0 && this.mob.random.nextInt(300) == 0);
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            super.start();
            this.mob.invulnerableTime = 20;
            this.mob.setMaxUpStep(1.0F);
            this.mob.setClone(true);
            this.mob.clearFire();
            this.mob.removeAllEffects();

            for(int i = 0; i < 7; ++i) {
                double x = this.attackTarget.getX() + (this.mob.random.nextDouble() - 0.5) * 24.0;
                double y = this.attackTarget.getY() + 8.0;
                double z = this.attackTarget.getZ() + (this.mob.random.nextDouble() - 0.5) * 24.0;
                this.createClone(x, y, z);
            }

            this.createClone(this.mob.xo, this.mob.yo, this.mob.zo);
            double x = this.attackTarget.getX() + (this.mob.random.nextDouble() - 0.5) * 24.0;
            double y = this.attackTarget.getY() + 8.0;
            double z = this.attackTarget.getZ() + (this.mob.random.nextDouble() - 0.5) * 24.0;
            this.mob.teleportToPosition(x, y, z);
            EntityUtil.divertAttackers(this.mob, this.getRandomClone());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.mob.getTarget() != null && !this.cloneList.isEmpty() && this.mob.hurtTime == 0;
        }

        @Override
        public void tick() {
            for(int i = this.cloneList.size() - 1; i >= 0; --i) {
                EndersoulClone clone = this.cloneList.get(i);
                if (!clone.isAlive() || clone.level() != this.mob.level()) {
                    this.cloneList.remove(i);
                }
            }

        }

        @Override
        public void stop() {
            super.stop();
            this.mob.setClone(false);
            this.mob.setMaxUpStep(1.4F);

            for (EndersoulClone clone : this.cloneList) {
                if (clone.isAlive()) {
                    clone.discard();
                    EntityUtil.divertAttackers(clone, this.mob);
                }
            }

            this.cloneList.clear();
            this.mob.getNavigation().stop();
            this.attackTarget.setLastHurtByMob(this.mob);
            this.attackTarget = null;
        }

        private void createClone(double x, double y, double z) {
            EndersoulClone clone = ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get().create(this.mob.level());
            clone.setCloner(this.mob);
            this.cloneList.add(clone);
            if (!EntityUtil.teleportTo(clone, x, y, z)) {
                clone.copyPosition(this.mob);
            }

            this.mob.level().addFreshEntity(clone);
        }

        private Mob getRandomClone() {
            return this.cloneList.isEmpty() ? this.mob : this.cloneList.get(this.mob.random.nextInt(this.cloneList.size()));
        }
    }

    static class MeleeGoal extends AnimationGoal<MutantEnderman> {
        public MeleeGoal(MutantEnderman mob) {
            super(mob);
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.MELEE_ANIMATION;
        }

        @Override
        public void tick() {
            if (this.mob.animationTick == 3) {
                this.mob.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, this.mob.getVoicePitch());
                boolean lower = this.mob.getActiveArm() >= 2;
                float attackDamage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);

                for (LivingEntity livingEntity : this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(4.0))) {
                    if (!(livingEntity instanceof MutantEnderman) && !(livingEntity instanceof EndersoulClone)) {
                        double dist = this.mob.distanceTo(livingEntity);
                        double x = this.mob.getX() - livingEntity.getX();
                        double z = this.mob.getZ() - livingEntity.getZ();
                        if (this.mob.getBoundingBox().minY <= livingEntity.getBoundingBox().maxY && dist <= 4.0 && EntityUtil.getHeadAngle(this.mob, x, z) < 3.0F + (1.0F - (float) dist / 4.0F) * 40.0F) {
                            livingEntity.hurt(this.mob.damageSources().mobAttack(this.mob), attackDamage > 0.0F ? attackDamage + (lower ? 1.0F : 3.0F) : 0.0F);
                            float power = 0.4F + this.mob.random.nextFloat() * 0.2F;
                            if (!lower) {
                                power += 0.2F;
                            }

                            livingEntity.setDeltaMovement(-x / dist * (double) power, power * 0.6F, -z / dist * (double) power);
                            EntityUtil.sendPlayerVelocityPacket(livingEntity);
                        }
                    }
                }
            }

        }
    }

    static class StareGoal extends AnimationGoal<MutantEnderman> {
        private LivingEntity attackTarget;

        public StareGoal(MutantEnderman mob) {
            super(mob);
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        protected Animation getAnimation() {
            return MutantEnderman.STARE_ANIMATION;
        }

        @Override
        public boolean canUse() {
            this.attackTarget = this.mob.getTarget();
            return this.attackTarget != null && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            this.mob.ambientSoundTime = -this.mob.getAmbientSoundInterval();
            this.mob.level().playSound(null, this.mob, ModRegistry.ENTITY_MUTANT_ENDERMAN_STARE_SOUND_EVENT.get(), this.mob.getSoundSource(), 2.5F, 0.7F + this.mob.random.nextFloat() * 0.2F);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.attackTarget.isAlive() && this.mob.isBeingLookedAtBy(this.attackTarget);
        }

        @Override
        public void tick() {
            this.mob.getNavigation().stop();
            this.mob.lookControl.setLookAt(this.attackTarget, 45.0F, 45.0F);
        }

        @Override
        public void stop() {
            super.stop();
            this.attackTarget.stopRiding();
            this.attackTarget.hurt(DamageSourcesHelper.source(this.mob.level(), ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, this.mob), 2.0F);
            this.attackTarget.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160 + this.mob.random.nextInt(140)));
            double x = this.mob.getX() - this.attackTarget.getX();
            double z = this.mob.getZ() - this.attackTarget.getZ();
            this.attackTarget.setDeltaMovement(x * 0.10000000149011612, 0.30000001192092896, z * 0.10000000149011612);
            EntityUtil.sendPlayerVelocityPacket(this.attackTarget);
            this.attackTarget = null;
        }
    }

    static class FindTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
        public FindTargetGoal(MutantEnderman mutantEnderman) {
            super(mutantEnderman, LivingEntity.class, 10, false, false, (target) -> {
                return (mutantEnderman.isAngryAt(target) || mutantEnderman.isBeingLookedAtBy(target) || EndersoulFragment.isProtected(target)) && target.attackable();
            });
        }

        @Override
        public boolean canUse() {
            boolean flag = !((MutantEnderman)this.mob).isAnimationPlaying() && super.canUse();
            if (flag && ((MutantEnderman)this.mob).isBeingLookedAtBy(this.target)) {
                ((MutantEnderman)this.mob).animation = MutantEnderman.STARE_ANIMATION;
            }

            return flag;
        }

        @Override
        protected AABB getTargetSearchArea(double targetDistance) {
            return this.mob.getBoundingBox().inflate(targetDistance, targetDistance / 2.0, targetDistance);
        }
    }
}
