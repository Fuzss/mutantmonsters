package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.SpiderPigModel;
import fuzs.mutantmonsters.client.renderer.entity.state.SpiderPigRenderState;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class SpiderPigRenderer extends MobRenderer<SpiderPig, SpiderPigRenderState, SpiderPigModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/spider_pig/spider_pig.png");
    public static final ResourceLocation SADDLE_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/spider_pig/saddle.png");

    public SpiderPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderPigModel(context.bakeLayer(ModelLayerLocations.SPIDER_PIG)), 0.8F);
        this.addLayer(new SaddleLayer<>(this, this.model, SADDLE_TEXTURE_LOCATION));
    }

    @Override
    public void extractRenderState(SpiderPig entity, SpiderPigRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.attackTime = entity.getAttackAnim(partialTick);
        reusedState.isSaddled = entity.isSaddled();
    }

    @Override
    public SpiderPigRenderState createRenderState() {
        return new SpiderPigRenderState();
    }

    @Override
    protected float getFlipDegrees() {
        return 180.0F;
    }

    @Override
    protected void scale(SpiderPigRenderState renderState, PoseStack poseStack) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderPigRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
