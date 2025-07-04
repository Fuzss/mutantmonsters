package fuzs.mutantmonsters.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSnowGolemRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.List;

public class MutantSnowGolemModel extends EntityModel<MutantSnowGolemRenderState> {
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
        super(modelPart);
        this.parts = modelPart.getAllParts();
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

    public static LayerDefinition createBodyLayer(int textureWidth, int textureHeight) {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition pelvis = root.addOrReplaceChild("pelvis",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0.0F, 13.5F, 5.0F));

        PartDefinition abdomen = pelvis.addOrReplaceChild("abdomen",
                CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F),
                PartPose.rotation(0.1308997F, 0.0F, 0.0F));

        PartDefinition chest = abdomen.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(24, 36).addBox(-8.0F, -12.0F, -6.0F, 16.0F, 12.0F, 12.0F),
                PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.1308997F, 0.0F, 0.0F));

        PartDefinition head = chest.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offsetAndRotation(0.0F, -12.0F, -2.0F, -0.2617994F, 0.0F, 0.0F));

        PartDefinition innerHead = head.addOrReplaceChild("inner_head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.ZERO);

        innerHead.addOrReplaceChild("head_core",
                CubeListBuilder.create()
                        .texOffs(64, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(80, 46)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)),
                PartPose.ZERO);

        PartDefinition arm1 = chest.addOrReplaceChild("arm1",
                CubeListBuilder.create().texOffs(68, 16),
                PartPose.offsetAndRotation(-9.0F, -11.0F, 0.0F, -0.31415927F, 0.0F, 0.0F));

        PartDefinition innerArm1 = arm1.addOrReplaceChild("inner_arm1",
                CubeListBuilder.create().texOffs(68, 16).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F),
                PartPose.rotation(0.0F, 0.5235988F, 0.5235988F));

        PartDefinition foreArm1 = innerArm1.addOrReplaceChild("fore_arm1",
                CubeListBuilder.create().texOffs(96, 0),
                PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, -0.5235988F, -0.2617994F));

        foreArm1.addOrReplaceChild("inner_fore_arm1",
                CubeListBuilder.create().texOffs(96, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 12.0F, 6.0F),
                PartPose.rotation(-0.5235988F, 0.0F, 0.0F));

        PartDefinition arm2 = chest.addOrReplaceChild("arm2",
                CubeListBuilder.create().texOffs(68, 16).mirror(),
                PartPose.offsetAndRotation(9.0F, -11.0F, 0.0F, -0.31415927F, 0.0F, 0.0F));

        PartDefinition innerArm2 = arm2.addOrReplaceChild("inner_arm2",
                CubeListBuilder.create().texOffs(68, 16).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 10.0F, 5.0F),
                PartPose.rotation(0.0F, -0.5235988F, -0.5235988F));

        PartDefinition foreArm2 = innerArm2.addOrReplaceChild("fore_arm2",
                CubeListBuilder.create().texOffs(96, 0).mirror(),
                PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, 0.5235988F, 0.2617994F));

        foreArm2.addOrReplaceChild("inner_fore_arm2",
                CubeListBuilder.create().texOffs(96, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 12.0F, 6.0F),
                PartPose.rotation(-0.5235988F, 0.0F, 0.0F));

        PartDefinition leg1 = pelvis.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(88, 18),
                PartPose.offsetAndRotation(-4.0F, -1.0F, -3.0F, -0.62831855F, 0.0F, 0.0F));

        PartDefinition innerLeg1 = leg1.addOrReplaceChild("inner_leg1",
                CubeListBuilder.create().texOffs(88, 18).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F),
                PartPose.rotation(0.0F, 0.0F, 0.5235988F));

        PartDefinition foreLeg1 = innerLeg1.addOrReplaceChild("fore_leg1",
                CubeListBuilder.create().texOffs(88, 32),
                PartPose.offsetAndRotation(-1.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.5235988F));

        foreLeg1.addOrReplaceChild("inner_fore_leg1",
                CubeListBuilder.create().texOffs(88, 32).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F),
                PartPose.rotation(0.69813174F, 0.0F, 0.0F));

        PartDefinition leg2 = pelvis.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(88, 18).mirror(),
                PartPose.offsetAndRotation(4.0F, -1.0F, -3.0F, -0.62831855F, 0.0F, 0.0F));

        PartDefinition innerLeg2 = leg2.addOrReplaceChild("inner_leg2",
                CubeListBuilder.create().texOffs(88, 18).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F),
                PartPose.rotation(0.0F, 0.0F, -0.5235988F));

        PartDefinition foreLeg2 = innerLeg2.addOrReplaceChild("fore_leg2",
                CubeListBuilder.create().texOffs(88, 32).mirror(),
                PartPose.offsetAndRotation(1.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.5235988F));

        foreLeg2.addOrReplaceChild("inner_fore_leg2",
                CubeListBuilder.create().texOffs(88, 32).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F),
                PartPose.rotation(0.69813174F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, textureWidth, textureHeight);
    }

    public MutantSnowGolemModel setRenderHeadOnly() {
        this.parts.forEach((ModelPart modelPart) -> modelPart.skipDraw = true);
        this.head.skipDraw = this.innerHead.skipDraw = false;
        return this;
    }

    @Override
    public void setupAnim(MutantSnowGolemRenderState renderState) {
        super.setupAnim(renderState);
        float walkAnim = Mth.sin(renderState.walkAnimationPos * 0.45F) * renderState.walkAnimationSpeed;
        float walkAnim1 =
                (Mth.cos((renderState.walkAnimationPos - 0.5F) * 0.45F) + 0.5F) * renderState.walkAnimationSpeed;
        float walkAnim2 = (Mth.cos((renderState.walkAnimationPos - 0.5F + 6.2831855F) * 0.45F) + 0.5F)
                * renderState.walkAnimationSpeed;
        float breatheAnim = Mth.sin(renderState.ageInTicks * 0.11F);
        float faceYaw = renderState.yRot * 3.1415927F / 180.0F;
        float facePitch = renderState.xRot * 3.1415927F / 180.0F;
        if (renderState.isThrowing) {
            this.animateThrow(renderState);
            float scale = 1.0F - Mth.clamp(renderState.throwingTime / 4.0F, 0.0F, 1.0F);
            walkAnim *= scale;
        }

        this.innerHead.xRot -= breatheAnim * 0.01F;
        this.chest.xRot -= breatheAnim * 0.01F;
        this.arm1.zRot += breatheAnim * 0.03F;
        this.arm2.zRot -= breatheAnim * 0.03F;
        this.innerHead.xRot += facePitch;
        this.innerHead.yRot += faceYaw;
        this.pelvis.y += Math.abs(walkAnim) * 1.5F;
        this.abdomen.xRot += renderState.walkAnimationSpeed * 0.2F;
        this.chest.yRot -= walkAnim * 0.1F;
        this.head.xRot -= renderState.walkAnimationSpeed * 0.2F;
        this.arm1.xRot -= walkAnim * 0.6F;
        this.arm2.xRot += walkAnim * 0.6F;
        this.innerForeArm1.xRot -= walkAnim * 0.2F;
        this.innerForeArm2.xRot += walkAnim * 0.2F;
        this.leg1.xRot += walkAnim1 * 1.1F;
        this.leg2.xRot += walkAnim2 * 1.1F;
        this.innerForeLeg1.xRot += walkAnim * 0.2F;
        this.innerForeLeg2.xRot -= walkAnim * 0.2F;
    }

    private void animateThrow(MutantSnowGolemRenderState renderState) {
        if (renderState.throwingTime < 7.0F) {
            float animationProgress = renderState.throwingTime / 7.0F;
            float rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += -rotationAmount * 0.2F;
            this.chest.xRot += -rotationAmount * 0.4F;
            this.arm1.xRot += -rotationAmount * 1.6F;
            this.arm1.zRot += rotationAmount * 0.8F;
            this.arm2.xRot += -rotationAmount * 1.6F;
            this.arm2.zRot += -rotationAmount * 0.8F;
        } else if (renderState.throwingTime < 10.0F) {
            float animationProgress = (renderState.throwingTime - 7.0F) / 3.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += -rotationAmount * 0.4F + 0.2F;
            this.chest.xRot += -rotationAmount * 0.6F + 0.2F;
            this.arm1.xRot += -rotationAmount * 0.8F - 0.8F;
            this.arm1.zRot += 0.8F;
            this.arm2.xRot += -rotationAmount * 0.8F - 0.8F;
            this.arm2.zRot += -0.8F;
        } else if (renderState.throwingTime < 14.0F) {
            this.abdomen.xRot += 0.2F;
            this.chest.xRot += 0.2F;
            this.arm1.xRot += -0.8F;
            this.arm1.zRot += 0.8F;
            this.arm2.xRot += -0.8F;
            this.arm2.zRot += -0.8F;
        } else if (renderState.throwingTime < 20.0F) {
            float animationProgress = (renderState.throwingTime - 14.0F) / 6.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += rotationAmount * 0.2F;
            this.chest.xRot += rotationAmount * 0.2F;
            this.arm1.xRot += -rotationAmount * 0.8F;
            this.arm1.zRot += rotationAmount * 0.8F;
            this.arm2.xRot += -rotationAmount * 0.8F;
            this.arm2.zRot += -rotationAmount * 0.8F;
        }
    }

    public void translateArm(PoseStack poseStack, boolean leftHanded) {
        this.pelvis.translateAndRotate(poseStack);
        this.abdomen.translateAndRotate(poseStack);
        this.chest.translateAndRotate(poseStack);
        if (leftHanded) {
            this.arm2.translateAndRotate(poseStack);
            this.innerArm2.translateAndRotate(poseStack);
            this.foreArm2.translateAndRotate(poseStack);
            this.innerForeArm2.translateAndRotate(poseStack);
        } else {
            this.arm1.translateAndRotate(poseStack);
            this.innerArm1.translateAndRotate(poseStack);
            this.foreArm1.translateAndRotate(poseStack);
            this.innerForeArm1.translateAndRotate(poseStack);
        }
    }
}
