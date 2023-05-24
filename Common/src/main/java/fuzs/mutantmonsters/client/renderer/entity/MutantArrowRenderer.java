package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantArrowModel;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MutantArrowRenderer extends EntityRenderer<MutantArrow> {
    public static final ResourceLocation TEXTURE = MutantMonstersClient.entityTexture("mutant_arrow");

    private final MutantArrowModel model;

    public MutantArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MutantArrowModel(context.bakeLayer(ClientModRegistry.MUTANT_ARROW));
    }

    @Override
    public boolean shouldRender(MutantArrow livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(MutantArrow entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);

        for(int i = 0; i < entityIn.getClones(); ++i) {
            matrixStackIn.pushPose();
            float scale = entityIn.getSpeed() - (float)i * 0.08F;
            double x = (entityIn.getTargetX() - entityIn.getX()) * (double)((float)entityIn.tickCount + partialTicks) * (double)scale;
            double y = (entityIn.getTargetY() - entityIn.getY()) * (double)((float)entityIn.tickCount + partialTicks) * (double)scale;
            double z = (entityIn.getTargetZ() - entityIn.getZ()) * (double)((float)entityIn.tickCount + partialTicks) * (double)scale;
            matrixStackIn.translate(x, y, z);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(entityIn.getYRot()));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(entityIn.getXRot()));
            matrixStackIn.scale(1.2F, 1.2F, 1.2F);
            VertexConsumer vertexBuilder = bufferIn.getBuffer(this.model.renderType(TEXTURE));
            this.model.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F - (float)i * 0.08F);
            matrixStackIn.popPose();
        }

    }

    @Override
    public ResourceLocation getTextureLocation(MutantArrow entity) {
        return TEXTURE;
    }
}
