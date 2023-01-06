package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.SpiderPigModel;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class SpiderPigRenderer extends MobRenderer<SpiderPig, SpiderPigModel> {
    private static final ResourceLocation TEXTURE = MutantMonstersClient.entityTexture("spider_pig/spider_pig");

    public SpiderPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderPigModel(context.bakeLayer(ClientModRegistry.SPIDER_PIG)), 0.8F);
        this.addLayer(new SaddleLayer<>(this, this.model, MutantMonstersClient.entityTexture("spider_pig/saddle")));
    }

    @Override
    protected float getFlipDegrees(SpiderPig entityLivingBaseIn) {
        return 180.0F;
    }

    @Override
    protected void scale(SpiderPig entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderPig entity) {
        return TEXTURE;
    }
}
