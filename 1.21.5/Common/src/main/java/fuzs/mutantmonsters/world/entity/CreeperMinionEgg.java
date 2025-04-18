package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CreeperMinionEgg extends Entity implements OwnableEntity {
    private static final EntityDataAccessor<Boolean> DATA_CHARGED = SynchedEntityData.defineId(CreeperMinionEgg.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(
            CreeperMinionEgg.class,
            EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    private final InterpolationHandler interpolation = new InterpolationHandler(this);
    private int health;
    private int age;
    private int recentlyHit;
    private int dismountTicks;

    public CreeperMinionEgg(EntityType<? extends CreeperMinionEgg> entityType, Level level) {
        super(entityType, level);
        this.health = 8;
        this.age = (60 + this.random.nextInt(40)) * 1200;
        this.blocksBuilding = true;
    }

    public CreeperMinionEgg(MutantCreeper spawner) {
        this(ModEntityTypes.CREEPER_MINION_EGG_ENTITY_TYPE.value(), spawner.level());
        this.setPos(spawner.getX(), spawner.getY(), spawner.getZ());
        this.setCharged(spawner.isCharged());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_CHARGED, false);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    private void setHealth(int health) {
        this.health = health;
    }

    private void setAge(int age) {
        this.age = age;
    }

    @Nullable
    @Override
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwner(@Nullable Player livingEntity) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(livingEntity).map(EntityReference::new));
    }

    public void setOwnerReference(@Nullable EntityReference<LivingEntity> entityReference) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(entityReference));
    }

    public boolean isCharged() {
        return this.entityData.get(DATA_CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(DATA_CHARGED, charged);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return Boat.canVehicleCollide(this, entity);
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public boolean isPushable() {
        return this.isAlive();
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return this.interpolation;
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        super.thunderHit(serverLevel, lightningBolt);
        this.setCharged(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, 0.04, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, 0.0, 0.7));
        }

        if (this.isPassenger()) {
            if (this.getFirstPassenger() != null) {
                this.dismountTicks = 10;
            } else if (this.dismountTicks > 0) {
                this.dismountTicks--;
            }
            Entity rootVehicle = this.getRootVehicle();
            if (this.isInWall() || !(rootVehicle.hasPose(Pose.STANDING) || rootVehicle.hasPose(Pose.CROUCHING)) ||
                    this.dismountTicks <= 0 && rootVehicle.isShiftKeyDown() || rootVehicle.isSpectator()) {
                this.stopRiding();
                this.playMountingSound(false);
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.health < 8 && this.tickCount - this.recentlyHit > 80 && this.tickCount % 20 == 0) {
                ++this.health;
            }

            if (--this.age <= 0) {
                EntityReference<LivingEntity> entityReference = this.getOwnerReference();
                if (entityReference != null) {
                    if (entityReference.getEntity(this.level(), LivingEntity.class) instanceof Player player &&
                            this.distanceToSqr(player) < 4096.0) {
                        this.hatch(player);
                    } else {
                        this.age = 1200;
                    }
                } else {
                    this.hurtServer(serverLevel, this.damageSources().magic(), 1000.0F);
                }
            }
        }
    }

    private void hatch(Player player) {
        CreeperMinion creeperMinion = ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value()
                .create(this.level(), EntitySpawnReason.BREEDING);
        if (creeperMinion != null) {
            if (!CommonAbstractions.INSTANCE.onAnimalTame(creeperMinion, player)) {
                creeperMinion.tame(player);
                creeperMinion.setOrderedToSit(true);
            }

            creeperMinion.setCharged(this.isCharged());
            creeperMinion.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(creeperMinion);
            this.playSound(ModSoundEvents.ENTITY_CREEPER_MINION_EGG_HATCH_SOUND_EVENT.value(),
                    0.7F,
                    0.9F + this.random.nextFloat() * 0.1F);
            this.discard();
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (!player.isSecondaryUseActive() && player.hasPose(Pose.STANDING) && !player.hasPassenger(this)) {
            if (this.startRiding(this.getTopPassenger(player))) {
                if (!this.level().isClientSide) {
                    this.setOwner(player);
                    this.playMountingSound(true);
                } else {
                    player.displayClientMessage(Component.translatable("mount.onboard", Component.keybind("key.sneak")),
                            true);
                }
                return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.interact(player, interactionHand);
    }

    private Entity getTopPassenger(Entity entity) {
        List<Entity> list = entity.getPassengers();
        return !list.isEmpty() ? this.getTopPassenger(list.getFirst()) : entity;
    }

    private void playMountingSound(boolean isMounting) {
        this.playSound(SoundEvents.ITEM_PICKUP, 0.7F, (isMounting ? 0.6F : 0.3F) + this.random.nextFloat() * 0.1F);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (this.isInvulnerableToBase(damageSource)) {
            return false;
        } else if (this.isAlive()) {
            this.markHurt();
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
                this.age = (int) ((float) this.age - damageAmount * 80.0F);
                EntityUtil.sendParticlePacket(this, ParticleTypes.HEART, (int) (damageAmount / 2.0F));
                return false;
            } else {
                this.recentlyHit = this.tickCount;
                this.setDeltaMovement(0.0, 0.2, 0.0);
                this.health = (int) ((float) this.health - damageAmount);
                if (this.health <= 0) {
                    float sizeIn = this.isCharged() ? 2.0F : 0.0F;
                    MutatedExplosionHelper.explode(this, sizeIn, false, Level.ExplosionInteraction.TNT);
                    if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        if (!this.isCharged() && this.random.nextInt(3) != 0) {
                            for (int i = 5 + this.random.nextInt(6); i > 0; --i) {
                                this.spawnAtLocation(serverLevel, Items.GUNPOWDER);
                            }
                        } else {
                            this.spawnAtLocation(serverLevel, ModItems.CREEPER_SHARD_ITEM.value());
                        }
                    }

                    this.discard();
                }

                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Health", this.health);
        compound.putInt("Age", this.age);
        compound.putInt("RecentlyHit", this.recentlyHit);
        compound.putBoolean("Charged", this.isCharged());
        compound.putByte("DismountTicks", (byte) this.dismountTicks);
        EntityReference<LivingEntity> entityReference = this.getOwnerReference();
        if (entityReference != null) {
            entityReference.store(compound, "Owner");
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        compound.getInt("Health").ifPresent(this::setHealth);
        compound.getInt("Age").ifPresent(this::setAge);
        this.recentlyHit = compound.getIntOr("RecentlyHit", 0);
        this.setCharged(compound.getBooleanOr("Charged", false));
        this.dismountTicks = compound.getByteOr("DismountTicks", (byte) 0);
        this.setOwnerReference(EntityReference.readWithOldOwnerConversion(compound, "Owner", this.level()));
    }
}
