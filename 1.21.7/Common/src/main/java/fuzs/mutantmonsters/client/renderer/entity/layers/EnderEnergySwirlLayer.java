package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.EndersoulCloneRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;

public abstract class EnderEnergySwirlLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {

    protected EnderEnergySwirlLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, S renderState, float yRot, float xRot) {
        poseStack.pushPose();
        this.scale(poseStack, this.getScale(renderState));
        this.getModel().setupAnim(renderState);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderType.energySwirl(EndersoulCloneRenderer.TEXTURE_LOCATION,
                renderState.ageInTicks * 0.008F,
                renderState.ageInTicks * 0.008F));
        int color = ARGB.colorFromFloat(this.getAlpha(renderState), 0.9F, 0.3F, 1.0F);
        this.getModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
    }

    protected EntityModel<? super S> getModel() {
        return this.getParentModel();
    }

    protected float getScale(S renderState) {
        return 1.0F;
    }

    protected void scale(PoseStack poseStack, float scale) {
        // NO-OP
    }

    protected float getAlpha(S renderState) {
        return 1.0F;
    }
}