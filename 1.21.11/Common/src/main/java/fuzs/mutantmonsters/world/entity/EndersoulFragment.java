package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import fuzs.puzzleslib.api.util.v1.DamageHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Owner implementation adapted from {@link net.minecraft.world.entity.projectile.Projectile}.
 */
public class EndersoulFragment extends Entity implements TraceableEntity {
    @Nullable
    private EntityReference<Entity> owner;
    private int explodeTick;
    public final float[][] stickRotations;

    public EndersoulFragment(EntityType<? extends EndersoulFragment> entityType, Level level) {
        super(entityType, level);
        this.stickRotations = new float[8][3];
        for (int i = 0; i < this.stickRotations.length; ++i) {
            for (int j = 0; j < this.stickRotations[i].length; ++j) {
                this.stickRotations[i][j] = this.random.nextFloat() * 2.0F * Mth.PI;
            }
        }
    }

    private EndersoulFragment(EntityType<? extends EndersoulFragment> entityType, Level level, MutantEnderman mutantEnderman) {
        this(entityType, level);
        this.setOwner(mutantEnderman);
        this.explodeTick = 20 + this.random.nextInt(20);
    }

    public EndersoulFragment(Level level, MutantEnderman mutantEnderman) {
        this(ModEntityTypes.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value(), level, mutantEnderman);
    }

    public static boolean isProtected(Entity entity) {
        return entity instanceof LivingEntity livingEntity
                && livingEntity.isHolding(ModItems.ENDERSOUL_HAND_ITEM.value());
    }

    private void setExplodeTick(int explodeTick) {
        this.explodeTick = explodeTick;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // NO-OP
    }

    protected void setOwner(@Nullable EntityReference<Entity> owner) {
        this.owner = owner;
    }

    public void setOwner(@Nullable Entity owner) {
        this.setOwner(EntityReference.of(owner));
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return EntityReference.getEntity(this.owner, this.level());
    }

    public boolean ownedBy(Entity entity) {
        return this.owner != null && this.owner.matches(entity);
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof EndersoulFragment endersoulFragment) {
            this.owner = endersoulFragment.owner;
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
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
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void tick() {
        super.tick();
        Entity owner = this.getOwner();
        if (owner == null) {
            Vec3 vec3 = this.getDeltaMovement();
            if (vec3.y > -0.05 && !this.isNoGravity()) {
                this.setDeltaMovement(vec3.x, Math.max(-0.05, vec3.y - 0.1), vec3.z);
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            this.noPhysics = true;
            Vec3 vec3 = new Vec3(owner.getX() - this.getX(),
                    owner.getY() + owner.getEyeHeight() / 2.0 - this.getY(),
                    owner.getZ() - this.getZ());
            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015, this.getZ());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9).add(vec3.normalize().scale(0.05)));
            this.setPos(this.position().add(this.getDeltaMovement()));
            this.applyEffectsFromBlocks();
        }

        if (this.level() instanceof ServerLevel serverLevel && --this.explodeTick <= 0) {
            this.explode(serverLevel);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (!player.isSecondaryUseActive()) {
            if (this.level() instanceof ServerLevel && !this.ownedBy(player)) {
                this.setOwner(player);
                this.explodeTick += 600;
                this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
            }

            return InteractionResultHelper.sidedSuccess(this.level().isClientSide());
        } else {
            return super.interact(player, interactionHand);
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
        this.playSound(ModSoundEvents.ENTITY_ENDERSOUL_FRAGMENT_EXPLODE_SOUND_EVENT.value(),
                1.0F,
                (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        serverLevel.broadcastEntityEvent(this, (byte) 3);
        for (Entity entity : serverLevel.getEntities(this,
                this.getBoundingBox().inflate(5.0),
                MutantEnderman.ENDER_TARGETS)) {
            if (!(entity instanceof EndersoulFragment) && !this.ownedBy(entity)) {
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
                    DamageSource damageSource = DamageHelper.damageSource(serverLevel,
                            ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE,
                            this,
                            this.getOwner());
                    entity.hurtServer(serverLevel, damageSource, 1.0F);
                }
            }
        }

        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        EntityReference.store(this.owner, valueOutput, "Owner");
        valueOutput.putInt("ExplodeTick", this.explodeTick);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        this.setOwner(EntityReference.read(valueInput, "Owner"));
        valueInput.getInt("ExplodeTick").ifPresent(this::setExplodeTick);
    }
}
