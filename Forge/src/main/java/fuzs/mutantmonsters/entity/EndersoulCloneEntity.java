package fuzs.mutantmonsters.entity;

import fuzs.mutantmonsters.entity.ai.goal.MBMeleeAttackGoal;
import fuzs.mutantmonsters.entity.mutant.MutantEndermanEntity;
import fuzs.mutantmonsters.pathfinding.MBGroundPathNavigator;
import fuzs.mutantmonsters.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;

public class EndersoulCloneEntity extends Monster {
    private MutantEndermanEntity cloner;

    public EndersoulCloneEntity(EntityType<? extends EndersoulCloneEntity> type, Level worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.0F;
        this.xpReward = this.random.nextInt(2);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, -1.0F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MBMeleeAttackGoal(this, 1.2));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0).add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 2.55F;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new MBGroundPathNavigator(this, worldIn);
    }

    public void setCloner(MutantEndermanEntity cloner) {
        this.cloner = cloner;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)cloner.getMaxHealth());
        this.setHealth(cloner.getHealth());
        if (cloner.hasCustomName()) {
            this.setCustomName(cloner.getCustomName());
            this.setCustomNameVisible(cloner.isCustomNameVisible());
        }

    }

    public int getMaxFallDistance() {
        return 3;
    }

    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 0) {
            EntityUtil.spawnEndersoulParticles(this, this.random, 256, 1.8F);
        }

    }

    public void aiStep() {
        this.jumping = false;
        super.aiStep();
        if (this.cloner != null && (this.cloner.isNoAi() || !this.cloner.isAlive() || this.cloner.level != this.level)) {
            this.remove();
        }

    }

    public boolean doHurtTarget(Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (!this.level.isClientSide && this.random.nextInt(3) != 0) {
            this.teleportToEntity(entityIn);
        }

        if (flag) {
            this.heal(2.0F);
        }

        this.swing(InteractionHand.MAIN_HAND);
        return flag;
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getEntity() instanceof EnderDragon) {
            return false;
        } else {
            boolean remove = !this.level.isClientSide && this.isAlive() && this.tickCount > 10;
            if (remove) {
                if (source.getEntity() instanceof Player) {
                    this.setLastHurtByPlayer((Player) source.getEntity());
                }

                this.dropAllDeathLoot(source);
                this.dropExperience();
                this.remove();
            }

            return remove;
        }
    }

    protected void customServerAiStep() {
        Entity entity = this.getTarget();
        if (this.random.nextInt(10) == 0 && entity != null && (this.isInWater() || this.isPassengerOfSameVehicle(entity) || this.distanceToSqr(entity) > 1024.0 || !this.isPathFinding())) {
            this.teleportToEntity(entity);
        }

        if (this.cloner != null && entity != this.cloner.getTarget()) {
            this.setTarget(this.cloner.getTarget());
        }

    }

    private boolean teleportToEntity(Entity entity) {
        double x = entity.getX() + (this.random.nextDouble() - 0.5) * 24.0;
        double y = entity.getY() + (double)this.random.nextInt(5) + 4.0;
        double z = entity.getZ() + (this.random.nextDouble() - 0.5) * 24.0;
        boolean teleport = EntityUtil.teleportTo(this, x, y, z);
        if (teleport) {
            this.level.playSound(null, this.xo, this.yo, this.zo, MBSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(MBSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT, 1.0F, 1.0F);
            this.stopRiding();
        }

        return teleport;
    }

    protected void pushEntities() {
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public boolean addEffect(MobEffectInstance effectInstanceIn, @Nullable Entity entity) {
        return false;
    }

    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.cloner != null;
    }

    public void remove() {
        super.remove();
        this.level.broadcastEntityEvent(this, (byte)0);
        this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    public boolean is(Entity entityIn) {
        return super.is(entityIn) || entityIn instanceof MutantEndermanEntity;
    }

    public boolean saveAsPassenger(CompoundTag compound) {
        return this.cloner == null && super.saveAsPassenger(compound);
    }

    public boolean isAlliedTo(Entity entityIn) {
        return this.cloner != null && (this.cloner == entityIn || this.cloner.isAlliedTo(entityIn)) || super.isAlliedTo(entityIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return MBSoundEvents.ENTITY_ENDERSOUL_CLONE_DEATH;
    }
}
