package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.services.ClientAbstractions;
import fuzs.mutantmonsters.services.CommonAbstractions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;

public abstract class AlternateMobRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    public AlternateMobRenderer(EntityRendererProvider.Context context, M entityModelIn, float shadowSizeIn) {
        super(context, entityModelIn, shadowSizeIn);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        if (ClientAbstractions.INSTANCE.onRenderLiving$Pre(entity, this, partialTick, poseStack, multiBufferSource, packedLight)) return;
        poseStack.pushPose();
        this.model.attackTime = this.getAttackAnim(entity, partialTick);
        boolean shouldSit = entity.isPassenger() && CommonAbstractions.INSTANCE.shouldRiderSit(entity.getVehicle());
        this.model.riding = shouldSit;
        this.model.young = entity.isBaby();
        float rotationYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        float rotationYawHead = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYaw = rotationYawHead - rotationYaw;
        float ageInTicks;
        if (shouldSit && entity.getVehicle() instanceof LivingEntity livingentity) {
            rotationYaw = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
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

        float headPitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        float limbSwingAmount;
        if (entity.getPose() == Pose.SLEEPING) {
            Direction direction = entity.getBedOrientation();
            if (direction != null) {
                limbSwingAmount = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                poseStack.translate((double)((float)(-direction.getStepX()) * limbSwingAmount), 0.0, (double)((float)(-direction.getStepZ()) * limbSwingAmount));
            }
        }

        float scale = entity.getScale();
        poseStack.scale(scale, scale, scale);
        ageInTicks = this.getBob(entity, partialTick);
        this.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, poseStack, partialTick);
        poseStack.translate(0.0, -1.5010000467300415, 0.0);
        limbSwingAmount = 0.0F;
        float limbSwing = 0.0F;
        if (!shouldSit) {
            limbSwingAmount = entity.walkAnimation.speed(partialTick);
            limbSwing = entity.walkAnimation.position(partialTick);
            if (entity.isBaby()) {
                limbSwing *= 3.0F;
            }

            if (limbSwingAmount > 1.0F) {
                limbSwingAmount = 1.0F;
            }
        }

        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        Minecraft minecraft = Minecraft.getInstance();
        boolean isVisible = this.isBodyVisible(entity);
        boolean visibleToSpectator = !isVisible && !entity.isInvisibleTo(minecraft.player);
        boolean isGlowing = minecraft.shouldEntityAppearGlowing(entity);
        RenderType rendertype = this.getRenderType(entity, isVisible, visibleToSpectator, isGlowing);
        if (rendertype != null && !this.hasAlternateRender(entity, partialTick, poseStack, multiBufferSource, packedLight)) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(rendertype);
            int packedOverlay = OverlayTexture.pack(this.getWhiteOverlayProgress(entity, partialTick), this.showsHurtColor(entity));
            float alpha = visibleToSpectator ? 0.15F : this.getAlpha(entity, partialTick);
            int color = FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }

        for (RenderLayer<T, M> layer : this.layers) {
            layer.render(poseStack, multiBufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
        }

        poseStack.popPose();
        ClientAbstractions.INSTANCE.getEntityDisplayName(entity, this, partialTick, poseStack, multiBufferSource, packedLight, this.shouldShowName(entity)).ifPresent(component -> {
            this.renderNameTag(entity, component, poseStack, multiBufferSource, packedLight, partialTick);
        });

        ClientAbstractions.INSTANCE.onRenderLiving$Post(entity, this, partialTick, poseStack, multiBufferSource, packedLight);
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
