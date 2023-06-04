package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import net.minecraft.client.Minecraft;
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
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
        this.shadowRadius = 0.6F;
    }

    @Override
    public void render(ThrowableBlock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, 0.5, 0.0);
        if (entityIn.getOwnerType() != ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get()) {
            matrixStackIn.scale(-0.75F, -0.75F, 0.75F);
        } else {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entityIn.getYRot()));
        }

        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(45.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(((float)entityIn.tickCount + partialTicks) * 20.0F));
        matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(((float)entityIn.tickCount + partialTicks) * 12.0F));
        matrixStackIn.translate(-0.5, -0.5, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        this.blockRenderer.renderSingleBlock(entityIn.getBlockState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ThrowableBlock pEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
