package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.CreeperMinionEggModel;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.layers.PowerableLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionEggRenderState;
import fuzs.mutantmonsters.world.entity.CreeperMinionEgg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public class CreeperMinionEggRenderer extends EntityRenderer<CreeperMinionEgg, CreeperMinionEggRenderState> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/creeper_minion_egg.png");

    private final CreeperMinionEggModel model;
    private final CreeperMinionEggModel armorModel;

    public CreeperMinionEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CreeperMinionEggModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_EGG));
        this.armorModel = new CreeperMinionEggModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_EGG_ARMOR));
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(CreeperMinionEggRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        if (renderState.isCharged) {
            this.renderArmor(renderState, poseStack, bufferSource, packedLight);
        }

        poseStack.popPose();
    }

    protected void renderArmor(CreeperMinionEggRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer vertexConsumer;
        RenderType renderType = ModRenderType.energySwirl(PowerableLayer.LIGHTNING_TEXTURE,
                renderState.ageInTicks * 0.01F,
                renderState.ageInTicks * 0.01F);
        vertexConsumer = bufferSource.getBuffer(renderType);
        int color = ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F);
        this.armorModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }

    @Override
    public void extractRenderState(CreeperMinionEgg entity, CreeperMinionEggRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.isCharged = entity.isCharged();
    }

    @Override
    public CreeperMinionEggRenderState createRenderState() {
        return new CreeperMinionEggRenderState();
    }
}
