package fuzs.mutantmonsters.client.model;

import fuzs.mutantmonsters.client.renderer.entity.state.EndersoulFragmentRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class EndersoulFragmentModel extends EntityModel<EndersoulFragmentRenderState> {
    private final ModelPart[] sticks = new ModelPart[8];

    public EndersoulFragmentModel(ModelPart root) {
        super(root);
        ModelPart base = root.getChild("base");
        for (int i = 0; i < this.sticks.length; i++) {
            this.sticks[i] = base.getChild("stick" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
        for(int i = 0; i < 8; ++i) {
            CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(0, 0);
            if (i < 8 / 2) {
                cubeListBuilder.addBox(-0.5F, -4.0F, -0.5F, 1.0F, 8.0F, 1.0F);
            } else {
                cubeListBuilder.addBox(-0.5F, -6.0F, -0.5F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.15F));
            }
            base.addOrReplaceChild("stick" + i, cubeListBuilder, PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(EndersoulFragmentRenderState renderState) {
        super.setupAnim(renderState);
        for(int i = 0; i < this.sticks.length; ++i) {
            this.sticks[i].xRot = renderState.stickRotations[i][0];
            this.sticks[i].yRot = renderState.stickRotations[i][1];
            this.sticks[i].zRot = renderState.stickRotations[i][2];
        }
    }
}
