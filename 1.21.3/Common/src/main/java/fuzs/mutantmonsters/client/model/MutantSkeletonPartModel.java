package fuzs.mutantmonsters.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public class MutantSkeletonPartModel extends Model {
    private final List<ModelPart> parts;

    public MutantSkeletonPartModel(ModelPart modelPart, ModelPart spineModelPart) {
        super(modelPart, RenderType::entityCutoutNoCull);
        // do this weird order thing because of order, index is somehow important
        ImmutableList.Builder<ModelPart> builder = ImmutableList.builder();
        builder.add(modelPart.getChild("pelvis"));
        for (int i = 0; i < 3; ++i) {
            MutantSkeletonModel.Spine spine = new MutantSkeletonModel.Spine(spineModelPart, "");
            spine.setAngles(i == 1);
            for (int j = 0; j < 3; j++) {
                builder.add(spine.side1[i], spine.side2[i]);
            }
        }
        ModelPart head = modelPart.getChild("head");
        builder.add(head);
        builder.add(head.getChild("jaw"));
        builder.add(modelPart.getChild("arm1"));
        builder.add(modelPart.getChild("arm2"));
        builder.add(modelPart.getChild("forearm1"));
        builder.add(modelPart.getChild("forearm2"));
        builder.add(modelPart.getChild("leg1"));
        builder.add(modelPart.getChild("leg2"));
        builder.add(modelPart.getChild("foreleg1"));
        builder.add(modelPart.getChild("foreleg2"));
        builder.add(modelPart.getChild("shoulder1"));
        builder.add(modelPart.getChild("shoulder2"));
        this.parts = builder.build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 6.0F, 6.0F), PartPose.ZERO);
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F)), PartPose.ZERO);
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.7F)), PartPose.offsetAndRotation(0.0F, 3.8F, 3.7F, 0.09817477F, 0.0F, 0.0F));
        root.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(0, 28).mirror().addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("forearm1", CubeListBuilder.create().texOffs(16, 28).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);
        root.addOrReplaceChild("forearm2", CubeListBuilder.create().texOffs(16, 28).mirror().addBox(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);
        root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 28).mirror().addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("foreleg1", CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("foreleg2", CubeListBuilder.create().texOffs(32, 28).mirror().addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.ZERO);
        root.addOrReplaceChild("shoulder1", CubeListBuilder.create().texOffs(28, 16).addBox(-4.0F, -1.5F, -3.0F, 8.0F, 3.0F, 6.0F), PartPose.ZERO);
        root.addOrReplaceChild("shoulder2", CubeListBuilder.create().texOffs(28, 16).mirror().addBox(-4.0F, -1.5F, -3.0F, 8.0F, 3.0F, 6.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    public ModelPart getPart(int index) {
        return this.parts.get(index);
    }
}
