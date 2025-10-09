package fuzs.mutantmonsters.world.entity.mutant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.ai.goal.AvoidDamageGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.HurtByNearestTargetGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.ai.goal.OwnerTargetGoal;
import fuzs.puzzleslib.api.util.v1.EntityHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class SpiderPig extends TamableAnimal implements PlayerRideableJumping, NeutralMob {
    private static final ResourceLocation STEP_HEIGHT_MODIFIER_WITH_PASSENGER_ID = MutantMonsters.id("with_passenger");
    private static final AttributeModifier STEP_HEIGHT_MODIFIER_WITH_PASSENGER = new AttributeModifier(
            STEP_HEIGHT_MODIFIER_WITH_PASSENGER_ID,
            0.4,
            AttributeModifier.Operation.ADD_VALUE);
    private static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(SpiderPig.class,
            EntityDataSerializers.BOOLEAN);
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

    public static AttributeSupplier.Builder createAttributes() {
        return createAnimalAttributes().add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.STEP_HEIGHT);
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
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.1, this::isFood, false));
        this.goalSelector.addGoal(7, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new OwnerTargetGoal(this));
        this.targetSelector.addGoal(1, new HurtByNearestTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3,
                new NonTameRandomTargetGoal<>(this,
                        Mob.class,
                        true,
                        (LivingEntity livingEntity, ServerLevel serverLevel) -> livingEntity.getType()
                                .is(ModTags.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG)));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CLIMBING, false);
    }

    public boolean isBesideClimbableBlock() {
        return this.entityData.get(DATA_CLIMBING);
    }

    private void setBesideClimbableBlock(boolean climbing) {
        this.entityData.set(DATA_CLIMBING, climbing);
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation(this, worldIn);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffect) {
        return mobEffect.getEffect() != MobEffects.POISON && super.canBeAffected(mobEffect);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ModTags.SPIDER_PIG_FOOD_ITEM_TAG);
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
        if (this.level() instanceof ServerLevel serverLevel) {
            this.updatePersistentAnger(serverLevel, true);
            this.leapCooldown = Math.max(0, this.leapCooldown - 1);
            if (this.leapTick > 10 && this.onGround()) {
                this.isLeaping = false;
            }

            this.updateWebList(serverLevel, false);
            this.updateChargeState(serverLevel);
            if (this.isAlive() && this.isTame() && this.tickCount % 600 == 0) {
                this.heal(1.0F);
            }
        }

    }

    private void updateWebList(ServerLevel serverLevel, boolean onlyCheckSize) {
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
                    this.removeWeb(serverLevel, first);
                }
            }
        }

        while (this.webs.size() > 12) {
            first = this.webs.remove(0);
            this.removeWeb(serverLevel, first);
        }
    }

    private void removeWeb(ServerLevel serverLevel, BlockPos pos) {
        if (this.level().getBlockState(pos).is(Blocks.COBWEB) && EntityHelper.isMobGriefingAllowed(serverLevel, this)) {
            this.level().destroyBlock(pos, false, this);
        }
    }

    private void updateChargeState(ServerLevel serverLevel) {
        if (this.chargingTick > 0) {

            for (LivingEntity livingEntity : this.level()
                    .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), EntitySelector.notRiding(this))) {
                if (livingEntity != this && livingEntity != this.getOwner() && livingEntity.attackable()) {
                    this.doHurtTarget(serverLevel, livingEntity);
                }
            }
        }

        this.chargingTick = Math.max(0, this.chargingTick - 1);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (!this.isVehicle()) {
            if (this.isFood(itemInHand) && itemInHand.has(DataComponents.FOOD)
                    && this.getHealth() < this.getMaxHealth()) {
                this.usePlayerItem(player, interactionHand, itemInHand);
                this.heal((float) itemInHand.get(DataComponents.FOOD).nutrition());
                return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
            } else {
                InteractionResult interactionResult = super.mobInteract(player, interactionHand);
                if (!interactionResult.consumesAction()) {
                    if (this.isSaddled() && !player.isSecondaryUseActive() && this.isOwnedBy(player)) {
                        if (!this.level().isClientSide) {
                            player.startRiding(this);
                        }

                        return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
                    } else if (this.isEquippableInSlot(itemInHand, EquipmentSlot.SADDLE)) {
                        return itemInHand.interactLivingEntity(player, this, interactionHand);
                    }
                } else {
                    return interactionResult;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return EntityUtil.shouldAttackEntity(target, owner, false);
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity target) {
        this.isLeaping = false;
        if (this.random.nextInt(2) == 0 && EntityHelper.isMobGriefingAllowed(serverLevel, this)) {
            double dx = target.getX() - target.xo;
            double dz = target.getZ() - target.zo;
            BlockPos pos = new BlockPos((int) (target.getX() + dx * 0.5),
                    Mth.floor(this.getBoundingBox().minY),
                    (int) (target.getZ() + dz * 0.5));
            BlockState state = serverLevel.getBlockState(pos);
            if (!state.isSolid() && !state.liquid() && !(state.getBlock() instanceof WebBlock)) {
                serverLevel.setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState());
                this.webs.add(new WebPos(pos, this.chargingTick > 0 ? 600 : 1200));
                this.updateWebList(serverLevel, true);
                this.setDeltaMovement(0.0, Math.max(0.25, this.getDeltaMovement().y), 0.0);
                this.fallDistance = 0.0F;
            }
        }

        float damageAmount = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (target.getType() != this.getType() && !target.getType().is(ModTags.SPIDER_PIG_FRIENDS_ENTITY_TYPE_TAG)) {
            if (this.level()
                    .getBlockStates(target.getBoundingBox())
                    .anyMatch(Blocks.COBWEB.defaultBlockState()::equals)) {
                damageAmount += 4.0F;
            }
        }

        DamageSource damageSource = this.level().damageSources().mobAttack(this);
        if (target.hurtServer(serverLevel, damageSource, damageAmount)) {
            EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
            return true;
        } else {
            return false;
        }
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
    public boolean canUseSlot(EquipmentSlot slot) {
        return slot != EquipmentSlot.SADDLE ? super.canUseSlot(slot) :
                this.isAlive() && this.isTame() && !this.isBaby();
    }

    @Override
    protected boolean canDispenserEquipIntoSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.SADDLE || super.canDispenserEquipIntoSlot(slot);
    }

    @Override
    protected Holder<SoundEvent> getEquipSound(EquipmentSlot equipmentSlot, ItemStack itemStack, Equippable equippable) {
        return equipmentSlot == EquipmentSlot.SADDLE ? SoundEvents.PIG_SADDLE :
                super.getEquipSound(equipmentSlot, itemStack, equippable);
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
            if (this.chargeExhausted || !(this.chargePower > 0.0F) || !this.onGround() && this.getInBlockState()
                    .getFluidState()
                    .isEmpty()) {
                this.chargePower = 0.0F;
            } else {
                double power = 1.6 * (double) this.chargePower;
                Vec3 lookVector = this.getLookAngle();
                this.setDeltaMovement(lookVector.x * power,
                        0.3 * (double) this.getBlockJumpFactor(),
                        lookVector.z * power);
                this.chargePower = 0.0F;
            }

            if (this.isLocalInstanceAuthoritative()) {
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
    public boolean killedEntity(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (livingEntity instanceof CreeperMinion minion && !this.isTame()) {
            LivingEntity owner = minion.getOwner();
            if (owner instanceof Player && !CommonAbstractions.INSTANCE.onAnimalTame(this, (Player) owner)) {
                serverLevel.broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                this.tame((Player) owner);
                minion.discard();
                return false;
            } else {
                serverLevel.broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
            }
        }

        if (livingEntity.getType().is(ModTags.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG) && livingEntity instanceof Mob mob) {
            return mob.convertTo(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(),
                    ConversionParams.single(this, false, false),
                    Function.identity()::apply) == null;
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
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableEntity) {
        if (this.random.nextInt(20) == 0) {
            return EntityType.PIG.create(serverLevel, EntitySpawnReason.BREEDING);
        } else {
            SpiderPig spiderPig = ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value()
                    .create(serverLevel, EntitySpawnReason.BREEDING);
            EntityReference<LivingEntity> ownerReference = this.getOwnerReference();
            if (spiderPig != null && ownerReference != null) {
                spiderPig.setOwnerReference(ownerReference);
                spiderPig.setTame(true, true);
            }

            return spiderPig;
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (this.dead && this.level() instanceof ServerLevel serverLevel && !this.webs.isEmpty()) {
            for (WebPos webPos : this.webs) {
                this.removeWeb(serverLevel, webPos);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        this.addPersistentAngerSaveData(valueOutput);
        valueOutput.store("Webs", WebPos.LIST_CODEC, this.webs);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.readPersistentAngerSaveData(this.level(), valueInput);
        valueInput.read("Webs", WebPos.LIST_CODEC).ifPresent(this.webs::addAll);
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

    static final class WebPos extends BlockPos {
        public static final Codec<WebPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockPos.CODEC.fieldOf(
                        "block_pos").forGetter(WebPos::getBlockPos),
                Codec.INT.fieldOf("time_left").forGetter(WebPos::getTimeLeft)).apply(instance, WebPos::new));
        public static final Codec<List<WebPos>> LIST_CODEC = CODEC.listOf();

        int timeLeft;

        public WebPos(BlockPos pos, int timeLeft) {
            super(pos);
            this.timeLeft = timeLeft;
        }

        public BlockPos getBlockPos() {
            return this;
        }

        public int getTimeLeft() {
            return this.timeLeft;
        }
    }

    class LeapAttackGoal extends Goal {

        @Override
        public boolean canUse() {
            LivingEntity target = SpiderPig.this.getTarget();
            return target != null && SpiderPig.this.leapCooldown <= 0 && (SpiderPig.this.onGround()
                    || !SpiderPig.this.getInBlockState().getFluidState().isEmpty()) && (
                    SpiderPig.this.distanceToSqr(target) < 64.0 && SpiderPig.this.random.nextInt(8) == 0
                            || SpiderPig.this.distanceToSqr(target) < 6.25);
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
                    (y / d * scale * 0.5 + 0.3) * (double) SpiderPig.this.getBlockJumpFactor(),
                    z / d * scale);
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
