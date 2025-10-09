package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.MutantArrowModel;
import fuzs.mutantmonsters.client.model.MutantSkeletonModel;
import fuzs.mutantmonsters.client.renderer.entity.MutantArrowRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSkeletonRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.HumanoidArm;

public class MutantSkeletonCrossbowLayer extends RenderLayer<MutantSkeletonRenderState, MutantSkeletonModel> {
    public static final ResourceLocation CROSSBOW_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_crossbow.png");
    private final MutantArrowModel model;

    public MutantSkeletonCrossbowLayer(RenderLayerParent<MutantSkeletonRenderState, MutantSkeletonModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.model = new MutantArrowModel(entityModelSet.bakeLayer(ModModelLayers.MUTANT_ARROW));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantSkeletonRenderState renderState, float yRot, float xRot) {
        poseStack.pushPose();
        boolean leftHanded = renderState.mainArm == HumanoidArm.LEFT;
        this.getParentModel().translateHand(leftHanded, poseStack);
        if (leftHanded) {
            poseStack.scale(-1.0F, 1.0F, -1.0F);
        }

        nodeCollector.submitModel(this.getParentModel().getCrossbow(),
                Unit.INSTANCE,
                poseStack,
                this.getParentModel().getCrossbow().renderType(CROSSBOW_TEXTURE_LOCATION),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                renderState.outlineColor,
                null);
        poseStack.popPose();
        if (renderState.animation == MutantSkeleton.SHOOT_ANIMATION && renderState.animationTime > 10.0F
                && renderState.animationTime < 26.0F
                || renderState.animation == MutantSkeleton.MULTI_SHOT_ANIMATION && renderState.animationTime > 16.0F
                && renderState.animationTime < 24.0F) {
            poseStack.pushPose();
            this.getParentModel().translateHand(leftHanded, poseStack);
            poseStack.translate(leftHanded ? 0.2 : -0.2, 0.4, -1.8);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(1.2F, 1.2F, 1.2F);
            nodeCollector.submitModel(this.model,
                    Unit.INSTANCE,
                    poseStack,
                    this.model.renderType(MutantArrowRenderer.TEXTURE_LOCATION),
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    renderState.outlineColor,
                    null);
            poseStack.popPose();
        }
    }
}
