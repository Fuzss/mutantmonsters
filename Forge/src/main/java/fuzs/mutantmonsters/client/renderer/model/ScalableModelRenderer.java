package fuzs.mutantmonsters.client.renderer.model;

import net.minecraft.client.model.geom.ModelPart;

public class ScalableModelRenderer extends ModelPart {
    private float scale = 1.0F;

    public ScalableModelRenderer(Model model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.visible) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(this.scale, this.scale, this.scale);
            super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStackIn.popPose();
        }

    }
}
