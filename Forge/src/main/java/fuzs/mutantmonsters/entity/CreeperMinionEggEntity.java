package fuzs.mutantmonsters.entity;

import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.util.MutatedExplosion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.List;
import java.util.UUID;

public class CreeperMinionEggEntity extends Entity {
    private static final EntityDataAccessor<Boolean> CHARGED;
    private int health;
    private int age;
    private int recentlyHit;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private UUID ownerUUID;

    public CreeperMinionEggEntity(EntityType<? extends CreeperMinionEggEntity> type, Level world) {
        super(type, world);
        this.health = 8;
        this.age = (60 + this.random.nextInt(40)) * 1200;
        this.blocksBuilding = true;
    }

    public CreeperMinionEggEntity(MutantCreeperEntity spawner, Entity owner) {
        this(MBEntityType.CREEPER_MINION_EGG, spawner.level);
        this.ownerUUID = owner.getUUID();
        this.setPos(spawner.getX(), spawner.getY(), spawner.getZ());
        if (spawner.isCharged()) {
            this.setCharged(true);
        }

    }

    public CreeperMinionEggEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MBEntityType.CREEPER_MINION_EGG, world);
    }

    protected void defineSynchedData() {
        this.entityData.define(CHARGED, false);
    }

    public boolean isCharged() {
        return (Boolean)this.entityData.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(CHARGED, charged);
    }

    public double getMyRidingOffset() {
        return this.getVehicle() instanceof Player ? (double)this.getBbHeight() - (this.getVehicle().getPose() == Pose.CROUCHING ? 0.35 : 0.2) : 0.0;
    }

    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight();
    }

    protected boolean isMovementNoisy() {
        return false;
    }

    public boolean canCollideWith(Entity entity) {
        return Boat.canVehicleCollide(this, entity);
    }

    public boolean isPickable() {
        return this.isAlive();
    }

    public boolean isPushable() {
        return this.isAlive();
    }

    public boolean canRiderInteract() {
        return true;
    }

    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        super.lerpTo(x, y, z, yaw, pitch, posRotationIncrements, teleport);
        this.setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    public void lerpMotion(double x, double y, double z) {
        super.lerpMotion(x, y, z);
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
    }

    private void hatch() {
        CreeperMinionEntity minion = (CreeperMinionEntity)MBEntityType.CREEPER_MINION.create(this.level);
        if (this.ownerUUID != null) {
            Player playerEntity = this.level.getPlayerByUUID(this.ownerUUID);
            if (playerEntity != null && !ForgeEventFactory.onAnimalTame(minion, playerEntity)) {
                minion.tame(playerEntity);
                minion.setOrderedToSit(true);
            }
        }

        if (this.isCharged()) {
            minion.setCharged(true);
        }

        minion.setPos(this.getX(), this.getY(), this.getZ());
        this.level.addFreshEntity(minion);
        this.playSound(MBSoundEvents.ENTITY_CREEPER_MINION_EGG_HATCH, 0.7F, 0.9F + this.random.nextFloat() * 0.1F);
        this.remove();
    }

    public void thunderHit(ServerLevel serverWorld, LightningBolt lightningBoltEntity) {
        super.thunderHit(serverWorld, lightningBoltEntity);
        this.setCharged(true);
    }

    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, 0.04, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, 0.0, 0.7));
        }

        if (this.isPassenger() && (this.isInWall() || this.getVehicle().getPose() != Pose.STANDING && this.getVehicle().getPose() != Pose.CROUCHING || this.getVehicle().isSpectator())) {
            this.stopRiding();
            this.playMountSound(false);
        }

        if (!this.level.isClientSide) {
            if (this.health < 8 && this.tickCount - this.recentlyHit > 80 && this.tickCount % 20 == 0) {
                ++this.health;
            }

            if (--this.age <= 0) {
                this.hatch();
            }
        }

    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.getVehicle() == player) {
            this.getTopPassenger(player).stopRiding();
            this.playMountSound(false);
            return InteractionResult.SUCCESS;
        } else if (player.getPose() != Pose.STANDING && player.getPose() != Pose.CROUCHING) {
            return InteractionResult.PASS;
        } else {
            this.startRiding(this.getTopPassenger(player), true);
            this.playMountSound(true);
            return InteractionResult.SUCCESS;
        }
    }

    private Entity getTopPassenger(Entity entity) {
        List<Entity> list = entity.getPassengers();
        return !list.isEmpty() ? this.getTopPassenger((Entity)list.get(0)) : entity;
    }

    private void playMountSound(boolean mount) {
        this.playSound(SoundEvents.ITEM_PICKUP, 0.7F, (mount ? 0.6F : 0.3F) + this.random.nextFloat() * 0.1F);
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && this.isAlive()) {
            this.markHurt();
            if (source.isExplosion()) {
                this.age = (int)((float)this.age - amount * 80.0F);
                EntityUtil.sendParticlePacket(this, ParticleTypes.HEART, (int)(amount / 2.0F));
                return false;
            } else {
                this.recentlyHit = this.tickCount;
                this.setDeltaMovement(0.0, 0.2, 0.0);
                this.health = (int)((float)this.health - amount);
                if (this.health <= 0) {
                    MutatedExplosion.create(this, this.isCharged() ? 2.0F : 0.0F, false, Explosion.BlockInteraction.BREAK);
                    if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        if (!this.isCharged() && this.random.nextInt(3) != 0) {
                            for(int i = 5 + this.random.nextInt(6); i > 0; --i) {
                                this.spawnAtLocation(Items.GUNPOWDER);
                            }
                        } else {
                            this.spawnAtLocation(MBItems.CREEPER_SHARD);
                        }
                    }

                    this.remove();
                }

                return true;
            }
        } else {
            return false;
        }
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Health", this.health);
        compound.putInt("Age", this.age);
        compound.putInt("RecentlyHit", this.recentlyHit);
        if (this.isCharged()) {
            compound.putBoolean("Charged", true);
        }

        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }

    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Health")) {
            this.health = compound.getInt("Health");
        }

        if (compound.contains("Age")) {
            this.age = compound.getInt("Age");
        }

        this.recentlyHit = compound.getInt("RecentlyHit");
        this.setCharged(compound.getBoolean("Charged"));
        if (compound.hasUUID("OwnerUUID")) {
            this.ownerUUID = compound.getUUID("OwnerUUID");
        } else if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }

    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static {
        CHARGED = SynchedEntityData.defineId(CreeperMinionEggEntity.class, EntityDataSerializers.BOOLEAN);
    }
}
