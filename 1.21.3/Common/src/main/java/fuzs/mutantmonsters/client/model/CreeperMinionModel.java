package fuzs.mutantmonsters.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CreeperMinionModel extends HierarchicalModel<CreeperMinion> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public CreeperMinionModel(ModelPart root) {
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
    public ModelPart root() {
        return this.root;
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leg1, this.leg2, this.leg3, this.leg4);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 9.0F / 16.0F, 0.0F);
        this.headParts().forEach(modelPart -> {
            modelPart.render(poseStack, buffer, packedLight, packedOverlay, color);
        });
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(0.0F, 24.0F / 16.0F, 0.0F);
        this.bodyParts().forEach(modelPart -> {
            modelPart.render(poseStack, buffer, packedLight, packedOverlay, color);
        });
        poseStack.popPose();
    }

    @Override
    public void setupAnim(CreeperMinion entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.setupAnim(entity.isInSittingPose(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void setupAnim(boolean isInSittingPose, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        if (isInSittingPose) {
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
