package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.EndersoulCloneRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
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
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
        poseStack.pushPose();
        this.scale(poseStack, this.getScale(renderState));
        RenderType renderType = ModRenderType.energySwirl(EndersoulCloneRenderer.TEXTURE_LOCATION,
                renderState.ageInTicks * 0.008F,
                renderState.ageInTicks * 0.008F);
        int color = ARGB.colorFromFloat(this.getAlpha(renderState), 0.9F, 0.3F, 1.0F);
        nodeCollector.submitModel(this.getModel(),
                renderState,
                poseStack,
                renderType,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                color,
                null,
                renderState.outlineColor,
                null);
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
