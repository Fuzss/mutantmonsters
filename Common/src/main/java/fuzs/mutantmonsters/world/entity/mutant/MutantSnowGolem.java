package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.FleeRainGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import fuzs.mutantmonsters.world.level.pathfinder.MutantGroundPathNavigation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class MutantSnowGolem extends AbstractGolem implements RangedAttackMob, Shearable {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(MutantSnowGolem.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Byte> STATUS = SynchedEntityData.defineId(MutantSnowGolem.class, EntityDataSerializers.BYTE);
    private boolean isThrowing;
    private int throwingTick;

    public MutantSnowGolem(EntityType<? extends MutantSnowGolem> type, Level worldIn) {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
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
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, (entity) -> {
            return entity instanceof Enemy && (!(entity instanceof Creeper) || ((Creeper)entity).getTarget() == this);
        }));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 80.0).add(Attributes.MOVEMENT_SPEED, 0.26);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
        this.entityData.define(STATUS, (byte)1);
    }

    @Nullable
    public Player getOwner() {
        return this.getOwnerId().map(this.level::getPlayerByUUID).orElse(null);
    }

    public Optional<UUID> getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    public boolean hasJackOLantern() {
        return (this.entityData.get(STATUS) & 1) != 0;
    }

    public void setJackOLantern(boolean jackOLantern) {
        byte b0 = this.entityData.get(STATUS);
        this.entityData.set(STATUS, jackOLantern ? (byte)(b0 | 1) : (byte)(b0 & -2));
    }

    public boolean getSwimJump() {
        return (this.entityData.get(STATUS) & 4) != 0;
    }

    public void setSwimJump(boolean swimJumping) {
        byte b0 = this.entityData.get(STATUS);
        this.entityData.set(STATUS, swimJumping ? (byte)(b0 | 4) : (byte)(b0 & -5));
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new MutantGroundPathNavigation(this, worldIn);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 2.0F;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target instanceof Enemy && target.canBeSeenAsEnemy();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        if (worldIn.getBiome(pos).value().shouldSnowGolemBurn(pos)) {
            return -10.0F;
        } else {
            return worldIn.getBlockState(pos).getBlock() == Blocks.SNOW ? 10.0F : 0.0F;
        }
    }

    @Override
    public void tick() {
        super.tick();
        int x;
        if (this.level.isClientSide && this.getSwimJump()) {
            for(x = 0; x < 6; ++x) {
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SNOW.defaultBlockState()), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
                d0 = this.random.nextGaussian() * 0.02;
                d1 = this.random.nextGaussian() * 0.02;
                d2 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.SPLASH, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
            }
        }

        if (this.isThrowing) {
            ++this.throwingTick;
            if (this.throwingTick >= 20) {
                this.isThrowing = false;
                this.throwingTick = 0;
            }
        }

        if (this.level.dimensionType().ultraWarm() || this.level.getBiome(this.blockPosition()).value().shouldSnowGolemBurn(this.blockPosition())) {
            if (this.random.nextFloat() > Math.min(80.0F, this.getHealth()) * 0.01F) {
                this.level.addParticle(ParticleTypes.FALLING_WATER, this.getRandomX(0.6), this.getRandomY() - 0.15, this.getRandomZ(0.6), 0.0, 0.0, 0.0);
            }

            if (this.tickCount % 60 == 0) {
                this.hurt(DamageSource.ON_FIRE, 1.0F);
            }
        }

        if (this.tickCount % 80 == 0 && this.isAlive() && this.getHealth() < this.getMaxHealth() && this.isSnowingAt(this.blockPosition())) {
            this.heal(1.0F);
        }

        if (!this.level.isClientSide && this.onGround && !this.level.dimensionType().ultraWarm() && CommonAbstractions.INSTANCE.getMobGriefingEvent(this.level, this)) {
            x = Mth.floor(this.getX());
            int y = Mth.floor(this.getBoundingBox().minY);
            int z = Mth.floor(this.getZ());

            for(int i = -2; i <= 2; ++i) {
                for(int j = -2; j <= 2; ++j) {
                    if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                        BlockPos pos = new BlockPos(x + i, y, z + j);
                        BlockPos posDown = pos.below();
                        BlockPos posAbove = pos.above();
                        boolean placeSnow = this.level.isEmptyBlock(pos) && Blocks.SNOW.defaultBlockState().canSurvive(this.level, pos);
                        boolean placeIce = this.level.isWaterAt(posDown);
                        if (this.level.getFluidState(pos).getType() == Fluids.FLOWING_WATER) {
                            this.level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                        }

                        if (this.level.getFluidState(posAbove).getType() == Fluids.FLOWING_WATER) {
                            this.level.setBlockAndUpdate(posAbove, Blocks.ICE.defaultBlockState());
                        }

                        if ((!placeSnow || (Math.abs(i) != 2 && Math.abs(j) != 2 || this.random.nextInt(20) == 0) && (Math.abs(i) != 1 && Math.abs(j) != 1 || this.random.nextInt(10) == 0)) && (!placeIce || (Math.abs(i) != 2 && Math.abs(j) != 2 || this.random.nextInt(14) == 0) && (Math.abs(i) != 1 && Math.abs(j) != 1 || this.random.nextInt(6) == 0))) {
                            if (placeSnow) {
                                this.level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
                            }

                            if (placeIce) {
                                this.level.setBlockAndUpdate(posDown, Blocks.ICE.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean isSnowingAt(BlockPos position) {
        if (!this.level.isRaining()) {
            return false;
        } else if (!this.level.canSeeSky(position)) {
            return false;
        } else if (this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() > position.getY()) {
            return false;
        } else {
            Biome biome = this.level.getBiome(position).value();
            return biome.getPrecipitation() == Biome.Precipitation.SNOW && biome.coldEnoughToSnow(position);
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
    public void shear(SoundSource source) {
        this.level.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, source, 1.0F, 1.0F);
        if (!this.level.isClientSide()) {
            this.setJackOLantern(false);
            this.spawnAtLocation(new ItemStack(Items.JACK_O_LANTERN), 1.7F);
        }
    }

    @Override
    protected void customServerAiStep() {
        if (!this.isLeashed()) {
            Player owner = this.getOwner();
            if (owner != null && owner.isAlive()) {
                this.restrictTo(owner.blockPosition(), this.getTarget() == null ? 8 : 16);
            } else if (this.hasRestriction()) {
                this.restrictTo(BlockPos.ZERO, -1);
            }

        }
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
        this.level.broadcastEntityEvent(this, (byte)(isThrowing ? 1 : 0));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id != 0 && id != 1) {
            super.handleEntityEvent(id);
            if (id == 2 || id == 33 || id == 36 || id == 37 || id == 44) {
                for(int i = 0; i < 30; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02;
                    double d1 = this.random.nextGaussian() * 0.02;
                    double d2 = this.random.nextGaussian() * 0.02;
                    this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SNOW.defaultBlockState()), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
                }
            }
        } else {
            this.isThrowing = id == 1;
            this.throwingTick = 0;
        }

    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getDirectEntity() instanceof Snowball) {
            if (this.isAlive() && this.getHealth() < this.getMaxHealth()) {
                this.heal(1.0F);
                double d0 = this.random.nextGaussian() * 0.02;
                double d1 = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), d0, d1, d2);
            }

            return false;
        } else {
            return super.hurt(source, amount);
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
            return !((CreeperMinion)target).isTame();
        } else {
            return target instanceof Enemy || target instanceof Mob && ((Mob)target).getTarget() == attacker || ((MutantSnowGolem)attacker).getTarget() == target;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        InteractionResult actionresulttype = itemStack.interactLivingEntity(player, this, hand);
        if (actionresulttype.consumesAction()) {
            return actionresulttype;
        } else if ((!this.getOwnerId().isPresent() || player == this.getOwner()) && itemStack.getItem() != Items.SNOWBALL) {
            if (!this.level.isClientSide) {
                this.setOwnerId(!this.getOwnerId().isPresent() ? player.getUUID() : null);
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.9F * this.getEyeHeight(), (double)this.getBbWidth() * 0.20000000298023224);
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), this.getOwner().getUUID());
        }

        super.die(cause);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("JackOLantern", this.hasJackOLantern());
        this.getOwnerId().ifPresent((uuid) -> {
            compound.putUUID("Owner", uuid);
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Pumpkin") || compound.contains("JackOLantern")) {
            this.setJackOLantern(compound.getBoolean("Pumpkin") || compound.getBoolean("JackOLantern"));
        }

        if (compound.hasUUID("OwnerUUID")) {
            this.setOwnerId(compound.getUUID("OwnerUUID"));
        } else if (compound.hasUUID("Owner")) {
            this.setOwnerId(compound.getUUID("Owner"));
        }

    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModRegistry.ENTITY_MUTANT_SNOW_GOLEM_HURT_SOUND_EVENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModRegistry.ENTITY_MUTANT_SNOW_GOLEM_DEATH_SOUND_EVENT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SNOW_STEP, 0.15F, 1.0F);
    }

    class ThrowIceGoal extends Goal {
        private LivingEntity attackTarget;

        @Override
        public boolean canUse() {
            this.attackTarget = MutantSnowGolem.this.getTarget();
            return this.attackTarget != null && MutantSnowGolem.this.isThrowing;
        }

        @Override
        public boolean canContinueToUse() {
            return MutantSnowGolem.this.isThrowing && MutantSnowGolem.this.throwingTick < 20;
        }

        @Override
        public void tick() {
            MutantSnowGolem.this.getNavigation().stop();
            MutantSnowGolem.this.yBodyRot = MutantSnowGolem.this.getYRot();
            if (MutantSnowGolem.this.throwingTick == 7) {
                ThrowableBlock block = new ThrowableBlock(MutantSnowGolem.this);
                double x = this.attackTarget.getX() - block.getX();
                double y = this.attackTarget.getY() - block.getY();
                double z = this.attackTarget.getZ() - block.getZ();
                double xz = Math.sqrt(x * x + z * z);
                block.shoot(x, y + xz * 0.4000000059604645, z, 0.9F, 1.0F);
                MutantSnowGolem.this.level.addFreshEntity(block);
            }

        }

        @Override
        public void stop() {
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
            this.prevPos = new BlockPos.MutableBlockPos(this.golem.getX(), this.golem.getBoundingBox().minY - 1.0, this.golem.getZ());
            this.golem.setDeltaMovement((this.golem.random.nextFloat() - this.golem.random.nextFloat()) * 0.9F, 1.5, (this.golem.random.nextFloat() - this.golem.random.nextFloat()) * 0.9F);
            this.golem.hurt(DamageSource.DROWN, 16.0F);
            this.golem.setSwimJump(true);
        }

        @Override
        public boolean canContinueToUse() {
            return this.jumpTick > 0;
        }

        @Override
        public void tick() {
            --this.jumpTick;
            if (!this.waterReplaced && !this.golem.isInWater() && this.jumpTick < 17 && CommonAbstractions.INSTANCE.getMobGriefingEvent(this.golem.level, this.golem)) {
                this.prevPos.setY(this.getWaterSurfaceHeight(this.golem.level, this.prevPos));
                if ((double)this.prevPos.getY() > this.golem.getY()) {
                    return;
                }

                for(int x = -2; x <= 2; ++x) {
                    for(int y = -1; y <= 1; ++y) {
                        for(int z = -2; z <= 2; ++z) {
                            if (y == 0 || Math.abs(x) != 2 && Math.abs(z) != 2) {
                                BlockPos pos = this.prevPos.offset(x, y, z);
                                if (this.golem.level.isEmptyBlock(pos) || this.golem.level.isWaterAt(pos)) {
                                    if (y != 0) {
                                        if ((Math.abs(x) == 1 || Math.abs(z) == 1) && this.golem.random.nextInt(4) == 0) {
                                            continue;
                                        }
                                    } else if ((Math.abs(x) == 2 || Math.abs(z) == 2) && this.golem.random.nextInt(3) == 0) {
                                        continue;
                                    }

                                    this.golem.level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                                }
                            }
                        }
                    }
                }

                BlockPos topPos = this.prevPos.above(2);
                if (this.golem.level.isEmptyBlock(topPos)) {
                    this.golem.level.setBlockAndUpdate(topPos, Blocks.ICE.defaultBlockState());
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
