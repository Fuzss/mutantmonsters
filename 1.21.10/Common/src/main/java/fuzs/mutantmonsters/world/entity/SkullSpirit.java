package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SkullSpirit extends Entity {
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_TARGETUUID_ID = SynchedEntityData.defineId(
            SkullSpirit.class,
            EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Boolean> DATA_ATTACHED = SynchedEntityData.defineId(SkullSpirit.class,
            EntityDataSerializers.BOOLEAN);

    private int startTick;
    private int attachedTick;
    @Nullable
    private UUID conversionStarter;

    public SkullSpirit(EntityType<? extends SkullSpirit> type, Level level) {
        super(type, level);
        this.startTick = 15;
        this.attachedTick = 80 + this.random.nextInt(40);
        this.noPhysics = true;
    }

    public SkullSpirit(Level level, Mob target, @Nullable UUID conversionStarter) {
        this(ModEntityTypes.SKULL_SPIRIT_ENTITY_TYPE.value(), level);
        this.setTarget(target);
        this.conversionStarter = conversionStarter;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TARGETUUID_ID, Optional.empty());
        builder.define(DATA_ATTACHED, false);
    }

    @Nullable
    public LivingEntity getTarget() {
        return EntityReference.get(this.getTargetReference(), this.level(), LivingEntity.class);
    }

    @Nullable
    public EntityReference<LivingEntity> getTargetReference() {
        return this.entityData.get(DATA_TARGETUUID_ID).orElse(null);
    }

    public void setTarget(@Nullable LivingEntity livingEntity) {
        this.entityData.set(DATA_TARGETUUID_ID, Optional.ofNullable(livingEntity).map(EntityReference::of));
    }

    public void setTargetReference(@Nullable EntityReference<LivingEntity> entityReference) {
        this.entityData.set(DATA_TARGETUUID_ID, Optional.ofNullable(entityReference));
    }

    public boolean isAttached() {
        return this.entityData.get(DATA_ATTACHED);
    }

    private void setAttached(boolean attached) {
        this.entityData.set(DATA_ATTACHED, attached);
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public void tick() {
        if (this.getTarget() instanceof Mob target && target.isAlive()) {
            if (this.isAttached()) {
                if (!this.level().isClientSide()) {
                    target.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.1F,
                            target.getDeltaMovement().y,
                            (this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    if (--this.attachedTick <= 0) {
                        EntityType<?> mutantType = ChemicalXMobEffect.getMutantOf(target);
                        if (mutantType != null && this.random.nextInt(4) != 0) {
                            MutatedExplosionHelper.explode(this, 2.0F, false, Level.ExplosionInteraction.NONE);
                            Mob mob = target.convertTo((EntityType<? extends Mob>) mutantType,
                                    ConversionParams.single(target, true, true),
                                    Function.identity()::apply);
                            if (mob != null) {
                                mob.setPersistenceRequired();
                                AABB boundingBox = mob.getBoundingBox();

                                for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(boundingBox.minX),
                                        Mth.floor(mob.getY()),
                                        Mth.floor(boundingBox.minZ),
                                        Mth.floor(boundingBox.maxX),
                                        Mth.floor(boundingBox.maxY),
                                        Mth.floor(boundingBox.maxZ))) {
                                    if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) > -1.0F) {
                                        this.level().destroyBlock(pos, true);
                                    }
                                }

                                if (this.conversionStarter != null) {
                                    Player player = this.level().getPlayerByUUID(this.conversionStarter);
                                    if (player instanceof ServerPlayer serverPlayer) {
                                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, mob);
                                    }
                                }
                            }
                        } else {
                            this.setAttached(false);
                            MutatedExplosionHelper.explode(this, 2.0F, false, Level.ExplosionInteraction.NONE);
                        }
                        this.discard();
                    }
                }

                this.setPos(target.getX(), target.getY(), target.getZ());
                if (this.random.nextInt(8) == 0) {
                    target.hurt(this.level().damageSources().magic(), 0.0F);
                }

                for (int i = 0; i < 3; ++i) {
                    double posX = target.getX() + (double) (this.random.nextFloat() * target.getBbWidth() * 2.0F)
                            - (double) target.getBbWidth();
                    double posY = target.getY() + 0.5 + (double) (this.random.nextFloat() * target.getBbHeight());
                    double posZ = target.getZ() + (double) (this.random.nextFloat() * target.getBbWidth() * 2.0F)
                            - (double) target.getBbWidth();
                    double x = this.random.nextGaussian() * 0.02;
                    double y = this.random.nextGaussian() * 0.02;
                    double z = this.random.nextGaussian() * 0.02;
                    this.level().addParticle(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value(), posX, posY, posZ, x, y, z);
                }
            } else {
                this.xo = this.getX();
                this.yo = this.getY();
                this.zo = this.getZ();
                this.setDeltaMovement(Vec3.ZERO);
                if (this.startTick-- >= 0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.3F * (float) this.startTick / 15.0F, 0.0));
                }

                double x = target.getX() - this.getX();
                double y = target.getY() - this.getY();
                double z = target.getZ() - this.getZ();
                double d = Math.sqrt(x * x + y * y + z * z);
                if (d != 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(x / d * 0.2, y / d * 0.2, z / d * 0.2));
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }

                if (!this.level().isClientSide() && this.distanceToSqr(target) < 1.0) {
                    this.setAttached(true);
                }

                for (int i = 0; i < 16; ++i) {
                    float xx = (this.random.nextFloat() - 0.5F) * 1.2F;
                    float yy = (this.random.nextFloat() - 0.5F) * 1.2F;
                    float zz = (this.random.nextFloat() - 0.5F) * 1.2F;
                    this.level()
                            .addParticle(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value(),
                                    this.getX() + (double) xx,
                                    this.getY() + (double) yy,
                                    this.getZ() + (double) zz,
                                    0.0,
                                    0.0,
                                    0.0);
                }
            }
        } else {
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        valueOutput.putBoolean("Attached", this.isAttached());
        valueOutput.putInt("AttachedTick", this.attachedTick);
        EntityReference<LivingEntity> entityReference = this.getTargetReference();
        if (entityReference != null) {
            entityReference.store(valueOutput, "Target");
        }
        valueOutput.storeNullable("ConversionPlayer", UUIDUtil.CODEC, this.conversionStarter);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        this.setAttached(valueInput.getBooleanOr("Attached", false));
        this.attachedTick = valueInput.getIntOr("AttachedTick", 0);
        this.setTargetReference(EntityReference.read(valueInput, "Target"));
        this.conversionStarter = valueInput.read("ConversionPlayer", UUIDUtil.CODEC).orElse(null);
    }
}
