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
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        if (ClientAbstractions.INSTANCE.onRenderLiving$Pre(entity, this, partialTicks, poseStack, multiBufferSource, packedLight)) return;
        poseStack.pushPose();
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        boolean shouldSit = entity.isPassenger() && CommonAbstractions.INSTANCE.shouldRiderSit(entity.getVehicle());
        this.model.riding = shouldSit;
        this.model.young = entity.isBaby();
        float rotationYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float rotationYawHead = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float netHeadYaw = rotationYawHead - rotationYaw;
        float ageInTicks;
        if (shouldSit && entity.getVehicle() instanceof LivingEntity livingentity) {
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

        float headPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        float limbSwingAmount;
        if (entity.getPose() == Pose.SLEEPING) {
            Direction direction = entity.getBedOrientation();
            if (direction != null) {
                limbSwingAmount = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                poseStack.translate((double)((float)(-direction.getStepX()) * limbSwingAmount), 0.0, (double)((float)(-direction.getStepZ()) * limbSwingAmount));
            }
        }

        ageInTicks = this.getBob(entity, partialTicks);
        this.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, poseStack, partialTicks);
        poseStack.translate(0.0, -1.5010000467300415, 0.0);
        limbSwingAmount = 0.0F;
        float limbSwing = 0.0F;
        if (!shouldSit) {
            limbSwingAmount = entity.walkAnimation.speed(partialTicks);
            limbSwing = entity.walkAnimation.position(partialTicks);
            if (entity.isBaby()) {
                limbSwing *= 3.0F;
            }

            if (limbSwingAmount > 1.0F) {
                limbSwingAmount = 1.0F;
            }
        }

        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        Minecraft minecraft = Minecraft.getInstance();
        boolean isVisible = this.isBodyVisible(entity);
        boolean visibleToSpectator = !isVisible && !entity.isInvisibleTo(minecraft.player);
        boolean isGlowing = minecraft.shouldEntityAppearGlowing(entity);
        RenderType rendertype = this.getRenderType(entity, isVisible, visibleToSpectator, isGlowing);
        if (rendertype != null && !this.hasAlternateRender(entity, partialTicks, poseStack, multiBufferSource, packedLight)) {
            VertexConsumer ivertexbuilder = multiBufferSource.getBuffer(rendertype);
            int packedOverlay = OverlayTexture.pack(this.getWhiteOverlayProgress(entity, partialTicks), this.showsHurtColor(entity));
            this.model.renderToBuffer(poseStack, ivertexbuilder, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, visibleToSpectator ? 0.15F : this.getAlpha(entity, partialTicks));
        }

        for (RenderLayer<T, M> layer : this.layers) {
            layer.render(poseStack, multiBufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        poseStack.popPose();
        ClientAbstractions.INSTANCE.getEntityDisplayName(entity, this, partialTicks, poseStack, multiBufferSource, packedLight, this.shouldShowName(entity)).ifPresent(component -> {
            this.renderNameTag(entity, component, poseStack, multiBufferSource, packedLight);
        });

        ClientAbstractions.INSTANCE.onRenderLiving$Post(entity, this, partialTicks, poseStack, multiBufferSource, packedLight);
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
