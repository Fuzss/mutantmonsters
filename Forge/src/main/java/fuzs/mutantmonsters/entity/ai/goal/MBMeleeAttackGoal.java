package fuzs.mutantmonsters.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class MBMeleeAttackGoal extends MeleeAttackGoal {
    protected int maxAttackTick = 20;
    protected final double moveSpeed;
    protected int ticksUntilNextPathRecalculation;
    protected int attackTick;

    public MBMeleeAttackGoal(PathfinderMob creatureEntity, double moveSpeed) {
        super(creatureEntity, moveSpeed, true);
        this.moveSpeed = moveSpeed;
    }

    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            this.mob.setTarget(null);
            return false;
        } else {
            return true;
        }
    }

    public void start() {
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
    }

    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            if (--this.ticksUntilNextPathRecalculation <= 0) {
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                this.mob.getNavigation().moveTo(livingentity, this.moveSpeed);
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);
            this.checkAndPerformAttack(livingentity, this.mob.distanceToSqr(livingentity));
        }
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        if ((distToEnemySqr <= this.getAttackReachSqr(enemy) || this.mob.getBoundingBox().intersects(enemy.getBoundingBox())) && this.attackTick <= 0) {
            this.attackTick = this.maxAttackTick;
            this.mob.doHurtTarget(enemy);
        }

    }

    public void stop() {
        this.mob.getNavigation().stop();
        if (this.mob.getTarget() == null) {
            this.mob.setAggressive(false);
        }

    }

    public MBMeleeAttackGoal setMaxAttackTick(int max) {
        this.maxAttackTick = max;
        return this;
    }
}
