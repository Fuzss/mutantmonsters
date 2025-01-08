package fuzs.mutantmonsters.client.model;

import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionRenderState;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.Set;

public class CreeperMinionModel extends EntityModel<CreeperMinionRenderState> {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 9.0F, 0.0F, Set.of("head"));

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public CreeperMinionModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
    }

    public static LayerDefinition createBodyLayer(CubeDeformation cubeDeformation) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubeDeformation), PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, cubeDeformation), PartPose.offset(0.0F, 6.0F, 0.0F));
        root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offset(-2.0F, 18.0F, 4.0F));
        root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offset(2.0F, 18.0F, 4.0F));
        root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offset(-2.0F, 18.0F, -4.0F));
        root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, cubeDeformation), PartPose.offset(2.0F, 18.0F, -4.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(CreeperMinionRenderState renderState) {
        this.setupAnim(renderState, renderState.walkAnimationPos,
                renderState.walkAnimationSpeed, renderState.ageInTicks, renderState.yRot, renderState.xRot);
    }

    private void setupAnim(CreeperMinionRenderState renderState, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        limbSwing *= 3.0F;
        this.head.yRot = netHeadYaw * 0.017453292F;
        this.head.xRot = headPitch * 0.017453292F;
        this.leg1.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
        this.leg3.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
        this.leg4.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.head.y = 6.0F;
        this.body.y = 6.0F;
        this.leg1.setPos(-2.0F, 18.0F, 4.0F);
        this.leg2.setPos(2.0F, 18.0F, 4.0F);
        this.leg3.setPos(-2.0F, 18.0F, -4.0F);
        this.leg4.setPos(2.0F, 18.0F, -4.0F);
        if (renderState.inSittingPose) {
            this.head.y += 3.0F;
            this.body.y += 6.0F;
            this.leg1.y += 4.0F;
            this.leg1.z -= 2.0F;
            this.leg2.y += 4.0F;
            this.leg2.z -= 2.0F;
            this.leg3.y += 4.0F;
            this.leg3.z += 2.0F;
            this.leg4.y += 4.0F;
            this.leg4.z += 2.0F;
            this.leg1.xRot = 1.5707964F;
            this.leg2.xRot = 1.5707964F;
            this.leg3.xRot = -1.5707964F;
            this.leg4.xRot = -1.5707964F;
        }
    }
}
