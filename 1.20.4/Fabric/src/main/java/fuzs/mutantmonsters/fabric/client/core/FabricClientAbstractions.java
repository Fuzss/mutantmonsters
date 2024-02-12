package fuzs.mutantmonsters.fabric.client.core;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.core.ClientAbstractions;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class FabricClientAbstractions implements ClientAbstractions {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> boolean onRenderLiving$Pre(T entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return FabricRendererEvents.BEFORE_RENDER_LIVING.invoker().onBeforeRenderEntity(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight).isInterrupt();
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void onRenderLiving$Post(T entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        FabricRendererEvents.AFTER_RENDER_LIVING.invoker().onAfterRenderEntity(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
    }

    @Override
    public <T extends Entity> Optional<Component> getEntityDisplayName(T entity, EntityRenderer<T> renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean shouldShowName) {
        return shouldShowName ? Optional.of(entity.getDisplayName()) : Optional.empty();
    }
}
