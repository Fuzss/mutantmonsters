package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.MutantEndermanRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;

public class MutantEndermanEyesLayer extends RenderLayer<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> {
    private static final RenderType EYES_RENDER_TYPE = ModRenderType.eyes(MutantMonsters.id(
            "textures/entity/mutant_enderman/eyes.png"));

    public MutantEndermanEyesLayer(RenderLayerParent<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (!renderState.isClone) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(EYES_RENDER_TYPE);
            float alpha =
                    renderState.deathTime > 80 ? 1.0F - MutantEndermanRenderer.getDeathProgress(renderState) : 1.0F;
            int color = ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, color);
        }
    }
}
