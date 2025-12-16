package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.MutantSnowGolemModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSnowGolemRenderState;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class MutantSnowGolemJackOLanternLayer extends RenderLayer<MutantSnowGolemRenderState, MutantSnowGolemModel> {
    public static final Identifier JACK_O_LANTERN_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_snow_golem/jack_o_lantern.png");
    public static final RenderType GLOW_RENDER_TYPE = RenderTypes.eyes(MutantMonsters.id(
            "textures/entity/mutant_snow_golem/glow.png"));

    private final MutantSnowGolemModel model;

    public MutantSnowGolemJackOLanternLayer(RenderLayerParent<MutantSnowGolemRenderState, MutantSnowGolemModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.model = new MutantSnowGolemModel(entityModelSet.bakeLayer(ModModelLayers.MUTANT_SNOW_GOLEM_HEAD));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantSnowGolemRenderState renderState, float yRot, float xRot) {
        if (renderState.hasJackOLantern) {
            coloredCutoutModelCopyLayerRender(this.model,
                    JACK_O_LANTERN_TEXTURE_LOCATION,
                    poseStack,
                    nodeCollector,
                    packedLight,
                    renderState,
                    -1,
                    renderState.outlineColor);
            float green = Math.max(0.0F, 0.8F + 0.05F * Mth.cos(renderState.ageInTicks * 0.15F));
            float blue = Math.max(0.0F, 0.15F + 0.2F * Mth.cos(renderState.ageInTicks * 0.1F));
            int color = ARGB.colorFromFloat(1.0F, 1.0F, green, blue);
            nodeCollector.submitModel(this.getParentModel(),
                    renderState,
                    poseStack,
                    GLOW_RENDER_TYPE,
                    0xF00000,
                    OverlayTexture.NO_OVERLAY,
                    color,
                    null,
                    renderState.outlineColor,
                    null);
        }
    }
}
