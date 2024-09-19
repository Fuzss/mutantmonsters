package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.UUID;

public class SkullSpirit extends Entity {
    private static final EntityDataAccessor<OptionalInt> TARGET_ENTITY_ID = SynchedEntityData.defineId(SkullSpirit.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Boolean> ATTACHED = SynchedEntityData.defineId(SkullSpirit.class, EntityDataSerializers.BOOLEAN);

    private Mob target;
    private int startTick;
    private int attachedTick;
    private UUID targetUUID;
    @Nullable
    private UUID conversionStarter;

    public SkullSpirit(EntityType<? extends SkullSpirit> type, Level level) {
        super(type, level);
        this.startTick = 15;
        this.attachedTick = 80 + this.random.nextInt(40);
        this.noPhysics = true;
    }

    public SkullSpirit(Level level, Mob target, @Nullable UUID conversionStarter) {
        this(ModRegistry.SKULL_SPIRIT_ENTITY_TYPE.value(), level);
        this.entityData.set(TARGET_ENTITY_ID, OptionalInt.of(target.getId()));
        this.conversionStarter = conversionStarter;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TARGET_ENTITY_ID, OptionalInt.empty());
        this.entityData.define(ATTACHED, false);
    }

    public boolean isAttached() {
        return this.entityData.get(ATTACHED);
    }

    private void setAttached(boolean attached) {
        this.entityData.set(ATTACHED, attached);
    }

    public Mob getTarget() {
        return this.target;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY_ID.equals(key)) {
            this.entityData.get(TARGET_ENTITY_ID).ifPresent((id) -> {
                Entity entity = this.level().getEntity(id);
                if (entity instanceof Mob mob) {
                    this.target = mob;
                }
            });
        }
    }

    @Override
    public void tick() {
        if (this.targetUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.targetUUID);
            if (entity instanceof Mob mob) {
                this.entityData.set(TARGET_ENTITY_ID, OptionalInt.of(mob.getId()));
                this.targetUUID = null;
            }
        }

        if (this.target != null && this.target.isAlive()) {
            if (this.isAttached()) {
                if (!this.level().isClientSide) {
                    this.target.setDeltaMovement((this.random.nextFloat() - this.random.nextFloat()) * 0.1F, this.target.getDeltaMovement().y, (this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    if (--this.attachedTick <= 0) {
                        EntityType<?> mutantType = ChemicalXMobEffect.getMutantOf(this.target);
                        if (mutantType != null && this.random.nextInt(4) != 0) {
                            MutatedExplosionHelper.explode(this, 2.0F, false,
                                    Level.ExplosionInteraction.NONE
                            );
                            Mob mutant = this.target.convertTo((EntityType<? extends Mob>) mutantType, true);
                            if (mutant != null) {
                                mutant.setPersistenceRequired();
                                AABB boundingBox = mutant.getBoundingBox();

                                for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(boundingBox.minX), Mth.floor(mutant.getY()), Mth.floor(boundingBox.minZ), Mth.floor(boundingBox.maxX), Mth.floor(boundingBox.maxY), Mth.floor(boundingBox.maxZ))) {
                                    if (this.level().getBlockState(pos).getDestroySpeed(this.level(), pos) > -1.0F) {
                                        this.level().destroyBlock(pos, true);
                                    }
                                }

                                if (this.conversionStarter != null) {
                                    Player player = this.level().getPlayerByUUID(this.conversionStarter);
                                    if (player instanceof ServerPlayer serverPlayer) {
                                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, mutant);
                                    }
                                }
                            }
                        } else {
                            this.setAttached(false);
                            MutatedExplosionHelper.explode(this, 2.0F, false,
                                    Level.ExplosionInteraction.NONE
                            );
                        }
                        this.discard();
                    }
                }

                this.setPos(this.target.getX(), this.target.getY(), this.target.getZ());
                if (this.random.nextInt(8) == 0) {
                    this.target.hurt(this.level().damageSources().magic(), 0.0F);
                }

                for (int i = 0; i < 3; ++i) {
                    double posX = this.target.getX() + (double) (this.random.nextFloat() * this.target.getBbWidth() * 2.0F) - (double) this.target.getBbWidth();
                    double posY = this.target.getY() + 0.5 + (double) (this.random.nextFloat() * this.target.getBbHeight());
                    double posZ = this.target.getZ() + (double) (this.random.nextFloat() * this.target.getBbWidth() * 2.0F) - (double) this.target.getBbWidth();
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

                double x = this.target.getX() - this.getX();
                double y = this.target.getY() - this.getY();
                double z = this.target.getZ() - this.getZ();
                double d = Math.sqrt(x * x + y * y + z * z);
                if (d != 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().add(x / d * 0.2, y / d * 0.2, z / d * 0.2));
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }
                if (!this.level().isClientSide && this.distanceToSqr(this.target) < 1.0) {
                    this.setAttached(true);
                }

                for (int i = 0; i < 16; ++i) {
                    float xx = (this.random.nextFloat() - 0.5F) * 1.2F;
                    float yy = (this.random.nextFloat() - 0.5F) * 1.2F;
                    float zz = (this.random.nextFloat() - 0.5F) * 1.2F;
                    this.level().addParticle(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value(), this.getX() + (double) xx, this.getY() + (double) yy, this.getZ() + (double) zz, 0.0, 0.0, 0.0);
                }
            }
        } else {
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Attached", this.isAttached());
        compound.putInt("AttachedTick", this.attachedTick);
        if (this.target != null) {
            compound.putUUID("Target", this.target.getUUID());
        }
        if (this.conversionStarter != null) {
            compound.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setAttached(compound.getBoolean("Attached"));
        this.attachedTick = compound.getInt("AttachedTick");
        if (compound.hasUUID("Target")) {
            this.targetUUID = compound.getUUID("Target");
        }
        if (compound.hasUUID("ConversionPlayer")) {
            this.conversionStarter = compound.getUUID("ConversionPlayer");
        }
    }
}
