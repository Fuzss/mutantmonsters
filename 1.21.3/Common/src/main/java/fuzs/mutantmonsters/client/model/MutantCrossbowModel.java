package fuzs.mutantmonsters.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MutantCrossbowModel extends Model {
    public final ModelPart armWear;
    public final ModelPart middle;
    public final ModelPart middle1;
    public final ModelPart middle2;
    public final ModelPart side1;
    public final ModelPart side2;
    public final ModelPart side3;
    public final ModelPart side4;
    public final ModelPart rope1;
    public final ModelPart rope2;

    public MutantCrossbowModel(ModelPart modelPart) {
        super(RenderType::entityCutoutNoCull);
        this.armWear = modelPart.getChild("arm_wear");
        this.middle = this.armWear.getChild("middle");
        this.middle1 = this.middle.getChild("middle1");
        this.middle2 = this.middle.getChild("middle2");
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
        PartDefinition armWear = root.addOrReplaceChild("arm_wear", CubeListBuilder.create().texOffs(0, 64).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition middle = armWear.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(16, 64).addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 6.0F), PartPose.offset(-3.5F, 0.0F, 0.0F));
        PartDefinition middle1 = middle.addOrReplaceChild("middle1", CubeListBuilder.create().texOffs(36, 64).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F), PartPose.offset(0.0F, 0.6F, -4.0F));
        PartDefinition middle2 = middle.addOrReplaceChild("middle2", CubeListBuilder.create().texOffs(36, 64).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 6.0F), PartPose.offset(0.0F, 0.6F, 4.0F));
        PartDefinition side1 = middle1.addOrReplaceChild("side1", CubeListBuilder.create().texOffs(0, 74).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 8.0F), PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition side2 = middle2.addOrReplaceChild("side2", CubeListBuilder.create().texOffs(0, 74).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 2.0F));
        PartDefinition side3 = side1.addOrReplaceChild("side3", CubeListBuilder.create().texOffs(20, 74).addBox(-0.5F, -0.5F, -8.0F, 1.0F, 1.0F, 8.0F), PartPose.offset(0.0F, 0.0F, -5.0F));
        PartDefinition side4 = side2.addOrReplaceChild("side4", CubeListBuilder.create().texOffs(20, 74).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 5.0F));
        side3.addOrReplaceChild("rope1", CubeListBuilder.create().texOffs(0, 84).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 15.0F, new CubeDeformation(-0.4F)), PartPose.offset(0.0F, 0.0F, -6.0F));
        side4.addOrReplaceChild("rope2", CubeListBuilder.create().texOffs(0, 84).addBox(-0.5F, -0.5F, -15.0F, 1.0F, 1.0F, 15.0F, new CubeDeformation(-0.4F)), PartPose.offset(0.0F, 0.0F, 6.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    public void setAngles(float PI) {
        this.middle1.xRot = PI / 8.0F;
        this.middle2.xRot = -PI / 8.0F;
        this.side1.xRot = -PI / 5.0F;
        this.side2.xRot = PI / 5.0F;
        this.side3.xRot = -PI / 4.0F;
        this.side4.xRot = PI / 4.0F;
    }

    public void rotateRope() {
        this.rope1.xRot = -(this.middle1.xRot + this.side1.xRot + this.side3.xRot);
        this.rope2.xRot = -(this.middle2.xRot + this.side2.xRot + this.side4.xRot);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.armWear.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
