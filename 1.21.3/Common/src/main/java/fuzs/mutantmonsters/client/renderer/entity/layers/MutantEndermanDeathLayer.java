package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.renderer.entity.MutantEndermanRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class MutantEndermanDeathLayer extends RenderLayer<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> {
    private static final ResourceLocation DEATH_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_enderman/death.png");
    private static final RenderType DEATH_RENDER_TYPE = RenderType.entityDecal(MutantEndermanRenderer.TEXTURE_LOCATION);

    public MutantEndermanDeathLayer(RenderLayerParent<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        // parent model is not rendered at the same time
        if (renderState.deathTime > 80.0F) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.dragonExplosionAlpha(
                    DEATH_TEXTURE_LOCATION));
            int color = ARGB.colorFromFloat(MutantEndermanRenderer.getDeathProgress(renderState), 1.0F, 1.0F, 1.0F);
            this.getParentModel()
                    .renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            vertexConsumer = bufferSource.getBuffer(DEATH_RENDER_TYPE);
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
