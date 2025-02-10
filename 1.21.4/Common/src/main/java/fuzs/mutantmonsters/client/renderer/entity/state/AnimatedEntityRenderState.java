package fuzs.mutantmonsters.client.renderer.entity.state;

import fuzs.mutantmonsters.world.entity.AnimatedEntity;
import fuzs.mutantmonsters.world.entity.EntityAnimation;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;

public class AnimatedEntityRenderState extends ArmedEntityRenderState {
    public float animationTime;
    public EntityAnimation animation;

    public static <T extends LivingEntity & AnimatedEntity> void extractAnimatedEntityRenderState(T animatedEntity, AnimatedEntityRenderState reusedState, float partialTick, ItemModelResolver itemModelResolver) {
        ArmedEntityRenderState.extractArmedEntityRenderState(animatedEntity, reusedState, itemModelResolver);
        reusedState.animationTime =
                animatedEntity.getAnimationTick() > 0 ? animatedEntity.getAnimationTick() + partialTick :
                        animatedEntity.getAnimationTick();
        reusedState.animation = animatedEntity.getAnimation();
    }
}
