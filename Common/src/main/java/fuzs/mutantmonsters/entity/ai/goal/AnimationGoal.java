package fuzs.mutantmonsters.entity.ai.goal;

import fuzs.mutantmonsters.animation.Animation;
import fuzs.mutantmonsters.animation.AnimatedEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AnimationGoal<T extends Mob & AnimatedEntity> extends Goal {
    protected final T mob;

    public AnimationGoal(T mob) {
        this.mob = mob;
    }

    public boolean isInterruptable() {
        return false;
    }

    protected abstract Animation getAnimation();

    public boolean canUse() {
        return this.mob.getAnimation() == this.getAnimation();
    }

    public void start() {
        AnimatedEntity.sendAnimationPacket(this.mob, this.getAnimation());
    }

    public boolean canContinueToUse() {
        return this.mob.getAnimation() == this.getAnimation() && this.mob.getAnimationTick() < this.getAnimation().duration();
    }

    public void stop() {
        AnimatedEntity.sendAnimationPacket(this.mob, Animation.NONE);
    }
}
