package fuzs.mutantmonsters.client.renderer.entity.state;

import fuzs.mutantmonsters.world.entity.animation.AnimatedEntity;
import fuzs.mutantmonsters.world.entity.animation.EntityAnimation;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;

public class AnimatedEntityRenderState extends ArmedEntityRenderState {
    public float animationTime;
    public EntityAnimation animation;

    public static <T extends LivingEntity & AnimatedEntity> void extractAnimatedEntityRenderState(T animatedEntity, AnimatedEntityRenderState renderState, ItemModelResolver itemModelResolver, float partialTick) {
        ArmedEntityRenderState.extractArmedEntityRenderState(animatedEntity,
                renderState,
                itemModelResolver,
                partialTick);
        renderState.animationTime =
                animatedEntity.getAnimationTick() > 0 ? animatedEntity.getAnimationTick() + partialTick :
                        animatedEntity.getAnimationTick();
        renderState.animation = animatedEntity.getAnimation();
    }
}
