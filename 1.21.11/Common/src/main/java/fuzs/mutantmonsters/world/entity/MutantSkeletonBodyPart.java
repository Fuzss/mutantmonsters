package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class MutantSkeletonBodyPart extends Entity implements TraceableEntity {
    static final String TAG_BODY_PART = "BodyPart";
    static final String TAG_DESPAWN_TIMER = "DespawnTimer";
    private static final EntityDataAccessor<BodyPart> DATA_BODY_PART = SynchedEntityData.defineId(MutantSkeletonBodyPart.class,
            ModRegistry.BODY_PART_ENTITY_DATA_SERIALIZER.value());

    private final InterpolationHandler interpolation = new InterpolationHandler(this);
    private final boolean yawPositive;
    private final boolean pitchPositive;
    @Nullable
    private Mob owner;
    private int despawnTimer;

    public MutantSkeletonBodyPart(EntityType<? extends MutantSkeletonBodyPart> type, Level level) {
        super(type, level);
        this.setYRot(this.random.nextFloat() * 360.0F);
        this.yRotO = this.getYRot();
        this.setXRot(this.random.nextFloat() * 360.0F);
        this.xRotO = this.getXRot();
        this.yawPositive = this.random.nextBoolean();
        this.pitchPositive = this.random.nextBoolean();
    }

    public MutantSkeletonBodyPart(Level level, Mob owner, BodyPart bodyPart) {
        this(ModEntityTypes.BODY_PART_ENTITY_TYPE.value(), level);
        this.owner = owner;
        this.setBodyPart(bodyPart);
        this.setPos(owner.getX(), owner.getY() + 3.2F * (0.25F + this.random.nextFloat() * 0.5F), owner.getZ());
        this.setRemainingFireTicks(owner.getRemainingFireTicks());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BODY_PART, BodyPart.PELVIS);
    }

    public BodyPart getBodyPart() {
        return this.entityData.get(DATA_BODY_PART);
    }

    private void setBodyPart(BodyPart bodyPart) {
        this.entityData.set(DATA_BODY_PART, bodyPart);
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
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return this.interpolation;
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof MutantSkeletonBodyPart mutantSkeletonBodyPart) {
            this.owner = mutantSkeletonBodyPart.owner;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, 0.045, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.96));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.7));
        }

        if (!this.onGround() && this.stuckSpeedMultiplier == Vec3.ZERO) {
            this.setYRot(this.getYRot() + 10.0F * (float) (this.yawPositive ? 1 : -1));
            this.setXRot(this.getXRot() + 15.0F * (float) (this.pitchPositive ? 1 : -1));

            if (this.level() instanceof ServerLevel serverLevel) {
                for (Entity entity : serverLevel.getEntities(this, this.getBoundingBox(), this::canHarm)) {
                    DamageSource damageSource = serverLevel.damageSources()
                            .thrown(this, this.owner != null ? this.owner : this);
                    float damageAmount = 4.0F + (float) this.random.nextInt(4);
                    if (entity.hurtServer(serverLevel, damageSource, damageAmount)) {
                        entity.igniteForSeconds(this.getRemainingFireTicks() / 20.0F);
                    }
                }
            }

            if (this.despawnTimer > 0) {
                --this.despawnTimer;
            }
        } else {
            ++this.despawnTimer;
        }

        if (!this.level().isClientSide() && this.despawnTimer >= this.getMaxAge()) {
            this.discard();
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level() instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
            ResourceKey<LootTable> resourceKey = this.getItemPartLootTableId();
            LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(resourceKey);
            LootParams lootParams = new LootParams.Builder(serverLevel).withParameter(LootContextParams.THIS_ENTITY,
                    this).create(ModRegistry.BODY_PART_LOOT_CONTEXT_PARAM_SET);
            List<ItemStack> list = lootTable.getRandomItems(lootParams);
            for (ItemStack item : list) {
                if (!item.isEmpty()) {
                    this.spawnAtLocation(serverLevel, item).setNoPickUpDelay();
                }
            }
        }

        this.discard();
        return InteractionResultHelper.sidedSuccess(this.level().isClientSide());
    }

    private boolean canHarm(Entity entity) {
        return entity.isPickable() && entity.getType() != ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value();
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getLegacyItemByPart().getDescriptionId());
    }

    private Item getLegacyItemByPart() {
        return this.getBodyPart().group.item.get().value();
    }

    private ResourceKey<LootTable> getItemPartLootTableId() {
        return this.getBodyPart().group.lootTable.get();
    }

    public int getMaxAge() {
        return 6000;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        valueOutput.store(TAG_BODY_PART, BodyPart.CODEC, this.getBodyPart());
        valueOutput.putInt(TAG_DESPAWN_TIMER, this.despawnTimer);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        valueInput.read(TAG_BODY_PART, BodyPart.CODEC).ifPresent(this::setBodyPart);
        this.despawnTimer = valueInput.getIntOr(TAG_DESPAWN_TIMER, 0);
    }

    @Override
    public @Nullable Entity getOwner() {
        return this.owner;
    }

    public enum BodyPart implements StringRepresentable {
        PELVIS(BodyPartGroup.PELVIS),
        LEFT_UPPER_RIB(BodyPartGroup.RIB),
        RIGHT_UPPER_RIB(BodyPartGroup.RIB),
        LEFT_MIDDLE_RIB(BodyPartGroup.RIB),
        RIGHT_MIDDLE_RIB(BodyPartGroup.RIB),
        LEFT_LOWER_RIB(BodyPartGroup.RIB),
        RIGHT_LOWER_RIB(BodyPartGroup.RIB),
        HEAD(BodyPartGroup.SKULL),
        LEFT_ARM(BodyPartGroup.LIMB),
        RIGHT_ARM(BodyPartGroup.LIMB),
        LEFT_FORE_ARM(BodyPartGroup.LIMB),
        RIGHT_FORE_ARM(BodyPartGroup.LIMB),
        LEFT_LEG(BodyPartGroup.LIMB),
        RIGHT_LEG(BodyPartGroup.LIMB),
        LEFT_FORE_LEG(BodyPartGroup.LIMB),
        RIGHT_FORE_LEG(BodyPartGroup.LIMB),
        LEFT_SHOULDER(BodyPartGroup.SHOULDER),
        RIGHT_SHOULDER(BodyPartGroup.SHOULDER);

        private static final BodyPart[] VALUES = values();
        public static final StringRepresentable.StringRepresentableCodec<BodyPart> CODEC = StringRepresentable.fromEnum(
                () -> VALUES);
        public static final IntFunction<BodyPart> BY_ID = ByIdMap.continuous(BodyPart::ordinal,
                VALUES,
                ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, BodyPart> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID,
                BodyPart::ordinal);

        public final BodyPartGroup group;

        BodyPart(BodyPartGroup group) {
            this.group = group;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum BodyPartGroup {
        PELVIS(() -> ModItems.MUTANT_SKELETON_PELVIS_ITEM, () -> ModRegistry.MUTANT_SKELETON_PELVIS_LOOT_TABLE),
        RIB(() -> ModItems.MUTANT_SKELETON_RIB_ITEM, () -> ModRegistry.MUTANT_SKELETON_RIB_LOOT_TABLE),
        SKULL(() -> ModItems.MUTANT_SKELETON_SKULL_ITEM, () -> ModRegistry.MUTANT_SKELETON_SKULL_LOOT_TABLE),
        LIMB(() -> ModItems.MUTANT_SKELETON_LIMB_ITEM, () -> ModRegistry.MUTANT_SKELETON_LIMB_LOOT_TABLE),
        SHOULDER(() -> ModItems.MUTANT_SKELETON_SHOULDER_PAD_ITEM,
                () -> ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE);

        public final Supplier<Holder.Reference<Item>> item;
        public final Supplier<ResourceKey<LootTable>> lootTable;

        BodyPartGroup(Supplier<Holder.Reference<Item>> item, Supplier<ResourceKey<LootTable>> lootTable) {
            this.item = item;
            this.lootTable = lootTable;
        }
    }
}
