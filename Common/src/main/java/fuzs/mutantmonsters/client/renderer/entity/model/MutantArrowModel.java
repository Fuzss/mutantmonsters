package fuzs.mutantmonsters.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MutantArrowModel extends Model {
    private final ModelPart stick;
    private final ModelPart point1;
    private final ModelPart point2;
    private final ModelPart point3;
    private final ModelPart point4;

    public MutantArrowModel(ModelPart root) {
        super(RenderType::itemEntityTranslucentCull);
        this.stick = root.getChild("stick");
        this.point1 = this.stick.getChild("point1");
        this.point2 = this.stick.getChild("point2");
        this.point3 = this.stick.getChild("point3");
        this.point4 = this.stick.getChild("point4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition stick = root.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -13.0F, 1.0F, 1.0F, 26.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        stick.addOrReplaceChild("point1", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, 0.0F, 0.7853982F, 0.0F));
        stick.addOrReplaceChild("point2", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.251F)), PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, 0.0F, -0.7853982F, 0.0F));
        stick.addOrReplaceChild("point3", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, -13.0F, -0.7853982F, 0.0F, 0.0F));
        stick.addOrReplaceChild("point4", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.251F)), PartPose.offsetAndRotation(0.0F, 0.0F, -13.0F, 0.7853982F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        this.stick.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
