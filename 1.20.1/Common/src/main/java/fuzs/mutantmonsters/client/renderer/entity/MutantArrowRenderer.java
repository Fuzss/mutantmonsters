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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MutantArrowRenderer extends EntityRenderer<MutantArrow> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonstersClient.entityTexture("mutant_arrow");

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
    public void render(MutantArrow mutantArrow, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(mutantArrow, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);

        for (int i = 0; i < mutantArrow.getClones(); ++i) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -2.35F, 0.5F);
            Vec3 deltaMovement = mutantArrow.getDeltaMovement().scale(-0.1).multiply(i, i, i);
            poseStack.translate(deltaMovement.x, deltaMovement.y, deltaMovement.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, mutantArrow.yRotO, mutantArrow.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, mutantArrow.xRotO, mutantArrow.getXRot())));
            poseStack.scale(1.2F, 1.2F, 1.2F);
            VertexConsumer vertexBuilder = bufferIn.getBuffer(this.model.renderType(this.getTextureLocation(mutantArrow)));
            this.model.renderToBuffer(poseStack, vertexBuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F - (float) i * 0.08F);
            poseStack.popPose();
        }

    }

    @Override
    public ResourceLocation getTextureLocation(MutantArrow entity) {
        return TEXTURE_LOCATION;
    }
}
