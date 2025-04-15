package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import fuzs.puzzleslib.api.util.v1.DamageSourcesHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

public class EndersoulFragment extends Entity {
    public static final Predicate<Entity> IS_VALID_TARGET = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(
            (Entity entity) -> {
        EntityType<?> type = entity.getType();
        return type != EntityType.ITEM && type != EntityType.EXPERIENCE_ORB && type != EntityType.END_CRYSTAL && type != EntityType.ENDER_DRAGON && type != EntityType.ENDERMAN && type != ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value() && type != ModEntityTypes.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value() && type != ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value();
    });
    private static final EntityDataAccessor<Boolean> TAMED = SynchedEntityData.defineId(EndersoulFragment.class, EntityDataSerializers.BOOLEAN);
    public final float[][] stickRotations;
    private int explodeTick;
    private WeakReference<MutantEnderman> spawner;
    private Player owner;

    public EndersoulFragment(EntityType<? extends EndersoulFragment> type, Level world) {
        super(type, world);
        this.stickRotations = new float[8][3];
        this.explodeTick = 20 + this.random.nextInt(20);

        for (int i = 0; i < this.stickRotations.length; ++i) {
            for (int j = 0; j < this.stickRotations[i].length; ++j) {
                this.stickRotations[i][j] = this.random.nextFloat() * 2.0F * 3.1415927F;
            }
        }

    }

    public EndersoulFragment(Level world, MutantEnderman spawner) {
        this(ModEntityTypes.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value(), world);
        this.spawner = new WeakReference<>(spawner);
    }

    public static boolean isProtected(Entity entity) {
        return entity instanceof LivingEntity && ((LivingEntity) entity).isHolding(ModItems.ENDERSOUL_HAND_ITEM.value());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TAMED, false);
    }

    public Player getOwner() {
        return this.owner;
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(TAMED, tamed);
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
    public boolean isPushable() {
        return this.isAlive();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            EntityUtil.spawnEndersoulParticles(this, this.random, 64, 0.8F);
        }

    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3d = this.getDeltaMovement();
        if (this.owner == null && vec3d.y > -0.05000000074505806 && !this.isNoGravity()) {
            this.setDeltaMovement(vec3d.x, Math.max(-0.05000000074505806, vec3d.y - 0.10000000149011612), vec3d.z);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        if (this.owner != null && (!this.owner.isAlive() || this.owner.isSpectator())) {
            this.owner = null;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (!this.isTamed() && --this.explodeTick == 0) {
                this.explode(serverLevel);
            }

            if (this.owner != null && this.distanceToSqr(this.owner) > 9.0) {
                float scale = 0.05F;
                this.push((this.owner.getX() - this.getX()) * (double) scale, (this.owner.getY() + (double) (this.owner.getEyeHeight() / 3.0F) - this.getY()) * (double) scale, (this.owner.getZ() - this.getZ()) * (double) scale);
            }
        }

    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.isTamed()) {
            if (this.owner == null && !player.isSecondaryUseActive()) {
                this.owner = player;
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
                return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
            } else if (this.owner == player && player.isSecondaryUseActive()) {
                this.owner = null;
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.5F);
                return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        } else {
            if (!this.level().isClientSide) {
                this.setTamed(true);
            }

            this.owner = player;
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.5F);
            return InteractionResultHelper.sidedSuccess(this.level().isClientSide);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        if (this.isInvulnerableToBase(damageSource)) {
            return false;
        } else {
            if (this.isAlive() && this.tickCount > 0) {
                this.explode(serverLevel);
            }

            return true;
        }
    }

    private void explode(ServerLevel serverLevel) {
        this.playSound(ModSoundEvents.ENTITY_ENDERSOUL_FRAGMENT_EXPLODE_SOUND_EVENT.value(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        serverLevel.broadcastEntityEvent(this, (byte) 3);

        for (Entity entity : serverLevel.getEntities(this, this.getBoundingBox().inflate(5.0), IS_VALID_TARGET)) {
            if (this.distanceToSqr(entity) <= 25.0) {
                boolean hitChance = this.random.nextInt(3) != 0;
                if (isProtected(entity)) {
                    hitChance = this.random.nextInt(3) == 0;
                } else {
                    double x = entity.getX() - this.getX();
                    double z = entity.getZ() - this.getZ();
                    double d = Math.sqrt(x * x + z * z);
                    entity.setDeltaMovement(0.8 * x / d, this.random.nextFloat() * 0.6F - 0.1F, 0.8 * z / d);
                    EntityUtil.sendPlayerVelocityPacket(entity);
                }

                if (hitChance) {
                    DamageSource damageSource = DamageSourcesHelper.source(serverLevel,
                            ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE,
                            this,
                            this.spawner != null ? this.spawner.get() : this);
                    entity.hurtServer(serverLevel, damageSource, 1.0F);
                }
            }
        }

        this.discard();
    }

    @Override
    public SoundSource getSoundSource() {
        return this.isTamed() ? SoundSource.NEUTRAL : SoundSource.HOSTILE;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Tamed", this.isTamed());
        compound.putInt("ExplodeTick", this.explodeTick);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setTamed(compound.getBoolean("Collected") || compound.getBoolean("Tamed"));
        if (compound.contains("ExplodeTick")) {
            this.explodeTick = compound.getInt("ExplodeTick");
        }
    }
}
