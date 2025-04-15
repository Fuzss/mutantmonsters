package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.proxy.Proxy;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.OwnerTargetGoal;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class CreeperMinion extends ShoulderRidingEntity {
    private static final EntityDataAccessor<Byte> CREEPER_MINION_FLAGS = SynchedEntityData.defineId(CreeperMinion.class,
            EntityDataSerializers.BYTE
    );
    private static final EntityDataAccessor<Integer> EXPLODE_STATE = SynchedEntityData.defineId(CreeperMinion.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Float> EXPLOSION_RADIUS = SynchedEntityData.defineId(CreeperMinion.class,
            EntityDataSerializers.FLOAT
    );
    private static final int TOTAL_FUSE_TIME = 26;

    private int lastActiveTime;
    private int timeSinceIgnited;

    public CreeperMinion(EntityType<? extends CreeperMinion> entityType, Level level) {
        super(entityType, level);
        this.setDestroyBlocks(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new CreeperMinionSwellGoal());
        this.goalSelector.addGoal(3, new AvoidDamageGoal(this, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Animal.class, 6.0F, 1.0, 1.2, EntityUtil::isFeline) {
            @Override
            public boolean canUse() {
                return !CreeperMinion.this.isTame() && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new MutantMeleeAttackGoal(this, 1.2));
        this.goalSelector.addGoal(5, new CreeperMinionFollowOwnerGoal());
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LandOnOwnersShoulderGoal(this) {
            @Override
            public boolean canUse() {
                return CreeperMinion.this.isTame() && CreeperMinion.this.getOwner() instanceof Player && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new OwnerTargetGoal(this));
        this.targetSelector.addGoal(1, new HurtByNearestTargetGoal(this));
        this.targetSelector.addGoal(2, new NonTameRandomTargetGoal<>(this, Player.class, true, null));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 4.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CREEPER_MINION_FLAGS, (byte) 0);
        builder.define(EXPLODE_STATE, -1);
        builder.define(EXPLOSION_RADIUS, 2.0F);
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = this.getOwnerUUID();
        if (uuid == null) {
            return null;
        } else {
            Entity entity = this.level().getPlayerByUUID(uuid);
            if (entity == null && this.level() instanceof ServerLevel) {
                entity = ((ServerLevel) this.level()).getEntity(uuid);
            }

            return entity instanceof LivingEntity livingEntity ? livingEntity : null;
        }
    }

    public int getExplodeState() {
        return this.entityData.get(EXPLODE_STATE);
    }

    public void setExplodeState(int state) {
        this.entityData.set(EXPLODE_STATE, state);
    }

    public boolean isCharged() {
        return (this.entityData.get(CREEPER_MINION_FLAGS) & 1) != 0;
    }

    public void setCharged(boolean charged) {
        byte b0 = this.entityData.get(CREEPER_MINION_FLAGS);
        this.entityData.set(CREEPER_MINION_FLAGS, charged ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public boolean hasIgnited() {
        return (this.entityData.get(CREEPER_MINION_FLAGS) & 4) != 0;
    }

    public void ignite() {
        byte b0 = this.entityData.get(CREEPER_MINION_FLAGS);
        this.entityData.set(CREEPER_MINION_FLAGS, (byte) (b0 | 4));
    }

    public boolean canExplodeContinuously() {
        return (this.entityData.get(CREEPER_MINION_FLAGS) & 8) != 0;
    }

    public void setCanExplodeContinuously(boolean continuously) {
        byte b0 = this.entityData.get(CREEPER_MINION_FLAGS);
        this.entityData.set(CREEPER_MINION_FLAGS, continuously ? (byte) (b0 | 8) : (byte) (b0 & -9));
    }

    public boolean canDestroyBlocks() {
        return (this.entityData.get(CREEPER_MINION_FLAGS) & 16) != 0;
    }

    public void setDestroyBlocks(boolean destroy) {
        byte b0 = this.entityData.get(CREEPER_MINION_FLAGS);
        this.entityData.set(CREEPER_MINION_FLAGS, destroy ? (byte) (b0 | 16) : (byte) (b0 & -17));
    }

    public boolean canRideOnShoulder() {
        return (this.entityData.get(CREEPER_MINION_FLAGS) & 32) != 0;
    }

    public void setCanRideOnShoulder(boolean canRide) {
        byte b0 = this.entityData.get(CREEPER_MINION_FLAGS);
        this.entityData.set(CREEPER_MINION_FLAGS, canRide ? (byte) (b0 | 32) : (byte) (b0 & -33));
    }

    public float getExplosionRadius() {
        return this.entityData.get(EXPLOSION_RADIUS);
    }

    public void setExplosionRadius(float radius) {
        this.entityData.set(EXPLOSION_RADIUS, radius);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_FLAGS_ID.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isInSittingPose() ? super.getDefaultDimensions(pose).scale(1.0F, 0.75F) :
                super.getDefaultDimensions(pose);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canSitOnShoulder() {
        return super.canSitOnShoulder() && this.canRideOnShoulder() && this.getTarget() == null &&
                this.getExplodeState() <= 0;
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        super.thunderHit(level, lightning);
        this.setCharged(true);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return !this.isTame();
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        boolean hasFallen = super.causeFallDamage(distance, damageMultiplier, source);
        this.timeSinceIgnited = (int) ((float) this.timeSinceIgnited + distance * 1.5F);
        if (this.timeSinceIgnited > TOTAL_FUSE_TIME - 5) {
            this.timeSinceIgnited = TOTAL_FUSE_TIME - 5;
        }

        return hasFallen;
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            if (this.hasIgnited()) {
                this.setExplodeState(1);
            }

            int i = this.getExplodeState();
            if (i > 0 && this.timeSinceIgnited == 0) {
                this.playSound(ModSoundEvents.ENTITY_CREEPER_MINION_PRIMED_SOUND_EVENT.value(), 1.0F,
                        this.getVoicePitch()
                );
            }

            this.timeSinceIgnited += i;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }

            if (this.timeSinceIgnited >= TOTAL_FUSE_TIME) {
                this.timeSinceIgnited = 0;
                if (this.level() instanceof ServerLevel serverLevel) {
                    float sizeIn = this.getExplosionRadius() + (this.isCharged() ? 2.0F : 0.0F);
                    Level.ExplosionInteraction interaction =
                            this.canDestroyBlocks() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.NONE;
                    MutatedExplosionHelper.explode(this, sizeIn, false, interaction);
                    if (!this.canExplodeContinuously()) {
                        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) &&
                                this.getOwner() instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendSystemMessage(
                                    Component.translatable("death.attack.explosion", this.getDisplayName()));
                        }

                        this.dead = true;
                        this.discard();
                        EntityUtil.spawnLingeringCloud(this);
                    }
                }

                this.setExplodeState(-TOTAL_FUSE_TIME);
            }

            if (this.getDeltaMovement().lengthSqr() > 0.800000011920929 && this.getTarget() != null &&
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(0.5).intersects(
                            this.getTarget().getBoundingBox())) {
                this.timeSinceIgnited = TOTAL_FUSE_TIME;
            }
        }

        super.tick();
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, (float) this.lastActiveTime, (float) this.timeSinceIgnited) /
                (float) (TOTAL_FUSE_TIME - 2);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (this.isTame()) {
            if (itemInHand.is(ModItems.CREEPER_MINION_TRACKER_ITEM.value())) {
                if (this.level().isClientSide) {
                    Proxy.INSTANCE.displayCreeperMinionTrackerGUI(this);
                }
                return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
            } else {
                if (this.isOwnedBy(player)) {
                    if (itemInHand.is(Items.GUNPOWDER)) {
                        double d0;
                        double d1;
                        double d2;
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.heal(1.0F);
                            itemInHand.shrink(1);
                            d0 = this.random.nextGaussian() * 0.02;
                            d1 = this.random.nextGaussian() * 0.02;
                            d2 = this.random.nextGaussian() * 0.02;
                            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY(),
                                    this.getRandomZ(1.0), d0, d1, d2
                            );
                            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
                        }

                        if (this.getMaxHealth() < 20.0F) {
                            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() + 1.0F);
                            itemInHand.shrink(1);
                            d0 = this.random.nextGaussian() * 0.02;
                            d1 = this.random.nextGaussian() * 0.02;
                            d2 = this.random.nextGaussian() * 0.02;
                            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY(),
                                    this.getRandomZ(1.0), d0, d1, d2
                            );
                            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
                        }
                    } else {
                        if (!itemInHand.is(Items.TNT)) {
                            if (!this.level().isClientSide) {
                                this.setOrderedToSit(!this.isOrderedToSit());
                                this.setLastHurtByMob(null);
                                this.setTarget(null);
                            }

                            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
                        }

                        if (!this.canExplodeContinuously()) {
                            this.forcedAgeTimer += 15;
                            this.setCanExplodeContinuously(true);
                            itemInHand.shrink(1);
                            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
                        }

                        float explosionRadius = this.getExplosionRadius();
                        if (explosionRadius < 4.0F) {
                            this.forcedAgeTimer += 10;
                            this.setExplosionRadius(explosionRadius + 0.11F);
                            itemInHand.shrink(1);
                            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
                        }
                    }
                }

                return InteractionResult.PASS;
            }
        } else if (itemInHand.is(ItemTags.CREEPER_IGNITERS) && !this.hasIgnited()) {
            player.awardStat(Stats.ITEM_USED.get(itemInHand.getItem()));
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE,
                    this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F
            );
            if (!this.level().isClientSide) {
                this.ignite();
                if (!itemInHand.isDamageableItem()) {
                    itemInHand.shrink(1);
                } else {
                    ItemHelper.hurtAndBreak(itemInHand, 1, player, interactionHand);
                }
            }

            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
        } else if (player.isCreative() && itemInHand.is(ModItems.CREEPER_MINION_TRACKER_ITEM.value()) &&
                this.getOwner() == null) {
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                this.setTame(true, true);
                this.setOwnerUUID(player.getUUID());
                serverPlayer.sendSystemMessage(Component.translatable(
                        ModItems.CREEPER_MINION_TRACKER_ITEM.value().getDescriptionId() + ".tame_success",
                        this.getDisplayName(), player.getDisplayName()
                ));
            }

            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return EntityUtil.shouldAttackEntity(target, owner, true);
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (this.isInvulnerableTo(serverLevel, damageSource)) {
            return false;
        } else {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
                if (this.isTame()) {
                    return false;
                }

                if (damageAmount >= 2.0F) {
                    damageAmount = 2.0F;
                }
            }

            this.setOrderedToSit(false);
            return super.hurtServer(serverLevel, damageSource, damageAmount);
        }
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return this.isTame();
    }

    @Override
    public boolean canBeLeashed() {
        return super.canBeLeashed() && this.isTame();
    }

    @Override
    public boolean canAttackType(EntityType<?> entityType) {
        return super.canAttackType(entityType) && entityType != ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.72F * this.getEyeHeight(), this.getBbWidth() * 0.2F);
    }

    @Override
    @Nullable
    public PlayerTeam getTeam() {
        LivingEntity owner = this.getOwner();
        return owner != null ? owner.getTeam() : super.getTeam();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null && this.getExplodeState() <= 0) {
            super.playAmbientSound();
        }
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSoundEvents.ENTITY_CREEPER_MINION_HURT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_CREEPER_MINION_DEATH_SOUND_EVENT.value();
    }

    @Override
    public SoundSource getSoundSource() {
        return this.isTame() ? SoundSource.NEUTRAL : SoundSource.HOSTILE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Tamed", this.isTame());
        compound.putBoolean("ExplodesContinuously", this.canExplodeContinuously());
        compound.putBoolean("DestroysBlocks", this.canDestroyBlocks());
        compound.putBoolean("CanRideOnShoulder", this.canRideOnShoulder());
        compound.putBoolean("Ignited", this.hasIgnited());
        compound.putFloat("ExplosionRadius", this.getExplosionRadius());
        if (this.isCharged()) {
            compound.putBoolean("Powered", true);
        }

        for (String s : new String[]{"Age", "ForcedAge", "InLove", "LoveCause"}) {
            compound.remove(s);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setTame(compound.getBoolean("Tamed"), false);
        this.setCanExplodeContinuously(compound.getBoolean("ExplodesContinuously"));
        this.setDestroyBlocks(compound.getBoolean("DestroysBlocks"));
        this.setCanRideOnShoulder(compound.getBoolean("CanRideOnShoulder"));
        this.setCharged(compound.getBoolean("Powered"));
        if (compound.contains("ExplosionRadius")) {
            this.setExplosionRadius(compound.getFloat("ExplosionRadius"));
        }

        if (compound.getBoolean("Ignited")) {
            this.ignite();
        }

    }

    class CreeperMinionFollowOwnerGoal extends FollowOwnerGoal {

        public CreeperMinionFollowOwnerGoal() {
            super(CreeperMinion.this, 1.2, 10.0F, 5.0F);
        }

        @Override
        public void tick() {
            if (!CreeperMinion.this.isTame()) {
                if (CreeperMinion.this.getOwner() != null) {
                    CreeperMinion.this.getNavigation().moveTo(CreeperMinion.this.getOwner(), 1.2);
                }
            } else {
                super.tick();
            }
        }
    }

    class CreeperMinionSwellGoal extends Goal {

        public CreeperMinionSwellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = CreeperMinion.this.getTarget();
            return !CreeperMinion.this.isOrderedToSit() && (CreeperMinion.this.getExplodeState() > 0 ||
                    livingentity != null && CreeperMinion.this.distanceToSqr(livingentity) < 9.0 &&
                            CreeperMinion.this.hasLineOfSight(livingentity));
        }

        @Override
        public void tick() {
            CreeperMinion.this.setExplodeState(
                    CreeperMinion.this.getTarget() != null && !(CreeperMinion.this.distanceToSqr(
                            CreeperMinion.this.getTarget()) > 36.0) &&
                            CreeperMinion.this.getSensing().hasLineOfSight(CreeperMinion.this.getTarget()) ? 1 : -1);
        }

        @Override
        public void start() {
            CreeperMinion.this.getNavigation().stop();
        }
    }
}
