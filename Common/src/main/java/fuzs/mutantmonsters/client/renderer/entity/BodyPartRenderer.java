package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantSkeletonPartModel;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BodyPartRenderer extends EntityRenderer<MutantSkeletonBodyPart> {
    private final MutantSkeletonPartModel partModel;

    public BodyPartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.partModel = new MutantSkeletonPartModel(context.bakeLayer(ClientModRegistry.MUTANT_SKELETON_PART), context.bakeLayer(ClientModRegistry.MUTANT_SKELETON_PART_SPINE));
    }

    @Override
    public void render(MutantSkeletonBodyPart entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.scale(1.2F, -1.2F, -1.2F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.partModel.renderType(this.getTextureLocation(entityIn)));
        this.partModel.getPart(entityIn.getPart()).render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MutantSkeletonBodyPart entity) {
        return MutantSkeletonRenderer.TEXTURE;
    }
}
