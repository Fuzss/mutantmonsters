package fuzs.mutantmonsters.client.renderer.entity.state;

import fuzs.mutantmonsters.world.entity.EntityAnimation;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MutantEndermanRenderState extends EndermanRenderState {
    public float animationTime;
    public EntityAnimation animation;
    public float armScale;
    public boolean isClone;
    public int[] heldBlocks = new int[4];
    public int activeArm;
    public float[] heldBlockTicks = new float[4];
    @Nullable
    public BlockPos teleportPosition;
    @Nullable
    public Vec3 renderOffset;
}
