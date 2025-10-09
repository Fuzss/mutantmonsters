package fuzs.mutantmonsters.client.renderer.entity;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.MutantSnowGolemModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.MutantSnowGolemHeldBlockLayer;
import fuzs.mutantmonsters.client.renderer.entity.layers.MutantSnowGolemJackOLanternLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantSnowGolemRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantSnowGolem;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.resources.ResourceLocation;

public class MutantSnowGolemRenderer extends MobRenderer<MutantSnowGolem, MutantSnowGolemRenderState, MutantSnowGolemModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_snow_golem/mutant_snow_golem.png");

    public MutantSnowGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantSnowGolemModel(context.bakeLayer(ModModelLayers.MUTANT_SNOW_GOLEM)), 0.7F);
        this.addLayer(new MutantSnowGolemJackOLanternLayer(this, context.getModelSet()));
        this.addLayer(new MutantSnowGolemHeldBlockLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public void extractRenderState(MutantSnowGolem entity, MutantSnowGolemRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, reusedState, this.itemModelResolver);
        reusedState.isThrowing = entity.isThrowing();
        reusedState.throwingTime = entity.getThrowingTick() > 0 ? entity.getThrowingTick() + partialTick : 0;
        reusedState.hasJackOLantern = entity.hasJackOLantern();
    }

    @Override
    public MutantSnowGolemRenderState createRenderState() {
        return new MutantSnowGolemRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(MutantSnowGolemRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
