package fuzs.mutantmonsters.client.model;

import fuzs.mutantmonsters.client.renderer.entity.state.MutantSkeletonRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class MutantCrossbowModel extends Model<MutantSkeletonRenderState> {
    private final ModelPart middle1;
    private final ModelPart middle2;
    private final ModelPart side1;
    private final ModelPart side2;
    private final ModelPart side3;
    private final ModelPart side4;
    private final ModelPart rope1;
    private final ModelPart rope2;

    public MutantCrossbowModel(ModelPart modelPart) {
        super(modelPart, RenderType::entityCutoutNoCull);
        ModelPart armWear = modelPart.getChild("arm_wear");
        ModelPart middle = armWear.getChild("middle");
        this.middle1 = middle.getChild("middle1");
        this.middle2 = middle.getChild("middle2");
        this.side1 = this.middle1.getChild("side1");
        this.side2 = this.middle2.getChild("side2");
        this.side3 = this.side1.getChild("side3");
        this.side4 = this.side2.getChild("side4");
        this.rope1 = this.side3.getChild("rope1");
        this.rope2 = this.side4.getChild("rope2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition armWear = root.addOrReplaceChild("arm_wear",
                CubeListBuilder.create()
                        .texOffs(0, 64)
                        .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.3F)),
                PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition middle = armWear.addOrReplaceChild("middle",
                CubeListBuilder.create().texOffs(16, 64).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 6.0F),
                PartPose.offset(-3.5F, 0.0F, 0.0F));
        PartDefinition middle1 = middle.addOrReplaceChild("middle1",
                CubeListBuilder.create().texOffs(36, 64).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, 0.6F, -4.0F));
        PartDefinition middle2 = middle.addOrReplaceChild("middle2",
                CubeListBuilder.create().texOffs(36, 64).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, 0.6F, 4.0F));
        PartDefinition side1 = middle1.addOrReplaceChild("side1",
                CubeListBuilder.create().texOffs(0, 74).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition side2 = middle2.addOrReplaceChild("side2",
                CubeListBuilder.create().texOffs(0, 74).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition side3 = side1.addOrReplaceChild("side3",
                CubeListBuilder.create().texOffs(20, 74).addBox(-0.5F, -0.5F, -8.0F, 1.0F, 1.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, -5.0F));
        PartDefinition side4 = side2.addOrReplaceChild("side4",
                CubeListBuilder.create().texOffs(20, 74).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 5.0F));
        side3.addOrReplaceChild("rope1",
                CubeListBuilder.create()
                        .texOffs(0, 84)
                        .addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 15.0F, new CubeDeformation(-0.4F)),
                PartPose.offset(0.0F, 0.0F, -6.0F));
        side4.addOrReplaceChild("rope2",
                CubeListBuilder.create()
                        .texOffs(0, 84)
                        .addBox(-0.5F, -0.5F, -15.0F, 1.0F, 1.0F, 15.0F, new CubeDeformation(-0.4F)),
                PartPose.offset(0.0F, 0.0F, 6.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(MutantSkeletonRenderState renderState) {
        super.setupAnim(renderState);
        this.middle1.xRot = Mth.PI / 8.0F;
        this.middle2.xRot = -Mth.PI / 8.0F;
        this.side1.xRot = -Mth.PI / 5.0F;
        this.side2.xRot = Mth.PI / 5.0F;
        this.side3.xRot = -Mth.PI / 4.0F;
        this.side4.xRot = Mth.PI / 4.0F;
        this.animate(renderState);
    }

    private void animate(MutantSkeletonRenderState renderState) {
        if (renderState.animation == MutantSkeleton.SHOOT_ANIMATION) {
            this.animShoot(renderState);
        } else if (renderState.animation == MutantSkeleton.MULTI_SHOT_ANIMATION) {
            this.animMultiShoot(renderState);
        } else {
            this.animRope();
        }
    }

    private void animRope() {
        this.rope1.xRot = -(this.middle1.xRot + this.side1.xRot + this.side3.xRot);
        this.rope2.xRot = -(this.middle2.xRot + this.side2.xRot + this.side4.xRot);
    }

    private void animShoot(MutantSkeletonRenderState renderState) {
        if (renderState.animationTime < 5.0F) {
            this.animRope();
        } else if (renderState.animationTime < 12.0F) {
            float time = (renderState.animationTime - 5.0F) / 7.0F;
            this.animShoot(Mth.sin(time * Mth.PI / 2.0F * 0.4F));
        } else if (renderState.animationTime < 26.0F) {
            float time = Mth.clamp(renderState.animationTime - 25.0F, 0.0F, 1.0F);
            this.animShoot(Mth.cos(time * Mth.PI / 2.0F));
        } else if (renderState.animationTime < 30.0F) {
            this.animRope();
        }
    }

    private void animMultiShoot(MutantSkeletonRenderState renderState) {
        if (renderState.animationTime < 17.0F) {
            this.animRope();
        } else if (renderState.animationTime < 20.0F) {
            float time = (renderState.animationTime - 17.0F) / 3.0F;
            this.animShoot(Mth.sin(time * Mth.PI / 2.0F * 0.4F));
        } else if (renderState.animationTime < 24.0F) {
            float time = Mth.clamp(renderState.animationTime - 25.0F, 0.0F, 1.0F);
            this.animShoot(Mth.cos(time * Mth.PI / 2.0F));
        } else if (renderState.animationTime < 28.0F) {
            this.animRope();
        }
    }

    private void animShoot(float amount) {
        this.middle1.xRot += -amount * Mth.PI / 16.0F;
        this.side1.xRot += -amount * Mth.PI / 24.0F;
        this.middle2.xRot += amount * Mth.PI / 16.0F;
        this.side2.xRot += amount * Mth.PI / 24.0F;
        this.animRope();
        this.rope1.xRot += amount * Mth.PI / 6.0F;
        this.rope2.xRot += -amount * Mth.PI / 6.0F;
    }
}
