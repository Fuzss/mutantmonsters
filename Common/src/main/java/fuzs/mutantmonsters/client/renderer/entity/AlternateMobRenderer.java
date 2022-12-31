package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.core.ClientAbstractions;
import fuzs.mutantmonsters.core.CommonAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;

public abstract class AlternateMobRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    public AlternateMobRenderer(EntityRendererProvider.Context context, M entityModelIn, float shadowSizeIn) {
        super(context, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (ClientAbstractions.INSTANCE.onRenderLiving$Pre(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn)) return;
        matrixStackIn.pushPose();
        this.model.attackTime = this.getAttackAnim(entityIn, partialTicks);
        boolean shouldSit = entityIn.isPassenger() && CommonAbstractions.INSTANCE.shouldRiderSit(entityIn.getVehicle());
        this.model.riding = shouldSit;
        this.model.young = entityIn.isBaby();
        float rotationYaw = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float rotationYawHead = Mth.rotLerp(partialTicks, entityIn.yHeadRotO, entityIn.yHeadRot);
        float netHeadYaw = rotationYawHead - rotationYaw;
        float ageInTicks;
        if (shouldSit && entityIn.getVehicle() instanceof LivingEntity livingentity) {
            rotationYaw = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            netHeadYaw = rotationYawHead - rotationYaw;
            ageInTicks = Mth.wrapDegrees(netHeadYaw);
            if (ageInTicks < -85.0F) {
                ageInTicks = -85.0F;
            }

            if (ageInTicks >= 85.0F) {
                ageInTicks = 85.0F;
            }

            rotationYaw = rotationYawHead - ageInTicks;
            if (ageInTicks * ageInTicks > 2500.0F) {
                rotationYaw += ageInTicks * 0.2F;
            }

            netHeadYaw = rotationYawHead - rotationYaw;
        }

        float headPitch = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        float limbSwingAmount;
        if (entityIn.getPose() == Pose.SLEEPING) {
            Direction direction = entityIn.getBedOrientation();
            if (direction != null) {
                limbSwingAmount = entityIn.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStackIn.translate((double)((float)(-direction.getStepX()) * limbSwingAmount), 0.0, (double)((float)(-direction.getStepZ()) * limbSwingAmount));
            }
        }

        ageInTicks = this.getBob(entityIn, partialTicks);
        this.setupRotations(entityIn, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0.0, -1.5010000467300415, 0.0);
        limbSwingAmount = 0.0F;
        float limbSwing = 0.0F;
        if (!shouldSit) {
            limbSwingAmount = Mth.lerp(partialTicks, entityIn.animationSpeedOld, entityIn.animationSpeed);
            limbSwing = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - partialTicks);
            if (entityIn.isBaby()) {
                limbSwing *= 3.0F;
            }

            if (limbSwingAmount > 1.0F) {
                limbSwingAmount = 1.0F;
            }
        }

        this.model.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        Minecraft minecraft = Minecraft.getInstance();
        boolean isVisible = this.isBodyVisible(entityIn);
        boolean visibleToSpectator = !isVisible && !entityIn.isInvisibleTo(minecraft.player);
        boolean isGlowing = minecraft.shouldEntityAppearGlowing(entityIn);
        RenderType rendertype = this.getRenderType(entityIn, isVisible, visibleToSpectator, isGlowing);
        if (rendertype != null && !this.hasAlternateRender(entityIn, partialTicks, matrixStackIn, bufferIn, packedLightIn)) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(rendertype);
            int packedOverlay = OverlayTexture.pack(this.getWhiteOverlayProgress(entityIn, partialTicks), this.showsHurtColor(entityIn));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, packedOverlay, 1.0F, 1.0F, 1.0F, visibleToSpectator ? 0.15F : this.getAlpha(entityIn, partialTicks));
        }

        for (RenderLayer<T, M> layer : this.layers) {
            layer.render(matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        matrixStackIn.popPose();
        ClientAbstractions.INSTANCE.getEntityDisplayName(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn, this.shouldShowName(entityIn)).ifPresent(component -> {
            this.renderNameTag(entityIn, component, matrixStackIn, bufferIn, packedLightIn);
        });

        ClientAbstractions.INSTANCE.onRenderLiving$Post(entityIn, this, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected boolean hasAlternateRender(T mob, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        return false;
    }

    protected float getAlpha(T mob, float partialTicks) {
        return 1.0F;
    }

    protected boolean showsHurtColor(T mob) {
        return mob.hurtTime > 0;
    }
}
