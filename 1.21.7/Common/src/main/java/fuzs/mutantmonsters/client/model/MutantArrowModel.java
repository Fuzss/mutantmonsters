package fuzs.mutantmonsters.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class MutantArrowModel extends Model {

    public MutantArrowModel(ModelPart root) {
        super(root, RenderType::itemEntityTranslucentCull);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition stick = root.addOrReplaceChild("stick",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -13.0F, 1.0F, 1.0F, 26.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        stick.addOrReplaceChild("point1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, 0.0F, 0.7853982F, 0.0F));
        stick.addOrReplaceChild("point2",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, -0.5F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.251F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -12.0F, 0.0F, -0.7853982F, 0.0F));
        stick.addOrReplaceChild("point3",
                CubeListBuilder.create()
                        .texOffs(0, 2)
                        .addBox(-0.5F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.25F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -13.0F, -0.7853982F, 0.0F, 0.0F));
        stick.addOrReplaceChild("point4",
                CubeListBuilder.create()
                        .texOffs(0, 2)
                        .addBox(-0.5F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.251F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, -13.0F, 0.7853982F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }
}
