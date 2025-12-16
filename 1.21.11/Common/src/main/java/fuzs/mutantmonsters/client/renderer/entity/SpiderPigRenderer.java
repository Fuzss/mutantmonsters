package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.SpiderPigModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.layers.ModEquipmentLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.SpiderPigRenderState;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class SpiderPigRenderer extends MobRenderer<SpiderPig, SpiderPigRenderState, SpiderPigModel> {
    public static final Identifier TEXTURE_LOCATION = MutantMonsters.id("textures/entity/spider_pig.png");

    private final AdultAndBabyModelPair<SpiderPigModel> models;

    public SpiderPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderPigModel(context.bakeLayer(ModModelLayers.SPIDER_PIG)), 0.8F);
        this.models = new AdultAndBabyModelPair<>(new SpiderPigModel(context.bakeLayer(ModModelLayers.SPIDER_PIG)),
                new SpiderPigModel(context.bakeLayer(ModModelLayers.SPIDER_PIG_BABY)));
        this.addLayer(new ModEquipmentLayer<>(this,
                EquipmentAssets.SADDLE,
                MutantMonsters.id("saddle"),
                "spider_pig_saddle",
                (SpiderPigRenderState renderState) -> renderState.saddle,
                new SpiderPigModel(context.bakeLayer(ModModelLayers.SPIDER_PIG_SADDLE)),
                new SpiderPigModel(context.bakeLayer(ModModelLayers.SPIDER_PIG_BABY_SADDLE))));
    }

    @Override
    public SpiderPigRenderState createRenderState() {
        return new SpiderPigRenderState();
    }

    @Override
    public void extractRenderState(SpiderPig spiderPig, SpiderPigRenderState reusedState, float partialTick) {
        super.extractRenderState(spiderPig, reusedState, partialTick);
        reusedState.attackTime = spiderPig.getAttackAnim(partialTick);
        reusedState.saddle = spiderPig.getItemBySlot(EquipmentSlot.SADDLE).copy();
    }

    @Override
    public void submit(SpiderPigRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        this.model = this.models.getModel(renderState.isBaby);
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
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
    public Identifier getTextureLocation(SpiderPigRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
