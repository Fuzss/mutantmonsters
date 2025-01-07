package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.CreeperMinionEggModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperChargeLayer;
import fuzs.mutantmonsters.world.entity.CreeperMinionEgg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class CreeperMinionEggRenderer extends EntityRenderer<CreeperMinionEgg> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/creeper_minion_egg.png");

    private final CreeperMinionEggModel eggModel;
    private final CreeperMinionEggModel chargedModel;

    public CreeperMinionEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.eggModel = new CreeperMinionEggModel(context.bakeLayer(ClientModRegistry.CREEPER_MINION_EGG));
        this.chargedModel = new CreeperMinionEggModel(context.bakeLayer(ClientModRegistry.CREEPER_MINION_EGG_ARMOR));
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(CreeperMinionEgg creeperMinionEgg, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(creeperMinionEgg, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.translate(0.0, -1.5, 0.0);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.eggModel.renderType(this.getTextureLocation(creeperMinionEgg)));
        this.eggModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        if (creeperMinionEgg.isCharged()) {
            float ageInTicks = (float)creeperMinionEgg.tickCount + partialTick;
            vertexConsumer = multiBufferSource.getBuffer(MutantRenderTypes.energySwirl(CreeperChargeLayer.LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
            int color = FastColor.ARGB32.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F);
            this.chargedModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperMinionEgg creeperMinionEgg) {
        return TEXTURE_LOCATION;
    }
}
