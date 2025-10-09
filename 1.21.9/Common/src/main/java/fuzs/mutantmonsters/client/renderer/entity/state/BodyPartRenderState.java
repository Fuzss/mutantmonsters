package fuzs.mutantmonsters.client.renderer.entity.state;

import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class BodyPartRenderState extends EntityRenderState {
    public float xRot;
    public float yRot;
    public MutantSkeletonBodyPart.BodyPart bodyPart;
}
