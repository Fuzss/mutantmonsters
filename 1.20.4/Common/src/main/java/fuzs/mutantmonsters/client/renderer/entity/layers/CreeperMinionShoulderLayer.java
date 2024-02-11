package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.CreeperMinionRenderer;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class CreeperMinionShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    private final CreeperMinionModel model;
    private final CreeperMinionModel chargedModel;
    
    public CreeperMinionShoulderLayer(RenderLayerParent<T, PlayerModel<T>> entityRenderer, EntityModelSet entityModelSet) {
        super(entityRenderer);
        this.model = new CreeperMinionModel(entityModelSet.bakeLayer(ClientModRegistry.CREEPER_MINION_SHOULDER));
        this.chargedModel = new CreeperMinionModel(entityModelSet.bakeLayer(ClientModRegistry.CREEPER_MINION_SHOULDER_ARMOR));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderCreeperMinion(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, true);
        this.renderCreeperMinion(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, false);
    }

    private void renderCreeperMinion(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, boolean leftShoulderIn) {
        CompoundTag compoundnbt = leftShoulderIn ? livingEntity.getShoulderEntityLeft() : livingEntity.getShoulderEntityRight();
        EntityType.byString(compoundnbt.getString("id")).filter(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get()::equals).ifPresent((entityType) -> {
            matrixStackIn.pushPose();
            matrixStackIn.translate(leftShoulderIn ? 0.41999998688697815 : -0.41999998688697815, livingEntity.isCrouching() ? -0.550000011920929 : -0.75, 0.0);
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(CreeperMinionRenderer.TEXTURE));
            this.model.setupAnim(null, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            if (compoundnbt.getBoolean("Powered")) {
                VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(MutantRenderTypes.energySwirl(CreeperChargeLayer.LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
                this.chargedModel.setupAnim(null, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                this.chargedModel.renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
            }

            matrixStackIn.popPose();
        });
    }
}
