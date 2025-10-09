package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.FleeRainGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;

public class MutantSnowGolem extends AbstractGolem implements RangedAttackMob, Shearable, OwnableEntity {
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(
            MutantSnowGolem.class,
            EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Byte> DATA_STATUS = SynchedEntityData.defineId(MutantSnowGolem.class,
            EntityDataSerializers.BYTE);
    private boolean isThrowing;
    private int throwingTick;

    public MutantSnowGolem(EntityType<? extends MutantSnowGolem> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimJumpGoal(this));
        this.goalSelector.addGoal(1, new FleeRainGoal(this, 1.1));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.1, 30, 12.0F));
        this.goalSelector.addGoal(3, new ThrowIceGoal());
        this.goalSelector.addGoal(4, new AvoidDamageGoal(this, 1.1));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.1));
        this.goalSelector.addGoal(6, new MoveBackToVillageGoal(this, 1.0, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new HurtByNearestTargetGoal(this));
        this.targetSelector.addGoal(1,
                new NearestAttackableTargetGoal<>(this,
                        Mob.class,
                        10,
                        true,
                        false,
                        (LivingEntity livingEntity, ServerLevel serverLevel) -> {
                            return livingEntity instanceof Enemy && (!(livingEntity instanceof Creeper)
                                    || ((Creeper) livingEntity).getTarget() == this);
                        }));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 80.0).add(Attributes.MOVEMENT_SPEED, 0.26);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_STATUS, (byte) 1);
    }

    @Nullable
    @Override
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(livingEntity).map(EntityReference::of));
    }

    public void setOwnerReference(@Nullable EntityReference<LivingEntity> entityReference) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(entityReference));
    }

    public boolean hasJackOLantern() {
        return (this.entityData.get(DATA_STATUS) & 1) != 0;
    }

    public void setJackOLantern(boolean jackOLantern) {
        byte b0 = this.entityData.get(DATA_STATUS);
        this.entityData.set(DATA_STATUS, jackOLantern ? (byte) (b0 | 1) : (byte) (b0 & -2));
    }

    public boolean getSwimJump() {
        return (this.entityData.get(DATA_STATUS) & 4) != 0;
    }

    public void setSwimJump(boolean swimJumping) {
        byte b0 = this.entityData.get(DATA_STATUS);
        this.entityData.set(DATA_STATUS, swimJumping ? (byte) (b0 | 4) : (byte) (b0 & -5));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && target instanceof Enemy;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        if (level.getBiome(pos).is(BiomeTags.SNOW_GOLEM_MELTS)) {
            return -10.0F;
        } else {
            return level.getBlockState(pos).getBlock() == Blocks.SNOW ? 10.0F : 0.0F;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && this.getSwimJump()) {
            this.level().broadcastEntityEvent(this, (byte) 36);
            this.level().broadcastEntityEvent(this, (byte) 37);
        }

        if (this.isThrowing) {
            ++this.throwingTick;
            if (this.throwingTick >= 20) {
                this.isThrowing = false;
                this.throwingTick = 0;
            }
        }

        if (this.level().dimensionType().ultraWarm() || this.level()
                .getBiome(this.blockPosition())
                .is(BiomeTags.SNOW_GOLEM_MELTS)) {
            if (this.random.nextFloat() > Math.min(80.0F, this.getHealth()) * 0.01F) {
                this.level()
                        .addParticle(ParticleTypes.FALLING_WATER,
                                this.getRandomX(0.6),
                                this.getRandomY() - 0.15,
                                this.getRandomZ(0.6),
                                0.0,
                                0.0,
                                0.0);
            }

            if (this.tickCount % 60 == 0) {
                this.hurt(this.level().damageSources().onFire(), 1.0F);
            }
        }

        if (this.tickCount % 80 == 0 && this.isAlive() && this.getHealth() < this.getMaxHealth() && this.isSnowingAt(
                this.blockPosition())) {
            this.heal(1.0F);
        }

        if (this.level() instanceof ServerLevel serverLevel && this.onGround() && !serverLevel.dimensionType()
                .ultraWarm() && EntityHelper.isMobGriefingAllowed(serverLevel, this)) {
            int x = Mth.floor(this.getX());
            int y = Mth.floor(this.getBoundingBox().minY);
            int z = Mth.floor(this.getZ());

            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                        BlockPos pos = new BlockPos(x + i, y, z + j);
                        BlockPos posDown = pos.below();
                        BlockPos posAbove = pos.above();
                        boolean placeSnow = serverLevel.isEmptyBlock(pos) && Blocks.SNOW.defaultBlockState()
                                .canSurvive(serverLevel, pos);
                        boolean placeIce = serverLevel.isWaterAt(posDown) && serverLevel.getBlockState(posDown)
                                .getBlock() instanceof LiquidBlock;
                        if (serverLevel.getFluidState(pos).getType() == Fluids.FLOWING_WATER) {
                            serverLevel.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                        }

                        if (serverLevel.getFluidState(posAbove).getType() == Fluids.FLOWING_WATER) {
                            serverLevel.setBlockAndUpdate(posAbove, Blocks.ICE.defaultBlockState());
                        }

                        if ((!placeSnow || (Math.abs(i) != 2 && Math.abs(j) != 2 || this.random.nextInt(20) == 0) && (
                                Math.abs(i) != 1 && Math.abs(j) != 1 || this.random.nextInt(10) == 0)) && (!placeIce
                                || (Math.abs(i) != 2 && Math.abs(j) != 2 || this.random.nextInt(14) == 0) && (
                                Math.abs(i) != 1 && Math.abs(j) != 1 || this.random.nextInt(6) == 0))) {
                            if (placeSnow) {
                                serverLevel.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                            }

                            if (placeIce) {
                                serverLevel.setBlockAndUpdate(posDown, Blocks.ICE.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isSnowingAt(BlockPos blockPos) {
        if (!this.level().isRaining()) {
            return false;
        } else if (!this.level().canSeeSky(blockPos)) {
            return false;
        } else if (this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > blockPos.getY()) {
            return false;
        } else {
            return this.level().getBiome(blockPos).value().getPrecipitationAt(blockPos, this.level().getSeaLevel())
                    == Biome.Precipitation.SNOW;
        }
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && this.hasJackOLantern();
    }

    @Override
    public void shear(ServerLevel serverLevel, SoundSource soundSource, ItemStack itemStack) {
        serverLevel.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, soundSource, 1.0F, 1.0F);
        this.setJackOLantern(false);
        this.spawnAtLocation(serverLevel, new ItemStack(Items.JACK_O_LANTERN), 1.7F);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        if (!this.isLeashed()) {
            if (this.getOwner() instanceof Player owner && owner.isAlive()) {
                this.setHomeTo(owner.blockPosition(), this.getTarget() == null ? 8 : 16);
            } else if (this.hasHome()) {
                this.setHomeTo(BlockPos.ZERO, -1);
            }
        }
        super.customServerAiStep(serverLevel);
    }

    public boolean isThrowing() {
        return this.isThrowing;
    }

    public int getThrowingTick() {
        return this.throwingTick;
    }

    private void setThrowing(boolean isThrowing) {
        this.isThrowing = isThrowing;
        this.throwingTick = 0;
        this.level().broadcastEntityEvent(this, (byte) (isThrowing ? 1 : 0));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 0) {
            this.isThrowing = false;
            this.throwingTick = 0;
        } else if (id == 1) {
            this.isThrowing = true;
            this.throwingTick = 0;
        } else if (id == 18) {
            for (int i = 0; i < 7; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level()
                        .addParticle(ParticleTypes.HEART,
                                this.getRandomX(1.0),
                                this.getRandomY() + 0.5,
                                this.getRandomZ(1.0),
                                d,
                                e,
                                f);
            }
        } else if (id == 36) {
            for (int i = 0; i < 6; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level()
                        .addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SNOW.defaultBlockState()),
                                this.getRandomX(1.0),
                                this.getRandomY() + 0.5,
                                this.getRandomZ(1.0),
                                d0,
                                d1,
                                d2);
            }
        } else if (id == 37) {
            for (int i = 0; i < 6; ++i) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level()
                        .addParticle(ParticleTypes.SPLASH,
                                this.getRandomX(1.0),
                                this.getRandomY() + 0.5,
                                this.getRandomZ(1.0),
                                d0,
                                d1,
                                d2);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (this.isInvulnerableTo(serverLevel, damageSource)) {
            return false;
        } else if (damageSource.getDirectEntity() instanceof Snowball) {
            if (this.isAlive() && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level()
                        .addParticle(ParticleTypes.HEART,
                                this.getRandomX(1.0),
                                this.getRandomY(),
                                this.getRandomZ(1.0),
                                d0,
                                d1,
                                d2);
            }

            return false;
        } else {
            return super.hurtServer(serverLevel, damageSource, damageAmount);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!this.isThrowing) {
            this.setThrowing(true);
        }
    }

    public static boolean canHarm(Entity attacker, Entity target) {
        if (!(attacker instanceof MutantSnowGolem)) {
            return true;
        } else if (target instanceof CreeperMinion) {
            return !((CreeperMinion) target).isTame();
        } else {
            return target instanceof Enemy || target instanceof Mob && ((Mob) target).getTarget() == attacker
                    || ((MutantSnowGolem) attacker).getTarget() == target;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        InteractionResult interactionResult = itemInHand.interactLivingEntity(player, this, interactionHand);
        if (interactionResult.consumesAction()) {
            return interactionResult;
        } else if (itemInHand.getItem() == Items.SNOWBALL) {
            return InteractionResult.PASS;
        } else if (itemInHand.isEmpty() && this.getOwnerReference() == null) {
            if (!this.level().isClientSide()) {
                this.setOwner(player);
                this.level().broadcastEntityEvent(this, (byte) 18);
            }

            return InteractionResultHelper.sidedSuccess(this.level().isClientSide());
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.9F * this.getEyeHeight(), (double) this.getBbWidth() * 0.2);
    }

    @Override
    public void die(DamageSource cause) {
        if (this.level() instanceof ServerLevel serverLevel && serverLevel.getGameRules()
                .getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(this.getCombatTracker().getDeathMessage());
        }

        super.die(cause);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("JackOLantern", this.hasJackOLantern());
        EntityReference<LivingEntity> entityReference = this.getOwnerReference();
        if (entityReference != null) {
            entityReference.store(valueOutput, "Owner");
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setJackOLantern(valueInput.getBooleanOr("JackOLantern", false));
        this.setOwnerReference(EntityReference.readWithOldOwnerConversion(valueInput, "Owner", this.level()));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSoundEvents.ENTITY_MUTANT_SNOW_GOLEM_HURT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_MUTANT_SNOW_GOLEM_DEATH_SOUND_EVENT.value();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(SoundEvents.SNOW_STEP, 0.15F, 1.0F);
    }

    class ThrowIceGoal extends Goal {

        @Override
        public boolean canUse() {
            return MutantSnowGolem.this.getTarget() != null && MutantSnowGolem.this.isThrowing;
        }

        @Override
        public boolean canContinueToUse() {
            return MutantSnowGolem.this.isThrowing && MutantSnowGolem.this.throwingTick < 20;
        }

        @Override
        public void tick() {
            LivingEntity target = MutantSnowGolem.this.getTarget();
            if (target != null) {
                MutantSnowGolem.this.getNavigation().stop();
                MutantSnowGolem.this.yBodyRot = MutantSnowGolem.this.getYRot();
                if (MutantSnowGolem.this.throwingTick == 7) {
                    ThrowableBlock block = new ThrowableBlock(MutantSnowGolem.this);
                    double x = target.getX() - block.getX();
                    double y = target.getY() - block.getY();
                    double z = target.getZ() - block.getZ();
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    block.shoot(x, y + distance * 0.5, z, 0.9F, 1.0F);
                    MutantSnowGolem.this.level().addFreshEntity(block);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    static class SwimJumpGoal extends Goal {
        private final MutantSnowGolem golem;
        private int jumpTick = 20;
        private boolean waterReplaced;
        private BlockPos.MutableBlockPos prevPos;

        public SwimJumpGoal(MutantSnowGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.JUMP));
            golem.navigation.setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return this.golem.isInWater();
        }

        @Override
        public void start() {
            this.prevPos = new BlockPos.MutableBlockPos(this.golem.getX(),
                    this.golem.getBoundingBox().minY - 1.0,
                    this.golem.getZ());
            this.golem.setDeltaMovement((this.golem.random.nextFloat() - this.golem.random.nextFloat()) * 0.9F,
                    1.5,
                    (this.golem.random.nextFloat() - this.golem.random.nextFloat()) * 0.9F);
            this.golem.hurt(this.golem.level().damageSources().drown(), 16.0F);
            this.golem.setSwimJump(true);
        }

        @Override
        public boolean canContinueToUse() {
            return this.jumpTick > 0;
        }

        @Override
        public void tick() {
            --this.jumpTick;
            if (!this.waterReplaced && !this.golem.isInWater() && this.jumpTick < 17
                    && EntityHelper.isMobGriefingAllowed((ServerLevel) this.golem.level(), this.golem)) {
                this.prevPos.setY(this.getWaterSurfaceHeight(this.golem.level(), this.prevPos));
                if ((double) this.prevPos.getY() > this.golem.getY()) {
                    return;
                }

                for (int x = -2; x <= 2; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        for (int z = -2; z <= 2; ++z) {
                            if (y == 0 || Math.abs(x) != 2 && Math.abs(z) != 2) {
                                BlockPos pos = this.prevPos.offset(x, y, z);
                                if (this.golem.level().isEmptyBlock(pos) || this.golem.level().isWaterAt(pos)) {
                                    if (y != 0) {
                                        if ((Math.abs(x) == 1 || Math.abs(z) == 1)
                                                && this.golem.random.nextInt(4) == 0) {
                                            continue;
                                        }
                                    } else if ((Math.abs(x) == 2 || Math.abs(z) == 2)
                                            && this.golem.random.nextInt(3) == 0) {
                                        continue;
                                    }

                                    this.golem.level().setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                                }
                            }
                        }
                    }
                }

                BlockPos topPos = this.prevPos.above(2);
                if (this.golem.level().isEmptyBlock(topPos)) {
                    this.golem.level().setBlockAndUpdate(topPos, Blocks.ICE.defaultBlockState());
                }

                this.waterReplaced = true;
            }
        }

        @Override
        public void stop() {
            this.jumpTick = 20;
            this.waterReplaced = false;
            this.golem.setSwimJump(false);
            this.prevPos = null;
        }

        private int getWaterSurfaceHeight(Level world, BlockPos coord) {
            int y;
            y = coord.getY();
            while (world.isWaterAt(new BlockPos(coord.getX(), y + 1, coord.getZ()))) {
                ++y;
            }

            return y;
        }
    }
}
