package fuzs.mutantmonsters.client.renderer.entity.state;

import fuzs.mutantmonsters.world.entity.AnimatedEntity;
import fuzs.mutantmonsters.world.entity.EntityAnimation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AnimatedEntityRenderState extends LivingEntityRenderState {
    public float animationTime;
    public EntityAnimation animation;

    public static void extractAnimatedEntityRenderState(AnimatedEntity animatedEntity, AnimatedEntityRenderState reusedState, float partialTick) {
        reusedState.animationTime =
                animatedEntity.getAnimationTick() > 0 ? animatedEntity.getAnimationTick() + partialTick :
                        animatedEntity.getAnimationTick();
        reusedState.animation = animatedEntity.getAnimation();
    }
}
