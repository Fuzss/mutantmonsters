package fuzs.mutantmonsters.client.model;

import fuzs.mutantmonsters.client.animation.Animator;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantZombieRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MutantZombieModel extends EntityModel<MutantZombieRenderState> {
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

    public MutantZombieModel(ModelPart modelPart) {
        super(modelPart);
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
    }

    public static LayerDefinition createBodyLayer() {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partDefinition = mesh.getRoot();

        PartDefinition pelvis = partDefinition.addOrReplaceChild("pelvis",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 10.0F, 6.0F));

        PartDefinition waist = pelvis.addOrReplaceChild("waist",
                CubeListBuilder.create().texOffs(0, 44).addBox(-7.0F, -16.0F, -6.0F, 14.0F, 16.0F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.19634955F, 0.0F, 0.0F));

        PartDefinition chest = waist.addOrReplaceChild("chest",
                CubeListBuilder.create().texOffs(0, 16).addBox(-12.0F, -12.0F, -8.0F, 24.0F, 12.0F, 16.0F),
                PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.5235988F, 0.0F, 0.0F));

        chest.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offsetAndRotation(0.0F, -11.0F, -4.0F, -0.71994835F, 0.0F, 0.0F));

        PartDefinition arm1 = chest.addOrReplaceChild("arm1",
                CubeListBuilder.create().texOffs(104, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F),
                PartPose.offsetAndRotation(-11.0F, -8.0F, 2.0F, -0.32724923F, 0.0F, 0.3926991F));

        PartDefinition arm2 = chest.addOrReplaceChild("arm2",
                CubeListBuilder.create().texOffs(104, 0).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F),
                PartPose.offsetAndRotation(11.0F, -8.0F, 2.0F, -0.32724923F, 0.0F, -0.3926991F));

        arm1.addOrReplaceChild("fore_arm1",
                CubeListBuilder.create()
                        .texOffs(104, 22)
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -1.0471976F, 0.0F, 0.0F));

        arm2.addOrReplaceChild("fore_arm2",
                CubeListBuilder.create()
                        .texOffs(104, 22)
                        .mirror()
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -1.0471976F, 0.0F, 0.0F));

        PartDefinition leg1 = pelvis.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(80, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 11.0F, 6.0F),
                PartPose.offsetAndRotation(-5.0F, -2.0F, 0.0F, -0.7853982F, 0.0F, 0.0F));

        PartDefinition leg2 = pelvis.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(80, 0).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 11.0F, 6.0F),
                PartPose.offsetAndRotation(5.0F, -2.0F, 0.0F, -0.7853982F, 0.0F, 0.0F));

        leg1.addOrReplaceChild("fore_leg1",
                CubeListBuilder.create()
                        .texOffs(80, 17)
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 9.5F, 0.0F, 0.7853982F, 0.0F, 0.0F));

        leg2.addOrReplaceChild("fore_leg2",
                CubeListBuilder.create()
                        .texOffs(80, 17)
                        .mirror()
                        .addBox(-3.0F, 0.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 9.5F, 0.0F, 0.7853982F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MutantZombieRenderState renderState) {
        super.setupAnim(renderState);
        float walkAnim1 = (Mth.sin((renderState.walkAnimationPos - 0.7F) * 0.4F) + 0.7F) *
                renderState.walkAnimationSpeed;
        float walkAnim2 = -(Mth.sin((renderState.walkAnimationPos + 0.7F) * 0.4F) - 0.7F) *
                renderState.walkAnimationSpeed;
        float walkAnim = Mth.sin(renderState.walkAnimationPos * 0.4F) * renderState.walkAnimationSpeed;
        float breatheAnim = Mth.sin(renderState.ageInTicks * 0.1F);
        float faceYaw = renderState.yRot * 3.1415927F / 180.0F;
        float facePitch = renderState.xRot * 3.1415927F / 180.0F;
        float scale;
        if (renderState.deathTime <= 0) {
            if (renderState.animation == MutantZombie.SLAM_GROUND_ANIMATION) {
                this.animateMelee(renderState);
            }

            if (renderState.animation == MutantZombie.ROAR_ANIMATION) {
                this.animateRoar(renderState);
                scale = 1.0F - Mth.clamp(renderState.animationTime / 6.0F, 0.0F, 1.0F);
                walkAnim1 *= scale;
                walkAnim2 *= scale;
                walkAnim *= scale;
                facePitch *= scale;
            }

            if (renderState.animation == MutantZombie.THROW_ANIMATION) {
                this.animateThrow(renderState);
                scale = 1.0F - Mth.clamp(renderState.animationTime / 3.0F, 0.0F, 1.0F);
                walkAnim1 *= scale;
                walkAnim2 *= scale;
                walkAnim *= scale;
                facePitch *= scale;
            }
        } else {
            this.animateDeath(renderState);
            scale = 1.0F - Mth.clamp(renderState.deathTime / 6.0F, 0.0F, 1.0F);
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
        this.pelvis.y += Mth.sin(renderState.walkAnimationPos * 0.8F) * renderState.walkAnimationSpeed * 0.5F;
        this.chest.yRot -= walkAnim * 0.1F;
        this.arm1.xRot -= walkAnim * 0.6F;
        this.arm2.xRot += walkAnim * 0.6F;
        this.leg1.xRot += walkAnim1 * 0.9F;
        this.leg2.xRot += walkAnim2 * 0.9F;
    }

    private void animateMelee(MutantZombieRenderState renderState) {
        this.arm1.zRot = 0.0F;
        this.arm2.zRot = 0.0F;
        float tick;
        float f;
        float f1;
        if (renderState.animationTime < 8.0F) {
            tick = renderState.animationTime / 8.0F;
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
        } else if (renderState.animationTime < 12.0F) {
            tick = (renderState.animationTime - 8.0F) / 4.0F;
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
        } else if (renderState.animationTime < 16.0F) {
            this.waist.xRot += 0.7F;
            this.chest.xRot += 0.7F;
            this.arm1.xRot -= 2.1F;
            this.arm1.zRot += 0.3F;
            this.arm2.xRot -= 2.1F;
            this.arm2.zRot -= 0.3F;
            this.forearm1.xRot += 0.2F;
            this.forearm2.xRot += 0.2F;
        } else if (renderState.animationTime < 24.0F) {
            tick = (renderState.animationTime - 16.0F) / 8.0F;
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

    private void animateRoar(MutantZombieRenderState renderState) {
        float tick;
        float f;
        float f1;
        if (renderState.animationTime < 10.0F) {
            tick = renderState.animationTime / 10.0F;
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
        } else if (renderState.animationTime < 15.0F) {
            tick = (renderState.animationTime - 10.0F) / 5.0F;
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
        } else if (renderState.animationTime < 75.0F) {
            this.waist.xRot -= 0.19634955F;
            this.chest.xRot -= 0.2F;
            this.head.xRot -= 0.2F;
            Animator.addRotationAngle(this.arm1, 1.0F, 0.4F, 0.6F);
            Animator.addRotationAngle(this.arm2, 1.0F, -0.4F, -0.6F);
            this.forearm1.xRot += 0.2F;
            this.forearm2.xRot += 0.2F;
            this.leg1.yRot += 0.3F;
            this.leg2.yRot -= 0.3F;
        } else if (renderState.animationTime < 90.0F) {
            tick = (renderState.animationTime - 75.0F) / 15.0F;
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
        } else if (renderState.animationTime < 110.0F) {
            this.waist.xRot += 0.5F;
            this.chest.xRot += 0.5F;
            this.head.xRot += 0.4F;
            Animator.addRotationAngle(this.arm1, -1.6F, 0.0F, -0.3926991F);
            Animator.addRotationAngle(this.arm2, -1.6F, 0.0F, 0.3926991F);
            this.forearm1.xRot += 0.8F;
            this.forearm2.xRot += 0.8F;
        } else {
            tick = (renderState.animationTime - 110.0F) / 10.0F;
            f = Mth.cos(tick * 3.1415927F / 2.0F);
            this.waist.xRot += f * 0.5F;
            this.chest.xRot += f * 0.5F;
            this.head.xRot += f * 0.4F;
            Animator.addRotationAngle(this.arm1, f * -1.6F, 0.0F, f * -3.1415927F / 8.0F);
            Animator.addRotationAngle(this.arm2, f * -1.6F, 0.0F, f * 3.1415927F / 8.0F);
            this.forearm1.xRot += f * 0.8F;
            this.forearm2.xRot += f * 0.8F;
        }

        if (renderState.animationTime >= 10.0F && renderState.animationTime < 75.0F) {
            tick = (renderState.animationTime - 10.0F) / 65.0F;
            f = Mth.sin(tick * 3.1415927F * 8.0F);
            f1 = Mth.sin(tick * 3.1415927F * 8.0F + 0.7853982F);
            this.head.yRot += f * 0.5F - f1 * 0.2F;
            this.head.zRot -= f * 0.5F;
            this.chest.yRot += f1 * 0.06F;
        }
    }

    private void animateThrow(MutantZombieRenderState renderState) {
        float tick;
        float f;
        if (renderState.animationTime < 3.0F) {
            tick = renderState.animationTime / 3.0F;
            f = Mth.sin(tick * 3.1415927F / 2.0F);
            this.chest.xRot -= f * 0.4F;
            this.arm1.xRot -= f * 1.8F;
            this.arm1.zRot -= f * 3.1415927F / 8.0F;
            this.arm2.xRot -= f * 1.8F;
            this.arm2.zRot += f * 3.1415927F / 8.0F;
        } else if (renderState.animationTime < 5.0F) {
            this.chest.xRot -= 0.4F;
            --this.arm1.xRot;
            this.arm1.zRot = 0.0F;
            --this.arm2.xRot;
            this.arm2.zRot = 0.0F;
        } else {
            float f1;
            if (renderState.animationTime < 8.0F) {
                tick = (renderState.animationTime - 5.0F) / 3.0F;
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
            } else if (renderState.animationTime < 10.0F) {
                this.waist.xRot += 0.2F;
                this.chest.xRot += 0.2F;
                this.arm1.xRot += 0.4F;
                this.arm2.xRot += 0.4F;
                this.forearm1.xRot -= 0.4F;
                this.forearm2.xRot -= 0.4F;
            } else if (renderState.animationTime < 15.0F) {
                tick = (renderState.animationTime - 10.0F) / 5.0F;
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
            } else if (renderState.throwHitTime == -1.0F) {
                this.waist.xRot -= 0.19634955F;
                this.chest.xRot -= 0.6F;
                this.arm1.xRot -= 2.6F;
                this.arm2.xRot -= 2.6F;
                this.leg1.xRot += 0.6F;
                this.leg2.xRot += 0.6F;
            } else if (renderState.throwHitTime < 5.0F) {
                tick = renderState.throwHitTime / 3.0F;
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
            } else if (renderState.throwFinishTime == -1.0F) {
                this.waist.xRot += 0.2F;
                this.chest.xRot += 0.2F;
                Animator.addRotationAngle(this.arm1, -0.4F, -0.3926991F, 0.4F);
                Animator.addRotationAngle(this.arm2, -0.4F, 0.3926991F, -0.4F);
                this.forearm1.xRot += 0.2F;
                this.forearm2.xRot += 0.2F;
                this.leg1.xRot -= 0.2F;
                this.leg2.xRot -= 0.2F;
            } else if (renderState.throwFinishTime < 10.0F) {
                tick = renderState.throwFinishTime / 10.0F;
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

    private void animateDeath(MutantZombieRenderState renderState) {
        float tick;
        float f;
        if (renderState.deathTime <= 20.0F) {
            tick = (renderState.deathTime - 1.0F) / 20.0F;
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
        } else if (renderState.deathTime <= 100.0F) {
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
            tick = (40.0F - (140.0F - renderState.deathTime)) / 40.0F;
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
}
