package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.EndersoulFragmentModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.state.EndersoulFragmentRenderState;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.util.Arrays;

public class EndersoulFragmentRenderer extends EntityRenderer<EndersoulFragment, EndersoulFragmentRenderState> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul_fragment.png");

    private final EndersoulFragmentModel model;

    public EndersoulFragmentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new EndersoulFragmentModel(context.bakeLayer(ModModelLayers.ENDERSOUL_FRAGMENT));
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
    public void submit(EndersoulFragmentRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        poseStack.pushPose();
        poseStack.translate(0.0F, -1.9F, 0.0F);
        poseStack.scale(1.6F, 1.6F, 1.6F);
        RenderType renderType = ModRenderType.energySwirl(TEXTURE_LOCATION,
                renderState.ageInTicks * 0.008F,
                renderState.ageInTicks * 0.008F);
        int color = ARGB.colorFromFloat(1.0F, 0.9F, 0.3F, 1.0F);
        nodeCollector.submitModel(this.model,
                renderState,
                poseStack,
                renderType,
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                color,
                null,
                renderState.outlineColor,
                null);
        poseStack.popPose();
    }

    @Override
    public EndersoulFragmentRenderState createRenderState() {
        return new EndersoulFragmentRenderState();
    }
}
