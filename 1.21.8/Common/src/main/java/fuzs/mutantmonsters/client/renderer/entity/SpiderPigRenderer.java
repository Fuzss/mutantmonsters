package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.SpiderPigModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.SimpleEquipmentLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.SpiderPigRenderState;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import net.minecraft.client.model.AdultAndBabyModelPair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class SpiderPigRenderer extends MobRenderer<SpiderPig, SpiderPigRenderState, SpiderPigModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/spider_pig.png");
    static final EquipmentClientInfo.Layer CLIENT_INFO_LAYER = new EquipmentClientInfo.Layer(MutantMonsters.id("saddle"));
    static final StringRepresentable LAYER_TYPE = () -> "spider_pig_saddle";

    private final AdultAndBabyModelPair<SpiderPigModel> models;

    public SpiderPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderPigModel(context.bakeLayer(ModelLayerLocations.SPIDER_PIG)), 0.8F);
        this.models = new AdultAndBabyModelPair<>(new SpiderPigModel(context.bakeLayer(ModelLayerLocations.SPIDER_PIG)),
                new SpiderPigModel(context.bakeLayer(ModelLayerLocations.SPIDER_PIG_BABY)));
        this.addLayer(new SimpleEquipmentLayer<>(this,
                EquipmentAssets.SADDLE,
                CLIENT_INFO_LAYER,
                LAYER_TYPE,
                (SpiderPigRenderState renderState) -> renderState.saddle,
                new SpiderPigModel(context.bakeLayer(ModelLayerLocations.SPIDER_PIG_SADDLE)),
                new SpiderPigModel(context.bakeLayer(ModelLayerLocations.SPIDER_PIG_BABY_SADDLE))));
    }

    @Override
    public void extractRenderState(SpiderPig spiderPig, SpiderPigRenderState reusedState, float partialTick) {
        super.extractRenderState(spiderPig, reusedState, partialTick);
        reusedState.attackTime = spiderPig.getAttackAnim(partialTick);
        reusedState.saddle = spiderPig.getItemBySlot(EquipmentSlot.SADDLE).copy();
    }

    @Override
    public void render(SpiderPigRenderState livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        this.model = this.models.getModel(livingEntityRenderState.isBaby);
        super.render(livingEntityRenderState, poseStack, multiBufferSource, packedLight);
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
