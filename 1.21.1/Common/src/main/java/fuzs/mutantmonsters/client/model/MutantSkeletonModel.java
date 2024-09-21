package fuzs.mutantmonsters.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.animation.Animator;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.stream.Stream;

public class MutantSkeletonModel extends EntityModel<MutantSkeleton> {
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
    private float partialTick;

    public MutantSkeletonModel(ModelPart modelPart, ModelPart crossbowModelPart) {
        this.parts = Stream.of(modelPart, crossbowModelPart).flatMap(ModelPart::getAllParts).collect(ImmutableList.toImmutableList());
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

        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 3.0F, 0.0F));
        PartDefinition pelvis = base.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F), PartPose.ZERO);
        PartDefinition waist = pelvis.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -8.0F, -2.0F, 5.0F, 8.0F, 4.0F), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition middle = waist;
        for (int i = 0; i < 3; i++) {
            Spine.createSpineLayer(middle, i);
            middle = middle.getChild("middle" + (i + 1));
        }

        PartDefinition neck = middle.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(64, 0).addBox(-1.5F, -4.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(0.0F, -4.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, -4.0F, -1.0F));
        PartDefinition innerHead = head.addOrReplaceChild("inner_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F)), PartPose.ZERO);
        innerHead.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.7F)), PartPose.offset(0.0F, -0.2F, 3.5F));

        PartDefinition shoulder1 = middle.addOrReplaceChild("shoulder1", CubeListBuilder.create().texOffs(28, 16).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 3.0F, 6.0F), PartPose.offset(-7.0F, -3.0F, -1.0F));
        PartDefinition shoulder2 = middle.addOrReplaceChild("shoulder2", CubeListBuilder.create().texOffs(28, 16).mirror().addBox(-4.0F, -3.0F, -3.0F, 8.0F, 3.0F, 6.0F), PartPose.offset(7.0F, -3.0F, -1.0F));
        PartDefinition arm1 = shoulder1.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(0, 28), PartPose.offset(-1.0F, -1.0F, 0.0F));
        PartDefinition innerArm1 = arm1.addOrReplaceChild("inner_arm1", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        PartDefinition arm2 = shoulder2.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(0, 28).mirror(), PartPose.offset(1.0F, -1.0F, 0.0F));
        PartDefinition innerArm2 = arm2.addOrReplaceChild("inner_arm2", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        PartDefinition foreArm1 = innerArm1.addOrReplaceChild("fore_arm1", CubeListBuilder.create().texOffs(16, 28), PartPose.offset(0.0F, 11.0F, 0.0F));
        foreArm1.addOrReplaceChild("inner_fore_arm1", CubeListBuilder.create().texOffs(16, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);
        PartDefinition foreArm2 = innerArm2.addOrReplaceChild("fore_arm2", CubeListBuilder.create().texOffs(16, 28).mirror(), PartPose.offset(0.0F, 11.0F, 0.0F));
        foreArm2.addOrReplaceChild("inner_fore_arm2", CubeListBuilder.create().texOffs(16, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);

        PartDefinition leg1 = pelvis.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 28), PartPose.offset(-2.5F, -2.5F, 0.0F));
        PartDefinition innerLeg1 = leg1.addOrReplaceChild("inner_leg1", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        PartDefinition leg2 = pelvis.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 28).mirror(), PartPose.offset(2.5F, -2.5F, 0.0F));
        PartDefinition innerLeg2 = leg2.addOrReplaceChild("inner_leg2", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        PartDefinition foreLeg1 = innerLeg1.addOrReplaceChild("fore_leg1", CubeListBuilder.create().texOffs(32, 28), PartPose.offset(0.0F, 12.0F, 0.0F));
        foreLeg1.addOrReplaceChild("inner_fore_leg1", CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        PartDefinition foreLeg2 = innerLeg2.addOrReplaceChild("fore_leg2", CubeListBuilder.create().texOffs(32, 28).mirror(), PartPose.offset(0.0F, 12.0F, 0.0F));
        foreLeg2.addOrReplaceChild("inner_fore_leg2", CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.skeleBase.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    @Override
    public void setupAnim(MutantSkeleton entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.partialTick = Mth.frac(ageInTicks);
        this.animator.update(entityIn, this.partialTick);
        this.setAngles();
        this.animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private void setAngles() {
        for (ModelPart renderer : this.parts) {
            renderer.xRot = 0.0F;
            renderer.yRot = 0.0F;
            renderer.zRot = 0.0F;
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
        this.crossbow.setAngles(3.1415927F);
        this.crossbow.rotateRope();
    }

    private void animate(MutantSkeleton skele, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float walkAnim1 = Mth.sin(limbSwing * 0.5F);
        float walkAnim2 = Mth.sin(limbSwing * 0.5F - 1.1F);
        float breatheAnim = Mth.sin(ageInTicks * 0.1F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        float scale;
        if (skele.getAnimation() == MutantSkeleton.MELEE_ANIMATION) {
            this.animateMelee(skele.getAnimationTick(), skele.isLeftHanded());
            this.crossbow.rotateRope();
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
        } else if (skele.getAnimation() == MutantSkeleton.SHOOT_ANIMATION) {
            this.animateShoot(skele.getAnimationTick(), facePitch, faceYaw, skele.isLeftHanded());
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            facePitch *= scale;
            faceYaw *= scale;
        } else if (skele.getAnimation() == MutantSkeleton.MULTI_SHOT_ANIMATION) {
            this.animateMultiShoot(skele.getAnimationTick(), facePitch, faceYaw, skele.isLeftHanded());
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            facePitch *= scale;
            faceYaw *= scale;
        } else if (this.animator.setAnimation(MutantSkeleton.CONSTRICT_RIBS_ANIMATION)) {
            this.animateConstrict();
            this.crossbow.rotateRope();
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 6.0F, 0.0F, 1.0F);
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

    private void animateMelee(int fullTick, boolean leftHanded) {
        ModelPart meleeArm = leftHanded ? this.arm2 : this.arm1;
        ModelPart offArm = leftHanded ? this.arm1 : this.arm2;
        int offset = leftHanded ? -1 : 1;
        float tick;
        float f;
        Spine[] var8;
        int var9;
        int var10;
        Spine spine;
        if (fullTick < 3) {
            tick = ((float) fullTick + this.partialTick) / 3.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            var8 = this.spine;
            var9 = var8.length;

            for (var10 = 0; var10 < var9; ++var10) {
                spine = var8[var10];
                spine.middle.yRot += f * 3.1415927F / 16.0F * (float) offset;
            }

            meleeArm.yRot += f * 3.1415927F / 10.0F * (float) offset;
            meleeArm.zRot += f * 3.1415927F / 4.0F * (float) offset;
            offArm.zRot += f * -3.1415927F / 16.0F * (float) offset;
        } else if (fullTick < 5) {
            tick = ((float) (fullTick - 3) + this.partialTick) / 2.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            var8 = this.spine;
            var9 = var8.length;

            for (var10 = 0; var10 < var9; ++var10) {
                spine = var8[var10];
                spine.middle.yRot += (f * 0.5890486F - 0.3926991F) * (float) offset;
            }

            meleeArm.yRot += (f * 2.7307692F - 2.41661F) * (float) offset;
            meleeArm.zRot += (f * 1.1780972F - 0.3926991F) * (float) offset;
            offArm.zRot += -0.19634955F * (float) offset;
        } else if (fullTick < 8) {
            var8 = this.spine;
            var9 = var8.length;

            for (var10 = 0; var10 < var9; ++var10) {
                spine = var8[var10];
                spine.middle.yRot += -0.3926991F * (float) offset;
            }

            meleeArm.yRot += -2.41661F * (float) offset;
            meleeArm.zRot += -0.3926991F * (float) offset;
            offArm.zRot += -0.19634955F * (float) offset;
        } else if (fullTick < 14) {
            tick = ((float) (fullTick - 8) + this.partialTick) / 6.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            var8 = this.spine;
            var9 = var8.length;

            for (var10 = 0; var10 < var9; ++var10) {
                spine = var8[var10];
                spine.middle.yRot += f * -3.1415927F / 8.0F * (float) offset;
            }

            meleeArm.yRot += f * -3.1415927F / 1.3F * (float) offset;
            meleeArm.zRot += f * -3.1415927F / 8.0F * (float) offset;
            offArm.zRot += f * -3.1415927F / 16.0F * (float) offset;
        }

    }

    private void animateShoot(int fullTick, float facePitch, float faceYaw, boolean leftHanded) {
        ModelPart drawingArm = leftHanded ? this.arm2 : this.arm1;
        ModelPart holdingArm = leftHanded ? this.arm1 : this.arm2;
        ModelPart innerDrawingArm = leftHanded ? this.innerarm2 : this.innerarm1;
        ModelPart innerHoldingArm = leftHanded ? this.innerarm1 : this.innerarm2;
        ModelPart drawingForearm = leftHanded ? this.forearm2 : this.forearm1;
        ModelPart holdingforearm = leftHanded ? this.forearm1 : this.forearm2;
        int offset = leftHanded ? -1 : 1;
        float tick;
        float f;
        if (fullTick < 5) {
            tick = ((float) fullTick + this.partialTick) / 5.0F;
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
        } else if (fullTick < 12) {
            tick = ((float) (fullTick - 5) + this.partialTick) / 7.0F;
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
            Spine[] var18;
            int var19;
            int var20;
            Spine spine;
            if (fullTick < 26) {
                this.innerhead.yRot += 0.7853982F * (float) offset;
                var18 = this.spine;
                var19 = var18.length;

                for (var20 = 0; var20 < var19; ++var20) {
                    spine = var18[var20];
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
                tick = Mth.clamp((float) (fullTick - 25) + this.partialTick, 0.0F, 1.0F);
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                this.crossbow.middle1.xRot += -f * 3.1415927F / 16.0F;
                this.crossbow.side1.xRot += -f * 3.1415927F / 24.0F;
                this.crossbow.middle2.xRot += f * 3.1415927F / 16.0F;
                this.crossbow.side2.xRot += f * 3.1415927F / 24.0F;
                this.crossbow.rotateRope();
                this.crossbow.rope1.xRot += f * 3.1415927F / 6.0F;
                this.crossbow.rope2.xRot += -f * 3.1415927F / 6.0F;
            } else if (fullTick < 30) {
                tick = ((float) (fullTick - 26) + this.partialTick) / 4.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                this.innerhead.yRot += f * 3.1415927F / 4.0F * (float) offset;
                var18 = this.spine;
                var19 = var18.length;

                for (var20 = 0; var20 < var19; ++var20) {
                    spine = var18[var20];
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

    private void animateMultiShoot(int fullTick, float facePitch, float faceYaw, boolean leftHanded) {
        float tick;
        float f;
        if (fullTick < 10) {
            tick = ((float) fullTick + this.partialTick) / 10.0F;
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
            if (fullTick < 12) {
                tick = ((float) (fullTick - 10) + this.partialTick) / 2.0F;
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
            } else if (fullTick < 14) {
                drawingArm.zRot += -0.22439948F * (float) offset;
                holdingArm.zRot += 0.22439948F * (float) offset;
                this.leg1.zRot += -0.1308997F;
                this.leg2.zRot += 0.1308997F;
                this.foreleg1.zRot += 0.049087387F;
                this.foreleg2.zRot += -0.049087387F;
                this.crossbow.rotateRope();
            } else if (fullTick < 17) {
                tick = ((float) (fullTick - 14) + this.partialTick) / 3.0F;
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
                int var15;
                if (fullTick < 20) {
                    tick = ((float) (fullTick - 17) + this.partialTick) / 3.0F;
                    f = Mth.cos(tick * 3.1415927F / 2.0F);
                    f1 = Mth.sin(tick * 3.1415927F / 2.0F);
                    float f1s = Mth.sin(tick * 3.1415927F / 2.0F * 0.4F);
                    this.innerhead.yRot += f1 * 3.1415927F / 4.0F * (float) offset;
                    Spine[] var14 = this.spine;
                    var15 = var14.length;

                    for (int var16 = 0; var16 < var15; ++var16) {
                        Spine spine = var14[var16];
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
                    Spine[] var18;
                    int var19;
                    Spine spine;
                    if (fullTick < 24) {
                        this.innerhead.yRot += 0.7853982F * (float) offset;
                        var18 = this.spine;
                        var19 = var18.length;

                        for (var15 = 0; var15 < var19; ++var15) {
                            spine = var18[var15];
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
                        tick = Mth.clamp((float) (fullTick - 25) + this.partialTick, 0.0F, 1.0F);
                        f = Mth.cos(tick * 3.1415927F / 2.0F);
                        this.crossbow.middle1.xRot += -f * 3.1415927F / 16.0F;
                        this.crossbow.side1.xRot += -f * 3.1415927F / 24.0F;
                        this.crossbow.middle2.xRot += f * 3.1415927F / 16.0F;
                        this.crossbow.side2.xRot += f * 3.1415927F / 24.0F;
                        this.crossbow.rotateRope();
                        this.crossbow.rope1.xRot += f * 3.1415927F / 6.0F;
                        this.crossbow.rope2.xRot += -f * 3.1415927F / 6.0F;
                    } else if (fullTick < 28) {
                        tick = ((float) (fullTick - 24) + this.partialTick) / 4.0F;
                        f = Mth.cos(tick * 3.1415927F / 2.0F);
                        this.innerhead.yRot += f * 3.1415927F / 4.0F * (float) offset;
                        var18 = this.spine;
                        var19 = var18.length;

                        for (var15 = 0; var15 < var19; ++var15) {
                            spine = var18[var15];
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

    private void animateConstrict() {
        this.animator.startPhase(5);
        this.animator.rotate(this.waist, 0.1308997F, 0.0F, 0.0F);

        int animTick;
        float tick;
        float f;
        for (animTick = 0; animTick < this.spine.length; ++animTick) {
            tick = animTick == 0 ? 0.3926991F : (animTick == 2 ? -0.3926991F : 0.0F);
            f = animTick == 1 ? 0.3926991F : 0.31415927F;
            this.animator.rotate(this.spine[animTick].side1[0], tick, f, 0.0F);
            this.animator.rotate(this.spine[animTick].side1[1], 0.0F, 0.15707964F, 0.0F);
            this.animator.rotate(this.spine[animTick].side1[2], 0.0F, 0.2617994F, 0.0F);
            this.animator.rotate(this.spine[animTick].side2[0], tick, -f, 0.0F);
            this.animator.rotate(this.spine[animTick].side2[1], 0.0F, -0.15707964F, 0.0F);
            this.animator.rotate(this.spine[animTick].side2[2], 0.0F, -0.2617994F, 0.0F);
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

        for (animTick = 0; animTick < this.spine.length; ++animTick) {
            tick = animTick == 0 ? 0.1308997F : (animTick == 2 ? -0.1308997F : 0.0F);
            f = animTick == 1 ? -0.17453294F : -0.22439948F;
            this.animator.rotate(this.spine[animTick].side1[0], tick - 0.08F, f, 0.0F);
            this.animator.rotate(this.spine[animTick].side1[1], 0.0F, 0.15707964F, 0.0F);
            this.animator.rotate(this.spine[animTick].side1[2], 0.0F, 0.2617994F, 0.0F);
            this.animator.rotate(this.spine[animTick].side2[0], tick + 0.08F, -f, 0.0F);
            this.animator.rotate(this.spine[animTick].side2[1], 0.0F, -0.15707964F, 0.0F);
            this.animator.rotate(this.spine[animTick].side2[2], 0.0F, -0.2617994F, 0.0F);
        }

        this.animator.move(this.skeleBase, 0.0F, 1.0F, 0.0F);
        this.animator.rotate(this.leg1, -0.44879895F, 0.0F, 0.0F);
        this.animator.rotate(this.leg2, -0.44879895F, 0.0F, 0.0F);
        this.animator.rotate(this.innerforeleg1, 0.5235988F, 0.0F, 0.0F);
        this.animator.rotate(this.innerforeleg2, 0.5235988F, 0.0F, 0.0F);
        this.animator.endPhase();
        this.animator.setStationaryPhase(4);
        this.animator.resetPhase(8);
        animTick = this.animator.getEntity().getAnimationTick();
        
        if (true) return;
        int var6;
        Spine spine;
        if (animTick < 5) {
            tick = ((float) animTick + this.partialTick) / 5.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);

            for (var6 = 0; var6 < this.spine.length; ++var6) {
                spine = this.spine[var6];
//                spine.side1[0].setScale(1.0F + f * 0.6F);
//                spine.side2[0].setScale(1.0F + f * 0.6F);
            }
        } else if (animTick < 12) {

            for (var6 = 0; var6 < this.spine.length; ++var6) {
                spine = this.spine[var6];
//                spine.side1[0].setScale(1.6F);
//                spine.side2[0].setScale(1.6F);
            }
        } else if (animTick < 20) {
            tick = ((float) (animTick - 12) + this.partialTick) / 8.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);

            for (var6 = 0; var6 < this.spine.length; ++var6) {
                spine = this.spine[var6];
//                spine.side1[0].setScale(1.0F + f * 0.6F);
//                spine.side2[0].setScale(1.0F + f * 0.6F);
            }
        }

    }

    public void translateHand(boolean leftHanded, PoseStack matrixStackIn) {
        this.skeleBase.translateAndRotate(matrixStackIn);
        this.pelvis.translateAndRotate(matrixStackIn);
        this.waist.translateAndRotate(matrixStackIn);

        for (Spine spine : this.spine) {
            spine.middle.translateAndRotate(matrixStackIn);
        }

        if (leftHanded) {
            this.shoulder2.translateAndRotate(matrixStackIn);
            this.arm2.translateAndRotate(matrixStackIn);
            this.innerarm2.translateAndRotate(matrixStackIn);
            this.forearm2.translateAndRotate(matrixStackIn);
            this.innerforearm2.translateAndRotate(matrixStackIn);
        } else {
            this.shoulder1.translateAndRotate(matrixStackIn);
            this.arm1.translateAndRotate(matrixStackIn);
            this.innerarm1.translateAndRotate(matrixStackIn);
            this.forearm1.translateAndRotate(matrixStackIn);
            this.innerforearm1.translateAndRotate(matrixStackIn);
        }

    }

    public MutantCrossbowModel getCrossbow() {
        return this.crossbow;
    }

    public static class Spine {
        public final ModelPart middle;
        public final ModelPart[] side1 = new ModelPart[3];
        public final ModelPart[] side2 = new ModelPart[3];

        public Spine(ModelPart modelPart, String indexString) {
            this.middle = modelPart.getChild("middle" +  indexString);
            modelPart = this.middle;
            for (int i = 0; i < 3; i++) {
                modelPart = this.side1[i] = modelPart.getChild("side1" + (i + 1) + indexString);
            }
            modelPart = this.middle;
            for (int i = 0; i < 3; i++) {
                modelPart = this.side2[i] = modelPart.getChild("side2" + (i + 1) + indexString);
            }
        }

        public static void createSpineLayer(PartDefinition root, int index) {
            PartPose partPose = PartPose.ZERO;
            if (index == 0) {
                partPose = PartPose.offset(0.0F, -7.0F, 0.0F);
            } else if (index > 0) {
                partPose = PartPose.offset(0.0F, -5.0F, 0.0F);
            }
            final boolean skeletonPart = index < 0;
            String indexString = skeletonPart ? "" : "" + (index + 1);
            PartDefinition middle = root.addOrReplaceChild("middle" + indexString, CubeListBuilder.create().texOffs(50, 0).addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)), partPose);
            partPose = !skeletonPart ? PartPose.offset(-3.0F, -1.0F, 1.75F) : PartPose.ZERO;
            PartDefinition side11 = middle.addOrReplaceChild("side11" + indexString, CubeListBuilder.create().texOffs(32, 12).addBox(skeletonPart ? 0.0F : -6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.25F)), partPose);
            PartDefinition side12 = side11.addOrReplaceChild("side12" + indexString, CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(skeletonPart ? -0.5F : -6.5F, 0.0F, 0.0F));
            side12.addOrReplaceChild("side13" + indexString, CubeListBuilder.create().texOffs(32, 12).addBox(-6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offset(-6.4F, 0.0F, 0.0F));
            partPose = !skeletonPart ? PartPose.offset(3.0F, -1.0F, 1.75F) : PartPose.ZERO;
            PartDefinition side21 = middle.addOrReplaceChild("side21" + indexString, CubeListBuilder.create().texOffs(32, 12).mirror().addBox(skeletonPart ? -6.0F : 0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.25F)), partPose);
            PartDefinition side22 = side21.addOrReplaceChild("side22" + indexString, CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(skeletonPart ? 0.5F : 6.5F, 0.0F, 0.0F));
            side22.addOrReplaceChild("side23" + indexString, CubeListBuilder.create().texOffs(32, 12).mirror().addBox(0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offset(6.4F, 0.0F, 0.0F));
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

//            this.side1[0].setScale(1.0F);
//            this.side2[0].setScale(1.0F);
        }

        public void animate(float breatheAnim) {
            this.side1[1].yRot += breatheAnim * 0.02F;
            this.side2[1].yRot -= breatheAnim * 0.02F;
        }
    }
}
