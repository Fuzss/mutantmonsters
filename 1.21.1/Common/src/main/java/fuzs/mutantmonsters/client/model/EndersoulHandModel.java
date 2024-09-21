package fuzs.mutantmonsters.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.animation.Animator;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class EndersoulHandModel extends Model {
    private final ModelPart hand;
    private final ModelPart[] finger = new ModelPart[3];
    private final ModelPart[] foreFinger = new ModelPart[3];
    private final ModelPart thumb;
    private final boolean right;

    public EndersoulHandModel(ModelPart root, boolean right) {
        super(RenderType::entitySolid);
        this.right = right;
        this.hand = root.getChild("hand");
        for (int i = 0; i < this.finger.length; i++) {
            this.finger[i] = this.hand.getChild("finger" + i);
            this.foreFinger[i] = this.finger[i].getChild("fore_finger" + i);
        }
        this.thumb = this.hand.getChild("thumb");
    }

    public static LayerDefinition createBodyLayer(boolean right) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition hand = root.addOrReplaceChild("hand", CubeListBuilder.create().texOffs(0, 0), PartPose.offset(0.0F, 17.5F, 0.0F));
        for(int i = 0; i < 3; ++i) {
            CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(i * 4, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, i == 1 ? 6.0F : 5.0F, 1.0F, new CubeDeformation(0.6F)).mirror(!right);
            PartPose partPose;
            if (i == 0) {
                partPose = PartPose.offset(right ? -0.5F : 0.5F, 0.0F, -1.0F);
            } else if (i == 1) {
                partPose = PartPose.offset(right ? -0.5F : 0.5F, 0.0F, 0.0F);
            } else {
                partPose = PartPose.offset(right ? -0.5F : 0.5F, 0.0F, 1.0F);
            }
            hand.addOrReplaceChild("finger" + i, cubeListBuilder, partPose);
        }

        for(int i = 0; i < 3; ++i) {
            hand.getChild("finger" + i).addOrReplaceChild("fore_finger" + i, CubeListBuilder.create().texOffs(1 + i * 5, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, i == 1 ? 6.0F : 5.0F, 1.0F, new CubeDeformation(0.6F - 0.01F)).mirror(!right), PartPose.offset(0.0F, 0.5F + (float)(i == 1 ? 6 : 5), 0.0F));
        }
        hand.addOrReplaceChild("thumb", CubeListBuilder.create().texOffs(14, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.6F)), PartPose.offset(right ? 0.5F : -0.5F, 0.0F, -0.5F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    public void setAngles() {
        Animator.resetAngles(this.hand);

        for(int i = 0; i < this.finger.length; ++i) {
            Animator.resetAngles(this.finger[i]);
            Animator.resetAngles(this.foreFinger[i]);
        }

        Animator.resetAngles(this.thumb);
        if (this.right) {
            this.hand.yRot = -0.3926991F;
            this.finger[0].xRot = -0.2617994F;
            this.finger[1].zRot = 0.17453294F;
            this.finger[2].xRot = 0.2617994F;
            this.foreFinger[0].zRot = -0.2617994F;
            this.foreFinger[1].zRot = -0.3926991F;
            this.foreFinger[2].zRot = -0.2617994F;
            this.thumb.xRot = -0.62831855F;
            this.thumb.zRot = -0.3926991F;
        } else {
            this.hand.yRot = 0.3926991F;
            this.finger[0].xRot = -0.2617994F;
            this.finger[1].zRot = -0.17453294F;
            this.finger[2].xRot = 0.2617994F;
            this.foreFinger[0].zRot = 0.2617994F;
            this.foreFinger[1].zRot = 0.3926991F;
            this.foreFinger[2].zRot = 0.2617994F;
            this.thumb.xRot = -0.62831855F;
            this.thumb.zRot = 0.3926991F;
        }

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.hand.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
