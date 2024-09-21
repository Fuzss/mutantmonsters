package fuzs.mutantmonsters.world.entity.projectile;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.AdditionalSpawnDataEntity;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import fuzs.mutantmonsters.world.entity.mutant.MutantSnowGolem;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class ThrowableBlock extends ThrowableProjectile implements AdditionalSpawnDataEntity {
    private static final EntityDataAccessor<OptionalInt> OWNER_ENTITY_ID = SynchedEntityData.defineId(ThrowableBlock.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Boolean> HELD = SynchedEntityData.defineId(ThrowableBlock.class, EntityDataSerializers.BOOLEAN);
    private BlockState blockState;
    @Nullable
    private EntityType<?> ownerType;

    public ThrowableBlock(EntityType<? extends ThrowableBlock> type, Level worldIn) {
        super(type, worldIn);
        this.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
    }

    public ThrowableBlock(double x, double y, double z, LivingEntity entity) {
        super(ModEntityTypes.THROWABLE_BLOCK_ENTITY_TYPE.value(), x, y, z, entity.level());
        this.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
        this.setOwner(entity);
        this.ownerType = entity.getType();
    }

    public ThrowableBlock(MutantEnderman enderman, int id) {
        this(enderman.getX(), enderman.getY() + 4.7, enderman.getZ(), enderman);
        this.blockState = Block.stateById(enderman.getHeldBlock(id));
        boolean outer = id <= 1;
        boolean right = (id & 1) == 0;
        LivingEntity attackTarget = enderman.getTarget();
        Vec3 forward = EntityUtil.getDirVector(this.getYRot(), outer ? 2.7F : 1.4F);
        Vec3 strafe = EntityUtil.getDirVector(this.getYRot() + (right ? 90.0F : -90.0F), outer ? 2.2F : 2.0F);
        this.setPos(this.getX() + forward.x + strafe.x, this.getY() + (double) (outer ? 2.8F : 1.1F) - 4.8, this.getZ() + forward.z + strafe.z);
        if (attackTarget != null) {
            double d0 = attackTarget.getX() - this.getX();
            double d1 = attackTarget.getY(0.33) - this.getY();
            double d2 = attackTarget.getZ() - this.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            this.shoot(d0, d1 + d3 * 0.1, d2, 1.4F, 1.0F);
        } else {
            this.throwBlock(enderman);
        }

    }

    public ThrowableBlock(MutantSnowGolem mutantSnowGolem) {
        this(mutantSnowGolem.getX(), mutantSnowGolem.getY() + 1.955 - 0.1 + 1.0, mutantSnowGolem.getZ(), mutantSnowGolem);
        this.setYRot(mutantSnowGolem.getYRot());
        this.blockState = Blocks.ICE.defaultBlockState();
    }

    public ThrowableBlock(Player player, BlockState blockState, BlockPos pos) {
        this((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, player);
        this.blockState = blockState;
        this.setHeld(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ENTITY_ID, OptionalInt.empty());
        builder.define(HELD, false);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Nullable
    public EntityType<?> getOwnerType() {
        return this.ownerType;
    }

    public boolean isHeld() {
        return this.entityData.get(HELD);
    }

    private void setHeld(boolean held) {
        this.entityData.set(HELD, held);
    }

    @Override
    public void setOwner(Entity entityIn) {
        super.setOwner(entityIn);
        if (entityIn != null) {
            this.entityData.set(OWNER_ENTITY_ID, OptionalInt.of(entityIn.getId()));
        }

    }

    @Override
    protected double getDefaultGravity() {
        if (this.ownerType == EntityType.PLAYER) {
            return 0.04;
        } else if (this.ownerType == ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value()) {
            return 0.06;
        } else {
            return 0.01;
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.EVENTS;
    }

    @Override
    public boolean isPickable() {
        return this.isAlive() && this.ownerType != ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value();
    }

    @Override
    public boolean isPushable() {
        return this.isHeld() && this.isAlive();
    }

    @Override
    public void push(Entity entityIn) {
        if (entityIn != this.getOwner()) {
            super.push(entityIn);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 60; ++i) {
                double x = this.getX() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                double y = this.getY() + 0.5 + (double) (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double) this.getBbWidth();
                double motx = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                double moty = 0.5F + this.random.nextFloat() * 2.0F;
                double motz = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.blockState), x, y, z, motx, moty, motz);
            }
        }
    }

    @Override
    public void tick() {
        if (this.isHeld()) {
            if (!this.level().isClientSide) {
                this.setSharedFlag(6, this.hasGlowingTag());
            }

            this.baseTick();
            Entity thrower = this.getOwner();
            if (thrower == null) {
                OptionalInt optionalInt = this.entityData.get(OWNER_ENTITY_ID);
                if (optionalInt.isPresent()) {
                    Entity entity = this.level().getEntity(optionalInt.getAsInt());
                    if (entity instanceof LivingEntity) {
                        thrower = entity;
                    }
                }

                if (thrower != null) {
                    this.setOwner(thrower);
                }
            } else if (thrower instanceof LivingEntity) {
                Vec3 vec = thrower.getLookAngle();
                double x = thrower.getX() + vec.x * 1.6 - this.getX();
                double y = thrower.getEyeY() + vec.y * 1.6 - this.getY();
                double z = thrower.getZ() + vec.z * 1.6 - this.getZ();
                float offset = 0.6F;
                this.setDeltaMovement(x * (double) offset, y * (double) offset, z * (double) offset);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (!this.level().isClientSide && (!thrower.isAlive() || thrower.isSpectator() || !((LivingEntity) thrower).isHolding(
                        ModItems.ENDERSOUL_HAND_ITEM.value()))) {
                    this.setHeld(false);
                }
            }
        } else {
            super.tick();
            // for some reason Fabric doesn't sync velocity updates to the client anymore since 1.19.4, so set this to force updates every tick while the block is flying
            // changing FabricEntityTypeBuilder::forceTrackedVelocityUpdates doesn't have any effect, it's on by default anyway which is the desired behavior which was working fine prior to 1.19.4
            // Forge is without the issue however and doesn't need this, Fabric behavior can be reproduced though by setting EntityType$Builder::setShouldReceiveVelocityUpdates to false
            this.hasImpulse = true;
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) {
            return false;
        } else {
            return this.ownerType != ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value() || MutantSnowGolem.canHarm(this.getOwner(), entity);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (itemStack.getItem() != ModItems.ENDERSOUL_HAND_ITEM.value()) {
            return InteractionResult.PASS;
        } else if (this.isHeld()) {
            if (this.getOwner() == player) {
                if (!this.level().isClientSide) {
                    this.setHeld(false);
                    this.throwBlock(player);
                }

                ItemHelper.hurtAndBreak(itemStack, 1, player, interactionHand);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        } else {
            if (!this.level().isClientSide) {
                this.setHeld(true);
                this.setOwner(player);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
    }

    private void throwBlock(LivingEntity thrower) {
        this.setYRot(thrower.getYRot());
        this.setXRot(thrower.getXRot());
        float f = 0.4F;
        this.shoot(-Mth.sin(this.getYRot() / 180.0F * 3.1415927F) * Mth.cos(this.getXRot() / 180.0F * 3.1415927F) * f, -Mth.sin(this.getXRot() / 180.0F * 3.1415927F) * f, Mth.cos(this.getYRot() / 180.0F * 3.1415927F) * Mth.cos(this.getXRot() / 180.0F * 3.1415927F) * f, 1.4F, 1.0F);
    }

    @Override
    protected void onHit(HitResult result) {
        Entity thrower = this.getOwner();
        LivingEntity livingEntity = thrower instanceof LivingEntity ? (LivingEntity) thrower : null;
        if (this.ownerType == ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value()) {

            for (Mob mobEntity : this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(2.5, 2.0, 2.5), this::canHitEntity)) {
                if (this.distanceToSqr(mobEntity) <= 6.25) {
                    mobEntity.hurt(this.level().damageSources().mobProjectile(this, livingEntity), 4.0F + (float) this.random.nextInt(3));
                }
            }

            if (result.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) result).getEntity();
                if (entity.hurt(this.level().damageSources().thrown(this, livingEntity), 4.0F) && entity.getType() == EntityType.ENDERMAN) {
                    return;
                }
            }

            if (!this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }

            this.playSound(this.blockState.getSoundType().getBreakSound(), 0.8F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.8F);
        } else {
            boolean canOwnerPlace = livingEntity instanceof Player && ((Player) livingEntity).mayBuild() || livingEntity instanceof Mob && CommonAbstractions.INSTANCE.getMobGriefingRule(this.level(), livingEntity);
            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) result;
                this.onHitBlock(blockHitResult);
                if (!this.level().isClientSide) {
                    BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                    if (canOwnerPlace && this.level().getBlockState(blockPos).canBeReplaced() && this.blockState.canSurvive(this.level(), blockPos)) {
                        this.level().setBlockAndUpdate(blockPos, this.blockState);
                        this.blockState.getBlock().setPlacedBy(this.level(), blockPos, this.blockState, livingEntity, ItemStack.EMPTY);
                        SoundType soundType = this.blockState.getSoundType();
                        this.playSound(soundType.getPlaceSound(), (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                    } else {
                        this.level().levelEvent(2001, blockPos, Block.getId(this.blockState));
                        if (canOwnerPlace && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.spawnAtLocation(this.blockState.getBlock());
                        }
                    }
                }
            } else if (result.getType() == HitResult.Type.ENTITY && !this.level().isClientSide) {
                Entity entity = ((EntityHitResult) result).getEntity();
                if (entity.hurt(this.level().damageSources().thrown(this, livingEntity), 4.0F) && entity.getType() == EntityType.ENDERMAN) {
                    return;
                }

                this.level().levelEvent(2001, this.blockPosition(), Block.getId(this.blockState));
                if (canOwnerPlace && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(this.blockState.getBlock());
                }
            }

            for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(2.0), this::canHitEntity)) {
                if (!entity.is(livingEntity) && this.distanceToSqr(entity) <= 4.0) {
                    entity.hurt(this.level().damageSources().mobProjectile(this, livingEntity), (float) (6 + this.random.nextInt(3)));
                }
            }

            if (!this.level().isClientSide) {
                this.discard();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("BlockState", NbtUtils.writeBlockState(this.blockState));
        compound.putBoolean("Held", this.isHeld());
        if (this.ownerType != null) {
            compound.putString("OwnerType", BuiltInRegistries.ENTITY_TYPE.getKey(this.ownerType).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setHeld(compound.getBoolean("Held"));
        if (compound.contains("BlockState", 10)) {
            this.blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound("BlockState"));
        }

        if (compound.contains("OwnerType")) {
            this.ownerType = EntityType.byString(compound.getString("OwnerType")).orElse(null);
        }
    }

    @Override
    public void writeAdditionalAddEntityData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(Block.getId(this.blockState));
        buffer.writeUtf(this.ownerType == null ? "" : BuiltInRegistries.ENTITY_TYPE.getKey(this.ownerType).toString());
    }

    @Override
    public void readAdditionalAddEntityData(FriendlyByteBuf additionalData) {
        this.blockState = Block.stateById(additionalData.readVarInt());
        this.ownerType = EntityType.byString(additionalData.readUtf(32767)).orElse(null);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return AdditionalSpawnDataEntity.getPacket(this, serverEntity);
    }
}
