package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.MutantArrowModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantArrowRenderState;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Unit;
import net.minecraft.world.phys.Vec3;

public class MutantArrowRenderer extends EntityRenderer<MutantArrow, MutantArrowRenderState> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_arrow.png");

    private final MutantArrowModel model;

    public MutantArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MutantArrowModel(context.bakeLayer(ModModelLayers.MUTANT_ARROW));
    }

    @Override
    public boolean shouldRender(MutantArrow mutantArrow, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void extractRenderState(MutantArrow entity, MutantArrowRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.xRot = entity.getXRot(partialTick);
        reusedState.yRot = entity.getYRot(partialTick);
        reusedState.clones = entity.getClones();
        reusedState.deltaMovement = entity.getDeltaMovement();
    }

    @Override
    public MutantArrowRenderState createRenderState() {
        return new MutantArrowRenderState();
    }

    @Override
    public void submit(MutantArrowRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        for (int i = 0; i < renderState.clones; ++i) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -2.35F, 0.5F);
            Vec3 deltaMovement = renderState.deltaMovement.scale(-0.1).multiply(i, i, i);
            poseStack.translate(deltaMovement.x, deltaMovement.y, deltaMovement.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
            poseStack.mulPose(Axis.XP.rotationDegrees(renderState.xRot));
            poseStack.scale(1.2F, 1.2F, 1.2F);
            RenderType renderType = this.model.renderType(TEXTURE_LOCATION);
            int color = ARGB.colorFromFloat(1.0F - i * 0.08F, 1.0F, 1.0F, 1.0F);
            nodeCollector.submitModel(this.model,
                    Unit.INSTANCE,
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
    }
}
