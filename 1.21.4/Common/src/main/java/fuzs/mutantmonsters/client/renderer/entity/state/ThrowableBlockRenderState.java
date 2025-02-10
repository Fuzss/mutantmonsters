package fuzs.mutantmonsters.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ThrowableBlockRenderState extends EntityRenderState {
    public BlockState blockState;
    public EntityType<?> ownerType;
    public float yRot;
}
