package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperChargeLayer;
import fuzs.mutantmonsters.client.model.CreeperMinionEggModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.world.entity.CreeperMinionEgg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class CreeperMinionEggRenderer extends EntityRenderer<CreeperMinionEgg> {
    private static final ResourceLocation TEXTURE = MutantMonsters.entityTexture("creeper_minion_egg");
    private final CreeperMinionEggModel eggModel;
    private final CreeperMinionEggModel chargedModel;

    public CreeperMinionEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.eggModel = new CreeperMinionEggModel(context.bakeLayer(ClientModRegistry.CREEPER_MINION_EGG));
        this.chargedModel = new CreeperMinionEggModel(context.bakeLayer(ClientModRegistry.CREEPER_MINION_EGG_ARMOR));
        this.shadowRadius = 0.4F;
    }

    @Override
    public void render(CreeperMinionEgg entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        matrixStackIn.translate(0.0, -1.5010000467300415, 0.0);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.eggModel.renderType(this.getTextureLocation(entityIn)));
        this.eggModel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        if (entityIn.isCharged()) {
            float ageInTicks = (float)entityIn.tickCount + partialTicks;
            VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(MutantRenderTypes.energySwirl(CreeperChargeLayer.LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
            this.chargedModel.renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperMinionEgg entity) {
        return TEXTURE;
    }
}
