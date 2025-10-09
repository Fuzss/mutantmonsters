package fuzs.mutantmonsters.client.renderer.entity;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.MutantSkeletonModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.MutantSkeletonCrossbowLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.AnimatedEntityRenderState;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSkeletonRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MutantSkeletonRenderer extends MobRenderer<MutantSkeleton, MutantSkeletonRenderState, MutantSkeletonModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_skeleton.png");

    public MutantSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context,
                new MutantSkeletonModel(context.bakeLayer(ModModelLayers.MUTANT_SKELETON),
                        context.bakeLayer(ModModelLayers.MUTANT_CROSSBOW)),
                0.6F);
        this.addLayer(new MutantSkeletonCrossbowLayer(this, context.getModelSet()));
    }

    @Override
    public void extractRenderState(MutantSkeleton entity, MutantSkeletonRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        AnimatedEntityRenderState.extractAnimatedEntityRenderState(entity,
                reusedState,
                partialTick,
                this.itemModelResolver);
    }

    @Override
    public MutantSkeletonRenderState createRenderState() {
        return new MutantSkeletonRenderState();
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantSkeletonRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
