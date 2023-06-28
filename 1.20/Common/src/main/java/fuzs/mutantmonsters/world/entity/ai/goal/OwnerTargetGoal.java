package fuzs.mutantmonsters.world.entity.ai.goal;

import fuzs.mutantmonsters.world.entity.CreeperMinion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class OwnerTargetGoal extends TargetGoal {
    private final TamableAnimal tameable;
    private int timestamp;

    public OwnerTargetGoal(TamableAnimal tameable) {
        super(tameable, false);
        this.tameable = tameable;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if ((this.tameable.isTame() || this.tameable instanceof CreeperMinion) && !this.tameable.isOrderedToSit()) {
            LivingEntity owner = this.tameable.getOwner();
            if (owner == null) {
                return false;
            } else {
                int currentTimestamp;
                if (owner.getLastHurtByMob() != null) {
                    this.targetMob = owner.getLastHurtByMob();
                    currentTimestamp = owner.getLastHurtByMobTimestamp();
                } else if (owner instanceof Mob) {
                    this.targetMob = ((Mob)owner).getTarget();
                    currentTimestamp = owner.tickCount;
                } else {
                    this.targetMob = owner.getLastHurtMob();
                    currentTimestamp = owner.getLastHurtMobTimestamp();
                }

                boolean hasTarget = currentTimestamp != this.timestamp && this.canAttack(this.targetMob, TargetingConditions.DEFAULT) && this.tameable.wantsToAttack(this.targetMob, owner);
                if (hasTarget) {
                    this.timestamp = currentTimestamp;
                }

                return hasTarget;
            }
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        super.start();
        this.mob.setTarget(this.targetMob);
    }

    @Override
    protected boolean canAttack(LivingEntity potentialTarget, TargetingConditions targetPredicate) {
        return potentialTarget != null && targetPredicate.test(this.mob, potentialTarget);
    }
}
