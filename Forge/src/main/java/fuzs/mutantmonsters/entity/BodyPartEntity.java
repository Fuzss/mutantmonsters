package fuzs.mutantmonsters.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.lang.ref.WeakReference;

public class BodyPartEntity extends Entity {
    private static final EntityDataAccessor<Byte> PART;
    private boolean yawPositive;
    private boolean pitchPositive;
    private WeakReference<Mob> owner;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private int despawnTimer;

    public BodyPartEntity(EntityType<? extends BodyPartEntity> type, Level world) {
        super(type, world);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.yRotO = this.getYRot();
        this.setXRot(this.random.nextFloat() * 360.0F);
        this.xRotO = this.getXRot();
        this.yawPositive = this.random.nextBoolean();
        this.pitchPositive = this.random.nextBoolean();
    }

    public BodyPartEntity(Level world, Mob owner, int part) {
        this(MBEntityType.BODY_PART, world);
        this.owner = new WeakReference(owner);
        this.setPart(part);
        this.setPos(owner.getX(), owner.getY() + (double)(3.2F * (0.25F + this.random.nextFloat() * 0.5F)), owner.getZ());
        this.setRemainingFireTicks(owner.getRemainingFireTicks());
    }

    public BodyPartEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MBEntityType.BODY_PART, world);
    }

    protected void defineSynchedData() {
        this.entityData.define(PART, (byte)0);
    }

    public int getPart() {
        return (Byte)this.entityData.get(PART);
    }

    private void setPart(int id) {
        this.entityData.set(PART, (byte)id);
    }

    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(this.getItemByPart());
    }

    protected boolean isMovementNoisy() {
        return false;
    }

    public boolean isPickable() {
        return this.isAlive();
    }

    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPos(x, y, z);
        this.setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    public void lerpMotion(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        this.setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, 0.045, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.96));
        if (this.onGround) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.7));
        }

        if (!this.onGround && this.stuckSpeedMultiplier == Vec3.ZERO) {
            this.setYRot(this.getYRot() + 10.0F * (float)(this.yawPositive ? 1 : -1));
            this.setXRot(this.getXRot() + 15.0F * (float)(this.pitchPositive ? 1 : -1));

            for (Entity entity : this.level.getEntities(this, this.getBoundingBox(), this::canHarm)) {
                if (entity.hurt(DamageSource.thrown(this, (Entity) (this.owner != null ? (Entity) this.owner.get() : this)), 4.0F + (float) this.random.nextInt(4))) {
                    entity.setSecondsOnFire(this.getRemainingFireTicks() / 20);
                }
            }

            if (this.despawnTimer > 0) {
                --this.despawnTimer;
            }
        } else {
            ++this.despawnTimer;
        }

        if (!this.level.isClientSide && this.despawnTimer >= this.getMaxAge()) {
            this.remove();
        }

    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(this.getItemByPart()).setNoPickUpDelay();
        }

        this.discard();
        return InteractionResult.SUCCESS;
    }

    private boolean canHarm(Entity entity) {
        return entity.isPickable() && entity.getType() != MBEntityType.MUTANT_SKELETON;
    }

    protected Component getTypeName() {
        return Component.translatable(this.getItemByPart().getDescriptionId());
    }

    public Item getItemByPart() {
        int part = this.getPart();
        if (part == 0) {
            return MBItems.MUTANT_SKELETON_PELVIS;
        } else if (part >= 1 && part < 19) {
            return MBItems.MUTANT_SKELETON_RIB;
        } else if (part == 19) {
            return MBItems.MUTANT_SKELETON_SKULL;
        } else if (part >= 21 && part < 29) {
            return MBItems.MUTANT_SKELETON_LIMB;
        } else {
            return part != 29 && part != 30 ? Items.AIR : MBItems.MUTANT_SKELETON_SHOULDER_PAD;
        }
    }

    public int getMaxAge() {
        return 6000;
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putByte("Part", (byte)this.getPart());
        compound.putShort("DespawnTimer", (short)this.despawnTimer);
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setPart(compound.getByte("Part"));
        this.despawnTimer = compound.getShort("DespawnTimer");
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static {
        PART = SynchedEntityData.defineId(BodyPartEntity.class, EntityDataSerializers.BYTE);
    }
}
