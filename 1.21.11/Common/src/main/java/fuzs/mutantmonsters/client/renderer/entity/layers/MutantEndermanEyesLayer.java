package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.MutantEndermanRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;

public class MutantEndermanEyesLayer extends RenderLayer<MutantEndermanRenderState, MutantEndermanModel> {
    private static final RenderType EYES_RENDER_TYPE = RenderTypes.eyes(MutantMonsters.id(
            "textures/entity/mutant_enderman/eyes.png"));

    public MutantEndermanEyesLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (!renderState.isClone) {
            float alpha =
                    renderState.deathTime > 80.0F ? 1.0F - MutantEndermanRenderer.getDeathProgress(renderState) : 1.0F;
            int color = ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
            nodeCollector.submitModel(this.getParentModel(),
                    renderState,
                    poseStack,
                    EYES_RENDER_TYPE,
                    0xF00000,
                    OverlayTexture.NO_OVERLAY,
                    color,
                    null,
                    renderState.outlineColor,
                    null);
        }
    }
}
