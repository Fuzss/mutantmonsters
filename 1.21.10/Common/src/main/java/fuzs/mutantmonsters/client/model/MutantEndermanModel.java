package fuzs.mutantmonsters.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.animation.Animator;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.Arrays;

public class MutantEndermanModel extends EntityModel<MutantEndermanRenderState> {
    private final ModelPart pelvis;
    private final ModelPart abdomen;
    private final ModelPart chest;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart mouth;
    private final Arm rightArm;
    private final Arm leftArm;
    private final Arm lowerRightArm;
    private final Arm lowerLeftArm;
    private final ModelPart legJoint1;
    private final ModelPart legJoint2;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart foreLeg1;
    private final ModelPart foreLeg2;

    public MutantEndermanModel(ModelPart modelPart) {
        super(modelPart);
        this.pelvis = modelPart.getChild("pelvis");
        this.abdomen = this.pelvis.getChild("abdomen");
        this.chest = this.abdomen.getChild("chest");
        this.neck = this.chest.getChild("neck");
        this.head = this.neck.getChild("head");
        this.mouth = this.head.getChild("mouth");
        this.rightArm = new Arm("right_", this.chest, true);
        this.leftArm = new Arm("left_", this.chest, false);
        this.lowerRightArm = new Arm("lower_right_", this.chest, true);
        this.lowerLeftArm = new Arm("lower_left_", this.chest, false);
        this.legJoint1 = this.abdomen.getChild("leg_joint1");
        this.legJoint2 = this.abdomen.getChild("leg_joint2");
        this.leg1 = this.legJoint1.getChild("leg1");
        this.leg2 = this.legJoint2.getChild("leg2");
        this.foreLeg1 = this.leg1.getChild("fore_leg1");
        this.foreLeg2 = this.leg2.getChild("fore_leg2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition pelvis = root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, -15.5F, 8.0F));
        PartDefinition abdomen = pelvis.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -2.0F, 8.0F, 10.0F, 4.0F), PartPose.ZERO);
        PartDefinition chest = abdomen.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(50, 8).addBox(-5.0F, -16.0F, -3.0F, 10.0F, 16.0F, 6.0F), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(32, 14).addBox(-1.5F, -4.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(0.0F, -15.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.5F)).texOffs(0, 14).addBox(-4.0F, 3.0F, -8.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -5.0F, 3.0F));
        head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 24).addBox(-4.0F, 3.0F, -8.0F, 8.0F, 2.0F, 8.0F), PartPose.ZERO);
        Arm.createArmLayer("right_", chest, true, false);
        Arm.createArmLayer("left_", chest, false, false);
        Arm.createArmLayer("lower_right_", chest, true, true);
        Arm.createArmLayer("lower_left_", chest, false, true);
        PartDefinition legJoint1 = abdomen.addOrReplaceChild("leg_joint1", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(-1.5F, 0.0F, 0.75F));
        PartDefinition legJoint2 = abdomen.addOrReplaceChild("leg_joint2", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(1.5F, 0.0F, 0.75F));
        PartDefinition leg1 = legJoint1.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 34).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 24.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition leg2 = legJoint2.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 34).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 24.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        leg1.addOrReplaceChild("fore_leg1", CubeListBuilder.create().texOffs(12, 34).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 24.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 23.0F, 0.0F));
        leg2.addOrReplaceChild("fore_leg2", CubeListBuilder.create().texOffs(12, 34).mirror().addBox(-1.5F, 0.0F, -1.5F, 3.0F, 24.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 23.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(MutantEndermanRenderState renderState) {
        super.setupAnim(renderState);
        // TODO remove setting initial angles like so
        this.setupInitialAngles();
        float limbSwing = renderState.walkAnimationPos;
        float limbSwingAmount = renderState.walkAnimationSpeed;
        float ageInTicks = renderState.ageInTicks;
        float netHeadYaw = renderState.yRot;
        float headPitch = renderState.xRot;
        this.animate(renderState, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        Animator.setScale(this.lowerRightArm.arm, renderState.armScale);
        Animator.setScale(this.lowerLeftArm.arm, renderState.armScale);
    }

    private void setupInitialAngles() {
        this.pelvis.y = -15.5F;
        this.abdomen.xRot = 0.31415927F;
        this.chest.xRot = 0.3926991F;
        this.chest.yRot = 0.0F;
        this.chest.zRot = 0.0F;
        this.neck.xRot = 0.19634955F;
        this.neck.zRot = 0.0F;
        this.head.xRot = -0.7853982F;
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;
        this.mouth.xRot = 0.0F;
        this.rightArm.setAngles();
        this.leftArm.setAngles();
        this.lowerRightArm.setAngles();
        this.lowerRightArm.arm.xRot += 0.1F;
        this.lowerRightArm.arm.zRot -= 0.2F;
        this.lowerLeftArm.setAngles();
        this.lowerLeftArm.arm.xRot += 0.1F;
        this.lowerLeftArm.arm.zRot += 0.2F;
        this.legJoint1.xRot = 0.0F;
        this.legJoint2.xRot = 0.0F;
        this.leg1.xRot = -0.8975979F;
        this.leg1.yRot = 0.0F;
        this.leg1.zRot = 0.2617994F;
        this.leg2.xRot = -0.8975979F;
        this.leg2.yRot = 0.0F;
        this.leg2.zRot = -0.2617994F;
        this.foreLeg1.xRot = 0.7853982F;
        this.foreLeg1.zRot = -0.1308997F;
        this.foreLeg2.xRot = 0.7853982F;
        this.foreLeg2.zRot = 0.1308997F;
    }

    private void animate(MutantEndermanRenderState renderState, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float walkSpeed = 0.3F;
        float walkAnim1 = (Mth.sin((limbSwing - 0.8F) * walkSpeed) + 0.8F) * limbSwingAmount;
        float walkAnim2 = -(Mth.sin((limbSwing + 0.8F) * walkSpeed) - 0.8F) * limbSwingAmount;
        float walkAnim3 = (Mth.sin((limbSwing + 0.8F) * walkSpeed) - 0.8F) * limbSwingAmount;
        float walkAnim4 = -(Mth.sin((limbSwing - 0.8F) * walkSpeed) + 0.8F) * limbSwingAmount;
        float[] walkAnim = new float[5];
        Arrays.fill(walkAnim, Mth.sin(limbSwing * walkSpeed) * limbSwingAmount);
        float breatheAnim = Mth.sin(ageInTicks * 0.15F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;

        for (int i = 0; i < 4; ++i) {
            if (renderState.heldBlocks[i] != null) {
                this.animateHoldBlock(renderState, i);
                walkAnim[i] *= 0.4F;
            }
        }

        if (renderState.animation == MutantEnderman.MELEE_ANIMATION) {
            this.animateMelee(renderState, renderState.activeArm);
            walkAnim[renderState.activeArm] = 0.0F;
        }

        if (renderState.animation == MutantEnderman.THROW_ANIMATION) {
            this.animateThrowBlock(renderState, renderState.activeArm);
        }

        float scale;
        if (renderState.animation == MutantEnderman.SCREAM_ANIMATION) {
            this.animateScream(renderState);
            scale = 1.0F - Mth.clamp(renderState.animationTime / 6.0F, 0.0F, 1.0F);
            faceYaw *= scale;
            facePitch *= scale;
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            walkAnim3 *= scale;
            walkAnim4 *= scale;
            Arrays.fill(walkAnim, 0.0F);
        }

        if (renderState.animation == MutantEnderman.TELESMASH_ANIMATION) {
            this.animateTeleSmash(renderState);
        }

        if (renderState.animation == MutantEnderman.DEATH_ANIMATION) {
            this.animateDeath(renderState);
            scale = 1.0F - Mth.clamp(renderState.deathTime / 6.0F, 0.0F, 1.0F);
            faceYaw *= scale;
            facePitch *= scale;
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            walkAnim3 *= scale;
            walkAnim4 *= scale;
            Arrays.fill(walkAnim, 0.0F);
        }

        this.head.xRot += facePitch * 0.5F;
        this.head.yRot += faceYaw * 0.7F;
        this.head.zRot -= faceYaw * 0.7F;
        this.neck.xRot += facePitch * 0.3F;
        this.chest.xRot += facePitch * 0.2F;
        this.mouth.xRot += breatheAnim * 0.02F + 0.02F;
        this.neck.xRot -= breatheAnim * 0.02F;
        this.rightArm.arm.zRot += breatheAnim * 0.004F;
        this.leftArm.arm.zRot -= breatheAnim * 0.004F;

        for (ModelPart modelPart : this.rightArm.finger) {
            modelPart.zRot += breatheAnim * 0.05F;
        }

        this.rightArm.thumb.zRot -= breatheAnim * 0.05F;

        for (ModelPart modelPart : this.leftArm.finger) {
            modelPart.zRot -= breatheAnim * 0.05F;
        }

        this.leftArm.thumb.zRot += breatheAnim * 0.05F;
        this.lowerRightArm.arm.zRot += breatheAnim * 0.002F;
        this.lowerLeftArm.arm.zRot -= breatheAnim * 0.002F;

        for (ModelPart modelPart : this.lowerRightArm.finger) {
            modelPart.zRot += breatheAnim * 0.02F;
        }

        this.lowerRightArm.thumb.zRot -= breatheAnim * 0.02F;

        for (ModelPart modelPart : this.lowerLeftArm.finger) {
            modelPart.zRot -= breatheAnim * 0.02F;
        }

        this.lowerLeftArm.thumb.zRot += breatheAnim * 0.02F;
        this.pelvis.y -= Math.abs(walkAnim[4]);
        this.chest.yRot -= walkAnim[4] * 0.06F;
        this.rightArm.arm.xRot -= walkAnim[0] * 0.6F;
        this.leftArm.arm.xRot += walkAnim[1] * 0.6F;
        this.rightArm.foreArm.xRot -= walkAnim[0] * 0.2F;
        this.leftArm.foreArm.xRot += walkAnim[1] * 0.2F;
        this.lowerRightArm.arm.xRot -= walkAnim[2] * 0.3F;
        this.lowerLeftArm.arm.xRot += walkAnim[3] * 0.3F;
        this.lowerRightArm.foreArm.xRot -= walkAnim[2] * 0.1F;
        this.lowerLeftArm.foreArm.xRot += walkAnim[3] * 0.1F;
        this.legJoint1.xRot += walkAnim1 * 0.6F;
        this.legJoint2.xRot += walkAnim2 * 0.6F;
        this.foreLeg1.xRot += walkAnim3 * 0.3F;
        this.foreLeg2.xRot += walkAnim4 * 0.3F;
    }

    private void animateHoldBlock(MutantEndermanRenderState renderState, int armId) {
        float animationProgress = renderState.heldBlockTicks[armId] / 10.0F;
        float rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
        if (armId == 0) {
            this.rightArm.arm.zRot += rotationAmount * 0.8F;
            this.rightArm.foreArm.zRot += rotationAmount * 0.6F;
            this.rightArm.hand.yRot += rotationAmount * 0.8F;
            this.rightArm.finger[0].xRot += -rotationAmount * 0.2F;
            this.rightArm.finger[2].xRot += rotationAmount * 0.2F;

            for (ModelPart modelPart : this.rightArm.finger) {
                modelPart.zRot += rotationAmount * 0.6F;
            }

            this.rightArm.thumb.zRot += -rotationAmount * 0.4F;
        } else if (armId == 1) {
            this.leftArm.arm.zRot += -rotationAmount * 0.8F;
            this.leftArm.foreArm.zRot += -rotationAmount * 0.6F;
            this.leftArm.hand.yRot += -rotationAmount * 0.8F;
            this.leftArm.finger[0].xRot += -rotationAmount * 0.2F;
            this.leftArm.finger[2].xRot += rotationAmount * 0.2F;

            for (ModelPart modelPart : this.leftArm.finger) {
                modelPart.zRot += -rotationAmount * 0.6F;
            }

            this.leftArm.thumb.zRot += rotationAmount * 0.4F;
        } else if (armId == 2) {
            this.lowerRightArm.arm.zRot += rotationAmount * 0.5F;
            this.lowerRightArm.foreArm.zRot += rotationAmount * 0.4F;
            this.lowerRightArm.hand.yRot += rotationAmount * 0.4F;
            this.lowerRightArm.finger[0].xRot += -rotationAmount * 0.2F;
            this.lowerRightArm.finger[2].xRot += rotationAmount * 0.2F;

            for (ModelPart modelPart : this.lowerRightArm.finger) {
                modelPart.zRot += rotationAmount * 0.6F;
            }

            this.lowerRightArm.thumb.zRot += -rotationAmount * 0.4F;
        } else if (armId == 3) {
            this.lowerLeftArm.arm.zRot += -rotationAmount * 0.5F;
            this.lowerLeftArm.foreArm.zRot += -rotationAmount * 0.4F;
            this.lowerLeftArm.hand.yRot += -rotationAmount * 0.4F;
            this.lowerLeftArm.finger[0].xRot += -rotationAmount * 0.2F;
            this.lowerLeftArm.finger[2].xRot += rotationAmount * 0.2F;

            for (ModelPart modelPart : this.lowerLeftArm.finger) {
                modelPart.zRot += -rotationAmount * 0.6F;
            }

            this.lowerLeftArm.thumb.zRot += rotationAmount * 0.4F;
        }
    }

    private void animateMelee(MutantEndermanRenderState renderState, int armId) {
        float isRightArm = (armId & 1) == 0 ? 1.0F : -1.0F;
        Arm arm = this.getArmFromId(armId);
        if (renderState.animationTime < 2.0F) {
            float animationProgress = renderState.animationTime / 2.0F;
            float rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            arm.arm.xRot += rotationAmount * 0.2F;
            arm.finger[0].zRot += rotationAmount * 0.3F * isRightArm;
            arm.finger[1].zRot += rotationAmount * 0.3F * isRightArm;
            arm.finger[2].zRot += rotationAmount * 0.3F * isRightArm;
            arm.foreFinger[0].zRot += -rotationAmount * 0.5F * isRightArm;
            arm.foreFinger[1].zRot += -rotationAmount * 0.5F * isRightArm;
            arm.foreFinger[2].zRot += -rotationAmount * 0.5F * isRightArm;
        } else if (renderState.animationTime < 5.0F) {
            float animationProgress = (renderState.animationTime - 2.0F) / 3.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            float f1 = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.chest.yRot += -f1 * 0.1F * isRightArm;
            arm.arm.xRot += rotationAmount * 1.1F - 1.1F;
            arm.foreArm.xRot += -rotationAmount * 0.4F;
            arm.finger[0].zRot += 0.3F * isRightArm;
            arm.finger[1].zRot += 0.3F * isRightArm;
            arm.finger[2].zRot += 0.3F * isRightArm;
            arm.foreFinger[0].zRot += -0.5F * isRightArm;
            arm.foreFinger[1].zRot += -0.5F * isRightArm;
            arm.foreFinger[2].zRot += -0.5F * isRightArm;
        } else if (renderState.animationTime < 6.0F) {
            this.chest.yRot += -0.1F * isRightArm;
            arm.arm.xRot += -1.1F;
            arm.foreArm.xRot += -0.4F;
            arm.finger[0].zRot += 0.3F * isRightArm;
            arm.finger[1].zRot += 0.3F * isRightArm;
            arm.finger[2].zRot += 0.3F * isRightArm;
            arm.foreFinger[0].zRot += -0.5F * isRightArm;
            arm.foreFinger[1].zRot += -0.5F * isRightArm;
            arm.foreFinger[2].zRot += -0.5F * isRightArm;
        } else if (renderState.animationTime < 10.0F) {
            float animationProgress = (renderState.animationTime - 6.0F) / 4.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            this.chest.yRot += -rotationAmount * 0.1F * isRightArm;
            arm.arm.xRot += -rotationAmount * 1.1F;
            arm.foreArm.xRot += -rotationAmount * 0.4F;
            arm.finger[0].zRot += rotationAmount * 0.3F * isRightArm;
            arm.finger[1].zRot += rotationAmount * 0.3F * isRightArm;
            arm.finger[2].zRot += rotationAmount * 0.3F * isRightArm;
            arm.foreFinger[0].zRot += -rotationAmount * 0.5F * isRightArm;
            arm.foreFinger[1].zRot += -rotationAmount * 0.5F * isRightArm;
            arm.foreFinger[2].zRot += -rotationAmount * 0.5F * isRightArm;
        }
    }

    private void animateThrowBlock(MutantEndermanRenderState renderState, int armId) {
        switch (armId) {
            case 0 -> {
                if (renderState.animationTime < 4.0F) {
                    float animationProgress = renderState.animationTime / 4.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    float sinSwingAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
                    this.rightArm.arm.xRot += -sinSwingAmount * 1.5F;
                    this.rightArm.arm.zRot += cosSwingAmount * 0.8F;
                    this.rightArm.foreArm.zRot += cosSwingAmount * 0.6F;
                    this.rightArm.hand.yRot += cosSwingAmount * 0.8F;
                    this.rightArm.finger[0].xRot += -cosSwingAmount * 0.2F;
                    this.rightArm.finger[2].xRot += cosSwingAmount * 0.2F;

                    for (ModelPart finger : this.rightArm.finger) {
                        finger.zRot += cosSwingAmount * 0.6F;
                    }

                    this.rightArm.thumb.zRot += -cosSwingAmount * 0.4F;
                } else if (renderState.animationTime < 7.0F) {
                    this.rightArm.arm.xRot += -1.5F;
                } else if (renderState.animationTime < 14.0F) {
                    float animationProgress = (renderState.animationTime - 7.0F) / 7.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    this.rightArm.arm.xRot += -cosSwingAmount * 1.5F;
                }
            }
            case 1 -> {
                if (renderState.animationTime < 4.0F) {
                    float animationProgress = renderState.animationTime / 4.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    float sinSwingAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
                    this.leftArm.arm.xRot += -sinSwingAmount * 1.5F;
                    this.leftArm.arm.zRot += -cosSwingAmount * 0.8F;
                    this.leftArm.foreArm.zRot += -cosSwingAmount * 0.6F;
                    this.leftArm.hand.yRot += -cosSwingAmount * 0.8F;
                    this.leftArm.finger[0].xRot += -cosSwingAmount * 0.2F;
                    this.leftArm.finger[2].xRot += cosSwingAmount * 0.2F;

                    for (ModelPart finger : this.leftArm.finger) {
                        finger.zRot += -cosSwingAmount * 0.6F;
                    }

                    this.leftArm.thumb.zRot += cosSwingAmount * 0.4F;
                } else if (renderState.animationTime < 7.0F) {
                    this.leftArm.arm.xRot += -1.5F;
                } else if (renderState.animationTime < 14.0F) {
                    float animationProgress = (renderState.animationTime - 7.0F) / 7.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    this.leftArm.arm.xRot += -cosSwingAmount * 1.5F;
                }
            }
            case 2 -> {
                if (renderState.animationTime < 4.0F) {
                    float animationProgress = renderState.animationTime / 4.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    float sinSwingAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
                    this.lowerRightArm.arm.xRot += -sinSwingAmount * 1.5F;
                    this.lowerRightArm.arm.zRot += cosSwingAmount * 0.5F;
                    this.lowerRightArm.foreArm.zRot += cosSwingAmount * 0.4F;
                    this.lowerRightArm.hand.yRot += cosSwingAmount * 0.4F;
                    this.lowerRightArm.finger[0].xRot += -cosSwingAmount * 0.2F;
                    this.lowerRightArm.finger[2].xRot += cosSwingAmount * 0.2F;

                    for (ModelPart finger : this.lowerRightArm.finger) {
                        finger.zRot += cosSwingAmount * 0.6F;
                    }

                    this.lowerRightArm.thumb.zRot += -cosSwingAmount * 0.4F;
                } else if (renderState.animationTime < 7.0F) {
                    this.lowerRightArm.arm.xRot += -1.5F;
                } else if (renderState.animationTime < 14.0F) {
                    float animationProgress = (renderState.animationTime - 7.0F) / 7.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    this.lowerRightArm.arm.xRot += -cosSwingAmount * 1.5F;
                }
            }
            case 3 -> {
                if (renderState.animationTime < 4.0F) {
                    float animationProgress = renderState.animationTime / 4.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    float sinSwingAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
                    this.lowerLeftArm.arm.xRot += -sinSwingAmount * 1.5F;
                    this.lowerLeftArm.arm.zRot += -cosSwingAmount * 0.5F;
                    this.lowerLeftArm.foreArm.zRot += -cosSwingAmount * 0.4F;
                    this.lowerLeftArm.hand.yRot += -cosSwingAmount * 0.4F;
                    this.lowerLeftArm.finger[0].xRot += -cosSwingAmount * 0.2F;
                    this.lowerLeftArm.finger[2].xRot += cosSwingAmount * 0.2F;

                    for (ModelPart finger : this.lowerLeftArm.finger) {
                        finger.zRot += -cosSwingAmount * 0.6F;
                    }

                    this.lowerLeftArm.thumb.zRot += cosSwingAmount * 0.4F;
                } else if (renderState.animationTime < 7.0F) {
                    this.lowerLeftArm.arm.xRot += -1.5F;
                } else if (renderState.animationTime < 14.0F) {
                    float animationProgress = (renderState.animationTime - 7.0F) / 7.0F;
                    float cosSwingAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
                    this.lowerLeftArm.arm.xRot += -cosSwingAmount * 1.5F;
                }
            }
        }
    }

    private void animateScream(MutantEndermanRenderState renderState) {
        if (renderState.animationTime < 35.0F) {
            float animationProgress = renderState.animationTime / 35.0F;
            float rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += rotationAmount * 0.3F;
            this.chest.xRot += rotationAmount * 0.4F;
            this.neck.xRot += rotationAmount * 0.2F;
            this.head.xRot += rotationAmount * 0.3F;
            this.rightArm.arm.xRot += -rotationAmount * 0.6F;
            this.rightArm.arm.yRot += rotationAmount * 0.4F;
            this.rightArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.rightArm.hand.zRot += -rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.rightArm.finger[i].zRot += rotationAmount * 0.3F;
                this.rightArm.foreFinger[i].zRot += -rotationAmount * 0.5F;
            }

            this.leftArm.arm.xRot += -rotationAmount * 0.6F;
            this.leftArm.arm.yRot += -rotationAmount * 0.4F;
            this.leftArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.leftArm.hand.zRot += rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.leftArm.finger[i].zRot += -rotationAmount * 0.3F;
                this.leftArm.foreFinger[i].zRot += rotationAmount * 0.5F;
            }

            this.lowerRightArm.arm.xRot += -rotationAmount * 0.4F;
            this.lowerRightArm.arm.yRot += rotationAmount * 0.2F;
            this.lowerRightArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.lowerRightArm.hand.zRot += -rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.lowerRightArm.finger[i].zRot += rotationAmount * 0.3F;
                this.lowerRightArm.foreFinger[i].zRot += -rotationAmount * 0.5F;
            }

            this.lowerLeftArm.arm.xRot += -rotationAmount * 0.4F;
            this.lowerLeftArm.arm.yRot += -rotationAmount * 0.2F;
            this.lowerLeftArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.lowerLeftArm.hand.zRot += rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.lowerLeftArm.finger[i].zRot += -rotationAmount * 0.3F;
                this.lowerLeftArm.foreFinger[i].zRot += rotationAmount * 0.5F;
            }
        } else if (renderState.animationTime < 40.0F) {
            this.abdomen.xRot += 0.3F;
            this.chest.xRot += 0.4F;
            this.neck.xRot += 0.2F;
            this.head.xRot += 0.3F;
            this.rightArm.arm.xRot += -0.6F;
            this.rightArm.arm.yRot += 0.4F;
            this.rightArm.foreArm.xRot += -0.8F;
            this.rightArm.hand.zRot += -0.4F;

            for (int i = 0; i < 3; ++i) {
                this.rightArm.finger[i].zRot += 0.3F;
                this.rightArm.foreFinger[i].zRot += -0.5F;
            }

            this.leftArm.arm.xRot += -0.6F;
            this.leftArm.arm.yRot += -0.4F;
            this.leftArm.foreArm.xRot += -0.8F;
            this.leftArm.hand.zRot += 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.leftArm.finger[i].zRot += -0.3F;
                this.leftArm.foreFinger[i].zRot += 0.5F;
            }

            this.lowerRightArm.arm.xRot += -0.4F;
            this.lowerRightArm.arm.yRot += 0.2F;
            this.lowerRightArm.foreArm.xRot += -0.8F;
            this.lowerRightArm.hand.zRot += -0.4F;

            for (int i = 0; i < 3; ++i) {
                this.lowerRightArm.finger[i].zRot += 0.3F;
                this.lowerRightArm.foreFinger[i].zRot += -0.5F;
            }

            this.lowerLeftArm.arm.xRot += -0.4F;
            this.lowerLeftArm.arm.yRot += -0.2F;
            this.lowerLeftArm.foreArm.xRot += -0.8F;
            this.lowerLeftArm.hand.zRot += 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.lowerLeftArm.finger[i].zRot += -0.3F;
                this.lowerLeftArm.foreFinger[i].zRot += 0.5F;
            }
        } else if (renderState.animationTime < 44.0F) {
            float animationProgress = (renderState.animationTime - 40.0F) / 4.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            float f1 = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += -rotationAmount * 0.1F + 0.4F;
            this.chest.xRot += rotationAmount * 0.1F + 0.3F;
            this.chest.zRot += f1 * 0.5F;
            this.neck.xRot += rotationAmount * 0.2F;
            this.neck.zRot += f1 * 0.2F;
            this.head.xRot += rotationAmount * 1.2F - 0.8F;
            this.head.zRot += f1 * 0.4F;
            this.mouth.xRot += f1 * 0.6F;
            this.rightArm.arm.xRot += -rotationAmount * 0.6F;
            this.rightArm.arm.yRot += 0.4F;
            this.rightArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.rightArm.hand.zRot += -rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.rightArm.finger[i].zRot += rotationAmount * 0.3F;
                this.rightArm.foreFinger[i].zRot += -rotationAmount * 0.5F;
            }

            this.leftArm.arm.xRot += -rotationAmount * 0.6F;
            this.leftArm.arm.yRot += -0.4F;
            this.leftArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.leftArm.hand.zRot += rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.leftArm.finger[i].zRot += -rotationAmount * 0.3F;
                this.leftArm.foreFinger[i].zRot += rotationAmount * 0.5F;
            }

            this.lowerRightArm.arm.xRot += -rotationAmount * 0.4F;
            this.lowerRightArm.arm.yRot += -rotationAmount * 0.1F + 0.3F;
            this.lowerRightArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.lowerRightArm.hand.zRot += -rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.lowerRightArm.finger[i].zRot += rotationAmount * 0.3F;
                this.lowerRightArm.foreFinger[i].zRot += -rotationAmount * 0.5F;
            }

            this.lowerLeftArm.arm.xRot += -rotationAmount * 0.4F;
            this.lowerLeftArm.arm.yRot += rotationAmount * 0.1F - 0.3F;
            this.lowerLeftArm.foreArm.xRot += -rotationAmount * 0.8F;
            this.lowerLeftArm.hand.zRot += rotationAmount * 0.4F;

            for (int i = 0; i < 3; ++i) {
                this.lowerLeftArm.finger[i].zRot += -rotationAmount * 0.3F;
                this.lowerLeftArm.foreFinger[i].zRot += rotationAmount * 0.5F;
            }

            this.leg1.zRot += f1 * 0.1F;
            this.leg2.zRot += -f1 * 0.1F;
        } else if (renderState.animationTime < 155.0F) {
            float animationProgress = (renderState.animationTime - 44.0F) / 111.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += 0.4F;
            this.chest.xRot += 0.3F;
            this.chest.zRot += rotationAmount - 0.5F;
            this.neck.zRot += rotationAmount * 0.4F - 0.2F;
            this.head.xRot += -0.8F;
            this.head.zRot += rotationAmount * 0.8F - 0.4F;
            this.mouth.xRot += 0.6F;
            this.rightArm.arm.yRot += 0.4F;
            this.leftArm.arm.yRot += -0.4F;
            this.lowerRightArm.arm.yRot += 0.3F;
            this.lowerLeftArm.arm.yRot += -0.3F;
            this.leg1.zRot += 0.1F;
            this.leg2.zRot += -0.1F;
        } else if (renderState.animationTime < 160.0F) {
            float animationProgress = (renderState.animationTime - 155.0F) / 5.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            this.abdomen.xRot += rotationAmount * 0.4F;
            this.chest.xRot += rotationAmount * 0.3F;
            this.chest.zRot += -rotationAmount * 0.5F;
            this.neck.zRot += -rotationAmount * 0.2F;
            this.head.xRot += -rotationAmount * 0.8F;
            this.head.zRot += -rotationAmount * 0.4F;
            this.mouth.xRot += rotationAmount * 0.6F;
            this.rightArm.arm.yRot += rotationAmount * 0.4F;
            this.leftArm.arm.yRot += -rotationAmount * 0.4F;
            this.lowerRightArm.arm.yRot += rotationAmount * 0.3F;
            this.lowerLeftArm.arm.yRot += -rotationAmount * 0.3F;
            this.leg1.zRot += rotationAmount * 0.1F;
            this.leg2.zRot += -rotationAmount * 0.1F;
        }
    }

    private void animateTeleSmash(MutantEndermanRenderState renderState) {
        if (renderState.animationTime < 18.0F) {
            float animationProgress = renderState.animationTime / 18.0F;
            float rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.chest.xRot += -rotationAmount * 0.3F;
            this.rightArm.arm.yRot += rotationAmount * 0.2F;
            this.rightArm.arm.zRot += rotationAmount * 0.8F;
            this.rightArm.hand.yRot += rotationAmount * 1.7F;
            this.leftArm.arm.yRot += -rotationAmount * 0.2F;
            this.leftArm.arm.zRot += -rotationAmount * 0.8F;
            this.leftArm.hand.yRot += -rotationAmount * 1.7F;
            this.lowerRightArm.arm.yRot += rotationAmount * 0.2F;
            this.lowerRightArm.arm.zRot += rotationAmount * 0.6F;
            this.lowerRightArm.hand.yRot += rotationAmount * 1.7F;
            this.lowerLeftArm.arm.yRot += -rotationAmount * 0.2F;
            this.lowerLeftArm.arm.zRot += -rotationAmount * 0.6F;
            this.lowerLeftArm.hand.yRot += -rotationAmount * 1.7F;
        } else if (renderState.animationTime < 20.0F) {
            float animationProgress = (renderState.animationTime - 18.0F) / 2.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            float f1 = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.chest.xRot += -rotationAmount * 0.3F;
            this.rightArm.arm.xRot += -f1 * 0.8F;
            this.rightArm.arm.yRot += 0.2F;
            this.rightArm.arm.zRot += 0.8F;
            ++this.rightArm.hand.yRot;
            this.leftArm.arm.xRot += -f1 * 0.8F;
            this.leftArm.arm.yRot += -0.2F;
            this.leftArm.arm.zRot += -0.8F;
            this.leftArm.hand.yRot += -1.7F;
            this.lowerRightArm.arm.xRot += -f1 * 0.9F;
            this.lowerRightArm.arm.yRot += 0.2F;
            this.lowerRightArm.arm.zRot += 0.6F;
            ++this.lowerRightArm.hand.yRot;
            this.lowerLeftArm.arm.xRot += -f1 * 0.9F;
            this.lowerLeftArm.arm.yRot += -0.2F;
            this.lowerLeftArm.arm.zRot += -0.6F;
            this.lowerLeftArm.hand.yRot += -1.7F;
        } else if (renderState.animationTime < 24.0F) {
            this.rightArm.arm.xRot += -0.8F;
            this.rightArm.arm.yRot += 0.2F;
            this.rightArm.arm.zRot += 0.8F;
            ++this.rightArm.hand.yRot;
            this.leftArm.arm.xRot += -0.8F;
            this.leftArm.arm.yRot += -0.2F;
            this.leftArm.arm.zRot += -0.8F;
            this.leftArm.hand.yRot += -1.7F;
            this.lowerRightArm.arm.xRot += -0.9F;
            this.lowerRightArm.arm.yRot += 0.2F;
            this.lowerRightArm.arm.zRot += 0.6F;
            ++this.lowerRightArm.hand.yRot;
            this.lowerLeftArm.arm.xRot += -0.9F;
            this.lowerLeftArm.arm.yRot += -0.2F;
            this.lowerLeftArm.arm.zRot += -0.6F;
            this.lowerLeftArm.hand.yRot += -1.7F;
        } else if (renderState.animationTime < 30.0F) {
            float animationProgress = (renderState.animationTime- 24.0F) / 6.0F;
            float rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            this.rightArm.arm.xRot += -rotationAmount * 0.8F;
            this.rightArm.arm.yRot += rotationAmount * 0.2F;
            this.rightArm.arm.zRot += rotationAmount * 0.8F;
            this.rightArm.hand.yRot += rotationAmount * 1.7F;
            this.leftArm.arm.xRot += -rotationAmount * 0.8F;
            this.leftArm.arm.yRot += -rotationAmount * 0.2F;
            this.leftArm.arm.zRot += -rotationAmount * 0.8F;
            this.leftArm.hand.yRot += -rotationAmount * 1.7F;
            this.lowerRightArm.arm.xRot += -rotationAmount * 0.9F;
            this.lowerRightArm.arm.yRot += rotationAmount * 0.2F;
            this.lowerRightArm.arm.zRot += rotationAmount * 0.6F;
            this.lowerRightArm.hand.yRot += rotationAmount * 1.7F;
            this.lowerLeftArm.arm.xRot += -rotationAmount * 0.9F;
            this.lowerLeftArm.arm.yRot += -rotationAmount * 0.2F;
            this.lowerLeftArm.arm.zRot += -rotationAmount * 0.6F;
            this.lowerLeftArm.hand.yRot += -rotationAmount * 1.7F;
        }
    }

    private void animateDeath(MutantEndermanRenderState renderState) {
        float animationProgress;
        float rotationAmount;
        if (renderState.deathTime < 80.0F) {
            animationProgress = renderState.deathTime / 80.0F;
            rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.head.xRot += rotationAmount * 0.4F;
            this.neck.xRot += rotationAmount * 0.3F;
            this.pelvis.y += -rotationAmount * 12.0F;
            this.rightArm.arm.xRot += -rotationAmount * 0.4F;
            this.rightArm.arm.yRot += rotationAmount * 0.4F;
            this.rightArm.arm.zRot += rotationAmount * 0.6F;
            this.rightArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.leftArm.arm.xRot += -rotationAmount * 0.4F;
            this.leftArm.arm.yRot += -rotationAmount * 0.2F;
            this.leftArm.arm.zRot += -rotationAmount * 0.6F;
            this.leftArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.lowerRightArm.arm.xRot += -rotationAmount * 0.4F;
            this.lowerRightArm.arm.yRot += rotationAmount * 0.4F;
            this.lowerRightArm.arm.zRot += rotationAmount * 0.6F;
            this.lowerRightArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.lowerLeftArm.arm.xRot += -rotationAmount * 0.4F;
            this.lowerLeftArm.arm.yRot += -rotationAmount * 0.2F;
            this.lowerLeftArm.arm.zRot += -rotationAmount * 0.6F;
            this.lowerLeftArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.leg1.xRot += -rotationAmount * 0.9F;
            this.leg1.yRot += rotationAmount * 0.3F;
            this.leg2.xRot += -rotationAmount * 0.9F;
            this.leg2.yRot += -rotationAmount * 0.3F;
            this.foreLeg1.xRot += rotationAmount * 1.6F;
            this.foreLeg2.xRot += rotationAmount * 1.6F;
        } else if (renderState.deathTime < 84.0F) {
            animationProgress = (renderState.deathTime - 80.0F) / 4.0F;
            rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);
            float f1 = Mth.sin(animationProgress * 3.1415927F / 2.0F);
            this.head.xRot += rotationAmount * 0.4F;
            this.mouth.xRot += f1 * 0.6F;
            this.neck.xRot += rotationAmount * 0.4F - 0.1F;
            this.chest.xRot += -f1 * 0.8F;
            this.abdomen.xRot += -f1 * 0.2F;
            this.pelvis.y += -12.0F;
            this.rightArm.arm.xRot += -rotationAmount * 0.4F;
            this.rightArm.arm.yRot += -rotationAmount * 1.4F + 1.8F;
            this.rightArm.arm.zRot += rotationAmount * 0.6F;
            this.rightArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.leftArm.arm.xRot += -rotationAmount * 0.4F;
            this.leftArm.arm.yRot += rotationAmount * 1.6F - 1.8F;
            this.leftArm.arm.zRot += -rotationAmount * 0.6F;
            this.leftArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.lowerRightArm.arm.xRot += -rotationAmount * 0.5F + 0.1F;
            this.lowerRightArm.arm.yRot += -rotationAmount * 1.1F + 1.5F;
            this.lowerRightArm.arm.zRot += rotationAmount * 0.6F;
            this.lowerRightArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.lowerLeftArm.arm.xRot += -rotationAmount * 0.5F + 0.1F;
            this.lowerLeftArm.arm.yRot += rotationAmount * 1.1F - 1.5F;
            this.lowerLeftArm.arm.zRot += -rotationAmount * 0.6F;
            this.lowerLeftArm.foreArm.xRot += -rotationAmount * 1.2F;
            this.leg1.xRot += -rotationAmount * 1.7F + 0.8F;
            this.leg1.yRot += rotationAmount * 0.3F;
            this.leg1.zRot += f1 * 0.2F;
            this.leg2.xRot += -rotationAmount * 1.7F + 0.8F;
            this.leg2.yRot += -rotationAmount * 0.3F;
            this.leg2.zRot += -f1 * 0.2F;
            this.foreLeg1.xRot += rotationAmount * 1.6F;
            this.foreLeg2.xRot += rotationAmount * 1.6F;
        } else {
            this.mouth.xRot += 0.6F;
            this.neck.xRot += -0.1F;
            this.chest.xRot += -0.8F;
            this.abdomen.xRot += -0.2F;
            this.pelvis.y += -12.0F;
            ++this.rightArm.arm.yRot;
            this.leftArm.arm.yRot += -1.8F;
            this.lowerRightArm.arm.xRot += 0.1F;
            ++this.lowerRightArm.arm.yRot;
            this.lowerLeftArm.arm.xRot += 0.1F;
            this.lowerLeftArm.arm.yRot += -1.5F;
            this.leg1.xRot += 0.8F;
            this.leg1.zRot += 0.2F;
            this.leg2.xRot += 0.8F;
            this.leg2.zRot += -0.2F;
        }
    }

    private Arm getArmFromId(int id) {
        return id == 0 ? this.rightArm : (id == 1 ? this.leftArm : (id == 2 ? this.lowerRightArm : this.lowerLeftArm));
    }

    public void translateRotateArm(PoseStack poseStack, int id) {
        this.pelvis.translateAndRotate(poseStack);
        this.abdomen.translateAndRotate(poseStack);
        this.chest.translateAndRotate(poseStack);
        this.getArmFromId(id).translateRotate(poseStack);
    }

    static class Arm {
        private final ModelPart arm;
        private final ModelPart foreArm;
        private final ModelPart hand;
        private final ModelPart[] finger = new ModelPart[3];
        private final ModelPart[] foreFinger = new ModelPart[3];
        private final ModelPart thumb;
        private final boolean right;

        private Arm(String prefix, ModelPart modelPart, boolean right) {
            this.right = right;
            this.arm = modelPart.getChild(prefix + "arm");
            this.foreArm = this.arm.getChild(prefix + "fore_arm");
            this.hand = this.foreArm.getChild(prefix + "hand");
            for (int i = 0; i < 3; i++) {
                this.finger[i] = this.hand.getChild(prefix + "finger" + i);
                this.foreFinger[i] = this.finger[i].getChild(prefix + "fore_finger" + i);
            }
            this.thumb = this.hand.getChild(prefix + "thumb");
        }

        static void createArmLayer(String prefix, PartDefinition root, boolean right, boolean lower) {
            PartDefinition arm = root.addOrReplaceChild(prefix + "arm", CubeListBuilder.create().texOffs(92, 0).addBox(-1.5F, lower ? 6.0F : 0.0F, -1.5F, 3.0F, 22.0F, 3.0F, new CubeDeformation(0.1F)).mirror(!right), PartPose.offset(right ? -4.0F : 4.0F, -14.0F, 0.0F));
            PartDefinition foreArm = arm.addOrReplaceChild(prefix + "fore_arm", CubeListBuilder.create().texOffs(104, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 18.0F, 3.0F).mirror(!right), PartPose.offset(0.0F, 21.0F, 1.0F));
            PartDefinition hand = foreArm.addOrReplaceChild(prefix + "hand", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 17.5F, 0.0F));
            for (int i = 0; i < 3; ++i) {
                PartPose partPose;
                if (i == 0) {
                    partPose = PartPose.offset(right ? -0.5F : 0.5F, 0.0F, -1.0F);
                } else if (i == 1) {
                    partPose = PartPose.offset(right ? -0.5F : 0.5F, 0.0F, 0.0F);
                } else {
                    partPose = PartPose.offset(right ? -0.5F : 0.5F, 0.0F, 1.0F);
                }
                PartDefinition finger = hand.addOrReplaceChild(prefix + "finger" + i, CubeListBuilder.create().texOffs(76, 0).mirror(!right).addBox(-0.5F, 0.0F, -0.5F, 1.0F, i == 1 ? 6.0F : 5.0F, 1.0F, new CubeDeformation(0.6F)), partPose);
                finger.addOrReplaceChild(prefix + "fore_finger" + i, CubeListBuilder.create().texOffs(76, 0).mirror(!right).addBox(-0.5F, 0.0F, -0.5F, 1.0F, i == 1 ? 6.0F : 5.0F, 1.0F, new CubeDeformation(0.6F - 0.01F)), PartPose.offset(0.0F, 0.5F + (float) (i == 1 ? 6 : 5), 0.0F));
            }
            hand.addOrReplaceChild(prefix + "thumb", CubeListBuilder.create().texOffs(76, 0).mirror(right).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.6F)), PartPose.offset(right ? 0.5F : -0.5F, 0.0F, -0.5F));
        }

        private void setAngles() {
            Animator.resetAngles(this.arm);
            Animator.resetAngles(this.foreArm);
            Animator.resetAngles(this.hand);

            for (int i = 0; i < this.finger.length; ++i) {
                Animator.resetAngles(this.finger[i]);
                Animator.resetAngles(this.foreFinger[i]);
            }

            Animator.resetAngles(this.thumb);
            if (this.right) {
                this.arm.xRot = -0.5235988F;
                this.arm.zRot = 0.5235988F;
                this.foreArm.xRot = -0.62831855F;
                this.hand.yRot = -0.3926991F;
                this.finger[0].xRot = -0.2617994F;
                this.finger[1].zRot = 0.17453294F;
                this.finger[2].xRot = 0.2617994F;
                this.foreFinger[0].zRot = -0.2617994F;
                this.foreFinger[1].zRot = -0.3926991F;
                this.foreFinger[2].zRot = -0.2617994F;
                this.thumb.xRot = -0.62831855F;
                this.thumb.zRot = -0.3926991F;
            } else {
                this.arm.xRot = -0.5235988F;
                this.arm.zRot = -0.5235988F;
                this.foreArm.xRot = -0.62831855F;
                this.hand.yRot = 0.3926991F;
                this.finger[0].xRot = -0.2617994F;
                this.finger[1].zRot = -0.17453294F;
                this.finger[2].xRot = 0.2617994F;
                this.foreFinger[0].zRot = 0.2617994F;
                this.foreFinger[1].zRot = 0.3926991F;
                this.foreFinger[2].zRot = 0.2617994F;
                this.thumb.xRot = -0.62831855F;
                this.thumb.zRot = 0.3926991F;
            }

        }

        private void translateRotate(PoseStack matrixStackIn) {
            this.arm.translateAndRotate(matrixStackIn);
            this.foreArm.translateAndRotate(matrixStackIn);
            this.hand.translateAndRotate(matrixStackIn);
        }
    }
}