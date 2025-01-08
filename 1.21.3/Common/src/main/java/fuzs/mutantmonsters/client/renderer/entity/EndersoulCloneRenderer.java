package fuzs.mutantmonsters.client.renderer.entity;

import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.renderer.entity.layers.EndersoulLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.EndersoulCloneRenderState;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class EndersoulCloneRenderer extends MobRenderer<EndersoulClone, EndersoulCloneRenderState, EndermanModel<EndersoulCloneRenderState>> {

    public EndersoulCloneRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermanModel<>(context.bakeLayer(ModelLayerLocations.ENDERSOUL_CLONE)), 0.5F);
        this.addLayer(new EndersoulLayer<>(this));
        this.shadowStrength = 0.5F;
    }

    @Override
    public void extractRenderState(EndersoulClone entity, EndersoulCloneRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, reusedState, partialTick);
        reusedState.isCreepy = entity.isAggressive();
        reusedState.renderOffset = new Vec3(entity.getRandom().nextGaussian() * 0.02,
                0.0,
                entity.getRandom().nextGaussian() * 0.02);
    }

    @Override
    public EndersoulCloneRenderState createRenderState() {
        return new EndersoulCloneRenderState();
    }

    @Override
    public Vec3 getRenderOffset(EndersoulCloneRenderState renderState) {
        return renderState.isCreepy ? renderState.renderOffset : super.getRenderOffset(renderState);
    }

    @Override
    protected RenderType getRenderType(EndersoulCloneRenderState endersoulClone, boolean bodyVisible, boolean translucent, boolean glowing) {
        // TODO set parent model to invisible instead
        return null;
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(EndersoulCloneRenderState renderState) {
        return EndersoulLayer.TEXTURE_LOCATION;
    }
}
