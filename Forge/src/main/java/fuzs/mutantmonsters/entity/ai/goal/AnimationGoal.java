package fuzs.mutantmonsters.entity.ai.goal;

import fuzs.mutantmonsters.client.animationapi.Animation;
import fuzs.mutantmonsters.client.animationapi.IAnimatedEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AnimationGoal<T extends Mob & IAnimatedEntity> extends Goal {
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
        IAnimatedEntity.sendAnimationPacket(this.mob, this.getAnimation());
    }

    public boolean canContinueToUse() {
        return this.mob.getAnimation() == this.getAnimation() && this.mob.getAnimationTick() < this.getAnimation().getDuration();
    }

    public void stop() {
        IAnimatedEntity.sendAnimationPacket(this.mob, Animation.NONE);
    }
}
