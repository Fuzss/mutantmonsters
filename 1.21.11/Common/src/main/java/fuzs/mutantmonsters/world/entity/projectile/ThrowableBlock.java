package fuzs.mutantmonsters.world.entity.projectile;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import fuzs.mutantmonsters.world.entity.mutant.MutantSnowGolem;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.OptionalInt;

public class ThrowableBlock extends ThrowableProjectile {
    private static final EntityDataAccessor<OptionalInt> DATA_OWNER_ENTITY_ID = SynchedEntityData.defineId(
            ThrowableBlock.class,
            EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(ThrowableBlock.class,
            EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> DATA_IS_HELD = SynchedEntityData.defineId(ThrowableBlock.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_LARGE = SynchedEntityData.defineId(ThrowableBlock.class,
            EntityDataSerializers.BOOLEAN);

    public ThrowableBlock(EntityType<? extends ThrowableBlock> type, Level level) {
        super(type, level);
    }

    public ThrowableBlock(double x, double y, double z, LivingEntity livingEntity) {
        super(ModEntityTypes.THROWABLE_BLOCK_ENTITY_TYPE.value(), x, y, z, livingEntity.level());
        this.setOwner(livingEntity);
    }

    public ThrowableBlock(MutantEnderman mutantEnderman, int armIndex) {
        this(mutantEnderman.getX(), mutantEnderman.getY() + 4.7, mutantEnderman.getZ(), mutantEnderman);
        this.setBlockState(mutantEnderman.getHeldBlock(armIndex).orElseGet(Blocks.AIR::defaultBlockState));
        boolean outer = armIndex <= 1;
        boolean right = (armIndex & 1) == 0;
        LivingEntity attackTarget = mutantEnderman.getTarget();
        Vec3 forward = EntityUtil.getDirVector(this.getYRot(), outer ? 2.7F : 1.4F);
        Vec3 strafe = EntityUtil.getDirVector(this.getYRot() + (right ? 90.0F : -90.0F), outer ? 2.2F : 2.0F);
        this.setPos(this.getX() + forward.x + strafe.x,
                this.getY() + (double) (outer ? 2.8F : 1.1F) - 4.8,
                this.getZ() + forward.z + strafe.z);
        if (attackTarget != null) {
            double d0 = attackTarget.getX() - this.getX();
            double d1 = attackTarget.getY(0.33) - this.getY();
            double d2 = attackTarget.getZ() - this.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            this.shoot(d0, d1 + d3 * 0.1, d2, 1.4F, 1.0F);
        } else {
            this.throwBlock(mutantEnderman);
        }
    }

    public ThrowableBlock(MutantSnowGolem mutantSnowGolem) {
        this(mutantSnowGolem.getX(),
                mutantSnowGolem.getY() + 1.955 - 0.1 + 1.0,
                mutantSnowGolem.getZ(),
                mutantSnowGolem);
        this.setYRot(mutantSnowGolem.getYRot());
        this.setBlockState(Blocks.ICE.defaultBlockState());
    }

    public ThrowableBlock(Player player, BlockState blockState, BlockPos blockPos) {
        this(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, player);
        this.setBlockState(blockState);
        this.setHeld(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNER_ENTITY_ID, OptionalInt.empty());
        builder.define(DATA_BLOCK_STATE_ID, Blocks.GRASS_BLOCK.defaultBlockState());
        builder.define(DATA_IS_HELD, false);
        builder.define(DATA_IS_LARGE, false);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    private void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    public boolean isHeld() {
        return this.entityData.get(DATA_IS_HELD);
    }

    private void setHeld(boolean held) {
        this.entityData.set(DATA_IS_HELD, held);
    }

    public boolean isLarge() {
        return this.entityData.get(DATA_IS_LARGE);
    }

    @Override
    public void setOwner(Entity entity) {
        super.setOwner(entity);
        if (entity != null) {
            this.entityData.set(DATA_OWNER_ENTITY_ID, OptionalInt.of(entity.getId()));
            this.entityData.set(DATA_IS_LARGE, this.isThrownBySnowGolem());
        }
    }

    public boolean isThrownBySnowGolem() {
        return this.getOwner() != null
                && this.getOwner().getType() == ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value();
    }

    @Override
    protected double getDefaultGravity() {
        EntityType<?> entityType = this.getOwner() != null ? this.getOwner().getType() : null;
        if (entityType == EntityType.PLAYER) {
            return 0.04;
        } else if (entityType == ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value()) {
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
        return this.isAlive() && !this.isThrownBySnowGolem();
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
                double x = this.getX() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth();
                double y = this.getY() + 0.5 + (this.random.nextFloat() * this.getBbHeight());
                double z = this.getZ() + (this.random.nextFloat() * this.getBbWidth() * 2.0F) - this.getBbWidth();
                double motx = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                double moty = 0.5F + this.random.nextFloat() * 2.0F;
                double motz = (this.random.nextFloat() - this.random.nextFloat()) * 3.0F;
                this.level()
                        .addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockState()),
                                x,
                                y,
                                z,
                                motx,
                                moty,
                                motz);
            }
        }
    }

    @Override
    public void tick() {
        if (this.isHeld()) {
            if (!this.level().isClientSide()) {
                this.setSharedFlag(6, this.hasGlowingTag());
            }

            this.baseTick();
            Entity thrower = this.getOwner();
            if (thrower == null) {
                OptionalInt optionalInt = this.entityData.get(DATA_OWNER_ENTITY_ID);
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
                double offset = 0.6;
                this.setDeltaMovement(x * offset, y * offset, z * offset);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (!this.level().isClientSide() && (!thrower.isAlive() || thrower.isSpectator()
                        || !((LivingEntity) thrower).isHolding(ModItems.ENDERSOUL_HAND_ITEM.value()))) {
                    this.setHeld(false);
                }
            }
        } else {
            super.tick();
            // for some reason Fabric doesn't sync velocity updates to the client anymore since 1.19.4, so set this to force updates every tick while the block is flying
            // changing FabricEntityTypeBuilder::forceTrackedVelocityUpdates doesn't have any effect, it's on by default anyway which is the desired behavior which was working fine prior to 1.19.4
            // Forge is without the issue however and doesn't need this, Fabric behavior can be reproduced though by setting EntityType$Builder::setShouldReceiveVelocityUpdates to false
            this.needsSync = true;
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!super.canHitEntity(entity)) {
            return false;
        } else {
            return !this.isThrownBySnowGolem() || MutantSnowGolem.canHarm(this.getOwner(), entity);
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
                if (!this.level().isClientSide()) {
                    this.setHeld(false);
                    this.throwBlock(player);
                }

                ItemHelper.hurtAndBreak(itemStack, 1, player, interactionHand);
                return InteractionResultHelper.sidedSuccess(this.level().isClientSide());
            } else {
                return InteractionResult.PASS;
            }
        } else {
            if (!this.level().isClientSide()) {
                this.setHeld(true);
                this.setOwner(player);
            }

            return InteractionResultHelper.sidedSuccess(this.level().isClientSide());
        }
    }

    private void throwBlock(LivingEntity thrower) {
        this.setYRot(thrower.getYRot());
        this.setXRot(thrower.getXRot());
        float f = 0.4F;
        this.shoot(-Mth.sin(this.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(this.getXRot() * Mth.DEG_TO_RAD) * f,
                -Mth.sin(this.getXRot() * Mth.DEG_TO_RAD) * f,
                Mth.cos(this.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(this.getXRot() * Mth.DEG_TO_RAD) * f,
                1.4F,
                1.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        Entity thrower = this.getOwner();
        LivingEntity livingEntity = thrower instanceof LivingEntity ? (LivingEntity) thrower : null;
        if (this.isThrownBySnowGolem()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (Mob mob : this.level()
                        .getEntitiesOfClass(Mob.class,
                                this.getBoundingBox().inflate(2.5, 2.0, 2.5),
                                this::canHitEntity)) {
                    if (this.distanceToSqr(mob) <= 6.25) {
                        DamageSource damageSource = this.level().damageSources().mobProjectile(this, livingEntity);
                        mob.hurtServer(serverLevel, damageSource, 4.0F + this.random.nextInt(3));
                    }
                }

                if (hitResult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult) hitResult).getEntity();
                    DamageSource damageSource = this.level().damageSources().thrown(this, livingEntity);
                    entity.hurtServer(serverLevel, damageSource, 4.0F);
                }

                if (!this.level().isClientSide()) {
                    this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
                    this.discard();
                }
            }

            this.playSound(this.getBlockState().getSoundType().getBreakSound(),
                    0.8F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.8F);
        } else {
            boolean throwerAllowedToPlace = isThrowerAllowedToPlace(livingEntity);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                this.onHitBlock(blockHitResult);
                if (this.level() instanceof ServerLevel serverLevel) {
                    BlockPos blockPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
                    if (throwerAllowedToPlace && this.level().getBlockState(blockPos).canBeReplaced()
                            && this.getBlockState().canSurvive(this.level(), blockPos)) {
                        this.level().setBlockAndUpdate(blockPos, this.getBlockState());
                        this.getBlockState()
                                .getBlock()
                                .setPlacedBy(this.level(),
                                        blockPos,
                                        this.getBlockState(),
                                        livingEntity,
                                        ItemStack.EMPTY);
                        SoundType soundType = this.getBlockState().getSoundType();
                        this.playSound(soundType.getPlaceSound(),
                                (soundType.getVolume() + 1.0F) / 2.0F,
                                soundType.getPitch() * 0.8F);
                    } else {
                        this.level()
                                .levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK,
                                        blockPos,
                                        Block.getId(this.getBlockState()));
                        if (serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                            Block.dropResources(this.getBlockState(), serverLevel, this.blockPosition());
                        }
                    }
                }
            } else if (this.level() instanceof ServerLevel serverLevel
                    && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                DamageSource damageSource = serverLevel.damageSources().thrown(this, livingEntity);
                entity.hurtServer(serverLevel, damageSource, 4.0F);

                this.level()
                        .levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK,
                                this.blockPosition(),
                                Block.getId(this.getBlockState()));
                if (serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
                    Block.dropResources(this.getBlockState(), serverLevel, this.blockPosition());
                }
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                for (Entity entity : this.level()
                        .getEntities(this, this.getBoundingBox().inflate(2.0), this::canHitEntity)) {
                    if (!entity.is(livingEntity) && this.distanceToSqr(entity) <= 4.0) {
                        entity.hurtServer(serverLevel,
                                this.level().damageSources().mobProjectile(this, livingEntity),
                                (float) (6 + this.random.nextInt(3)));
                    }
                }

                this.discard();
            }
        }
    }

    public static boolean isThrowerAllowedToPlace(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            return player.mayBuild();
        } else if (livingEntity instanceof Mob mob) {
            if (!(mob.level() instanceof ServerLevel serverLevel)) {
                return true;
            } else {
                return EntityHelper.isMobGriefingAllowed(serverLevel, livingEntity);
            }
        } else {
            return false;
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("Held", this.isHeld());
        valueOutput.store("BlockState", BlockState.CODEC, this.getBlockState());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setHeld(valueInput.getBooleanOr("Held", false));
        valueInput.read("BlockState", BlockState.CODEC).ifPresent(this::setBlockState);
    }
}
