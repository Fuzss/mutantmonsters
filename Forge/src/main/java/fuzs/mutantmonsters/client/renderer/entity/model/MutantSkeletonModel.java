package fuzs.mutantmonsters.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.animationapi.Animator;
import fuzs.mutantmonsters.entity.mutant.MutantSkeletonEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MutantSkeletonModel extends EntityModel<MutantSkeletonEntity> {
    protected final Animator animator = new Animator();
    private final List<ModelPart> parts;
    protected ModelPart skeleBase;
    protected ModelPart pelvis;
    protected ModelPart waist;
    protected Spine[] spine;
    protected ModelPart neck;
    protected ModelPart head;
    protected ModelPart innerhead;
    protected ModelPart jaw;
    protected ModelPart shoulder1;
    protected ModelPart shoulder2;
    protected ModelPart arm1;
    protected ModelPart innerarm1;
    protected ModelPart arm2;
    protected ModelPart innerarm2;
    protected ModelPart forearm1;
    protected ModelPart innerforearm1;
    protected ModelPart forearm2;
    protected ModelPart innerforearm2;
    protected ModelPart leg1;
    protected ModelPart innerleg1;
    protected ModelPart leg2;
    protected ModelPart innerleg2;
    protected ModelPart foreleg1;
    protected ModelPart innerforeleg1;
    protected ModelPart foreleg2;
    protected ModelPart innerforeleg2;
    protected MutantCrossbowModel crossbow;
    protected float partialTick;

    public MutantSkeletonModel(ModelPart modelPart, ModelPart spineModelPart, ModelPart crossbowModelPart) {
        this.parts = Stream.of(modelPart, spineModelPart, crossbowModelPart).flatMap(ModelPart::getAllParts).filter(Predicate.not(ModelPart::isEmpty)).collect(ImmutableList.toImmutableList());


//        MeshDefinition mesh = new MeshDefinition();
//        PartDefinition root = mesh.getRoot();
//
//        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 3.0F, 0.0F));
//        PartDefinition pelvis = base.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -6.0F, -3.0F, 8.0F, 6.0F, 6.0F), PartPose.ZERO);
//        pelvis.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -8.0F, -2.0F, 5.0F, 8.0F, 4.0F), PartPose.offset(0.0F, -5.0F, 0.0F));
//
//
//        this.spine = new Spine[3];
//        this.spine[0] = new Spine(spineModelPart);
//        this.spine[0].middle.setPos(0.0F, -7.0F, 0.0F);
//        this.waist.addChild(this.spine[0].middle);
//
//        for (int i = 1; i < this.spine.length; ++i) {
//            this.spine[i] = new Spine(spineModelPart);
//            this.spine[i].middle.setPos(0.0F, -5.0F, 0.0F);
//            this.spine[i - 1].middle.addChild(this.spine[i].middle);
//        }
//
//        this.neck = CubeListBuilder.create().texOffs(64, 0);
//        this.neck.addBox(-1.5F, -4.0F, -1.5F, 3.0F, 4.0F, 3.0F);
//        this.neck.PartPose.offset(0.0F, -4.0F, 0.0F);
//        this.spine[2].middle.addChild(this.neck);
//
//
//
//
//
//
//
//        this.head = CubeListBuilder.create().texOffs(0, 0);
//        this.head.PartPose.offset(0.0F, -4.0F, -1.0F);
//        this.head = CubeListBuilder.create().texOffs(0, 0);
//        this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.4F);
//        this.neck.addChild(this.head);
//        this.jaw = CubeListBuilder.create().texOffs(72, 0);
//        this.jaw.addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, 0.7F);
//        this.jaw.PartPose.offset(0.0F, -0.2F, 3.5F);
//        this.head.addChild(this.jaw);
//        this.shoulder1 = CubeListBuilder.create().texOffs(28, 16);
//        this.shoulder1.addBox(-4.0F, -3.0F, -3.0F, 8.0F, 3.0F, 6.0F);
//        this.shoulder1.PartPose.offset(-7.0F, -3.0F, -1.0F);
//        this.spine[2].middle.addChild(this.shoulder1);
//        this.shoulder2 = CubeListBuilder.create().texOffs(28, 16);
//        this.shoulder2.mirror = true;
//        this.shoulder2.addBox(-4.0F, -3.0F, -3.0F, 8.0F, 3.0F, 6.0F);
//        this.shoulder2.PartPose.offset(7.0F, -3.0F, -1.0F);
//        this.spine[2].middle.addChild(this.shoulder2);
//        this.arm1 = CubeListBuilder.create().texOffs(0, 28);
//        this.arm1.PartPose.offset(-1.0F, -1.0F, 0.0F);
//        this.arm1 = CubeListBuilder.create().texOffs(0, 28);
//        this.arm1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
//        this.shoulder1.addChild(this.arm1);
//        this.arm2 = CubeListBuilder.create().texOffs(0, 28);
//        this.arm2.mirror = true;
//        this.arm2.PartPose.offset(1.0F, -1.0F, 0.0F);
//        this.arm2 = CubeListBuilder.create().texOffs(0, 28);
//        this.arm2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
//        this.shoulder2.addChild(this.arm2);
//        this.forearm1 = CubeListBuilder.create().texOffs(16, 28);
//        this.forearm1.PartPose.offset(0.0F, 11.0F, 0.0F);
//        this.forearm1 = CubeListBuilder.create().texOffs(16, 28);
//        this.forearm1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, -0.01F);
//        this.arm1.addChild(this.forearm1);
//        this.forearm2 = CubeListBuilder.create().texOffs(16, 28);
//        this.forearm2.mirror = true;
//        this.forearm2.PartPose.offset(0.0F, 11.0F, 0.0F);
//        this.forearm2 = CubeListBuilder.create().texOffs(16, 28);
//        this.forearm2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, -0.01F);
//        this.arm2.addChild(this.forearm2);
//        this.leg1 = CubeListBuilder.create().texOffs(0, 28);
//        this.leg1.PartPose.offset(-2.5F, -2.5F, 0.0F);
//        this.leg1 = CubeListBuilder.create().texOffs(0, 28);
//        this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
//        this.pelvis.addChild(this.leg1);
//        this.leg2 = CubeListBuilder.create().texOffs(0, 28);
//        this.leg2.mirror = true;
//        this.leg2.PartPose.offset(2.5F, -2.5F, 0.0F);
//        this.leg2 = CubeListBuilder.create().texOffs(0, 28);
//        this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
//        this.pelvis.addChild(this.leg2);
//        this.foreleg1 = CubeListBuilder.create().texOffs(32, 28);
//        this.foreleg1.PartPose.offset(0.0F, 12.0F, 0.0F);
//        this.foreleg1 = CubeListBuilder.create().texOffs(32, 28);
//        this.foreleg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
//        this.leg1.addChild(this.foreleg1);
//        this.foreleg2 = CubeListBuilder.create().texOffs(32, 28);
//        this.foreleg2.mirror = true;
//        this.foreleg2.PartPose.offset(0.0F, 12.0F, 0.0F);
//        this.foreleg2 = CubeListBuilder.create().texOffs(32, 28);
//        this.foreleg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
//        this.leg2.addChild(this.foreleg2);
//        this.crossbow = new MutantCrossbowModel(crossbowModelPart);
//        this.crossbow.armWear.setPos(0.0F, 8.0F, 0.0F);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -0.2F, 3.5F, 0.09817477F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.skeleBase.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(MutantSkeletonEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.animator.update(entityIn, this.partialTick);
        this.setAngles();
        this.animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    protected void setAngles() {
        for (ModelPart renderer : this.parts) {
            renderer.xRot = 0.0F;
            renderer.yRot = 0.0F;
            renderer.zRot = 0.0F;
        }

        this.skeleBase.y = 3.0F;
        this.pelvis.xRot = -0.31415927F;
        this.waist.xRot = 0.22439948F;

        for (int i = 0; i < this.spine.length; ++i) {
            this.spine[i].setAngles(3.1415927F, i == 1);
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

    protected void animate(MutantSkeletonEntity skele, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float walkAnim1 = Mth.sin(limbSwing * 0.5F);
        float walkAnim2 = Mth.sin(limbSwing * 0.5F - 1.1F);
        float breatheAnim = Mth.sin(ageInTicks * 0.1F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        float scale;
        if (skele.getAnimation() == MutantSkeletonEntity.MELEE_ANIMATION) {
            this.animateMelee(skele.getAnimationTick(), skele.isLeftHanded());
            this.crossbow.rotateRope();
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
        } else if (skele.getAnimation() == MutantSkeletonEntity.SHOOT_ANIMATION) {
            this.animateShoot(skele.getAnimationTick(), facePitch, faceYaw, skele.isLeftHanded());
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            facePitch *= scale;
            faceYaw *= scale;
        } else if (skele.getAnimation() == MutantSkeletonEntity.MULTI_SHOT_ANIMATION) {
            this.animateMultiShoot(skele.getAnimationTick(), facePitch, faceYaw, skele.isLeftHanded());
            scale = 1.0F - Mth.clamp((float) skele.getAnimationTick() / 4.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            facePitch *= scale;
            faceYaw *= scale;
        } else if (this.animator.setAnimation(MutantSkeletonEntity.CONSTRICT_RIBS_ANIMATION)) {
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
        Spine[] var13 = this.spine;

        for (Spine spine : var13) {
            spine.animate(breatheAnim);
        }

        this.head.xRot -= breatheAnim * 0.02F;
        this.jaw.xRot += breatheAnim * 0.04F + 0.04F;
        this.arm1.zRot += breatheAnim * 0.025F;
        this.arm2.zRot -= breatheAnim * 0.025F;
        this.innerhead.xRot += facePitch;
        this.innerhead.yRot += faceYaw;
    }

    protected void animateMelee(int fullTick, boolean leftHanded) {
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

    protected void animateShoot(int fullTick, float facePitch, float faceYaw, boolean leftHanded) {
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
            Spine[] var14 = this.spine;

            for (Spine spine : var14) {
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

    protected void animateMultiShoot(int fullTick, float facePitch, float faceYaw, boolean leftHanded) {
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

    protected void animateConstrict() {
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
        Spine[] var3 = this.spine;
        int var4 = var3.length;

        for (Spine spine : var3) {
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

    public void setLivingAnimations(MutantSkeletonEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.partialTick = partialTick;
    }

    static class Spine {
        public final ModelPart middle;
        public final ModelPart[] side1 = new ModelPart[3];
        public final ModelPart[] side2 = new ModelPart[3];

        public Spine(ModelPart modelPart) {
            this.middle = modelPart.getChild("middle");
            modelPart = this.middle;
            for (int i = 0; i < 3; i++) {
                modelPart = this.side1[i] = modelPart.getChild("side1" + (i + 1));
            }
            modelPart = this.middle;
            for (int i = 0; i < 3; i++) {
                modelPart = this.side2[i] = modelPart.getChild("side2" + (i + 1));
            }
        }

        public static LayerDefinition createBodyLayer(boolean skeletonPart) {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            PartDefinition middle = root.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(50, 0).addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.ZERO);
            PartPose partPose = !skeletonPart ? PartPose.offset(-3.0F, -1.0F, 1.75F) : PartPose.ZERO;
            PartDefinition side11 = middle.addOrReplaceChild("side11", CubeListBuilder.create().texOffs(32, 12).addBox(skeletonPart ? 0.0F : -6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.25F)), partPose);
            PartDefinition side12 = side11.addOrReplaceChild("side12", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(skeletonPart ? -0.5F : -6.5F, 0.0F, 0.0F));
            side12.addOrReplaceChild("side13", CubeListBuilder.create().texOffs(32, 12).addBox(-6.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offset(-6.4F, 0.0F, 0.0F));
            partPose = !skeletonPart ? PartPose.offset(3.0F, -1.0F, 1.75F) : PartPose.ZERO;
            PartDefinition side21 = middle.addOrReplaceChild("side21", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(skeletonPart ? -6.0F : 0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.25F)), partPose);
            PartDefinition side22 = side21.addOrReplaceChild("side22", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(skeletonPart ? 0.5F : 6.5F, 0.0F, 0.0F));
            side22.addOrReplaceChild("side23", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(0.0F, -2.0F, -2.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offset(6.4F, 0.0F, 0.0F));
            return LayerDefinition.create(mesh, 128, 128);
        }

        public void setAngles(float PI, boolean middleSpine) {
            Animator.resetAngles(this.middle);
            Animator.resetAngles(this.side1);
            Animator.resetAngles(this.side2);
            this.middle.xRot = PI / 18.0F;
            this.side1[0].yRot = -PI / 4.5F;
            this.side2[0].yRot = PI / 4.5F;
            this.side1[1].yRot = -PI / 3.0F;
            this.side2[1].yRot = PI / 3.0F;
            this.side1[2].yRot = -PI / 3.5F;
            this.side2[2].yRot = PI / 3.5F;
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
