package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.ai.goal.MutantMeleeAttackGoal;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class EndersoulClone extends Monster {
    @Nullable
    private MutantEnderman cloner;

    public EndersoulClone(EntityType<? extends EndersoulClone> entityType, Level level) {
        super(entityType, level);
        this.xpReward = Enemy.XP_REWARD_SMALL;
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MutantMeleeAttackGoal(this, 1.2));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    public void setCloner(MutantEnderman cloner) {
        this.cloner = cloner;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(cloner.getMaxHealth());
        this.setHealth(cloner.getHealth());
        if (cloner.hasCustomName()) {
            this.setCustomName(cloner.getCustomName());
            this.setCustomNameVisible(cloner.isCustomNameVisible());
        }
    }

    @Override
    public int getMaxFallDistance() {
        return 3;
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 0) {
            EntityUtil.spawnEndersoulParticles(this, this.random, 256, 1.8F);
        }
    }

    @Override
    public void aiStep() {
        this.jumping = false;
        super.aiStep();
        if (this.cloner != null && (this.cloner.isNoAi() || !this.cloner.isAlive()
                || this.cloner.level() != this.level())) {
            this.discard();
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel serverLevel, Entity entity) {
        boolean doHurtTarget = super.doHurtTarget(serverLevel, entity);
        if (this.random.nextInt(3) != 0) {
            this.teleportToEntity(entity);
        }

        if (doHurtTarget) {
            this.heal(2.0F);
        }

        this.swing(InteractionHand.MAIN_HAND);
        return doHurtTarget;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        if (this.isInvulnerableTo(serverLevel, damageSource)) {
            return false;
        } else if (damageSource.getEntity() instanceof EnderDragon) {
            return false;
        } else {
            if (this.isAlive() && this.tickCount > 10) {
                if (damageSource.getEntity() instanceof Player player) {
                    this.setLastHurtByPlayer(player, 100);
                }
                this.dropAllDeathLoot(serverLevel, damageSource);
                this.dropExperience(serverLevel, damageSource.getEntity());
                this.remove(Entity.RemovalReason.KILLED);
                this.gameEvent(GameEvent.ENTITY_DIE);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        Entity entity = this.getTarget();
        if (this.random.nextInt(10) == 0 && entity != null && (this.isInWater() || this.isPassengerOfSameVehicle(entity)
                || this.distanceToSqr(entity) > 1024.0 || !this.isPathFinding())) {
            this.teleportToEntity(entity);
        }

        if (this.cloner != null && entity != this.cloner.getTarget()) {
            this.setTarget(this.cloner.getTarget());
        }
        super.customServerAiStep(serverLevel);
    }

    private boolean teleportToEntity(Entity entity) {
        double x = entity.getX() + (this.random.nextDouble() - 0.5) * 24.0;
        double y = entity.getY() + (double) this.random.nextInt(5) + 4.0;
        double z = entity.getZ() + (this.random.nextDouble() - 0.5) * 24.0;
        boolean teleport = EntityUtil.teleportTo(this, x, y, z);
        if (teleport) {
            this.level()
                    .playSound(null,
                            this.xo,
                            this.yo,
                            this.zo,
                            ModSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT.value(),
                            this.getSoundSource(),
                            1.0F,
                            1.0F);
            this.playSound(ModSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT.value(), 1.0F, 1.0F);
            this.stopRiding();
        }

        return teleport;
    }

    @Override
    protected void pushEntities() {
        // NO-OP
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstanceIn, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.cloner != null;
    }

    @Override
    public void kill(ServerLevel serverLevel) {
        super.kill(serverLevel);
        serverLevel.broadcastEntityEvent(this, (byte) 0);
        this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public boolean is(Entity entityIn) {
        return super.is(entityIn) || entityIn instanceof MutantEnderman;
    }

    @Override
    public boolean saveAsPassenger(ValueOutput valueOutput) {
        return this.cloner == null && super.saveAsPassenger(valueOutput);
    }

    @Override
    public boolean considersEntityAsAlly(Entity entity) {
        return this.cloner != null && (this.cloner == entity || this.cloner.considersEntityAsAlly(entity))
                || super.considersEntityAsAlly(entity);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_ENDERSOUL_CLONE_DEATH_SOUND_EVENT.value();
    }
}
