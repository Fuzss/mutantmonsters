package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.renderer.entity.state.ThrowableBlockRenderState;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class ThrowableBlockRenderer extends EntityRenderer<ThrowableBlock, ThrowableBlockRenderState> {
    private final BlockRenderDispatcher blockRenderer;

    public ThrowableBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.6F;
    }

    @Override
    public void extractRenderState(ThrowableBlock entity, ThrowableBlockRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.blockState = entity.getBlockState();
        reusedState.ownerType = entity.getOwnerType();
        reusedState.yRot = entity.getYRot(partialTick);
    }

    @Override
    public ThrowableBlockRenderState createRenderState() {
        return new ThrowableBlockRenderState();
    }

    @Override
    public void render(ThrowableBlockRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);
        if (renderState.ownerType != ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value()) {
            poseStack.scale(-0.75F, -0.75F, 0.75F);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(renderState.ageInTicks * 20.0F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(renderState.ageInTicks * 12.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        this.blockRenderer.renderSingleBlock(renderState.blockState,
                poseStack,
                bufferSource,
                packedLight,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
