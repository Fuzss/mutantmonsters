package fuzs.mutantmonsters.client.model;

import com.google.common.collect.ImmutableList;
import fuzs.mutantmonsters.client.animation.Animator;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class SpiderPigModel extends AgeableListModel<SpiderPig> {
    private final ModelPart head;
    private final ModelPart innerHead;
    private final ModelPart base;
    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart butt;
    private final ModelPart frontLeg1;
    private final ModelPart innerFrontLeg1;
    private final ModelPart lowerFrontLeg1;
    private final ModelPart innerLowerFrontLeg1;
    private final ModelPart frontLeg2;
    private final ModelPart innerFrontLeg2;
    private final ModelPart lowerFrontLeg2;
    private final ModelPart innerLowerFrontLeg2;
    private final ModelPart middleLeg1;
    private final ModelPart innerMiddleLeg1;
    private final ModelPart lowerMiddleLeg1;
    private final ModelPart innerLowerMiddleLeg1;
    private final ModelPart middleLeg2;
    private final ModelPart innerMiddleLeg2;
    private final ModelPart lowerMiddleLeg2;
    private final ModelPart innerLowerMiddleLeg2;
    private final ModelPart backLeg1;
    private final ModelPart innerBackLeg1;
    private final ModelPart lowerBackLeg1;
    private final ModelPart innerLowerBackLeg1;
    private final ModelPart backLeg2;
    private final ModelPart innerBackLeg2;
    private final ModelPart lowerBackLeg2;
    private final ModelPart innerLowerBackLeg2;

    public SpiderPigModel(ModelPart modelPart) {
        this.base = modelPart.getChild("base");
        this.body2 = this.base.getChild("body2");
        this.body1 = this.body2.getChild("body1");
        this.butt = this.body2.getChild("butt");
        this.head = this.body1.getChild("head");
        this.innerHead = this.head.getChild("inner_head");
        this.frontLeg1 = this.body1.getChild("front_leg1");
        this.innerFrontLeg1 = this.frontLeg1.getChild("inner_front_leg1");
        this.lowerFrontLeg1 = this.innerFrontLeg1.getChild("lower_front_leg1");
        this.innerLowerFrontLeg1 = this.lowerFrontLeg1.getChild("inner_lower_front_leg1");
        this.frontLeg2 = this.body1.getChild("front_leg2");
        this.innerFrontLeg2 = this.frontLeg2.getChild("inner_front_leg2");
        this.lowerFrontLeg2 = this.innerFrontLeg2.getChild("lower_front_leg2");
        this.innerLowerFrontLeg2 = this.lowerFrontLeg2.getChild("inner_lower_front_leg2");
        this.middleLeg1 = this.body1.getChild("middle_leg1");
        this.innerMiddleLeg1 = this.middleLeg1.getChild("inner_middle_leg1");
        this.lowerMiddleLeg1 = this.innerMiddleLeg1.getChild("lower_middle_leg1");
        this.innerLowerMiddleLeg1 = this.lowerMiddleLeg1.getChild("inner_lower_middle_leg1");
        this.middleLeg2 = this.body1.getChild("middle_leg2");
        this.innerMiddleLeg2 = this.middleLeg2.getChild("inner_middle_leg2");
        this.lowerMiddleLeg2 = this.innerMiddleLeg2.getChild("lower_middle_leg2");
        this.innerLowerMiddleLeg2 = this.lowerMiddleLeg2.getChild("inner_lower_middle_leg2");
        this.backLeg1 = this.body2.getChild("back_leg1");
        this.innerBackLeg1 = this.backLeg1.getChild("inner_back_leg1");
        this.lowerBackLeg1 = this.innerBackLeg1.getChild("lower_back_leg1");
        this.innerLowerBackLeg1 = this.lowerBackLeg1.getChild("inner_lower_back_leg1");
        this.backLeg2 = this.body2.getChild("back_leg2");
        this.innerBackLeg2 = this.backLeg2.getChild("inner_back_leg2");
        this.lowerBackLeg2 = this.innerBackLeg2.getChild("lower_back_leg2");
        this.innerLowerBackLeg2 = this.lowerBackLeg2.getChild("inner_lower_back_leg2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 14.5F, -2.0F));
        PartDefinition body2 = base.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 10.0F).texOffs(44, 16).addBox(-5.0F, -5.0F, -4.0F, 10.0F, 8.0F, 12.0F, new CubeDeformation(-0.6F)), PartPose.ZERO);
        PartDefinition body1 = body2.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(64, 0), PartPose.offset(0.0F, -1.0F, 1.5F));
        body1.addOrReplaceChild("inner_body1", CubeListBuilder.create().texOffs(64, 0).addBox(-3.5F, -3.5F, -9.0F, 7.0F, 7.0F, 9.0F), PartPose.ZERO);
        body2.addOrReplaceChild("butt", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -4.5F, 0.0F, 10.0F, 9.0F, 12.0F), PartPose.offset(0.0F, 0.0F, 7.0F));
        PartDefinition head = body1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 0.0F, -8.0F));
        PartDefinition innerHead = head.addOrReplaceChild("inner_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        innerHead.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F), PartPose.ZERO);
        PartDefinition frontLeg1 = body1.addOrReplaceChild("front_leg1", CubeListBuilder.create().texOffs(0, 37), PartPose.offset(-3.5F, 0.0F, -5.0F));
        PartDefinition innerFrontLeg1 = frontLeg1.addOrReplaceChild("inner_front_leg1", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.ZERO);
        PartDefinition lowerFrontLeg1 = innerFrontLeg1.addOrReplaceChild("lower_front_leg1", CubeListBuilder.create().texOffs(8, 37), PartPose.offset(-0.0F, 12.0F, -0.1F));
        lowerFrontLeg1.addOrReplaceChild("inner_lower_front_leg1", CubeListBuilder.create().texOffs(8, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F), PartPose.ZERO);
        PartDefinition frontLeg2 = body1.addOrReplaceChild("front_leg2", CubeListBuilder.create().texOffs(0, 37).mirror(), PartPose.offset(3.5F, 0.0F, -5.0F));
        PartDefinition innerFrontLeg2 = frontLeg2.addOrReplaceChild("inner_front_leg2", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.ZERO);
        PartDefinition lowerFrontLeg2 = innerFrontLeg2.addOrReplaceChild("lower_front_leg2", CubeListBuilder.create().texOffs(8, 37).mirror(), PartPose.offset(0.0F, 12.0F, 0.1F));
        lowerFrontLeg2.addOrReplaceChild("inner_lower_front_leg2", CubeListBuilder.create().texOffs(8, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F), PartPose.ZERO);
        PartDefinition middleLeg1 = body1.addOrReplaceChild("middle_leg1", CubeListBuilder.create().texOffs(0, 37), PartPose.offset(-3.5F, 0.0F, -3.0F));
        PartDefinition innerMiddleLeg1 = middleLeg1.addOrReplaceChild("inner_middle_leg1", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.ZERO);
        PartDefinition lowerMiddleLeg1 = innerMiddleLeg1.addOrReplaceChild("lower_middle_leg1", CubeListBuilder.create().texOffs(8, 37), PartPose.offset(0.0F, 12.0F, -0.1F));
        lowerMiddleLeg1.addOrReplaceChild("inner_lower_middle_leg1", CubeListBuilder.create().texOffs(8, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F), PartPose.ZERO);
        PartDefinition middleLeg2 = body1.addOrReplaceChild("middle_leg2", CubeListBuilder.create().texOffs(0, 37).mirror(), PartPose.offset(3.5F, 0.0F, -3.0F));
        PartDefinition innerMiddleLeg2 = middleLeg2.addOrReplaceChild("inner_middle_leg2", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.ZERO);
        PartDefinition lowerMiddleLeg2 = innerMiddleLeg2.addOrReplaceChild("lower_middle_leg2", CubeListBuilder.create().texOffs(8, 37).mirror(), PartPose.offset(0.0F, 12.0F, 0.1F));
        lowerMiddleLeg2.addOrReplaceChild("inner_lower_middle_leg2", CubeListBuilder.create().texOffs(8, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 16.0F, 2.0F), PartPose.ZERO);
        PartDefinition backLeg1 = body2.addOrReplaceChild("back_leg1", CubeListBuilder.create().texOffs(16, 37), PartPose.offset(-2.5F, 2.0F, 7.0F));
        PartDefinition innerBackLeg1 = backLeg1.addOrReplaceChild("inner_back_leg1", CubeListBuilder.create().texOffs(16, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.ZERO);
        PartDefinition lowerBackLeg1 = innerBackLeg1.addOrReplaceChild("lower_back_leg1", CubeListBuilder.create().texOffs(16, 45), PartPose.offset(0.0F, 3.0F, 0.0F));
        lowerBackLeg1.addOrReplaceChild("inner_lower_back_leg1", CubeListBuilder.create().texOffs(16, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
        PartDefinition backLeg2 = body2.addOrReplaceChild("back_leg2", CubeListBuilder.create().texOffs(32, 37).mirror(), PartPose.offset(2.5F, 2.0F, 7.0F));
        PartDefinition innerBackLeg2 = backLeg2.addOrReplaceChild("inner_back_leg2", CubeListBuilder.create().texOffs(32, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.ZERO);
        PartDefinition lowerBackLeg2 = innerBackLeg2.addOrReplaceChild("lower_back_leg2", CubeListBuilder.create().texOffs(16, 45).mirror(), PartPose.offset(0.0F, 3.0F, 0.0F));
        lowerBackLeg2.addOrReplaceChild("inner_lower_back_leg2", CubeListBuilder.create().texOffs(16, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.base);
    }

    @Override
    public void setupAnim(SpiderPig entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setAngles();
        this.animate(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private void setAngles() {
        Animator.resetAngles(this.head, this.innerHead, this.body1, this.body2, this.butt);
        Animator.resetAngles(this.frontLeg1, this.innerFrontLeg1, this.lowerFrontLeg1, this.innerLowerFrontLeg1, this.frontLeg2, this.innerFrontLeg2, this.lowerFrontLeg2, this.innerLowerFrontLeg2);
        Animator.resetAngles(this.middleLeg1, this.innerMiddleLeg1, this.lowerMiddleLeg1, this.innerLowerMiddleLeg1, this.middleLeg2, this.innerMiddleLeg2, this.lowerMiddleLeg2, this.innerLowerMiddleLeg2);
        Animator.resetAngles(this.backLeg1, this.innerBackLeg1, this.lowerBackLeg1, this.innerLowerBackLeg1, this.backLeg2, this.innerBackLeg2, this.lowerBackLeg2, this.innerLowerBackLeg2);
        this.body1.xRot += 0.3926991F;
        this.body2.xRot += -0.05235988F;
        this.butt.xRot += 0.5711987F;
        this.head.xRot += -0.3926991F;
        this.frontLeg1.xRot += -(this.body1.xRot + this.body2.xRot);
        this.frontLeg1.yRot += -1.0471976F;
        this.innerFrontLeg1.zRot += 2.0943952F;
        this.lowerFrontLeg1.zRot += -1.6534699F;
        this.frontLeg2.xRot += -(this.body1.xRot + this.body2.xRot);
        this.frontLeg2.yRot += 1.0471976F;
        this.innerFrontLeg2.zRot += -2.0943952F;
        this.lowerFrontLeg2.zRot += 1.6534699F;
        this.middleLeg1.xRot += -(this.body1.xRot + this.body2.xRot);
        this.middleLeg1.yRot += -0.31415927F;
        this.innerMiddleLeg1.zRot += 2.0399954F;
        this.lowerMiddleLeg1.zRot += -1.6534699F;
        this.middleLeg2.xRot += -(this.body1.xRot + this.body2.xRot);
        this.middleLeg2.yRot += 0.31415927F;
        this.innerMiddleLeg2.zRot += -2.0399954F;
        this.lowerMiddleLeg2.zRot += 1.6534699F;
        this.backLeg1.xRot += -0.3926991F;
        this.innerBackLeg1.zRot += 0.3926991F;
        this.lowerBackLeg1.zRot += -0.3926991F;
        this.innerLowerBackLeg1.xRot += 0.5711987F;
        this.backLeg2.xRot += -0.3926991F;
        this.innerBackLeg2.zRot += -0.3926991F;
        this.lowerBackLeg2.zRot += 0.3926991F;
        this.innerLowerBackLeg2.xRot += 0.5711987F;
    }

    private void animate(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float moveAnim = Mth.sin(limbSwing * 0.9F) * limbSwingAmount;
        float moveAnim1 = Mth.sin(limbSwing * 0.9F + 0.3F) * limbSwingAmount;
        float moveAnim1d = Mth.sin(limbSwing * 0.9F + 0.3F + 0.5F) * limbSwingAmount;
        float moveAnim2 = Mth.sin(limbSwing * 0.9F + 0.9F) * limbSwingAmount;
        float moveAnim2d = Mth.sin(limbSwing * 0.9F + 0.9F + 0.5F) * limbSwingAmount;
        float moveAnim3 = Mth.sin(limbSwing * 0.9F - 0.3F) * limbSwingAmount;
        float moveAnim3d = Mth.sin(limbSwing * 0.9F - 0.3F + 0.5F) * limbSwingAmount;
        float moveAnim4 = Mth.sin(limbSwing * 0.9F - 0.9F) * limbSwingAmount;
        float moveAnim4d = Mth.sin(limbSwing * 0.9F - 0.9F + 0.5F) * limbSwingAmount;
        float breatheAnim = Mth.sin(ageInTicks * 0.2F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        this.head.xRot += breatheAnim * 0.02F;
        this.body1.xRot += breatheAnim * 0.005F;
        this.butt.xRot += -breatheAnim * 0.015F;
        this.innerHead.xRot += facePitch;
        this.innerHead.yRot += faceYaw;
        this.innerFrontLeg1.zRot += -moveAnim1 * 3.1415927F / 6.0F;
        this.innerFrontLeg1.xRot += -0.3926991F * limbSwingAmount;
        this.lowerFrontLeg1.zRot += moveAnim1d * 3.1415927F / 6.0F + 0.2617994F * limbSwingAmount;
        this.innerFrontLeg2.zRot += moveAnim2 * 3.1415927F / 6.0F;
        this.innerFrontLeg2.xRot += -0.3926991F * limbSwingAmount;
        this.lowerFrontLeg2.zRot += -(moveAnim2d * 3.1415927F / 6.0F + 0.2617994F * limbSwingAmount);
        this.innerMiddleLeg1.zRot += -moveAnim3 * 3.1415927F / 6.0F;
        this.innerMiddleLeg1.xRot += -0.8975979F * limbSwingAmount;
        this.lowerMiddleLeg1.zRot += moveAnim3d * 3.1415927F / 6.0F + 0.3926991F * limbSwingAmount;
        this.innerMiddleLeg2.zRot += moveAnim4 * 3.1415927F / 6.0F;
        this.innerMiddleLeg2.xRot += -0.8975979F * limbSwingAmount;
        this.lowerMiddleLeg2.zRot += -(moveAnim4d * 3.1415927F / 6.0F + 0.3926991F * limbSwingAmount);
        this.backLeg1.xRot += -moveAnim4 * 3.1415927F / 5.0F + 0.2617994F * limbSwingAmount;
        this.backLeg2.xRot += -moveAnim1 * 3.1415927F / 5.0F + 0.2617994F * limbSwingAmount;
        this.body2.xRot += -moveAnim * 3.1415927F / 20.0F;
        this.head.xRot += moveAnim * 3.1415927F / 20.0F;
        if (this.attackTime > 0.0F) {
            float swingAnim = Mth.sin(this.attackTime * 3.1415927F);
            this.body1.xRot -= swingAnim * 3.1415927F / 2.5F;
            this.innerFrontLeg1.zRot += swingAnim * 3.1415927F / 5.0F;
            this.innerFrontLeg2.zRot -= swingAnim * 3.1415927F / 5.0F;
            this.innerMiddleLeg1.yRot -= swingAnim * 3.1415927F / 2.5F;
            this.innerMiddleLeg2.yRot += swingAnim * 3.1415927F / 2.5F;
            this.head.xRot += swingAnim * 3.1415927F / 3.0F;
            this.butt.xRot += -swingAnim;
        }
    }
}
