package fuzs.mutantmonsters.client.renderer.entity.layers;

import fuzs.mutantmonsters.client.renderer.entity.state.PowerableRenderState;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

/**
 * We cannot use our {@link PowerableRenderState} as type parameter, as we rely on the vanilla creeper model, which does
 * not permit a custom render state type parameter for the model class.
 */
public class PowerableLayer<S extends EntityRenderState, M extends EntityModel<S>> extends EnergySwirlLayer<S, M> {
    public static final ResourceLocation LIGHTNING_TEXTURE = ResourceLocationHelper.withDefaultNamespace(
            "textures/entity/creeper/creeper_armor.png");

    private final M model;

    public PowerableLayer(RenderLayerParent<S, M> renderer, M model) {
        super(renderer);
        this.model = model;
    }

    @Override
    protected boolean isPowered(S renderState) {
        return ((PowerableRenderState) renderState).isPowered();
    }

    @Override
    protected float xOffset(float tickCount) {
        return tickCount * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return LIGHTNING_TEXTURE;
    }

    @Override
    protected M model() {
        return this.model;
    }
}
