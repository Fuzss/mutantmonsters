package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;

public class MutantEndermanSoulLayer extends EndersoulLayer<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> {
    private boolean teleportAttack;

    public MutantEndermanSoulLayer(RenderLayerParent<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> renderer) {
        super(renderer);
    }

    public void setTeleportAttack(boolean teleportAttack) {
        this.teleportAttack = teleportAttack;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        boolean teleport =
                renderState.animation == MutantEnderman.TELEPORT_ANIMATION && renderState.animationTime < 10.0F;
        boolean scream = renderState.animation == MutantEnderman.SCREAM_ANIMATION;
        boolean clone = renderState.isClone;
        if (teleport || scream || clone) {
            float scale = 0.0F;
            if (teleport) {
                scale = 1.2F + renderState.animationTime / 10.0F;
                if (this.teleportAttack) {
                    scale = 2.2F - renderState.animationTime / 10.0F;
                }
            }

            if (scream) {
                if (renderState.animationTime < 40.0F) {
                    scale = 1.2F + renderState.animationTime / 40.0F;
                } else if (renderState.animationTime < 160.0F) {
                    scale = 2.2F;
                } else {
                    scale = Math.max(0.0F, 2.2F - renderState.animationTime / 10.0F);
                }
            }

            poseStack.pushPose();
            if (!clone) {
                poseStack.scale(scale, scale * 0.8F, scale);
            }

            super.render(poseStack, bufferSource, packedLight, renderState, yRot, xRot);
            poseStack.popPose();
        }
    }

    @Override
    protected float getAlpha(MutantEndermanRenderState renderState) {
        float alphaValue = 1.0F;
        if (renderState.animation == MutantEnderman.TELEPORT_ANIMATION) {
            if (!this.teleportAttack && renderState.animationTime >= 8) {
                alphaValue -= (renderState.animationTime - 8.0F) / 2.0F;
            }

            if (this.teleportAttack && renderState.animationTime < 2.0F) {
                alphaValue = renderState.animationTime / 2.0F;
            }
        }

        if (renderState.animation == MutantEnderman.SCREAM_ANIMATION) {
            if (renderState.animationTime < 40.0F) {
                alphaValue = renderState.animationTime / 40.0F;
            } else if (renderState.animationTime >= 160.0F) {
                alphaValue = 1.0F - renderState.animationTime / 40.0F;
            }
        }

        return alphaValue;
    }
}
