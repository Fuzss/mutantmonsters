package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class EndersoulLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul.png");

    public EndersoulLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, S renderState, float yRot, float xRot) {
        this.getParentModel().setupAnim(renderState);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                ModRenderType.energySwirl(TEXTURE_LOCATION, renderState.ageInTicks * 0.008F, renderState.ageInTicks * 0.008F));
        int color = ARGB.colorFromFloat(this.getAlpha(renderState), 0.9F, 0.3F, 1.0F);
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    protected float getAlpha(S livingEntity) {
        return 1.0F;
    }
}
