package fuzs.mutantmonsters.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

import java.util.Map;

public class MutantSkeletonPartModel extends Model<Unit> {
    private final Map<MutantSkeletonBodyPart.BodyPart, ModelPart> bodyParts;

    public MutantSkeletonPartModel(ModelPart modelPart, ModelPart spineModelPart) {
        super(modelPart, RenderTypes::entityCutoutNoCull);
        ImmutableMap.Builder<MutantSkeletonBodyPart.BodyPart, ModelPart> builder = ImmutableMap.builder();
        builder.put(MutantSkeletonBodyPart.BodyPart.PELVIS, modelPart.getChild("pelvis"));
        MutantSkeletonModel.Spine spine = new MutantSkeletonModel.Spine(spineModelPart);
        spine.setupAnim(false);
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_UPPER_RIB, spine.side1[0]);
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_UPPER_RIB, spine.side2[0]);
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_MIDDLE_RIB, spine.side1[0]);
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_MIDDLE_RIB, spine.side2[0]);
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_LOWER_RIB, spine.side1[0]);
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_LOWER_RIB, spine.side2[0]);
        builder.put(MutantSkeletonBodyPart.BodyPart.HEAD, modelPart.getChild("head"));
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_ARM, modelPart.getChild("arm1"));
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_ARM, modelPart.getChild("arm2"));
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_FORE_ARM, modelPart.getChild("forearm1"));
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_FORE_ARM, modelPart.getChild("forearm2"));
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_LEG, modelPart.getChild("leg1"));
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_LEG, modelPart.getChild("leg2"));
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_FORE_LEG, modelPart.getChild("foreleg1"));
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_FORE_LEG, modelPart.getChild("foreleg2"));
        builder.put(MutantSkeletonBodyPart.BodyPart.LEFT_SHOULDER, modelPart.getChild("shoulder1"));
        builder.put(MutantSkeletonBodyPart.BodyPart.RIGHT_SHOULDER, modelPart.getChild("shoulder2"));
        this.bodyParts = Maps.immutableEnumMap(builder.build());
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("pelvis",
                CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F)),
                PartPose.ZERO);
        head.addOrReplaceChild("jaw",
                CubeListBuilder.create()
                        .texOffs(72, 0)
                        .addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.7F)),
                PartPose.offsetAndRotation(0.0F, 3.8F, 3.7F, 0.09817477F, 0.0F, 0.0F));
        root.addOrReplaceChild("arm1",
                CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("arm2",
                CubeListBuilder.create().texOffs(0, 28).mirror().addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("forearm1",
                CubeListBuilder.create()
                        .texOffs(16, 28)
                        .addBox(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)),
                PartPose.ZERO);
        root.addOrReplaceChild("forearm2",
                CubeListBuilder.create()
                        .texOffs(16, 28)
                        .mirror()
                        .addBox(-2.0F, -7.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(-0.01F)),
                PartPose.ZERO);
        root.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(0, 28).mirror().addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("foreleg1",
                CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("foreleg2",
                CubeListBuilder.create().texOffs(32, 28).mirror().addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("shoulder1",
                CubeListBuilder.create().texOffs(28, 16).addBox(-4.0F, -1.5F, -3.0F, 8.0F, 3.0F, 6.0F),
                PartPose.ZERO);
        root.addOrReplaceChild("shoulder2",
                CubeListBuilder.create().texOffs(28, 16).mirror().addBox(-4.0F, -1.5F, -3.0F, 8.0F, 3.0F, 6.0F),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    public ModelPart getBodyPart(MutantSkeletonBodyPart.BodyPart bodyPart) {
        return this.bodyParts.get(bodyPart);
    }
}
