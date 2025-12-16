package fuzs.mutantmonsters.client.model;

import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionRenderState;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;

import java.util.Set;

public class CreeperMinionModel extends CreeperModel {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(false, 9.0F, 0.0F, Set.of("head"));

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public CreeperMinionModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    @Override
    public void setupAnim(CreeperRenderState renderState) {
        super.setupAnim(renderState);
        if (((CreeperMinionRenderState) renderState).inSittingPose) {
            this.head.y += 3.0F;
            this.body.y += 3.0F;
            this.rightHindLeg.y += 2.0F;
            this.rightHindLeg.z -= 1.0F;
            this.leftHindLeg.y += 2.0F;
            this.leftHindLeg.z -= 1.0F;
            this.rightFrontLeg.y += 2.0F;
            this.rightFrontLeg.z += 1.0F;
            this.leftFrontLeg.y += 2.0F;
            this.leftFrontLeg.z += 1.0F;
            this.rightHindLeg.xRot = 1.5707964F;
            this.leftHindLeg.xRot = 1.5707964F;
            this.rightFrontLeg.xRot = -1.5707964F;
            this.leftFrontLeg.xRot = -1.5707964F;
        }
    }
}
