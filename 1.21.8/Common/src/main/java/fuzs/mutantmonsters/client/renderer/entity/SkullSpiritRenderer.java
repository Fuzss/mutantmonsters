package fuzs.mutantmonsters.client.renderer.entity;

import fuzs.mutantmonsters.world.entity.SkullSpirit;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SkullSpiritRenderer extends EntityRenderer<SkullSpirit, EntityRenderState> {

    public SkullSpiritRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
