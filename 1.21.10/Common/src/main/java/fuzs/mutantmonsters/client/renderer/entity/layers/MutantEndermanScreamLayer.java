package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;

public class MutantEndermanScreamLayer extends EnderEnergySwirlLayer<MutantEndermanRenderState, MutantEndermanModel> {

    public MutantEndermanScreamLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (renderState.animation == MutantEnderman.SCREAM_ANIMATION) {
            super.submit(poseStack, nodeCollector, packedLight, renderState, yRot, xRot);
        }
    }

    @Override
    protected float getScale(MutantEndermanRenderState renderState) {
        if (renderState.animationTime < 40.0F) {
            return 1.2F + renderState.animationTime / 40.0F;
        } else if (renderState.animationTime < 160.0F) {
            return 2.2F;
        } else {
            return Math.max(0.0F, 2.2F - renderState.animationTime / 10.0F);
        }
    }

    @Override
    protected void scale(PoseStack poseStack, float scale) {
        poseStack.scale(scale, scale * 0.8F, scale);
    }

    @Override
    protected float getAlpha(MutantEndermanRenderState renderState) {
        if (renderState.animationTime < 40.0F) {
            return renderState.animationTime / 40.0F;
        } else if (renderState.animationTime >= 160.0F) {
            return 1.0F - renderState.animationTime / 40.0F;
        } else {
            return 1.0F;
        }
    }
}
