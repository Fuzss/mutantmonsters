package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.OwnerTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpiderPig extends TamableAnimal implements PlayerRideableJumping, Saddleable, NeutralMob {
    private static final ResourceLocation STEP_HEIGHT_MODIFIER_WITH_PASSENGER_ID = MutantMonsters.id("with_passenger");
    private static final AttributeModifier STEP_HEIGHT_MODIFIER_WITH_PASSENGER = new AttributeModifier(
            STEP_HEIGHT_MODIFIER_WITH_PASSENGER_ID, 0.4, AttributeModifier.Operation.ADD_VALUE
    );
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(SpiderPig.class,
            EntityDataSerializers.BOOLEAN
    );
    private static final Ingredient TEMPTATION_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT,
            Items.PORKCHOP, Items.SPIDER_EYE
    );
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

    private final List<WebPos> webs = new ArrayList<>(12);
    private int leapCooldown;
    private int leapTick;
    private boolean isLeaping;
    private float chargePower;
    private int chargingTick;
    private int chargeExhaustion;
    private boolean chargeExhausted;
    private int angerTime;
    private UUID angerTarget;

    public SpiderPig(EntityType<? extends SpiderPig> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.ATTACK_DAMAGE, 3.0).add(
                Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.STEP_HEIGHT);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, (new MutantMeleeAttackGoal(this, 1.1)).setMaxAttackTick(16));
        this.goalSelector.addGoal(2, new LeapAttackGoal());
        this.goalSelector.addGoal(3, new AvoidDamageGoal(this, 1.1, this::isBaby));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, Ingredient.of(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, TEMPTATION_ITEMS, false));
        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new OwnerTargetGoal(this));
        this.targetSelector.addGoal(1, new HurtByNearestTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt)
        );
        this.targetSelector.addGoal(3, new NonTameRandomTargetGoal<>(this, Mob.class, true,
                livingEntity -> livingEntity.getType().is(ModRegistry.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG)
        ));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, false);
    }

    public boolean isBesideClimbableBlock() {
        return this.entityData.get(CLIMBING);
    }

    private void setBesideClimbableBlock(boolean climbing) {
        this.entityData.set(CLIMBING, climbing);
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation(this, worldIn);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return potioneffectIn.getEffect() != MobEffects.POISON && super.canBeAffected(potioneffectIn);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return TEMPTATION_ITEMS.test(stack);
    }

    @Override
    public void tick() {
        super.tick();
        this.setBesideClimbableBlock(this.horizontalCollision);
        if (this.chargeExhaustion >= 120) {
            this.chargeExhausted = true;
        }

        if (this.chargeExhaustion <= 0) {
            this.chargeExhausted = false;
        }

        this.chargeExhaustion = Math.max(0, this.chargeExhaustion - 1);
        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
            this.leapCooldown = Math.max(0, this.leapCooldown - 1);
            if (this.leapTick > 10 && this.onGround()) {
                this.isLeaping = false;
            }

            this.updateWebList(false);
            this.updateChargeState();
            if (this.isAlive() && this.isTame() && this.tickCount % 600 == 0) {
                this.heal(1.0F);
            }
        }

    }

    private void updateWebList(boolean onlyCheckSize) {
        WebPos first;
        if (!onlyCheckSize) {
            for (int i = 0; i < this.webs.size(); ++i) {
                WebPos coord = this.webs.get(i);
                if (this.level().getBlockState(coord) != Blocks.COBWEB.defaultBlockState()) {
                    this.webs.remove(i);
                    --i;
                } else {
                    --coord.timeLeft;
                }
            }

            if (!this.webs.isEmpty()) {
                first = this.webs.get(0);
                if (first.timeLeft < 0) {
                    this.webs.remove(0);
                    this.removeWeb(first);
                }
            }
        }

        while (this.webs.size() > 12) {
            first = this.webs.remove(0);
            this.removeWeb(first);
        }

    }

    private void removeWeb(BlockPos pos) {
        if (this.level().getBlockState(pos).is(Blocks.COBWEB) &&
                fuzs.puzzleslib.api.core.v1.CommonAbstractions.INSTANCE.getMobGriefingRule(this.level(), this)) {
            this.level().destroyBlock(pos, false, this);
        }

    }

    private void updateChargeState() {
        if (this.chargingTick > 0) {

            for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(),
                    EntitySelector.notRiding(this)
            )) {
                if (livingEntity != this && livingEntity != this.getOwner() && livingEntity.attackable()) {
                    this.doHurtTarget(livingEntity);
                }
            }
        }

        this.chargingTick = Math.max(0, this.chargingTick - 1);
    }

    @Override
    public InteractionResult mobInteract(Player playerEntity, InteractionHand hand) {
        InteractionResult interactionResult = super.mobInteract(playerEntity, hand);
        if (!interactionResult.consumesAction() && this.isOwnedBy(playerEntity)) {
            ItemStack itemstack = playerEntity.getItemInHand(hand);
            boolean isBreedingItem = this.isFood(itemstack);
            if (!isBreedingItem && this.isSaddled() && !this.isVehicle() && !playerEntity.isSecondaryUseActive()) {
                if (!this.level().isClientSide) {
                    playerEntity.startRiding(this);
                }

                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else if (itemstack.getItem() == Items.SADDLE) {
                return itemstack.interactLivingEntity(playerEntity, this, hand);
            } else if (itemstack.has(DataComponents.FOOD) && isBreedingItem && this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(playerEntity, hand, itemstack);
                this.heal((float) itemstack.get(DataComponents.FOOD).nutrition());
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return interactionResult;
        }
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return EntityUtil.shouldAttackEntity(target, owner, false);
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.isLeaping = false;
        if (this.random.nextInt(2) == 0 && fuzs.puzzleslib.api.core.v1.CommonAbstractions.INSTANCE.getMobGriefingRule(
                this.level(), this)) {
            double dx = target.getX() - target.xo;
            double dz = target.getZ() - target.zo;
            BlockPos pos = new BlockPos((int) (target.getX() + dx * 0.5), Mth.floor(this.getBoundingBox().minY),
                    (int) (target.getZ() + dz * 0.5)
            );
            BlockState state = this.level().getBlockState(pos);
            if (!state.isSolid() && !state.liquid() && !(state.getBlock() instanceof WebBlock)) {
                this.level().setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState());
                this.webs.add(new WebPos(pos, this.chargingTick > 0 ? 600 : 1200));
                this.updateWebList(true);
                this.setDeltaMovement(0.0, Math.max(0.25, this.getDeltaMovement().y), 0.0);
                this.fallDistance = 0.0F;
            }
        }

        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (!(target instanceof Spider) && !(target instanceof SpiderPig)) {
            if (this.level().getBlockStates(target.getBoundingBox()).anyMatch(
                    Blocks.COBWEB.defaultBlockState()::equals)) {
                damage += 4.0F;
            }
        }

        DamageSource damageSource = this.level().damageSources().mobAttack(this);
        boolean hurt = target.hurt(damageSource, damage);
        if (hurt && this.level() instanceof ServerLevel serverLevel) {
            EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
        }

        return hurt;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled() && !this.chargeExhausted && !this.horizontalCollision;
    }

    @Override
    public void onPlayerJump(int jumpPowerIn) {
        this.chargeExhaustion += 50 * jumpPowerIn / 100;
        this.chargePower = (float) jumpPowerIn / 100.0F;
    }

    @Override
    public void handleStartJump(int jumpPowerIn) {
        this.chargingTick = 8 * jumpPowerIn / 100;
    }

    @Override
    public void handleStopJump() {
        // NO-OP
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && this.isTame() && !this.isBaby();
    }

    @Override
    public void equipSaddle(ItemStack itemStack, @Nullable SoundSource soundCategory) {
        this.setSaddled(true);
        if (soundCategory != null) {
            this.level().playSound(null, this, SoundEvents.PIG_SADDLE, soundCategory, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return (this.entityData.get(DATA_FLAGS_ID) & 2) != 0;
    }

    private void setSaddled(boolean saddled) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        this.entityData.set(DATA_FLAGS_ID, saddled ? (byte) (b0 | 2) : (byte) (b0 & -3));
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angerTarget) {
        this.angerTarget = angerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isVehicle() && this.isSaddled();
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity entity ? entity : null;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(livingEntity);
        } else {
            int[][] aint = DismountHelper.offsetsForDirection(direction);
            BlockPos blockpos = this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

            for (Pose pose : livingEntity.getDismountPoses()) {
                AABB axisalignedbb = livingEntity.getLocalBoundsForPose(pose);

                for (int[] aint1 : aint) {
                    blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY(), blockpos.getZ() + aint1[1]);
                    double d0 = this.level().getBlockFloorHeight(blockpos$mutable);
                    if (DismountHelper.isBlockFloorValid(d0)) {
                        Vec3 vector3d = Vec3.upFromBottomCenterOf(blockpos$mutable, d0);
                        if (DismountHelper.canDismountTo(this.level(), livingEntity, axisalignedbb.move(vector3d))) {
                            livingEntity.setPose(pose);
                            return vector3d;
                        }
                    }
                }
            }

            return super.getDismountLocationForPassenger(livingEntity);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        AttributeInstance attribute = this.getAttribute(Attributes.STEP_HEIGHT);
        if (this.isVehicle() && this.getControllingPassenger() != null && this.isSaddled()) {
            LivingEntity passenger = this.getControllingPassenger();
            if (!attribute.hasModifier(STEP_HEIGHT_MODIFIER_WITH_PASSENGER_ID)) {
                attribute.addTransientModifier(STEP_HEIGHT_MODIFIER_WITH_PASSENGER);
            }
            this.setYRot(passenger.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(passenger.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;
            if (this.chargeExhausted || !(this.chargePower > 0.0F) ||
                    !this.onGround() && this.getInBlockState().getFluidState().isEmpty()) {
                this.chargePower = 0.0F;
            } else {
                double power = 1.6 * (double) this.chargePower;
                Vec3 lookVector = this.getLookAngle();
                this.setDeltaMovement(lookVector.x * power, 0.3 * (double) this.getBlockJumpFactor(),
                        lookVector.z * power
                );
                this.chargePower = 0.0F;
            }

            if (this.isControlledByLocalInstance()) {
                float strafe = passenger.xxa * 0.8F;
                float forward = passenger.zza * 0.6F;
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(strafe, travelVector.y, forward));
            } else if (passenger instanceof Player) {
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                this.calculateEntityAnimation(false);
            }
        } else {
            attribute.removeModifier(STEP_HEIGHT_MODIFIER_WITH_PASSENGER_ID);
            super.travel(travelVector);
        }

    }

    @Override
    public boolean killedEntity(ServerLevel serverWorld, LivingEntity livingEntity) {
        if (livingEntity instanceof CreeperMinion minion && !this.isTame()) {
            LivingEntity owner = minion.getOwner();
            if (owner instanceof Player && !CommonAbstractions.INSTANCE.onAnimalTame(this, (Player) owner)) {
                serverWorld.broadcastEntityEvent(this, (byte) 7);
                this.tame((Player) owner);
                minion.discard();
                return false;
            } else {
                serverWorld.broadcastEntityEvent(this, (byte) 6);
            }
        }

        if (livingEntity.getType().is(ModRegistry.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG) &&
                livingEntity instanceof Mob mob) {
            return mob.convertTo(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(), true) == null;
        }

        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !this.isTame();
    }

    @Override
    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    @Override
    public void makeStuckInBlock(BlockState blockState, Vec3 motionMultiplier) {
        if (!blockState.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(blockState, motionMultiplier);
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverWorld, AgeableMob ageableEntity) {
        if (this.random.nextInt(20) == 0) {
            return EntityType.PIG.create(serverWorld);
        } else {
            SpiderPig spiderPig = ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value().create(serverWorld);
            UUID uuid = this.getOwnerUUID();
            if (uuid != null) {
                spiderPig.setOwnerUUID(uuid);
                spiderPig.setTame(true, true);
            }

            return spiderPig;
        }
    }

    @Override
    protected void dropEquipment() {
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
            this.setSaddled(false);
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (this.dead && !this.level().isClientSide && !this.webs.isEmpty()) {
            for (WebPos webPos : this.webs) {
                this.removeWeb(webPos);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addPersistentAngerSaveData(compound);
        compound.putBoolean("Saddle", this.isSaddled());
        if (!this.webs.isEmpty()) {
            ListTag listnbt = new ListTag();

            for (WebPos web : this.webs) {
                CompoundTag compound1 = new CompoundTag();
                compound1.put("WebPosition", NbtUtils.writeBlockPos(web));
                compound1.putInt("TimeLeft", web.timeLeft);
                listnbt.add(compound1);
            }

            compound.put("Webs", listnbt);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (this.level() instanceof ServerLevel) {
            this.readPersistentAngerSaveData(this.level(), compound);
        }

        this.setSaddled(compound.getBoolean("Saddle") || compound.getBoolean("Saddled"));
        ListTag listnbt = compound.getList("Webs", 10);

        for (int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compound1 = listnbt.getCompound(i);
            Optional<BlockPos> optional = NbtUtils.readBlockPos(compound1, "WebPosition");
            optional.ifPresent(blockPos -> this.webs.add(new WebPos(blockPos, compound1.getInt("TimeLeft"))));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENTITY_SPIDER_PIG_AMBIENT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSoundEvents.ENTITY_SPIDER_PIG_HURT_SOUND_EVENT.value();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_SPIDER_PIG_DEATH_SOUND_EVENT.value();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    static class WebPos extends BlockPos {
        private int timeLeft;

        public WebPos(BlockPos pos, int timeLeft) {
            super(pos);
            this.timeLeft = timeLeft;
        }
    }

    class LeapAttackGoal extends Goal {

        @Override
        public boolean canUse() {
            LivingEntity target = SpiderPig.this.getTarget();
            return target != null && SpiderPig.this.leapCooldown <= 0 &&
                    (SpiderPig.this.onGround() || !SpiderPig.this.getInBlockState().getFluidState().isEmpty()) &&
                    (SpiderPig.this.distanceToSqr(target) < 64.0 && SpiderPig.this.random.nextInt(8) == 0 ||
                            SpiderPig.this.distanceToSqr(target) < 6.25);
        }

        @Override
        public void start() {
            SpiderPig.this.isLeaping = true;
            SpiderPig.this.leapCooldown = 15;
            LivingEntity target = SpiderPig.this.getTarget();
            double x = target.getX() - SpiderPig.this.getX();
            double y = target.getY() - SpiderPig.this.getY();
            double z = target.getZ() - SpiderPig.this.getZ();
            double d = Mth.sqrt((float) (x * x + y * y + z * z));
            double scale = 2.0F + 0.2F * SpiderPig.this.random.nextFloat() * SpiderPig.this.random.nextFloat();
            SpiderPig.this.setDeltaMovement(x / d * scale,
                    (y / d * scale * 0.5 + 0.3) * (double) SpiderPig.this.getBlockJumpFactor(), z / d * scale
            );
        }

        @Override
        public boolean canContinueToUse() {
            return SpiderPig.this.isLeaping && SpiderPig.this.leapTick < 40;
        }

        @Override
        public void tick() {
            ++SpiderPig.this.leapTick;
        }

        @Override
        public void stop() {
            SpiderPig.this.leapTick = 0;
        }
    }
}
