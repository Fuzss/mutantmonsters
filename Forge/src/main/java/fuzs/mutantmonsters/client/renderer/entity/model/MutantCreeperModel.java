package fuzs.mutantmonsters.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MutantCreeperModel extends EntityModel<MutantCreeperEntity> {
    private final ModelPart pelvis;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart frleg;
    private final ModelPart flleg;
    private final ModelPart frforeleg;
    private final ModelPart flforeleg;
    private final ModelPart brleg;
    private final ModelPart blleg;
    private final ModelPart brforeleg;
    private final ModelPart blforeleg;

    public MutantCreeperModel(ModelPart modelPart) {
        this.pelvis = modelPart.getChild("pelvis");
        this.body = this.pelvis.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.frleg = this.pelvis.getChild("front_right_leg");
        this.flleg = this.pelvis.getChild("front_left_leg");
        this.frforeleg = this.frleg.getChild("front_right_fore_leg");
        this.flforeleg = this.flleg.getChild("front_left_fore_leg");
        this.brleg = this.pelvis.getChild("back_right_leg");
        this.blleg = this.pelvis.getChild("back_left_leg");
        this.brforeleg = this.brleg.getChild("back_right_fore_leg");
        this.blforeleg = this.blleg.getChild("back_left_fore_leg");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition pelvis = root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -14.0F, -4.0F, 10.0F, 14.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, 14.0F, -3.0F));
        PartDefinition body = pelvis.addOrReplaceChild("body", CubeListBuilder.create().texOffs(36, 0).addBox(-4.5F, -14.0F, -3.5F, 9.0F, 16.0F, 7.0F, cubeDeformation), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 0).addBox(-4.0F, -14.0F, -3.0F, 8.0F, 14.0F, 6.0F, cubeDeformation), PartPose.offset(0.0F, -11.0F, 1.0F));
        neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 22).addBox(-5.0F, -12.0F, -5.0F, 10.0F, 12.0F, 10.0F, cubeDeformation), PartPose.offset(0.0F, -12.0F, 1.0F));
        PartDefinition frontRightLeg = pelvis.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(40, 24).addBox(-3.0F, -4.0F, -14.0F, 6.0F, 4.0F, 14.0F, cubeDeformation), PartPose.offset(3.0F, 0.0F, 0.0F));
        frontRightLeg.addOrReplaceChild("front_right_fore_leg", CubeListBuilder.create().texOffs(96, 0).addBox(-3.5F, 0.0F, -4.0F, 7.0F, 20.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, -4.0F, -14.0F));
        PartDefinition frontLeftLeg = pelvis.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(40, 24).mirror().addBox(-3.0F, -4.0F, -14.0F, 6.0F, 4.0F, 14.0F, cubeDeformation), PartPose.offset(-3.0F, 0.0F, 0.0F));
        frontLeftLeg.addOrReplaceChild("front_left_fore_leg", CubeListBuilder.create().texOffs(96, 0).mirror().addBox(-3.5F, 0.0F, -4.0F, 7.0F, 20.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, -4.0F, -14.0F));
        PartDefinition backLeftLeg = pelvis.addOrReplaceChild("back_left_leg", CubeListBuilder.create().texOffs(0, 44).addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 14.0F, cubeDeformation), PartPose.offset(2.0F, -2.0F, 4.0F));
        backLeftLeg.addOrReplaceChild("back_left_fore_leg", CubeListBuilder.create().texOffs(0, 44).mirror().addBox(-2.0F, -4.0F, 0.0F, 4.0F, 4.0F, 14.0F, cubeDeformation), PartPose.offset(-2.0F, -2.0F, 4.0F));
        PartDefinition backRightLeg = pelvis.addOrReplaceChild("back_right_leg", CubeListBuilder.create().texOffs(80, 28).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, cubeDeformation), PartPose.offset(0.0F, -4.0F, 14.0F));
        backRightLeg.addOrReplaceChild("back_right_fore_leg", CubeListBuilder.create().texOffs(80, 28).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 18.0F, 6.0F, cubeDeformation), PartPose.offset(0.0F, -4.0F, 14.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.pelvis.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(MutantCreeperEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setAngles();
        this.animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private void setAngles() {
        this.pelvis.y = 14.0F;
        this.pelvis.xRot = -0.7853982F;
        this.body.xRot = 0.9424778F;
        this.body.yRot = 0.0F;
        this.neck.xRot = 1.0471976F;
        this.head.xRot = 0.5235988F;
        this.frleg.xRot = 0.31415927F;
        this.frleg.yRot = -0.7853982F;
        this.frleg.zRot = 0.0F;
        this.flleg.xRot = 0.31415927F;
        this.flleg.yRot = 0.7853982F;
        this.flleg.zRot = 0.0F;
        this.frforeleg.xRot = -0.20943952F;
        this.frforeleg.yRot = 0.3926991F;
        this.flforeleg.xRot = -0.20943952F;
        this.flforeleg.yRot = -0.3926991F;
        this.brleg.xRot = 0.9F;
        this.brleg.yRot = 0.62831855F;
        this.brleg.zRot = 0.0F;
        this.blleg.xRot = 0.9F;
        this.blleg.yRot = -0.62831855F;
        this.blleg.zRot = 0.0F;
        this.brforeleg.xRot = 0.48332196F;
        this.blforeleg.xRot = 0.48332196F;
    }

    private void animate(MutantCreeperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        this.frleg.xRot -= walkAnim1 * 0.3F;
        this.frleg.yRot += walkAnim3 * 0.2F;
        this.frleg.zRot += walkAnim3 * 0.2F;
        this.flleg.xRot -= walkAnim2 * 0.3F;
        this.flleg.yRot -= walkAnim3 * 0.2F;
        this.flleg.zRot -= walkAnim3 * 0.2F;
        this.brleg.xRot += walkAnim5 * 0.3F;
        this.brleg.yRot -= walkAnim6 * 0.2F;
        this.brleg.zRot -= walkAnim6 * 0.2F;
        this.blleg.xRot += walkAnim4 * 0.3F;
        this.blleg.yRot += walkAnim6 * 0.2F;
        this.blleg.zRot += walkAnim6 * 0.2F;
        if (this.attackTime > 0.0F) {
            float swingAnim = Mth.sin(this.attackTime * 3.1415927F);
            this.body.xRot += swingAnim * 3.1415927F / 3.0F;
            this.neck.xRot -= swingAnim * 3.1415927F / 4.0F;
        }

    }
}
