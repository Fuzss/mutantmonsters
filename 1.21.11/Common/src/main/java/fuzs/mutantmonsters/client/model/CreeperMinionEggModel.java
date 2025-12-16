package fuzs.mutantmonsters.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

public class CreeperMinionEggModel extends Model<Unit> {

    public CreeperMinionEggModel(ModelPart root) {
        super(root, RenderTypes::entityCutoutNoCull);
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("egg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, 1.0F, -2.0F, 4.0F, 1.0F, 4.0F, cubeDeformation)
                        .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 4.0F, 6.0F, cubeDeformation)
                        .addBox(-1.0F, -6.0F, -1.0F, 2.0F, 1.0F, 2.0F, cubeDeformation)
                        .addBox(-2.0F, -5.0F, -2.0F, 4.0F, 2.0F, 4.0F, cubeDeformation),
                PartPose.offset(0.0F, 22.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }
}
