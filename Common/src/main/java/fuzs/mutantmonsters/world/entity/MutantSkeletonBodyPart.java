package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

public class MutantSkeletonBodyPart extends Entity {
    private static final EntityDataAccessor<Byte> PART = SynchedEntityData.defineId(MutantSkeletonBodyPart.class, EntityDataSerializers.BYTE);

    private final boolean yawPositive;
    private final boolean pitchPositive;
    private WeakReference<Mob> owner;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private int despawnTimer;

    public MutantSkeletonBodyPart(EntityType<? extends MutantSkeletonBodyPart> type, Level world) {
        super(type, world);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.yRotO = this.getYRot();
        this.setXRot(this.random.nextFloat() * 360.0F);
        this.xRotO = this.getXRot();
        this.yawPositive = this.random.nextBoolean();
        this.pitchPositive = this.random.nextBoolean();
    }

    public MutantSkeletonBodyPart(Level world, Mob owner, int part) {
        this(ModRegistry.BODY_PART_ENTITY_TYPE.get(), world);
        this.owner = new WeakReference<>(owner);
        this.setPart(part);
        this.setPos(owner.getX(), owner.getY() + (double) (3.2F * (0.25F + this.random.nextFloat() * 0.5F)), owner.getZ());
        this.setRemainingFireTicks(owner.getRemainingFireTicks());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PART, (byte) 0);
    }

    public int getPart() {
        return this.entityData.get(PART);
    }

    private void setPart(int id) {
        this.entityData.set(PART, (byte) id);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getLegacyItemByPart());
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPos(x, y, z);
        this.setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        this.setDeltaMovement(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
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
            this.setYRot(this.getYRot() + 10.0F * (float) (this.yawPositive ? 1 : -1));
            this.setXRot(this.getXRot() + 15.0F * (float) (this.pitchPositive ? 1 : -1));

            for (Entity entity : this.level.getEntities(this, this.getBoundingBox(), this::canHarm)) {
                if (entity.hurt(this.level.damageSources().thrown(this, this.owner != null ? (Entity) this.owner.get() : this), 4.0F + (float) this.random.nextInt(4))) {
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
            this.discard();
        }

    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ResourceLocation location = this.getItemPartLootTableId();
            if (location != null) {
                LootTable lootTable = this.level.getServer().getLootTables().get(location);
                List<ItemStack> list = lootTable.getRandomItems((new LootContext.Builder((ServerLevel) this.level)).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.level.random).create(ModRegistry.BODY_PART_LOOT_CONTEXT_PARAM_SET));
                for (ItemStack item : list) {
                    if (!item.isEmpty()) {
                        this.spawnAtLocation(item).setNoPickUpDelay();
                    }
                }
            }
        }
        this.discard();
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    private boolean canHarm(Entity entity) {
        return entity.isPickable() && entity.getType() != ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get();
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getLegacyItemByPart().getDescriptionId());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    private Item getLegacyItemByPart() {
        int part = this.getPart();
        if (part == 0) {
            return ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.get();
        } else if (part >= 1 && part < 19) {
            return ModRegistry.MUTANT_SKELETON_RIB_ITEM.get();
        } else if (part == 19) {
            return ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get();
        } else if (part >= 21 && part < 29) {
            return ModRegistry.MUTANT_SKELETON_LIMB_ITEM.get();
        } else {
            return part != 29 && part != 30 ? Items.AIR : ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.get();
        }
    }

    @Nullable
    public ResourceLocation getItemPartLootTableId() {
        int part = this.getPart();
        if (part == 0) {
            return ModRegistry.MUTANT_SKELETON_PELVIS_LOOT_TABLE;
        } else if (part >= 1 && part < 19) {
            return ModRegistry.MUTANT_SKELETON_RIB_LOOT_TABLE;
        } else if (part == 19) {
            return ModRegistry.MUTANT_SKELETON_SKULL_LOOT_TABLE;
        } else if (part >= 21 && part < 29) {
            return ModRegistry.MUTANT_SKELETON_LIMB_LOOT_TABLE;
        } else {
            return part != 29 && part != 30 ? null : ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE;
        }
    }

    public int getMaxAge() {
        return 6000;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putByte("Part", (byte) this.getPart());
        compound.putShort("DespawnTimer", (short) this.despawnTimer);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setPart(compound.getByte("Part"));
        this.despawnTimer = compound.getShort("DespawnTimer");
    }
}
