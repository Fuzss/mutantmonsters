package fuzs.mutantmonsters.forge.client.core;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.core.ClientAbstractions;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.Optional;

public class ForgeClientAbstractions implements ClientAbstractions {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> boolean onRenderLiving$Pre(T entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        return MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre<>(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void onRenderLiving$Post(T entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post<>(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public <T extends Entity> Optional<Component> getEntityDisplayName(T entity, EntityRenderer<T> renderer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, boolean shouldShowName) {
        RenderNameTagEvent evt = new RenderNameTagEvent(entity, entity.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTick);
        MinecraftForge.EVENT_BUS.post(evt);
        if (evt.getResult() != Event.Result.DENY && (evt.getResult() == Event.Result.ALLOW || shouldShowName)) {
            return Optional.of(evt.getContent());
        }
        return Optional.empty();
    }
}
