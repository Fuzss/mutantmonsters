package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.EndersoulFragmentModel;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.state.EndersoulFragmentRenderState;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.util.Arrays;

public class EndersoulFragmentRenderer extends EntityRenderer<EndersoulFragment, EndersoulFragmentRenderState> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul_fragment.png");

    private final EndersoulFragmentModel model;

    public EndersoulFragmentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new EndersoulFragmentModel(context.bakeLayer(ModelLayerLocations.ENDERSOUL_FRAGMENT));
        this.shadowRadius = 0.3F;
        this.shadowStrength = 0.5F;
    }

    @Override
    public void extractRenderState(EndersoulFragment entity, EndersoulFragmentRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.stickRotations = Arrays.stream(entity.stickRotations)
                .map((float[] array) -> Arrays.copyOf(array, array.length))
                .toArray(float[][]::new);
    }

    @Override
    public void render(EndersoulFragmentRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(0.0F, -1.9F, 0.0F);
        poseStack.scale(1.6F, 1.6F, 1.6F);
        this.model.setupAnim(renderState);
        RenderType renderType = ModRenderType.energySwirl(TEXTURE_LOCATION,
                renderState.ageInTicks * 0.008F,
                renderState.ageInTicks * 0.008F);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        int color = ARGB.colorFromFloat(1.0F, 0.9F, 0.3F, 1.0F);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
    }

    @Override
    public EndersoulFragmentRenderState createRenderState() {
        return new EndersoulFragmentRenderState();
    }
}
