package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.proxy.Proxy;
import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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

import java.util.List;
import java.util.UUID;

public class CreeperMinionEgg extends Entity {
    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(CreeperMinionEgg.class,
            EntityDataSerializers.BOOLEAN
    );

    private int health;
    private int age;
    private int recentlyHit;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private UUID owner;
    private int dismountTicks;

    public CreeperMinionEgg(EntityType<? extends CreeperMinionEgg> type, Level world) {
        super(type, world);
        this.health = 8;
        this.age = (60 + this.random.nextInt(40)) * 1200;
        this.blocksBuilding = true;
    }

    public CreeperMinionEgg(MutantCreeper spawner, Entity owner) {
        this(ModEntityTypes.CREEPER_MINION_EGG_ENTITY_TYPE.value(), spawner.level());
        this.owner = owner.getUUID();
        this.setPos(spawner.getX(), spawner.getY(), spawner.getZ());
        if (spawner.isCharged()) {
            this.setCharged(true);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CHARGED, false);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(CHARGED, charged);
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
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        super.lerpTo(x, y, z, yaw, pitch, steps);
        this.setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        super.lerpMotion(x, y, z);
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
    }

    private void hatch() {
        CreeperMinion minion = ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value().create(this.level(), EntitySpawnReason.BREEDING);
        if (this.owner != null) {
            Player playerEntity = this.level().getPlayerByUUID(this.owner);
            if (playerEntity != null && !CommonAbstractions.INSTANCE.onAnimalTame(minion, playerEntity)) {
                minion.tame(playerEntity);
                minion.setOrderedToSit(true);
            }
        }

        if (this.isCharged()) {
            minion.setCharged(true);
        }

        minion.setPos(this.getX(), this.getY(), this.getZ());
        this.level().addFreshEntity(minion);
        this.playSound(ModSoundEvents.ENTITY_CREEPER_MINION_EGG_HATCH_SOUND_EVENT.value(), 0.7F,
                0.9F + this.random.nextFloat() * 0.1F
        );
        this.discard();
    }

    @Override
    public void thunderHit(ServerLevel serverWorld, LightningBolt lightningBoltEntity) {
        super.thunderHit(serverWorld, lightningBoltEntity);
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
                this.playMountSound(false);
            }
        }

        if (!this.level().isClientSide) {
            if (this.health < 8 && this.tickCount - this.recentlyHit > 80 && this.tickCount % 20 == 0) {
                ++this.health;
            }

            if (--this.age <= 0 && this.owner != null) {
                Player player = this.level().getPlayerByUUID(this.owner);
                if (player != null && this.distanceToSqr(player) < 4096.0) {
                    this.hatch();
                }
            }
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!player.isSecondaryUseActive() && player.hasPose(Pose.STANDING) && !player.hasPassenger(this)) {
            Entity topPassenger = this.getTopPassenger(player);
            this.startRiding(topPassenger, true);
            this.playMountSound(true);
            if (this.level().isClientSide) {
                Proxy.INSTANCE.showDismountMessage();
            }
            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    private Entity getTopPassenger(Entity entity) {
        List<Entity> list = entity.getPassengers();
        return !list.isEmpty() ? this.getTopPassenger(list.get(0)) : entity;
    }

    private void playMountSound(boolean mount) {
        this.playSound(SoundEvents.ITEM_PICKUP, 0.7F, (mount ? 0.6F : 0.3F) + this.random.nextFloat() * 0.1F);
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
        if (this.isCharged()) {
            compound.putBoolean("Charged", true);
        }

        if (this.owner != null) {
            compound.putUUID("Owner", this.owner);
        }
        compound.putByte("DismountTicks", (byte) this.dismountTicks);

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Health")) {
            this.health = compound.getInt("Health");
        }

        if (compound.contains("Age")) {
            this.age = compound.getInt("Age");
        }

        this.recentlyHit = compound.getInt("RecentlyHit");
        this.setCharged(compound.getBoolean("Charged"));
        if (compound.hasUUID("Owner")) {
            this.owner = compound.getUUID("Owner");
        }
        this.dismountTicks = compound.getByte("DismountTicks");
    }
}
