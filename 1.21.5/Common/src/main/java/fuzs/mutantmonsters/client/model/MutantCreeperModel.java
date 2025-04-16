package fuzs.mutantmonsters.client.model;

import fuzs.mutantmonsters.client.renderer.entity.state.MutantCreeperRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MutantCreeperModel extends EntityModel<MutantCreeperRenderState> {
    private final ModelPart pelvis;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightForeLeg;
    private final ModelPart frontLeftForeLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backRightForeLeg;
    private final ModelPart backLeftForeLeg;

    public MutantCreeperModel(ModelPart modelPart) {
        super(modelPart);
        this.pelvis = modelPart.getChild("pelvis");
        this.body = this.pelvis.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.frontRightLeg = this.pelvis.getChild("front_right_leg");
        this.frontLeftLeg = this.pelvis.getChild("front_left_leg");
        this.frontRightForeLeg = this.frontRightLeg.getChild("front_right_fore_leg");
        this.frontLeftForeLeg = this.frontLeftLeg.getChild("front_left_fore_leg");
        this.backRightLeg = this.pelvis.getChild("back_right_leg");
        this.backLeftLeg = this.pelvis.getChild("back_left_leg");
        this.backRightForeLeg = this.backRightLeg.getChild("back_right_fore_leg");
        this.backLeftForeLeg = this.backLeftLeg.getChild("back_left_fore_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition pelvis = root.addOrReplaceChild("pelvis",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -14.0F, -4.0F, 10.0F, 14.0F, 8.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, 14.0F, -3.0F, -0.7853982F, 0.0F, 0.0F));

        PartDefinition body = pelvis.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(36, 0)
                        .addBox(-4.5F, -14.0F, -3.5F, 9.0F, 16.0F, 7.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.9424778F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create()
                        .texOffs(68, 0)
                        .addBox(-4.0F, -14.0F, -3.0F, 8.0F, 14.0F, 6.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -11.0F, 1.0F, 1.0471976F, 0.0F, 0.0F));

        neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -12.0F, 1.0F, 0.5235988F, 0.0F, 0.0F));

        PartDefinition frontRightLeg = pelvis.addOrReplaceChild("front_right_leg",
                CubeListBuilder.create()
                        .texOffs(40, 24)
                        .addBox(-3.0F, -4.0F, -14.0F, 6.0F, 4.0F, 14.0F, cubeDeformation),
                PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.31415927F, -0.7853982F, 0.0F));

        frontRightLeg.addOrReplaceChild("front_right_fore_leg",
                CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 20.0F, 8.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -4.0F, -14.0F, -0.20943952F, 0.3926991F, 0.0F));

        PartDefinition frontLeftLeg = pelvis.addOrReplaceChild("front_left_leg",
                CubeListBuilder.create()
                        .texOffs(40, 24)
                        .mirror()
                        .addBox(-3.0F, -4.0F, -14.0F, 6.0F, 4.0F, 14.0F, cubeDeformation),
                PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.31415927F, 0.7853982F, 0.0F));

        frontLeftLeg.addOrReplaceChild("front_left_fore_leg",
                CubeListBuilder.create()
                        .texOffs(96, 0)
                        .mirror()
                        .addBox(-3.5F, 0.0F, -4.0F, 7.0F, 20.0F, 8.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -4.0F, -14.0F, -0.20943952F, -0.3926991F, 0.0F));

        PartDefinition backRightLeg = pelvis.addOrReplaceChild("back_right_leg",
                CubeListBuilder.create().texOffs(0, 44).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 14.0F, cubeDeformation),
                PartPose.offsetAndRotation(2.0F, -2.0F, 4.0F, 0.9F, 0.62831855F, 0.0F));

        backRightLeg.addOrReplaceChild("back_right_fore_leg",
                CubeListBuilder.create().texOffs(80, 28).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -4.0F, 14.0F, 0.48332196F, 0.0F, 0.0F));

        PartDefinition backLeftLeg = pelvis.addOrReplaceChild("back_left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 44)
                        .mirror()
                        .addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 14.0F, cubeDeformation),
                PartPose.offsetAndRotation(-2.0F, -2.0F, 4.0F, 0.9F, -0.62831855F, 0.0F));

        backLeftLeg.addOrReplaceChild("back_left_fore_leg",
                CubeListBuilder.create()
                        .texOffs(80, 28)
                        .mirror()
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, cubeDeformation),
                PartPose.offsetAndRotation(0.0F, -4.0F, 14.0F, 0.48332196F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(MutantCreeperRenderState renderState) {
        super.setupAnim(renderState);
//        this.setupInitialAngles();
        this.animate(renderState,
                renderState.walkAnimationPos,
                renderState.walkAnimationSpeed,
                renderState.ageInTicks,
                renderState.yRot,
                renderState.xRot);
    }

    private void setupInitialAngles() {
        this.pelvis.y = 14.0F;
        this.pelvis.xRot = -0.7853982F;
        this.body.xRot = 0.9424778F;
        this.body.yRot = 0.0F;
        this.neck.xRot = 1.0471976F;
        this.head.xRot = 0.5235988F;
        this.frontRightLeg.xRot = 0.31415927F;
        this.frontRightLeg.yRot = -0.7853982F;
        this.frontRightLeg.zRot = 0.0F;
        this.frontLeftLeg.xRot = 0.31415927F;
        this.frontLeftLeg.yRot = 0.7853982F;
        this.frontLeftLeg.zRot = 0.0F;
        this.frontRightForeLeg.xRot = -0.20943952F;
        this.frontRightForeLeg.yRot = 0.3926991F;
        this.frontLeftForeLeg.xRot = -0.20943952F;
        this.frontLeftForeLeg.yRot = -0.3926991F;
        this.backRightLeg.xRot = 0.9F;
        this.backRightLeg.yRot = 0.62831855F;
        this.backRightLeg.zRot = 0.0F;
        this.backLeftLeg.xRot = 0.9F;
        this.backLeftLeg.yRot = -0.62831855F;
        this.backLeftLeg.zRot = 0.0F;
        this.backRightForeLeg.xRot = 0.48332196F;
        this.backLeftForeLeg.xRot = 0.48332196F;
    }

    private void animate(MutantCreeperRenderState renderState, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float breatheAnim = Mth.sin(ageInTicks * 0.1F);
        float walkAnim1 = (Mth.sin(limbSwing * 3.1415927F / 4.0F) + 0.4F) * limbSwingAmount;
        float walkAnim2 = (Mth.sin(limbSwing * 3.1415927F / 4.0F + 3.1415927F) + 0.4F) * limbSwingAmount;
        if (walkAnim1 < 0.0F) {
            walkAnim1 = 0.0F;
        }

        if (walkAnim2 < 0.0F) {
            walkAnim2 = 0.0F;
        }

        float walkAnim3 = Mth.sin(limbSwing * 3.1415927F / 8.0F) * limbSwingAmount;
        float walkAnim4 = (Mth.sin(limbSwing * 3.1415927F / 4.0F + 1.5707964F) + 0.4F) * limbSwingAmount;
        float walkAnim5 = (Mth.sin(limbSwing * 3.1415927F / 4.0F + 4.712389F) + 0.4F) * limbSwingAmount;
        if (walkAnim4 < 0.0F) {
            walkAnim4 = 0.0F;
        }

        if (walkAnim5 < 0.0F) {
            walkAnim5 = 0.0F;
        }

        float walkAnim6 = Mth.sin(limbSwing * 3.1415927F / 8.0F + 1.5707964F) * limbSwingAmount;
        float faceYaw = netHeadYaw / 57.295776F;
        float facePitch = headPitch / 57.295776F;
        float f6 = faceYaw / 3.0F;
        float f7 = facePitch / 3.0F;
        this.pelvis.y += Mth.sin(limbSwing * 3.1415927F / 4.0F) * limbSwingAmount * 0.5F;
        this.body.xRot += breatheAnim * 0.02F;
        this.body.xRot += f7;
        this.body.yRot += f6;
        this.neck.xRot += breatheAnim * 0.02F;
        this.neck.xRot += f7;
        this.neck.yRot = f6;
        this.head.xRot += breatheAnim * 0.02F;
        this.head.xRot += f7;
        this.head.yRot = f6;
        this.frontRightLeg.xRot -= walkAnim1 * 0.3F;
        this.frontRightLeg.yRot += walkAnim3 * 0.2F;
        this.frontRightLeg.zRot += walkAnim3 * 0.2F;
        this.frontLeftLeg.xRot -= walkAnim2 * 0.3F;
        this.frontLeftLeg.yRot -= walkAnim3 * 0.2F;
        this.frontLeftLeg.zRot -= walkAnim3 * 0.2F;
        this.backRightLeg.xRot += walkAnim5 * 0.3F;
        this.backRightLeg.yRot -= walkAnim6 * 0.2F;
        this.backRightLeg.zRot -= walkAnim6 * 0.2F;
        this.backLeftLeg.xRot += walkAnim4 * 0.3F;
        this.backLeftLeg.yRot += walkAnim6 * 0.2F;
        this.backLeftLeg.zRot += walkAnim6 * 0.2F;
        if (renderState.attackTime > 0.0F) {
            float swingAnim = Mth.sin(renderState.attackTime * 3.1415927F);
            this.body.xRot += swingAnim * 3.1415927F / 3.0F;
            this.neck.xRot -= swingAnim * 3.1415927F / 4.0F;
        }

    }
}
