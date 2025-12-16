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
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class MutantEndermanDeathLayer extends RenderLayer<MutantEndermanRenderState, MutantEndermanModel> {
    private static final Identifier DEATH_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_enderman/death.png");
    private static final RenderType DEATH_RENDER_TYPE = RenderTypes.entityDecal(MutantEndermanRenderer.TEXTURE_LOCATION);

    public MutantEndermanDeathLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        // parent model is not rendered at the same time
        if (renderState.deathTime > 80.0F) {
            int color = ARGB.colorFromFloat(MutantEndermanRenderer.getDeathProgress(renderState), 1.0F, 1.0F, 1.0F);
            nodeCollector.order(0)
                    .submitModel(this.getParentModel(),
                            renderState,
                            poseStack,
                            RenderTypes.dragonExplosionAlpha(DEATH_TEXTURE_LOCATION),
                            renderState.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            color,
                            null,
                            renderState.outlineColor,
                            null);
            nodeCollector.order(1)
                    .submitModel(this.getParentModel(),
                            renderState,
                            poseStack,
                            DEATH_RENDER_TYPE,
                            renderState.lightCoords,
                            OverlayTexture.NO_OVERLAY,
                            renderState.outlineColor,
                            null);
        }
    }
}
