package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.model.SpiderPigModel;
import fuzs.mutantmonsters.entity.mutant.SpiderPigEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class SpiderPigRenderer extends MobRenderer<SpiderPigEntity, SpiderPigModel> {
    private static final ResourceLocation TEXTURE = MutantMonsters.getEntityTexture("spider_pig/spider_pig");

    public SpiderPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderPigModel(context.bakeLayer(ClientModRegistry.SPIDER_PIG)), 0.8F);
        this.addLayer(new SaddleLayer<>(this, this.model, MutantMonsters.getEntityTexture("spider_pig/saddle")));
    }

    @Override
    protected float getFlipDegrees(SpiderPigEntity entityLivingBaseIn) {
        return 180.0F;
    }

    @Override
    protected void scale(SpiderPigEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderPigEntity entity) {
        return TEXTURE;
    }
}
