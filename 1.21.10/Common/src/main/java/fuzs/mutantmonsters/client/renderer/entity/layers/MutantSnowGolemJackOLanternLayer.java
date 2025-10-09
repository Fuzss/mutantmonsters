package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.MutantSnowGolemModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSnowGolemRenderState;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class MutantSnowGolemJackOLanternLayer extends RenderLayer<MutantSnowGolemRenderState, MutantSnowGolemModel> {
    public static final ResourceLocation JACK_O_LANTERN_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_snow_golem/jack_o_lantern.png");
    public static final RenderType GLOW_RENDER_TYPE = RenderType.eyes(MutantMonsters.id(
            "textures/entity/mutant_snow_golem/glow.png"));

    private final MutantSnowGolemModel headModel;

    public MutantSnowGolemJackOLanternLayer(RenderLayerParent<MutantSnowGolemRenderState, MutantSnowGolemModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.headModel = new MutantSnowGolemModel(entityModelSet.bakeLayer(ModelLayerLocations.MUTANT_SNOW_GOLEM_HEAD)).setRenderHeadOnly();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantSnowGolemRenderState renderState, float yRot, float xRot) {
        if (renderState.hasJackOLantern) {
            if (!renderState.isInvisible) {
                this.headModel.setupAnim(renderState);
                renderColoredCutoutModel(this.headModel,
                        JACK_O_LANTERN_TEXTURE_LOCATION,
                        poseStack,
                        bufferSource,
                        packedLight,
                        renderState,
                        -1);
            }

            float green = Math.max(0.0F, 0.8F + 0.05F * Mth.cos(renderState.ageInTicks * 0.15F));
            float blue = Math.max(0.0F, 0.15F + 0.2F * Mth.cos(renderState.ageInTicks * 0.1F));
            VertexConsumer vertexConsumer = bufferSource.getBuffer(GLOW_RENDER_TYPE);
            int color = ARGB.colorFromFloat(1.0F, 1.0F, green, blue);
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, color);
        }
    }
}
