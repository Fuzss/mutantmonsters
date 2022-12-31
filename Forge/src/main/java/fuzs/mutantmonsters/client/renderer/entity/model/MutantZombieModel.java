package fuzs.mutantmonsters.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.animationapi.Animator;
import fuzs.mutantmonsters.entity.mutant.MutantZombieEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MutantZombieModel extends EntityModel<MutantZombieEntity> {
    private final ModelPart pelvis;
    private final ModelPart waist;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart arm1;
    private final ModelPart arm2;
    private final ModelPart forearm1;
    private final ModelPart forearm2;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart foreleg1;
    private final ModelPart foreleg2;
    private float partialTick;

    public MutantZombieModel(ModelPart modelPart) {
        this.pelvis = modelPart.getChild("pelvis");
        this.waist = this.pelvis.getChild("waist");
        this.chest = this.waist.getChild("chest");
        this.head = this.chest.getChild("head");
        this.arm1 = this.chest.getChild("arm1");
        this.arm2 = this.chest.getChild("arm2");
        this.forearm1 = this.arm1.getChild("fore_arm1");
        this.forearm2 = this.arm2.getChild("fore_arm2");
        this.leg1 = this.pelvis.getChild("leg1");
        this.leg2 = this.pelvis.getChild("leg2");
        this.foreleg1 = this.leg1.getChild("fore_leg1");
        this.foreleg2 = this.leg2.getChild("fore_leg2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition pelvis = root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 10.0F, 6.0F));
        PartDefinition waist = pelvis.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(0, 44).addBox(-7.0F, -16.0F, -6.0F, 14.0F, 16.0F, 12.0F), PartPose.ZERO);
        PartDefinition chest = waist.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 16).addBox(-12.0F, -12.0F, -8.0F, 24.0F, 12.0F, 16.0F), PartPose.offset(0.0F, -12.0F, 0.0F));
        chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, -11.0F, -4.0F));
        PartDefinition arm1 = chest.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(104, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F), PartPose.offset(-11.0F, -8.0F, 2.0F));
        PartDefinition arm2 = chest.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(104, 0).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F), PartPose.offset(11.0F, -8.0F, 2.0F));
        arm1.addOrReplaceChild("fore_arm1", CubeListBuilder.create().texOffs(104, 22).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 14.0F, 0.0F));
        arm2.addOrReplaceChild("fore_arm2", CubeListBuilder.create().texOffs(104, 22).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 14.0F, 0.0F));
        PartDefinition leg1 = pelvis.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(80, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 11.0F, 6.0F), PartPose.offset(-5.0F, -2.0F, 0.0F));
        PartDefinition leg2 = pelvis.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(80, 0).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 11.0F, 6.0F), PartPose.offset(5.0F, -2.0F, 0.0F));
        leg1.addOrReplaceChild("fore_leg1", CubeListBuilder.create().texOffs(80, 17).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 9.5F, 0.0F));
        leg2.addOrReplaceChild("fore_leg2", CubeListBuilder.create().texOffs(80, 17).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 9.5F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.pelvis.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(MutantZombieEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setAngles();
        this.animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    private void setAngles() {
        this.pelvis.y = 10.0F;
        this.waist.xRot = 0.19634955F;
        this.chest.xRot = 0.5235988F;
        this.chest.yRot = 0.0F;
        this.head.xRot = -0.71994835F;
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;
        this.arm1.xRot = -0.32724923F;
        this.arm1.yRot = 0.0F;
        this.arm1.zRot = 0.3926991F;
        this.arm2.xRot = -0.32724923F;
        this.arm2.yRot = 0.0F;
        this.arm2.zRot = -0.3926991F;
        this.forearm1.xRot = -1.0471976F;
        this.forearm2.xRot = -1.0471976F;
        this.leg1.xRot = -0.7853982F;
        this.leg1.yRot = 0.0F;
        this.leg1.zRot = 0.0F;
        this.leg2.xRot = -0.7853982F;
        this.leg2.yRot = 0.0F;
        this.leg2.zRot = 0.0F;
        this.foreleg1.xRot = 0.7853982F;
        this.foreleg2.xRot = 0.7853982F;
    }

    private void animate(MutantZombieEntity zombie, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float walkAnim1 = (Mth.sin((limbSwing - 0.7F) * 0.4F) + 0.7F) * limbSwingAmount;
        float walkAnim2 = -(Mth.sin((limbSwing + 0.7F) * 0.4F) - 0.7F) * limbSwingAmount;
        float walkAnim = Mth.sin(limbSwing * 0.4F) * limbSwingAmount;
        float breatheAnim = Mth.sin(ageInTicks * 0.1F);
        float faceYaw = netHeadYaw * 3.1415927F / 180.0F;
        float facePitch = headPitch * 3.1415927F / 180.0F;
        float scale;
        if (zombie.deathTime <= 0) {
            if (zombie.getAnimation() == MutantZombieEntity.SLAM_GROUND_ANIMATION) {
                this.animateMelee(zombie.getAnimationTick());
            }

            if (zombie.getAnimation() == MutantZombieEntity.ROAR_ANIMATION) {
                this.animateRoar(zombie.getAnimationTick());
                scale = 1.0F - Mth.clamp((float)zombie.getAnimationTick() / 6.0F, 0.0F, 1.0F);
                walkAnim1 *= scale;
                walkAnim2 *= scale;
                walkAnim *= scale;
                facePitch *= scale;
            }

            if (zombie.getAnimation() == MutantZombieEntity.THROW_ANIMATION) {
                this.animateThrow(zombie);
                scale = 1.0F - Mth.clamp((float)zombie.getAnimationTick() / 3.0F, 0.0F, 1.0F);
                walkAnim1 *= scale;
                walkAnim2 *= scale;
                walkAnim *= scale;
                facePitch *= scale;
            }
        } else {
            this.animateDeath(zombie);
            scale = 1.0F - Mth.clamp((float)zombie.deathTime / 6.0F, 0.0F, 1.0F);
            walkAnim1 *= scale;
            walkAnim2 *= scale;
            walkAnim *= scale;
            breatheAnim *= scale;
            faceYaw *= scale;
            facePitch *= scale;
        }

        this.chest.xRot += breatheAnim * 0.02F;
        this.arm1.zRot -= breatheAnim * 0.05F;
        this.arm2.zRot += breatheAnim * 0.05F;
        this.head.xRot += facePitch * 0.6F;
        this.head.yRot += faceYaw * 0.8F;
        this.head.zRot -= faceYaw * 0.2F;
        this.chest.xRot += facePitch * 0.4F;
        this.chest.yRot += faceYaw * 0.2F;
        this.pelvis.y += Mth.sin(limbSwing * 0.8F) * limbSwingAmount * 0.5F;
        this.chest.yRot -= walkAnim * 0.1F;
        this.arm1.xRot -= walkAnim * 0.6F;
        this.arm2.xRot += walkAnim * 0.6F;
        this.leg1.xRot += walkAnim1 * 0.9F;
        this.leg2.xRot += walkAnim2 * 0.9F;
    }

    private void animateMelee(int fullTick) {
        this.arm1.zRot = 0.0F;
        this.arm2.zRot = 0.0F;
        float tick;
        float f;
        float f1;
        if (fullTick < 8) {
            tick = ((float)fullTick + this.partialTick) / 8.0F;
            f = -Mth.sin(tick * 3.1415927F / 2.0F);
            f1 = Mth.cos(tick * 3.1415927F / 2.0F);
            this.waist.xRot += f * 0.2F;
            this.chest.xRot += f * 0.2F;
            this.arm1.xRot += f * 2.3F;
            this.arm1.zRot += f1 * 3.1415927F / 8.0F;
            this.arm2.xRot += f * 2.3F;
            this.arm2.zRot -= f1 * 3.1415927F / 8.0F;
            this.forearm1.xRot += f * 0.8F;
            this.forearm2.xRot += f * 0.8F;
        } else if (fullTick < 12) {
            tick = ((float)(fullTick - 8) + this.partialTick) / 4.0F;
            f = -Mth.cos(tick * 3.1415927F / 2.0F);
            f1 = Mth.sin(tick * 3.1415927F / 2.0F);
            this.waist.xRot += f * 0.9F + 0.7F;
            this.chest.xRot += f * 0.9F + 0.7F;
            this.arm1.xRot += f * 0.2F - 2.1F;
            this.arm1.zRot += f1 * 0.3F;
            this.arm2.xRot += f * 0.2F - 2.1F;
            this.arm2.zRot -= f1 * 0.3F;
            this.forearm1.xRot += f + 0.2F;
            this.forearm2.xRot += f + 0.2F;
        } else if (fullTick < 16) {
            this.waist.xRot += 0.7F;
            this.chest.xRot += 0.7F;
            this.arm1.xRot -= 2.1F;
            this.arm1.zRot += 0.3F;
            this.arm2.xRot -= 2.1F;
            this.arm2.zRot -= 0.3F;
            this.forearm1.xRot += 0.2F;
            this.forearm2.xRot += 0.2F;
        } else if (fullTick < 24) {
            tick = ((float)(fullTick - 16) + this.partialTick) / 8.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.waist.xRot += f * 0.7F;
            this.chest.xRot += f * 0.7F;
            this.arm1.xRot -= f * 2.1F;
            this.arm1.zRot += f * -0.09269908F + 0.3926991F;
            this.arm2.xRot -= f * 2.1F;
            this.arm2.zRot -= f * -0.09269908F + 0.3926991F;
            this.forearm1.xRot += f * 0.2F;
            this.forearm2.xRot += f * 0.2F;
        } else {
            this.arm1.zRot += 0.3926991F;
            this.arm2.zRot += -0.3926991F;
        }

    }

    private void animateRoar(int fullTick) {
        float tick;
        float f;
        float f1;
        if (fullTick < 10) {
            tick = ((float)fullTick + this.partialTick) / 10.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            f1 = Mth.sin(tick * 3.1415927F * 3.1415927F / 8.0F);
            this.waist.xRot += f * 0.2F;
            this.chest.xRot += f * 0.4F;
            this.chest.yRot += f1 * 0.06F;
            this.head.xRot += f * 0.8F;
            this.arm1.xRot -= f * 1.2F;
            this.arm1.zRot += f * 0.6F;
            this.arm2.xRot -= f * 1.2F;
            this.arm2.zRot -= f * 0.6F;
            this.forearm1.xRot -= f * 0.8F;
            this.forearm2.xRot -= f * 0.8F;
        } else if (fullTick < 15) {
            tick = ((float)(fullTick - 10) + this.partialTick) / 5.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            f1 = Mth.sin(tick * 3.1415927F / 2.0F);
            this.waist.xRot += f * 0.39634955F - 0.19634955F;
            this.chest.xRot += f * 0.6F - 0.2F;
            this.head.xRot += f - 0.2F;
            this.arm1.xRot -= f * 2.2F - 1.0F;
            this.arm1.yRot += f1 * 0.4F;
            this.arm1.zRot += 0.6F;
            this.arm2.xRot -= f * 2.2F - 1.0F;
            this.arm2.yRot -= f1 * 0.4F;
            this.arm2.zRot -= 0.6F;
            this.forearm1.xRot -= f - 0.2F;
            this.forearm2.xRot -= f - 0.2F;
            this.leg1.yRot += f1 * 0.3F;
            this.leg2.yRot -= f1 * 0.3F;
        } else if (fullTick < 75) {
            this.waist.xRot -= 0.19634955F;
            this.chest.xRot -= 0.2F;
            this.head.xRot -= 0.2F;
            Animator.addRotationAngle(this.arm1, 1.0F, 0.4F, 0.6F);
            Animator.addRotationAngle(this.arm2, 1.0F, -0.4F, -0.6F);
            this.forearm1.xRot += 0.2F;
            this.forearm2.xRot += 0.2F;
            this.leg1.yRot += 0.3F;
            this.leg2.yRot -= 0.3F;
        } else if (fullTick < 90) {
            tick = ((float)(fullTick - 75) + this.partialTick) / 15.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.waist.xRot -= f * 0.69634956F - 0.5F;
            this.chest.xRot -= f * 0.7F - 0.5F;
            this.head.xRot -= f * 0.6F - 0.4F;
            Animator.addRotationAngle(this.arm1, f * 2.6F - 1.6F, f * 0.4F, f * 0.99269915F - 0.3926991F);
            Animator.addRotationAngle(this.arm2, f * 2.6F - 1.6F, -f * 0.4F, -f * 0.99269915F + 0.3926991F);
            this.forearm1.xRot += f * -0.6F + 0.8F;
            this.forearm2.xRot += f * -0.6F + 0.8F;
            this.leg1.yRot += f * 0.3F;
            this.leg2.yRot -= f * 0.3F;
        } else if (fullTick < 110) {
            this.waist.xRot += 0.5F;
            this.chest.xRot += 0.5F;
            this.head.xRot += 0.4F;
            Animator.addRotationAngle(this.arm1, -1.6F, 0.0F, -0.3926991F);
            Animator.addRotationAngle(this.arm2, -1.6F, 0.0F, 0.3926991F);
            this.forearm1.xRot += 0.8F;
            this.forearm2.xRot += 0.8F;
        } else {
            tick = ((float)(fullTick - 110) + this.partialTick) / 10.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.waist.xRot += f * 0.5F;
            this.chest.xRot += f * 0.5F;
            this.head.xRot += f * 0.4F;
            Animator.addRotationAngle(this.arm1, f * -1.6F, 0.0F, f * -3.1415927F / 8.0F);
            Animator.addRotationAngle(this.arm2, f * -1.6F, 0.0F, f * 3.1415927F / 8.0F);
            this.forearm1.xRot += f * 0.8F;
            this.forearm2.xRot += f * 0.8F;
        }

        if (fullTick >= 10 && fullTick < 75) {
            tick = ((float)(fullTick - 10) + this.partialTick) / 65.0F;
            f = Mth.sin(tick * 3.1415927F * 8.0F);
            f1 = Mth.sin(tick * 3.1415927F * 8.0F + 0.7853982F);
            this.head.yRot += f * 0.5F - f1 * 0.2F;
            this.head.zRot -= f * 0.5F;
            this.chest.yRot += f1 * 0.06F;
        }

    }

    private void animateThrow(MutantZombieEntity zombie) {
        float tick;
        float f;
        if (zombie.getAnimationTick() < 3) {
            tick = ((float)zombie.getAnimationTick() + this.partialTick) / 3.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            this.chest.xRot -= f * 0.4F;
            this.arm1.xRot -= f * 1.8F;
            this.arm1.zRot -= f * 3.1415927F / 8.0F;
            this.arm2.xRot -= f * 1.8F;
            this.arm2.zRot += f * 3.1415927F / 8.0F;
        } else if (zombie.getAnimationTick() < 5) {
            this.chest.xRot -= 0.4F;
            --this.arm1.xRot;
            this.arm1.zRot = 0.0F;
            --this.arm2.xRot;
            this.arm2.zRot = 0.0F;
        } else {
            float f1;
            if (zombie.getAnimationTick() < 8) {
                tick = ((float)(zombie.getAnimationTick() - 5) + this.partialTick) / 3.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                f1 = Mth.sin(tick * 3.1415927F / 2.0F);
                this.waist.xRot += f1 * 0.2F;
                this.chest.xRot -= f * 0.6F - 0.2F;
                this.arm1.xRot -= f * 2.2F - 0.4F;
                this.arm1.zRot -= f * 3.1415927F / 8.0F;
                this.arm2.xRot -= f * 2.2F - 0.4F;
                this.arm2.zRot += f * 3.1415927F / 8.0F;
                this.forearm1.xRot -= f1 * 0.4F;
                this.forearm2.xRot -= f1 * 0.4F;
            } else if (zombie.getAnimationTick() < 10) {
                this.waist.xRot += 0.2F;
                this.chest.xRot += 0.2F;
                this.arm1.xRot += 0.4F;
                this.arm2.xRot += 0.4F;
                this.forearm1.xRot -= 0.4F;
                this.forearm2.xRot -= 0.4F;
            } else if (zombie.getAnimationTick() < 15) {
                tick = ((float)(zombie.getAnimationTick() - 10) + this.partialTick) / 5.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                f1 = Mth.sin(tick * 3.1415927F / 2.0F);
                this.waist.xRot += f * 0.39634955F - 0.19634955F;
                this.chest.xRot += f * 0.8F - 0.6F;
                this.arm1.xRot += f * 3.0F - 2.6F;
                this.arm2.xRot += f * 3.0F - 2.6F;
                this.forearm1.xRot -= f * 0.4F;
                this.forearm2.xRot -= f * 0.4F;
                this.leg1.xRot += f1 * 0.6F;
                this.leg2.xRot += f1 * 0.6F;
            } else if (zombie.throwHitTick == -1) {
                this.waist.xRot -= 0.19634955F;
                this.chest.xRot -= 0.6F;
                this.arm1.xRot -= 2.6F;
                this.arm2.xRot -= 2.6F;
                this.leg1.xRot += 0.6F;
                this.leg2.xRot += 0.6F;
            } else if (zombie.throwHitTick < 5) {
                tick = ((float)zombie.throwHitTick + this.partialTick) / 3.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                f1 = Mth.sin(tick * 3.1415927F / 2.0F);
                this.waist.xRot -= f * 0.39634955F - 0.2F;
                this.chest.xRot -= f * 0.8F - 0.2F;
                Animator.addRotationAngle(this.arm1, -(f * 2.2F + 0.4F), -f1 * 3.1415927F / 8.0F, f1 * 0.4F);
                Animator.addRotationAngle(this.arm2, -(f * 2.2F + 0.4F), f1 * 3.1415927F / 8.0F, -f1 * 0.4F);
                this.forearm1.xRot += f1 * 0.2F;
                this.forearm2.xRot += f1 * 0.2F;
                this.leg1.xRot += f * 0.8F - 0.2F;
                this.leg2.xRot += f * 0.8F - 0.2F;
            } else if (zombie.throwFinishTick == -1) {
                this.waist.xRot += 0.2F;
                this.chest.xRot += 0.2F;
                Animator.addRotationAngle(this.arm1, -0.4F, -0.3926991F, 0.4F);
                Animator.addRotationAngle(this.arm2, -0.4F, 0.3926991F, -0.4F);
                this.forearm1.xRot += 0.2F;
                this.forearm2.xRot += 0.2F;
                this.leg1.xRot -= 0.2F;
                this.leg2.xRot -= 0.2F;
            } else if (zombie.throwFinishTick < 10) {
                tick = ((float)zombie.throwFinishTick + this.partialTick) / 10.0F;
                f = Mth.cos(tick * 3.1415927F / 2.0F);
                this.waist.xRot += f * 0.2F;
                this.chest.xRot += f * 0.2F;
                Animator.addRotationAngle(this.arm1, -f * 0.4F, -f * 3.1415927F / 8.0F, f * 0.4F);
                Animator.addRotationAngle(this.arm1, -f * 0.4F, f * 3.1415927F / 8.0F, -f * 0.4F);
                this.forearm1.xRot += f * 0.2F;
                this.forearm2.xRot += f * 0.2F;
                this.leg1.xRot -= f * 0.2F;
                this.leg2.xRot -= f * 0.2F;
            }
        }

    }

    private void animateDeath(MutantZombieEntity zombie) {
        float tick;
        float f;
        if (zombie.deathTime <= 20) {
            tick = ((float)zombie.deathTime + this.partialTick - 1.0F) / 20.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            this.pelvis.y += f * 28.0F;
            this.head.xRot -= f * 3.1415927F / 10.0F;
            this.head.yRot += f * 3.1415927F / 5.0F;
            this.chest.xRot -= f * 3.1415927F / 12.0F;
            this.waist.xRot -= f * 3.1415927F / 10.0F;
            this.arm1.xRot -= f * 3.1415927F / 2.0F;
            this.arm1.yRot += f * 3.1415927F / 2.8F;
            this.arm2.xRot -= f * 3.1415927F / 2.0F;
            this.arm2.yRot -= f * 3.1415927F / 2.8F;
            this.leg1.xRot += f * 3.1415927F / 6.0F;
            this.leg1.zRot += f * 3.1415927F / 12.0F;
            this.leg2.xRot += f * 3.1415927F / 6.0F;
            this.leg2.zRot -= f * 3.1415927F / 12.0F;
        } else if (zombie.deathTime <= 100) {
            this.pelvis.y += 28.0F;
            this.head.xRot -= 0.31415927F;
            this.head.yRot += 0.62831855F;
            this.chest.xRot -= 0.2617994F;
            this.waist.xRot -= 0.31415927F;
            --this.arm1.xRot;
            ++this.arm1.yRot;
            --this.arm2.xRot;
            --this.arm2.yRot;
            this.leg1.xRot += 0.5235988F;
            this.leg1.zRot += 0.2617994F;
            this.leg2.xRot += 0.5235988F;
            this.leg2.zRot -= 0.2617994F;
        } else {
            tick = ((float)(40 - (140 - zombie.deathTime)) + this.partialTick) / 40.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.pelvis.y += f * 28.0F;
            this.head.xRot -= f * 3.1415927F / 10.0F;
            this.head.yRot += f * 3.1415927F / 5.0F;
            this.chest.xRot -= f * 3.1415927F / 12.0F;
            this.waist.xRot -= f * 3.1415927F / 10.0F;
            this.arm1.xRot -= f * 3.1415927F / 2.0F;
            this.arm1.yRot += f * 3.1415927F / 2.8F;
            this.arm2.xRot -= f * 3.1415927F / 2.0F;
            this.arm2.yRot -= f * 3.1415927F / 2.8F;
            this.leg1.xRot += f * 3.1415927F / 6.0F;
            this.leg1.zRot += f * 3.1415927F / 12.0F;
            this.leg2.xRot += f * 3.1415927F / 6.0F;
            this.leg2.zRot -= f * 3.1415927F / 12.0F;
        }

    }

    @Override
    public void prepareMobModel(MutantZombieEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.partialTick = partialTick;
    }
}
