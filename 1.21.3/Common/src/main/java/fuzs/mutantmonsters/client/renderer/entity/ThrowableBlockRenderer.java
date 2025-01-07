package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class ThrowableBlockRenderer extends EntityRenderer<ThrowableBlock> {
    private final BlockRenderDispatcher blockRenderer;

    public ThrowableBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.6F;
    }

    @Override
    public void render(ThrowableBlock throwableBlock, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(throwableBlock, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(0.0, 0.5, 0.0);
        if (throwableBlock.getOwnerType() != ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value()) {
            poseStack.scale(-0.75F, -0.75F, 0.75F);
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(throwableBlock.getYRot()));
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(((float) throwableBlock.tickCount + partialTick) * 20.0F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(((float) throwableBlock.tickCount + partialTick) * 12.0F));
        poseStack.translate(-0.5, -0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        this.blockRenderer.renderSingleBlock(throwableBlock.getBlockState(), poseStack, multiBufferSource, packedLight,
                OverlayTexture.NO_OVERLAY
        );
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ThrowableBlock throwableBlock) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
