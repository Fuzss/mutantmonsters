package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantArrowModel;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MutantArrowRenderer extends EntityRenderer<MutantArrow> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_arrow.png");

    private final MutantArrowModel model;

    public MutantArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MutantArrowModel(context.bakeLayer(ClientModRegistry.MUTANT_ARROW));
    }

    @Override
    public boolean shouldRender(MutantArrow mutantArrow, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(MutantArrow mutantArrow, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(mutantArrow, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);

        for (int i = 0; i < mutantArrow.getClones(); ++i) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -2.35F, 0.5F);
            Vec3 deltaMovement = mutantArrow.getDeltaMovement().scale(-0.1).multiply(i, i, i);
            poseStack.translate(deltaMovement.x, deltaMovement.y, deltaMovement.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, mutantArrow.yRotO, mutantArrow.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, mutantArrow.xRotO, mutantArrow.getXRot())));
            poseStack.scale(1.2F, 1.2F, 1.2F);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(
                    this.model.renderType(this.getTextureLocation(mutantArrow)));
            int color = FastColor.ARGB32.colorFromFloat(1.0F - (float) i * 0.08F, 1.0F, 1.0F, 1.0F);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            poseStack.popPose();
        }

    }

    @Override
    public ResourceLocation getTextureLocation(MutantArrow mutantArrow) {
        return TEXTURE_LOCATION;
    }
}
