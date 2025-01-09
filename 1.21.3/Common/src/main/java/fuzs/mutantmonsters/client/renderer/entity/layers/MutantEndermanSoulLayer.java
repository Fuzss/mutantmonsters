package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.EndersoulCloneRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;

public class MutantEndermanSoulLayer extends RenderLayer<MutantEndermanRenderState, EntityModel<MutantEndermanRenderState>> {
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

            poseStack.pushPose();

            float scale = this.getScale(renderState, teleport, scream);
            if (scale != 0.0F) {
                poseStack.scale(scale, scale * 0.8F, scale);
            }

            VertexConsumer vertexConsumer = bufferSource.getBuffer(ModRenderType.energySwirl(EndersoulCloneRenderer.TEXTURE_LOCATION,
                    renderState.ageInTicks * 0.008F,
                    renderState.ageInTicks * 0.008F));
            int color = ARGB.colorFromFloat(this.getAlpha(renderState), 0.9F, 0.3F, 1.0F);
            this.getParentModel()
                    .renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);

            poseStack.popPose();
        }
    }

    private float getScale(MutantEndermanRenderState renderState, boolean teleport, boolean scream) {
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

        return scale;
    }

    private float getAlpha(MutantEndermanRenderState renderState) {
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
