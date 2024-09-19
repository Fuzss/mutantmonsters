package fuzs.mutantmonsters.world.entity.ai.goal;

import fuzs.mutantmonsters.world.entity.EntityAnimation;
import fuzs.mutantmonsters.world.entity.AnimatedEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AnimationGoal<T extends Mob & AnimatedEntity> extends Goal {
    protected final T mob;

    public AnimationGoal(T mob) {
        this.mob = mob;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    protected abstract EntityAnimation getAnimation();

    @Override
    public boolean canUse() {
        return this.mob.getAnimation() == this.getAnimation();
    }

    @Override
    public void start() {
        AnimatedEntity.sendAnimationPacket(this.mob, this.getAnimation());
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getAnimation() == this.getAnimation() && this.mob.getAnimationTick() < this.getAnimation().duration();
    }

    @Override
    public void stop() {
        AnimatedEntity.sendAnimationPacket(this.mob, EntityAnimation.NONE);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
