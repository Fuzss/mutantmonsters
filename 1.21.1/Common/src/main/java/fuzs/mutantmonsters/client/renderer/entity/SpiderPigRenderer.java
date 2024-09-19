package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.SpiderPigModel;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class SpiderPigRenderer extends MobRenderer<SpiderPig, SpiderPigModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/spider_pig/spider_pig.png");
    public static final ResourceLocation SADDLE_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/spider_pig/saddle.png");

    public SpiderPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderPigModel(context.bakeLayer(ClientModRegistry.SPIDER_PIG)), 0.8F);
        this.addLayer(new SaddleLayer<>(this, this.model, SADDLE_TEXTURE_LOCATION));
    }

    @Override
    protected float getFlipDegrees(SpiderPig spiderPig) {
        return 180.0F;
    }

    @Override
    protected void scale(SpiderPig spiderPig, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiderPig spiderPig) {
        return TEXTURE_LOCATION;
    }
}
