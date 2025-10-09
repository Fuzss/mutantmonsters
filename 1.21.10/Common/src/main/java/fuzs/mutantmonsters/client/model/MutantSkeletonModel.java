package fuzs.mutantmonsters.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.animation.Animator;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSkeletonRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class MutantSkeletonModel extends EntityModel<MutantSkeletonRenderState> {
    private final Animator animator = new Animator();
    private final List<ModelPart> parts;
    private final ModelPart skeleBase;
    private final ModelPart pelvis;
    private final ModelPart waist;
    private final Spine[] spine = new Spine[3];
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart innerhead;
    private final ModelPart jaw;
    private final ModelPart shoulder1;
    private final ModelPart shoulder2;
    private final ModelPart arm1;
    private final ModelPart innerarm1;
    private final ModelPart arm2;
    private final ModelPart innerarm2;
    private final ModelPart forearm1;
    private final ModelPart innerforearm1;
    private final ModelPart forearm2;
    private final ModelPart innerforearm2;
    private final ModelPart leg1;
    private final ModelPart innerleg1;
    private final ModelPart leg2;
    private final ModelPart innerleg2;
    private final ModelPart foreleg1;
    private final ModelPart innerforeleg1;
    private final ModelPart foreleg2;
    private final ModelPart innerforeleg2;
    private final MutantCrossbowModel crossbow;

    public MutantSkeletonModel(ModelPart modelPart, ModelPart crossbowModelPart) {
        super(modelPart);
        this.parts = Stream.of(modelPart, crossbowModelPart)
                .map(ModelPart::getAllParts)
                .flatMap(Collection::stream)
                .toList();
        this.skeleBase = modelPart.getChild("base");
        this.pelvis = this.skeleBase.getChild("pelvis");
        this.waist = this.pelvis.getChild("waist");
        modelPart = this.waist;
        for (int i = 0; i < 3; i++) {
            this.spine[i] = new Spine(modelPart, "" + (i + 1));
            modelPart = this.spine[i].middle;
        }
        this.neck = modelPart.getChild("neck");
        this.head = this.neck.getChild("head");
        this.innerhead = this.head.getChild("inner_head");
        this.jaw = this.innerhead.getChild("jaw");
        this.shoulder1 = modelPart.getChild("shoulder1");
        this.shoulder2 = modelPart.getChild("shoulder2");
        this.arm1 = this.shoulder1.getChild("arm1");
        this.innerarm1 = this.arm1.getChild("inner_arm1");
        this.arm2 = this.shoulder2.getChild("arm2");
        this.innerarm2 = this.arm2.getChild("inner_arm2");
        this.forearm1 = this.innerarm1.getChild("fore_arm1");
        this.innerforearm1 = this.forearm1.getChild("inner_fore_arm1");
        this.forearm2 = this.innerarm2.getChild("fore_arm2");
        this.innerforearm2 = this.forearm2.getChild("inner_fore_arm2");
        this.leg1 = this.pelvis.getChild("leg1");
        this.innerleg1 = this.leg1.getChild("inner_leg1");
        this.leg2 = this.pelvis.getChild("leg2");
        this.innerleg2 = this.leg2.getChild("inner_leg2");
        this.foreleg1 = this.innerleg1.getChild("fore_leg1");
        this.innerforeleg1 = this.foreleg1.getChild("inner_fore_leg1");
        this.foreleg2 = this.innerleg2.getChild("fore_leg2");
        this.innerforeleg2 = this.foreleg2.getChild("inner_fore_leg2");
        this.crossbow = new MutantCrossbowModel(crossbowModelPart);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition base = root.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0.0F, 3.0F, 0.0F));
        PartDefinition pelvis = base.addOrReplaceChild("pelvis",
                CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        PartDefinition waist = pelvis.addOrReplaceChild("waist",
                CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -8.0F, -2.0F, 5.0F, 8.0F, 4.0F),
                PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition middle = waist;
        for (int i = 0; i < 3; i++) {
            Spine.createSpineLayer(middle, i);
            middle = middle.getChild("middle" + (i + 1));
        }

        PartDefinition neck = middle.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(64, 0).addBox(-1.5F, -4.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0),
                PartPose.offset(0.0F, -4.0F, -1.0F));
        PartDefinition innerHead = head.addOrReplaceChild("inner_head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F)),
                PartPose.ZERO);
        innerHead.addOrReplaceChild("jaw",
                CubeListBuilder.create()
                        .texOffs(72, 0)
                        .addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.7F)),
                PartPose.offset(0.0F, -0.2F, 3.5F));

        PartDefinition shoulder1 = middle.addOrReplaceChild("shoulder1",
                CubeListBuilder.create().texOffs(28, 16).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 3.0F, 6.0F),
                PartPose.offset(-7.0F, -3.0F, -1.0F));
        PartDefinition shoulder2 = middle.addOrReplaceChild("shoulder2",
                CubeListBuilder.create().texOffs(28, 16).mirror().addBox(-4.0F, -3.0F, -3.0F, 8.0F, 3.0F, 6.0F),
                PartPose.offset(7.0F, -3.0F, -1.0F));
        PartDefinition arm1 = shoulder1.addOrReplaceChild("arm1",
                CubeListBuilder.create().texOffs(0, 28),
                PartPose.offset(-1.0F, -1.0F, 0.0F));
        PartDefinition innerArm1 = arm1.addOrReplaceChild("inner_arm1",
                CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        PartDefinition arm2 = shoulder2.addOrReplaceChild("arm2",
                CubeListBuilder.create().texOffs(0, 28).mirror(),
                PartPose.offset(1.0F, -1.0F, 0.0F));
        PartDefinition innerArm2 = arm2.addOrReplaceChild("inner_arm2",
                CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        PartDefinition foreArm1 = innerArm1.addOrReplaceChild("fore_arm1",
                CubeListBuilder.create().texOffs(16, 28),
                PartPose.offset(0.0F, 11.0F, 0.0F));
        foreArm1.addOrReplaceChild("inner_fore_arm1",
                CubeListBuilder.create()
                        .texOffs(16, 28)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)),
                PartPose.ZERO);
        PartDefinition foreArm2 = innerArm2.addOrReplaceChild("fore_arm2",
                CubeListBuilder.create().texOffs(16, 28).mirror(),
                PartPose.offset(0.0F, 11.0F, 0.0F));
        foreArm2.addOrReplaceChild("inner_fore_arm2",
                CubeListBuilder.create()
                        .texOffs(16, 28)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)),
                PartPose.ZERO);

        PartDefinition leg1 = pelvis.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(0, 28),
                PartPose.offset(-2.5F, -2.5F, 0.0F));
        PartDefinition innerLeg1 = leg1.addOrReplaceChild("inner_leg1",
                CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        PartDefinition leg2 = pelvis.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(0, 28).mirror(),
                PartPose.offset(2.5F, -2.5F, 0.0F));
        PartDefinition innerLeg2 = leg2.addOrReplaceChild("inner_leg2",
                CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        PartDefinition foreLeg1 = innerLeg1.addOrReplaceChild("fore_leg1",
                CubeListBuilder.create().texOffs(32, 28),
                PartPose.offset(0.0F, 12.0F, 0.0F));
        foreLeg1.addOrReplaceChild("inner_fore_leg1",
                CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        PartDefinition foreLeg2 = innerLeg2.addOrReplaceChild("fore_leg2",
                CubeListBuilder.create().texOffs(32, 28).mirror(),
                PartPose.offset(0.0F, 12.0F, 0.0F));
        foreLeg2.addOrReplaceChild("inner_fore_leg2",
                CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MutantSkeletonRenderState renderState) {
        super.setupAnim(renderState);
        this.animator.update(renderState);
        this.setupInitialAngles();
        this.animate(renderState,
                renderState.walkAnimationPos,
                renderState.walkAnimationSpeed,
                renderState.ageInTicks,
                renderState.yRot,
                renderState.xRot);
    }

    private void setupInitialAngles() {
        for (ModelPart renderer : this.parts) {
            Animator.resetAngles(renderer);
        }

        this.skeleBase.y = 3.0F;
        this.pelvis.xRot = -0.31415927F;
        this.waist.xRot = 0.22439948F;

        for (int i = 0; i < this.spine.length; ++i) {
            this.spine[i].setAngles(i == 1);
        }

        this.neck.xRot = -0.1308997F;
        this.head.xRot = -0.1308997F;
        this.jaw.xRot = 0.09817477F;
        this.shoulder1.xRot = -0.7853982F;
        this.shoulder2.xRot = -0.7853982F;
        this.innerarm1.xRot = 0.5235988F;
        this.innerarm1.zRot = 0.31415927F;
        this.innerarm2.xRot = 0.5235988F;
        this.innerarm2.zRot = -0.31415927F;
        this.innerforearm1.xRot = -0.5235988F;
        this.innerforearm2.xRot = -0.5235988F;
        this.leg1.xRot = -0.2617994F - this.pelvis.xRot;
        this.leg1.zRot = 0.19634955F;
        this.leg2.xRot = -0.2617994F - this.pelvis.xRot;
        this.leg2.zRot = -0.19634955F;
        this.foreleg1.zRot = -0.1308997F;
        this.innerforeleg1.xRot = 0.31415927F;
        this.foreleg2.zRot = 0.1308997F;
        this.innerforeleg2.xRot = 0.31415927F;
    }

    private void animate(MutantSkeletonRenderState renderState, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float walkAnim1 = Mth.sin(limbSwing * 0.5F);
        float walkAnim2 = Mth.sin(limbSwing * 0.5F - 1.1F);
        float breatheAnim = Mth.sin(ageInTicks * 0.1F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        float scale;
        if (renderState.animation == MutantSkeleton.MELEE_ANIMATION) {
            this.animateMelee(renderState);
            this.crossbow.rotateRope();
            scale = 1.0F - Mth.clamp(renderState.animationTime / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
        } else if (renderState.animation == MutantSkeleton.SHOOT_ANIMATION) {
            this.animateShoot(renderState, facePitch, faceYaw);
            scale = 1.0F - Mth.clamp(renderState.animationTime / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            facePitch *= scale;
            faceYaw *= scale;
        } else if (renderState.animation == MutantSkeleton.MULTI_SHOT_ANIMATION) {
            this.animateMultiShoot(renderState, facePitch, faceYaw);
            scale = 1.0F - Mth.clamp(renderState.animationTime / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            facePitch *= scale;
            faceYaw *= scale;
        } else if (this.animator.setAnimation(MutantSkeleton.CONSTRICT_RIBS_ANIMATION)) {
            this.animateConstrict(renderState);
            this.crossbow.rotateRope();
            scale = 1.0F - Mth.clamp(renderState.animationTime / 6.0F, 0.0F, 1.0F);
            facePitch *= scale;
            faceYaw *= scale;
        } else {
            this.crossbow.rotateRope();
        }

        this.skeleBase.y -= (-0.5F + Math.abs(walkAnim1)) * limbSwingAmount;
        this.spine[0].middle.yRot -= walkAnim1 * 0.06F * limbSwingAmount;
        this.arm1.xRot -= walkAnim1 * 0.9F * limbSwingAmount;
        this.arm2.xRot += walkAnim1 * 0.9F * limbSwingAmount;
        this.leg1.xRot += (0.2F + walkAnim1) * 1.0F * limbSwingAmount;
        this.leg2.xRot -= (-0.2F + walkAnim1) * 1.0F * limbSwingAmount;
        this.innerforeleg1.xRot += (0.6F + walkAnim2) * 0.6F * limbSwingAmount;
        this.innerforeleg2.xRot -= (-0.6F + walkAnim2) * 0.6F * limbSwingAmount;

        for (Spine spine : this.spine) {
            spine.animate(breatheAnim);
        }

        this.head.xRot -= breatheAnim * 0.02F;
        this.jaw.xRot += breatheAnim * 0.04F + 0.04F;
        this.arm1.zRot += breatheAnim * 0.025F;
        this.arm2.zRot -= breatheAnim * 0.025F;
        this.innerhead.xRot += facePitch;
        this.innerhead.yRot += faceYaw;
    }

    private void animateMelee(MutantSkeletonRenderState renderState) {
        boolean leftHanded = renderState.mainArm == HumanoidArm.LEFT;
        ModelPart meleeArm = leftHanded ? this.arm2 : this.arm1;
        ModelPart offArm = leftHanded ? this.arm1 : this.arm2;
        int offsetMultiplier = leftHanded ? -1 : 1;
        float animationProgress;
        float rotationAmount;
        if (renderState.animationTime < 3.0F) {
            animationProgress = renderState.animationTime / 3.0F;
            rotationAmount = Mth.sin(animationProgress * 3.1415927F / 2.0F);

            for (Spine spine : this.spine) {
                spine.middle.yRot += rotationAmount * 3.1415927F / 16.0F * (float) offsetMultiplier;
            }

            meleeArm.yRot += rotationAmount * 3.1415927F / 10.0F * (float) offsetMultiplier;
            meleeArm.zRot += rotationAmount * 3.1415927F / 4.0F * (float) offsetMultiplier;
            offArm.zRot += rotationAmount * -3.1415927F / 16.0F * (float) offsetMultiplier;
        } else if (renderState.animationTime < 5.0F) {
            animationProgress = (renderState.animationTime - 3.0F) / 2.0F;
            rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);

            for (Spine spine : this.spine) {
                spine.middle.yRot += (rotationAmount * 0.5890486F - 0.3926991F) * (float) offsetMultiplier;
            }

            meleeArm.yRot += (rotationAmount * 2.7307692F - 2.41661F) * (float) offsetMultiplier;
            meleeArm.zRot += (rotationAmount * 1.1780972F - 0.3926991F) * (float) offsetMultiplier;
            offArm.zRot += -0.19634955F * (float) offsetMultiplier;
        } else if (renderState.animationTime < 8.0F) {

            for (Spine spine : this.spine) {
                spine.middle.yRot += -0.3926991F * (float) offsetMultiplier;
            }

            meleeArm.yRot += -2.41661F * (float) offsetMultiplier;
            meleeArm.zRot += -0.3926991F * (float) offsetMultiplier;
            offArm.zRot += -0.19634955F * (float) offsetMultiplier;
        } else if (renderState.animationTime < 14.0F) {
            animationProgress = (renderState.animationTime - 8.0F) / 6.0F;
            rotationAmount = Mth.cos(animationProgress * 3.1415927F / 2.0F);

            for (Spine spine : this.spine) {
                spine.middle.yRot += rotationAmount * -3.1415927F / 8.0F * (float) offsetMultiplier;
            }

            meleeArm.yRot += rotationAmount * -3.1415927F / 1.3F * (float) offsetMultiplier;
            meleeArm.zRot += rotationAmount * -3.1415927F / 8.0F * (float) offsetMultiplier;
            offArm.zRot += rotationAmount * -3.1415927F / 16.0F * (float) offsetMultiplier;
        }
    }

    private void animateShoot(MutantSkeletonRenderState renderState, float facePitch, float faceYaw) {
        boolean leftHanded = renderState.mainArm == HumanoidArm.LEFT;
        ModelPart drawingArm = leftHanded ? this.arm2 : this.arm1;
        ModelPart holdingArm = leftHanded ? this.arm1 : this.arm2;
        ModelPart innerDrawingArm = leftHanded ? this.innerarm2 : this.innerarm1;
        ModelPart innerHoldingArm = leftHanded ? this.innerarm1 : this.innerarm2;
        ModelPart drawingForearm = leftHanded ? this.forearm2 : this.forearm1;
        ModelPart holdingforearm = leftHanded ? this.forearm1 : this.forearm2;
        int offset = leftHanded ? -1 : 1;
        float tick;
        float f;
        if (renderState.animationTime < 5.0F) {
            tick = renderState.animationTime / 5.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            innerDrawingArm.xRot += -f * 3.1415927F / 4.0F;
            drawingArm.yRot += -f * 3.1415927F / 2.0F * (float) offset;
            drawingArm.zRot += f * 3.1415927F / 16.0F * (float) offset;
            drawingForearm.xRot += f * 3.1415927F / 7.0F;
            innerHoldingArm.xRot += -f * 3.1415927F / 4.0F;
            holdingArm.yRot += f * 3.1415927F / 2.0F * (float) offset;
            holdingArm.zRot += -f * 3.1415927F / 16.0F * (float) offset;
            innerHoldingArm.zRot += -f * 3.1415927F / 8.0F * (float) offset;
            holdingforearm.xRot += -f * 3.1415927F / 6.0F;
            this.crossbow.rotateRope();
        } else if (renderState.animationTime < 12.0F) {
            tick = (renderState.animationTime - 5.0F) / 7.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            float f1 = Mth.sin(tick * 3.1415927F / 2.0F);
            float f1s = Mth.sin(tick * 3.1415927F / 2.0F * 0.4F);
            this.innerhead.yRot += f1 * 3.1415927F / 4.0F * (float) offset;

            for (Spine spine : this.spine) {
                spine.middle.yRot += -f1 * 3.1415927F / 12.0F * (float) offset;
                spine.middle.xRot += f1 * facePitch / 3.0F;
                spine.middle.yRot += f1 * faceYaw / 3.0F;
            }

            innerDrawingArm.xRot += f * 0.2617994F - 1.0471976F;
            drawingArm.yRot += (f * -0.9424778F - 0.62831855F) * (float) offset;
            drawingArm.zRot += (f * -0.850848F + 1.0471976F) * (float) offset;
            drawingForearm.xRot += 0.44879895F;
            innerHoldingArm.xRot += f * 1.8325956F - 2.6179938F;
            holdingArm.yRot += (f * 0.9424778F + 0.62831855F) * (float) offset;
            holdingArm.zRot += (f * 0.850848F - 1.0471976F) * (float) offset;
            innerHoldingArm.zRot += -f * 3.1415927F / 8.0F * (float) offset;
            holdingforearm.xRot += f * 0.10471976F - 0.62831855F;
            this.crossbow.middle1.xRot += -f1s * 3.1415927F / 16.0F;
            this.crossbow.side1.xRot += -f1s * 3.1415927F / 24.0F;
            this.crossbow.middle2.xRot += f1s * 3.1415927F / 16.0F;
            this.crossbow.side2.xRot += f1s * 3.1415927F / 24.0F;
            this.crossbow.rotateRope();
            this.crossbow.rope1.xRot += f1s * 3.1415927F / 6.0F;
            this.crossbow.rope2.xRot += -f1s * 3.1415927F / 6.0F;
        } else {
            if (renderState.animationTime < 26.0F) {
                this.innerhead.yRot += 0.7853982F * (float) offset;

                for (Spine spine : this.spine) {
                    spine.middle.yRot += -0.2617994F * (float) offset;
                    spine.middle.xRot += facePitch / 3.0F;
                    spine.middle.yRot += faceYaw / 3.0F;
                }

                innerDrawingArm.xRot += -1.0471976F;
                drawingArm.yRot += -0.62831855F * (float) offset;
                drawingArm.zRot += (float) offset;
                drawingForearm.xRot += 0.44879895F;
                innerHoldingArm.xRot += -2.6179938F;
                holdingArm.yRot += 0.62831855F * (float) offset;
                holdingArm.zRot += -1.0471976F * (float) offset;
                holdingforearm.xRot += -0.62831855F;
                tick = Mth.clamp(renderState.animationTime - 25.0F, 0.0F, 1.0F);
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                this.crossbow.middle1.xRot += -f * 3.1415927F / 16.0F;
                this.crossbow.side1.xRot += -f * 3.1415927F / 24.0F;
                this.crossbow.middle2.xRot += f * 3.1415927F / 16.0F;
                this.crossbow.side2.xRot += f * 3.1415927F / 24.0F;
                this.crossbow.rotateRope();
                this.crossbow.rope1.xRot += f * 3.1415927F / 6.0F;
                this.crossbow.rope2.xRot += -f * 3.1415927F / 6.0F;
            } else if (renderState.animationTime < 30.0F) {
                tick = (renderState.animationTime - 26.0F) / 4.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                this.innerhead.yRot += f * 3.1415927F / 4.0F * (float) offset;

                for (Spine spine : this.spine) {
                    spine.middle.yRot += -f * 3.1415927F / 12.0F * (float) offset;
                    spine.middle.xRot += f * facePitch / 3.0F;
                    spine.middle.yRot += f * faceYaw / 3.0F;
                }

                innerDrawingArm.xRot += -f * 3.1415927F / 3.0F;
                drawingArm.yRot += -f * 3.1415927F / 5.0F * (float) offset;
                drawingArm.zRot += f * 3.1415927F / 3.0F * (float) offset;
                drawingForearm.xRot += f * 3.1415927F / 7.0F;
                innerHoldingArm.xRot += -f * 3.1415927F / 1.2F;
                holdingArm.yRot += f * 3.1415927F / 5.0F * (float) offset;
                holdingArm.zRot += -f * 3.1415927F / 3.0F * (float) offset;
                holdingforearm.xRot += -f * 3.1415927F / 5.0F;
                this.crossbow.rotateRope();
            }
        }
    }

    private void animateMultiShoot(MutantSkeletonRenderState renderState, float facePitch, float faceYaw) {
        boolean leftHanded = renderState.mainArm == HumanoidArm.LEFT;
        float tick;
        float f;
        if (renderState.animationTime < 10.0F) {
            tick = renderState.animationTime / 10.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            this.skeleBase.y += f * 3.5F;
            this.spine[0].middle.xRot += f * 3.1415927F / 6.0F;
            this.head.xRot += -f * 3.1415927F / 4.0F;
            this.arm1.xRot += f * 3.1415927F / 6.0F;
            this.arm1.zRot += f * 3.1415927F / 16.0F;
            this.arm2.xRot += f * 3.1415927F / 6.0F;
            this.arm2.zRot += -f * 3.1415927F / 16.0F;
            this.leg1.xRot += -f * 3.1415927F / 8.0F;
            this.leg2.xRot += -f * 3.1415927F / 8.0F;
            this.innerforeleg1.xRot += f * 3.1415927F / 4.0F;
            this.innerforeleg2.xRot += f * 3.1415927F / 4.0F;
            this.crossbow.rotateRope();
        } else {
            ModelPart drawingArm = leftHanded ? this.arm2 : this.arm1;
            ModelPart holdingArm = leftHanded ? this.arm1 : this.arm2;
            ModelPart innerDrawingArm = leftHanded ? this.innerarm2 : this.innerarm1;
            ModelPart innerHoldingArm = leftHanded ? this.innerarm1 : this.innerarm2;
            ModelPart drawingForearm = leftHanded ? this.forearm2 : this.forearm1;
            ModelPart holdingforearm = leftHanded ? this.forearm1 : this.forearm2;
            int offset = leftHanded ? -1 : 1;
            float f1;
            if (renderState.animationTime < 12.0F) {
                tick = (renderState.animationTime - 10.0F) / 2.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                f1 = Mth.sin(tick * 3.1415927F / 2.0F);
                this.skeleBase.y += f * 3.5F;
                this.spine[0].middle.xRot += f * 3.1415927F / 6.0F;
                this.head.xRot += -f * 3.1415927F / 4.0F;
                drawingArm.xRot += f * 3.1415927F / 6.0F;
                drawingArm.zRot += f * 3.1415927F / 16.0F * (float) offset;
                holdingArm.xRot += f * 3.1415927F / 6.0F;
                holdingArm.zRot += -f * 3.1415927F / 16.0F * (float) offset;
                this.leg1.xRot += -f * 3.1415927F / 8.0F;
                this.leg2.xRot += -f * 3.1415927F / 8.0F;
                this.innerforeleg1.xRot += f * 3.1415927F / 4.0F;
                this.innerforeleg2.xRot += f * 3.1415927F / 4.0F;
                drawingArm.zRot += -f1 * 3.1415927F / 14.0F * (float) offset;
                holdingArm.zRot += f1 * 3.1415927F / 14.0F * (float) offset;
                this.leg1.zRot += -f1 * 3.1415927F / 24.0F;
                this.leg2.zRot += f1 * 3.1415927F / 24.0F;
                this.foreleg1.zRot += f1 * 3.1415927F / 64.0F;
                this.foreleg2.zRot += -f1 * 3.1415927F / 64.0F;
                this.crossbow.rotateRope();
            } else if (renderState.animationTime < 14.0F) {
                drawingArm.zRot += -0.22439948F * (float) offset;
                holdingArm.zRot += 0.22439948F * (float) offset;
                this.leg1.zRot += -0.1308997F;
                this.leg2.zRot += 0.1308997F;
                this.foreleg1.zRot += 0.049087387F;
                this.foreleg2.zRot += -0.049087387F;
                this.crossbow.rotateRope();
            } else if (renderState.animationTime < 17.0F) {
                tick = (renderState.animationTime - 14.0F) / 3.0F;
                f = Mth.sin(tick * 3.1415927F / 2.0F);
                f1 = Mth.cos(tick * 3.1415927F / 2.0F);
                drawingArm.zRot += -f1 * 3.1415927F / 14.0F * (float) offset;
                holdingArm.zRot += f1 * 3.1415927F / 14.0F * (float) offset;
                this.leg1.zRot += -f1 * 3.1415927F / 24.0F;
                this.leg2.zRot += f1 * 3.1415927F / 24.0F;
                this.foreleg1.zRot += f1 * 3.1415927F / 64.0F;
                this.foreleg2.zRot += -f1 * 3.1415927F / 64.0F;
                innerDrawingArm.xRot += -f * 3.1415927F / 4.0F;
                drawingArm.yRot += -f * 3.1415927F / 2.0F * (float) offset;
                drawingArm.zRot += f * 3.1415927F / 16.0F * (float) offset;
                drawingForearm.xRot += f * 3.1415927F / 7.0F;
                innerHoldingArm.xRot += -f * 3.1415927F / 4.0F;
                holdingArm.yRot += f * 3.1415927F / 2.0F * (float) offset;
                holdingArm.zRot += -f * 3.1415927F / 16.0F * (float) offset;
                innerHoldingArm.zRot += -f * 3.1415927F / 8.0F * (float) offset;
                holdingforearm.xRot += -f * 3.1415927F / 6.0F;
                this.crossbow.rotateRope();
            } else {
                if (renderState.animationTime < 20.0F) {
                    tick = (renderState.animationTime - 17.0F) / 3.0F;
                    f = Mth.cos(tick * 3.1415927F / 2.0F);
                    f1 = Mth.sin(tick * 3.1415927F / 2.0F);
                    float f1s = Mth.sin(tick * 3.1415927F / 2.0F * 0.4F);
                    this.innerhead.yRot += f1 * 3.1415927F / 4.0F * (float) offset;

                    for (Spine spine : this.spine) {
                        spine.middle.yRot += -f1 * 3.1415927F / 12.0F * (float) offset;
                        spine.middle.xRot += f1 * facePitch / 3.0F;
                        spine.middle.yRot += f1 * faceYaw / 3.0F;
                    }

                    innerDrawingArm.xRot += f * 0.2617994F - 1.0471976F;
                    drawingArm.yRot += (f * -0.9424778F - 0.62831855F) * (float) offset;
                    drawingArm.zRot += (f * -0.850848F + 1.0471976F) * (float) offset;
                    drawingForearm.xRot += 0.44879895F;
                    innerHoldingArm.xRot += f * 1.8325956F - 2.6179938F;
                    holdingArm.yRot += (f * 0.9424778F + 0.62831855F) * (float) offset;
                    holdingArm.zRot += (f * 0.850848F - 1.0471976F) * (float) offset;
                    innerHoldingArm.zRot += -f * 3.1415927F / 8.0F * (float) offset;
                    holdingforearm.xRot += f * 0.10471976F - 0.62831855F;
                    this.crossbow.middle1.xRot += -f1s * 3.1415927F / 16.0F;
                    this.crossbow.side1.xRot += -f1s * 3.1415927F / 24.0F;
                    this.crossbow.middle2.xRot += f1s * 3.1415927F / 16.0F;
                    this.crossbow.side2.xRot += f1s * 3.1415927F / 24.0F;
                    this.crossbow.rotateRope();
                    this.crossbow.rope1.xRot += f1s * 3.1415927F / 6.0F;
                    this.crossbow.rope2.xRot += -f1s * 3.1415927F / 6.0F;
                } else {
                    if (renderState.animationTime < 24.0F) {
                        this.innerhead.yRot += 0.7853982F * (float) offset;

                        for (Spine spine : this.spine) {
                            spine.middle.yRot += -0.2617994F * (float) offset;
                            spine.middle.xRot += facePitch / 3.0F;
                            spine.middle.yRot += faceYaw / 3.0F;
                        }

                        innerDrawingArm.xRot += -1.0471976F;
                        drawingArm.yRot += -0.62831855F * (float) offset;
                        drawingArm.zRot += (float) offset;
                        drawingForearm.xRot += 0.44879895F;
                        innerHoldingArm.xRot += -2.6179938F;
                        holdingArm.yRot += 0.62831855F * (float) offset;
                        holdingArm.zRot += -1.0471976F * (float) offset;
                        holdingforearm.xRot += -0.62831855F;
                        tick = Mth.clamp(renderState.animationTime - 25.0F, 0.0F, 1.0F);
                        f = Mth.cos(tick * 3.1415927F / 2.0F);
                        this.crossbow.middle1.xRot += -f * 3.1415927F / 16.0F;
                        this.crossbow.side1.xRot += -f * 3.1415927F / 24.0F;
                        this.crossbow.middle2.xRot += f * 3.1415927F / 16.0F;
                        this.crossbow.side2.xRot += f * 3.1415927F / 24.0F;
                        this.crossbow.rotateRope();
                        this.crossbow.rope1.xRot += f * 3.1415927F / 6.0F;
                        this.crossbow.rope2.xRot += -f * 3.1415927F / 6.0F;
                    } else if (renderState.animationTime < 28.0F) {
                        tick = (renderState.animationTime - 24.0F) / 4.0F;
                        f = Mth.cos(tick * 3.1415927F / 2.0F);
                        this.innerhead.yRot += f * 3.1415927F / 4.0F * (float) offset;

                        for (Spine spine : this.spine) {
                            spine.middle.yRot += -f * 3.1415927F / 12.0F * (float) offset;
                            spine.middle.xRot += f * facePitch / 3.0F;
                            spine.middle.yRot += f * faceYaw / 3.0F;
                        }

                        innerDrawingArm.xRot += -f * 3.1415927F / 3.0F;
                        drawingArm.yRot += -f * 3.1415927F / 5.0F * (float) offset;
                        drawingArm.zRot += f * 3.1415927F / 3.0F * (float) offset;
                        drawingForearm.xRot += f * 3.1415927F / 7.0F;
                        innerHoldingArm.xRot += -f * 3.1415927F / 1.2F;
                        holdingArm.yRot += f * 3.1415927F / 5.0F * (float) offset;
                        holdingArm.zRot += -f * 3.1415927F / 3.0F * (float) offset;
                        holdingforearm.xRot += -f * 3.1415927F / 5.0F;
                        this.crossbow.rotateRope();
                    }
                }
            }
        }
    }

    private void animateConstrict(MutantSkeletonRenderState renderState) {
        this.animator.startPhase(5);
        this.animator.rotate(this.waist, 0.1308997F, 0.0F, 0.0F);

        float tick;
        float f;
        for (int i = 0; i < this.spine.length; ++i) {
            tick = i == 0 ? 0.3926991F : (i == 2 ? -0.3926991F : 0.0F);
            f = i == 1 ? 0.3926991F : 0.31415927F;
            this.animator.rotate(this.spine[i].side1[0], tick, f, 0.0F);
            this.animator.rotate(this.spine[i].side1[1], 0.0F, 0.15707964F, 0.0F);
            this.animator.rotate(this.spine[i].side1[2], 0.0F, 0.2617994F, 0.0F);
            this.animator.rotate(this.spine[i].side2[0], tick, -f, 0.0F);
            this.animator.rotate(this.spine[i].side2[1], 0.0F, -0.15707964F, 0.0F);
            this.animator.rotate(this.spine[i].side2[2], 0.0F, -0.2617994F, 0.0F);
        }

        this.animator.rotate(this.arm1, 0.0F, 0.0F, 0.8975979F);
        this.animator.rotate(this.arm2, 0.0F, 0.0F, -0.8975979F);
        this.animator.move(this.skeleBase, 0.0F, 1.0F, 0.0F);
        this.animator.rotate(this.leg1, -0.44879895F, 0.0F, 0.0F);
        this.animator.rotate(this.leg2, -0.44879895F, 0.0F, 0.0F);
        this.animator.rotate(this.innerforeleg1, 0.5235988F, 0.0F, 0.0F);
        this.animator.rotate(this.innerforeleg2, 0.5235988F, 0.0F, 0.0F);
        this.animator.endPhase();
        this.animator.setStationaryPhase(2);
        this.animator.startPhase(1);
        this.animator.rotate(this.neck, 0.19634955F, 0.0F, 0.0F);
        this.animator.rotate(this.head, 0.15707964F, 0.0F, 0.0F);
        this.animator.rotate(this.waist, 0.31415927F, 0.0F, 0.0F);
        this.animator.rotate(this.spine[0].middle, 0.2617994F, 0.0F, 0.0F);

        for (int i = 0; i < this.spine.length; ++i) {
            tick = i == 0 ? 0.1308997F : (i == 2 ? -0.1308997F : 0.0F);
            f = i == 1 ? -0.17453294F : -0.22439948F;
            this.animator.rotate(this.spine[i].side1[0], tick - 0.08F, f, 0.0F);
            this.animator.rotate(this.spine[i].side1[1], 0.0F, 0.15707964F, 0.0F);
            this.animator.rotate(this.spine[i].side1[2], 0.0F, 0.2617994F, 0.0F);
            this.animator.rotate(this.spine[i].side2[0], tick + 0.08F, -f, 0.0F);
            this.animator.rotate(this.spine[i].side2[1], 0.0F, -0.15707964F, 0.0F);
            this.animator.rotate(this.spine[i].side2[2], 0.0F, -0.2617994F, 0.0F);
        }

        this.animator.move(this.skeleBase, 0.0F, 1.0F, 0.0F);
        this.animator.rotate(this.leg1, -0.44879895F, 0.0F, 0.0F);
        this.animator.rotate(this.leg2, -0.44879895F, 0.0F, 0.0F);
        this.animator.rotate(this.innerforeleg1, 0.5235988F, 0.0F, 0.0F);
        this.animator.rotate(this.innerforeleg2, 0.5235988F, 0.0F, 0.0F);
        this.animator.endPhase();
        this.animator.setStationaryPhase(4);
        this.animator.resetPhase(8);

        if (renderState.animationTime < 5.0F) {
            tick = renderState.animationTime / 5.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);

            for (Spine spine : this.spine) {
                Animator.setScale(spine.side1[0], 1.0F + f * 0.6F);
                Animator.setScale(spine.side2[0], 1.0F + f * 0.6F);
            }
        } else if (renderState.animationTime < 12.0F) {

            for (Spine spine : this.spine) {
                Animator.setScale(spine.side1[0], 1.6F);
                Animator.setScale(spine.side2[0], 1.6F);
            }
        } else if (renderState.animationTime < 20) {
            tick = (renderState.animationTime - 12.0F) / 8.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);

            for (Spine spine : this.spine) {
                Animator.setScale(spine.side1[0], 1.0F + f * 0.6F);
                Animator.setScale(spine.side2[0], 1.0F + f * 0.6F);
            }
        }
    }

    public void translateHand(boolean leftHanded, PoseStack poseStack) {
        this.skeleBase.translateAndRotate(poseStack);
        this.pelvis.translateAndRotate(poseStack);
        this.waist.translateAndRotate(poseStack);

        for (Spine spine : this.spine) {
            spine.middle.translateAndRotate(poseStack);
        }

        if (leftHanded) {
            this.shoulder2.translateAndRotate(poseStack);
            this.arm2.translateAndRotate(poseStack);
            this.innerarm2.translateAndRotate(poseStack);
            this.forearm2.translateAndRotate(poseStack);
            this.innerforearm2.translateAndRotate(poseStack);
        } else {
            this.shoulder1.translateAndRotate(poseStack);
            this.arm1.translateAndRotate(poseStack);
            this.innerarm1.translateAndRotate(poseStack);
            this.forearm1.translateAndRotate(poseStack);
            this.innerforearm1.translateAndRotate(poseStack);
        }
    }

    public MutantCrossbowModel getCrossbow() {
        return this.crossbow;
    }

    public static class Spine {
        public final ModelPart middle;
        public final ModelPart[] side1 = new ModelPart[3];
        public final ModelPart[] side2 = new ModelPart[3];

        public Spine(ModelPart modelPart) {
            this(modelPart, "");
        }

        public Spine(ModelPart modelPart, String indexString) {
            this.middle = modelPart.getChild("middle" + indexString);
            modelPart = this.middle;
            for (int i = 0; i < 3; i++) {
                modelPart = this.side1[i] = modelPart.getChild("side1" + (i + 1) + indexString);
            }
            modelPart = this.middle;
            for (int i = 0; i < 3; i++) {
                modelPart = this.side2[i] = modelPart.getChild("side2" + (i + 1) + indexString);
            }
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshDefinition = new MeshDefinition();
            PartDefinition root = meshDefinition.getRoot();
            MutantSkeletonModel.Spine.createSpineLayer(root, -1);
            return LayerDefinition.create(meshDefinition, 128, 128);
        }

        public static void createSpineLayer(PartDefinition root, int index) {
            PartPose partPose = PartPose.ZERO;
            if (index == 0) {
                partPose = PartPose.offset(0.0F, -7.0F, 0.0F);
            } else if (index > 0) {
                partPose = PartPose.offset(0.0F, -5.0F, 0.0F);
            }

            boolean skeletonPart = index < 0;
            String indexString = skeletonPart ? "" : "" + (index + 1);
            PartDefinition middle = root.addOrReplaceChild("middle" + indexString,
                    CubeListBuilder.create()
                            .texOffs(50, 0)
                            .addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)),
                    partPose);
            partPose = !skeletonPart ? PartPose.offset(-3.0F, -1.0F, 1.75F) : PartPose.ZERO;
            PartDefinition side11 = middle.addOrReplaceChild("side11" + indexString,
                    CubeListBuilder.create()
                            .texOffs(32, 12)
                            .addBox(skeletonPart ? 0.0F : -6.0F,
                                    -2.0F,
                                    -2.0F,
                                    6.0F,
                                    2.0F,
                                    2.0F,
                                    new CubeDeformation(0.25F)),
                    partPose);
            PartDefinition side12 = side11.addOrReplaceChild("side12" + indexString,
                    CubeListBuilder.create()
                            .texOffs(32, 12)
                            .mirror()
                            .addBox(-6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
                    PartPose.offset(skeletonPart ? -0.5F : -6.5F, 0.0F, 0.0F));
            side12.addOrReplaceChild("side13" + indexString,
                    CubeListBuilder.create()
                            .texOffs(32, 12)
                            .addBox(-6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)),
                    PartPose.offset(-6.4F, 0.0F, 0.0F));
            partPose = !skeletonPart ? PartPose.offset(3.0F, -1.0F, 1.75F) : PartPose.ZERO;
            PartDefinition side21 = middle.addOrReplaceChild("side21" + indexString,
                    CubeListBuilder.create()
                            .texOffs(32, 12)
                            .mirror()
                            .addBox(skeletonPart ? -6.0F : 0.0F,
                                    -2.0F,
                                    -2.0F,
                                    6.0F,
                                    2.0F,
                                    2.0F,
                                    new CubeDeformation(0.25F)),
                    partPose);
            PartDefinition side22 = side21.addOrReplaceChild("side22" + indexString,
                    CubeListBuilder.create()
                            .texOffs(32, 12)
                            .addBox(0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)),
                    PartPose.offset(skeletonPart ? 0.5F : 6.5F, 0.0F, 0.0F));
            side22.addOrReplaceChild("side23" + indexString,
                    CubeListBuilder.create()
                            .texOffs(32, 12)
                            .mirror()
                            .addBox(0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)),
                    PartPose.offset(6.4F, 0.0F, 0.0F));
        }

        public void setAngles(boolean middleSpine) {
            Animator.resetAngles(this.middle);
            Animator.resetAngles(this.side1);
            Animator.resetAngles(this.side2);
            this.middle.xRot = 3.1415927F / 18.0F;
            this.side1[0].yRot = -3.1415927F / 4.5F;
            this.side2[0].yRot = 3.1415927F / 4.5F;
            this.side1[1].yRot = -3.1415927F / 3.0F;
            this.side2[1].yRot = 3.1415927F / 3.0F;
            this.side1[2].yRot = -3.1415927F / 3.5F;
            this.side2[2].yRot = 3.1415927F / 3.5F;
            if (middleSpine) {
                for (int i = 0; i < this.side1.length; ++i) {
                    this.side1[i].yRot *= 0.98F;
                    this.side2[i].yRot *= 0.98F;
                }
            }

            Animator.setScale(this.side1[0], 1.0F);
            Animator.setScale(this.side2[0], 1.0F);
        }

        public void animate(float breatheAnim) {
            this.side1[1].yRot += breatheAnim * 0.02F;
            this.side2[1].yRot -= breatheAnim * 0.02F;
        }
    }
}
