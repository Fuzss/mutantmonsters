package fuzs.mutantmonsters.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.model.geom.FutureModelPart;
import fuzs.mutantmonsters.world.entity.mutant.MutantSnowGolem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.List;

public class MutantSnowGolemModel extends EntityModel<MutantSnowGolem> {
    private final List<ModelPart> parts;
    private final ModelPart pelvis;
    private final ModelPart abdomen;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart innerHead;
    private final ModelPart arm1;
    private final ModelPart innerArm1;
    private final ModelPart arm2;
    private final ModelPart innerArm2;
    private final ModelPart foreArm1;
    private final ModelPart innerForeArm1;
    private final ModelPart foreArm2;
    private final ModelPart innerForeArm2;
    private final ModelPart leg1;
    private final ModelPart innerLeg1;
    private final ModelPart leg2;
    private final ModelPart innerLeg2;
    private final ModelPart foreLeg1;
    private final ModelPart innerForeLeg1;
    private final ModelPart foreLeg2;
    private final ModelPart innerForeLeg2;
    private float partialTick;

    public MutantSnowGolemModel(ModelPart modelPart) {
        this.parts = modelPart.getAllParts().collect(ImmutableList.toImmutableList());
        this.pelvis = modelPart.getChild("pelvis");
        this.abdomen = this.pelvis.getChild("abdomen");
        this.chest = this.abdomen.getChild("chest");
        this.head = this.chest.getChild("head");
        this.innerHead = this.head.getChild("inner_head");
        this.arm1 = this.chest.getChild("arm1");
        this.innerArm1 = this.arm1.getChild("inner_arm1");
        this.arm2 = this.chest.getChild("arm2");
        this.innerArm2 = this.arm2.getChild("inner_arm2");
        this.foreArm1 = this.innerArm1.getChild("fore_arm1");
        this.innerForeArm1 = this.foreArm1.getChild("inner_fore_arm1");
        this.foreArm2 = this.innerArm2.getChild("fore_arm2");
        this.innerForeArm2 = this.foreArm2.getChild("inner_fore_arm2");
        this.leg1 = this.pelvis.getChild("leg1");
        this.innerLeg1 = this.leg1.getChild("inner_leg1");
        this.leg2 = this.pelvis.getChild("leg2");
        this.innerLeg2 = this.leg2.getChild("inner_leg2");
        this.foreLeg1 = this.innerLeg1.getChild("fore_leg1");
        this.innerForeLeg1 = this.foreLeg1.getChild("inner_fore_leg1");
        this.foreLeg2 = this.innerLeg2.getChild("fore_leg2");
        this.innerForeLeg2 = this.foreLeg2.getChild("inner_fore_leg2");
    }

    public MutantSnowGolemModel setRenderHeadOnly() {
        this.parts.forEach(t -> FutureModelPart.class.cast(t).mutantmonsters$setSkipDraw(true));
        FutureModelPart.class.cast(this.head).mutantmonsters$setSkipDraw(false);
        FutureModelPart.class.cast(this.innerHead).mutantmonsters$setSkipDraw(false);
        return this;
    }

    public static LayerDefinition createBodyLayer(int textureWidth, int textureHeight) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition pelvis = root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 13.5F, 5.0F));
        PartDefinition abdomen = pelvis.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F), PartPose.ZERO);
        PartDefinition chest = abdomen.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(24, 36).addBox(-8.0F, -12.0F, -6.0F, 16.0F, 12.0F, 12.0F), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, -12.0F, -2.0F));
        // texture size for inner head is decreased to 64, 32 in original, doesn't really work with new system
        PartDefinition innerHead = head.addOrReplaceChild("inner_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
        innerHead.addOrReplaceChild("head_core", CubeListBuilder.create().texOffs(64, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F).texOffs(80, 46).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition arm1 = chest.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(68, 16), PartPose.offset(-9.0F, -11.0F, 0.0F));
        PartDefinition innerArm1 = arm1.addOrReplaceChild("inner_arm1", CubeListBuilder.create().texOffs(68, 16).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F), PartPose.ZERO);
        PartDefinition foreArm1 = innerArm1.addOrReplaceChild("fore_arm1", CubeListBuilder.create().texOffs(96, 0), PartPose.offset(0.0F, 10.0F, 0.0F));
        foreArm1.addOrReplaceChild("inner_fore_arm1", CubeListBuilder.create().texOffs(96, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 12.0F, 6.0F), PartPose.ZERO);

        PartDefinition arm2 = chest.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(68, 16).mirror(), PartPose.offset(9.0F, -11.0F, 0.0F));
        PartDefinition innerArm2 = arm2.addOrReplaceChild("inner_arm2", CubeListBuilder.create().texOffs(68, 16).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F), PartPose.ZERO);
        PartDefinition foreArm2 = innerArm2.addOrReplaceChild("fore_arm2", CubeListBuilder.create().texOffs(96, 0).mirror(), PartPose.offset(0.0F, 10.0F, 0.0F));
        foreArm2.addOrReplaceChild("inner_fore_arm2", CubeListBuilder.create().texOffs(96, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 12.0F, 6.0F), PartPose.ZERO);

        PartDefinition leg1 = pelvis.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(88, 18), PartPose.offset(-4.0F, -1.0F, -3.0F));
        PartDefinition innerLeg1 = leg1.addOrReplaceChild("inner_leg1", CubeListBuilder.create().texOffs(88, 18).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F), PartPose.ZERO);
        PartDefinition foreLeg1 = innerLeg1.addOrReplaceChild("fore_leg1", CubeListBuilder.create().texOffs(88, 32), PartPose.offset(-1.0F, 6.0F, -0.0F));
        foreLeg1.addOrReplaceChild("inner_fore_leg1", CubeListBuilder.create().texOffs(88, 32).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F), PartPose.ZERO);

        PartDefinition leg2 = pelvis.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(88, 18).mirror(), PartPose.offset(4.0F, -1.0F, -3.0F));
        PartDefinition innerLeg2 = leg2.addOrReplaceChild("inner_leg2", CubeListBuilder.create().texOffs(88, 18).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F), PartPose.ZERO);
        PartDefinition foreLeg2 = innerLeg2.addOrReplaceChild("fore_leg2", CubeListBuilder.create().texOffs(88, 32).mirror(), PartPose.offset(1.0F, 6.0F, -0.0F));
        foreLeg2.addOrReplaceChild("inner_fore_leg2", CubeListBuilder.create().texOffs(88, 32).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, textureWidth, textureHeight);
    }

    public void copyPropertiesTo(MutantSnowGolemModel otherModel) {
        for (int i = 0; i < this.parts.size(); i++) {
            otherModel.parts.get(i).copyFrom(this.parts.get(i));
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.pelvis.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(MutantSnowGolem entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setAngles();
        this.animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private void setAngles() {
        this.pelvis.y = 13.5F;
        this.abdomen.xRot = 0.1308997F;
        this.chest.xRot = 0.1308997F;
        this.chest.yRot = 0.0F;
        this.head.xRot = -0.2617994F;
        this.innerHead.xRot = 0.0F;
        this.innerHead.yRot = 0.0F;
        this.arm1.xRot = -0.31415927F;
        this.arm1.zRot = 0.0F;
        this.innerArm1.xRot = 0.0F;
        this.innerArm1.yRot = 0.5235988F;
        this.innerArm1.zRot = 0.5235988F;
        this.foreArm1.yRot = -0.5235988F;
        this.foreArm1.zRot = -0.2617994F;
        this.innerForeArm1.xRot = -0.5235988F;
        this.arm2.xRot = -0.31415927F;
        this.arm2.zRot = 0.0F;
        this.innerArm2.xRot = 0.0F;
        this.innerArm2.yRot = -0.5235988F;
        this.innerArm2.zRot = -0.5235988F;
        this.foreArm2.yRot = 0.5235988F;
        this.foreArm2.zRot = 0.2617994F;
        this.innerForeArm2.xRot = -0.5235988F;
        this.leg1.xRot = -0.62831855F;
        this.innerLeg1.zRot = 0.5235988F;
        this.foreLeg1.zRot = -0.5235988F;
        this.innerForeLeg1.xRot = 0.69813174F;
        this.leg2.xRot = -0.62831855F;
        this.innerLeg2.zRot = -0.5235988F;
        this.foreLeg2.zRot = 0.5235988F;
        this.innerForeLeg2.xRot = 0.69813174F;
    }

    private void animate(MutantSnowGolem golem, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float temp = 0.5F;
        float walkAnim = Mth.sin(limbSwing * 0.45F) * limbSwingAmount;
        float walkAnim1 = (Mth.cos((limbSwing - temp) * 0.45F) + temp) * limbSwingAmount;
        float walkAnim2 = (Mth.cos((limbSwing - temp + 6.2831855F) * 0.45F) + temp) * limbSwingAmount;
        float breatheAnim = Mth.sin(ageInTicks * 0.11F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        if (golem.isThrowing()) {
            this.animateThrow(golem.getThrowingTick());
            float scale = 1.0F - Mth.clamp((float)golem.getThrowingTick() / 4.0F, 0.0F, 1.0F);
            walkAnim *= scale;
        }

        this.innerHead.xRot -= breatheAnim * 0.01F;
        this.chest.xRot -= breatheAnim * 0.01F;
        this.arm1.zRot += breatheAnim * 0.03F;
        this.arm2.zRot -= breatheAnim * 0.03F;
        this.innerHead.xRot += facePitch;
        this.innerHead.yRot += faceYaw;
        this.pelvis.y += Math.abs(walkAnim) * 1.5F;
        this.abdomen.xRot += limbSwingAmount * 0.2F;
        this.chest.yRot -= walkAnim * 0.1F;
        this.head.xRot -= limbSwingAmount * 0.2F;
        this.arm1.xRot -= walkAnim * 0.6F;
        this.arm2.xRot += walkAnim * 0.6F;
        this.innerForeArm1.xRot -= walkAnim * 0.2F;
        this.innerForeArm2.xRot += walkAnim * 0.2F;
        this.leg1.xRot += walkAnim1 * 1.1F;
        this.leg2.xRot += walkAnim2 * 1.1F;
        this.innerForeLeg1.xRot += walkAnim * 0.2F;
        this.innerForeLeg2.xRot -= walkAnim * 0.2F;
    }

    private void animateThrow(int fullTick) {
        float tick;
        float f;
        if (fullTick < 7) {
            tick = ((float)fullTick + this.partialTick) / 7.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            this.abdomen.xRot += -f * 0.2F;
            this.chest.xRot += -f * 0.4F;
            this.arm1.xRot += -f * 1.6F;
            this.arm1.zRot += f * 0.8F;
            this.arm2.xRot += -f * 1.6F;
            this.arm2.zRot += -f * 0.8F;
        } else if (fullTick < 10) {
            tick = ((float)(fullTick - 7) + this.partialTick) / 3.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.abdomen.xRot += -f * 0.4F + 0.2F;
            this.chest.xRot += -f * 0.6F + 0.2F;
            this.arm1.xRot += -f * 0.8F - 0.8F;
            this.arm1.zRot += 0.8F;
            this.arm2.xRot += -f * 0.8F - 0.8F;
            this.arm2.zRot += -0.8F;
        } else if (fullTick < 14) {
            this.abdomen.xRot += 0.2F;
            this.chest.xRot += 0.2F;
            this.arm1.xRot += -0.8F;
            this.arm1.zRot += 0.8F;
            this.arm2.xRot += -0.8F;
            this.arm2.zRot += -0.8F;
        } else if (fullTick < 20) {
            tick = ((float)(fullTick - 14) + this.partialTick) / 6.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.abdomen.xRot += f * 0.2F;
            this.chest.xRot += f * 0.2F;
            this.arm1.xRot += -f * 0.8F;
            this.arm1.zRot += f * 0.8F;
            this.arm2.xRot += -f * 0.8F;
            this.arm2.zRot += -f * 0.8F;
        }

    }

    @Override
    public void prepareMobModel(MutantSnowGolem entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.partialTick = partialTick;
    }

    public void translateArm(boolean leftHanded, PoseStack matrixStackIn) {
        this.pelvis.translateAndRotate(matrixStackIn);
        this.abdomen.translateAndRotate(matrixStackIn);
        this.chest.translateAndRotate(matrixStackIn);
        if (leftHanded) {
            this.arm2.translateAndRotate(matrixStackIn);
            this.innerArm2.translateAndRotate(matrixStackIn);
            this.foreArm2.translateAndRotate(matrixStackIn);
            this.innerForeArm2.translateAndRotate(matrixStackIn);
        } else {
            this.arm1.translateAndRotate(matrixStackIn);
            this.innerArm1.translateAndRotate(matrixStackIn);
            this.foreArm1.translateAndRotate(matrixStackIn);
            this.innerForeArm1.translateAndRotate(matrixStackIn);
        }

    }
}
