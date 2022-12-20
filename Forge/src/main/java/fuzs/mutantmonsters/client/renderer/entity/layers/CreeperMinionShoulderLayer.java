package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class CreeperMinionShoulderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    public CreeperMinionShoulderLayer(RenderLayerParent<T, PlayerModel<T>> entityRenderer) {
        super(entityRenderer);
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
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(CreeperMinionRenderer.MODEL.renderType(CreeperMinionRenderer.TEXTURE));
            CreeperMinionRenderer.MODEL.setRotationAngles((CreeperMinionEntity)null, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            CreeperMinionRenderer.MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            if (compoundnbt.getBoolean("Powered")) {
                IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.energySwirl(CreeperChargeLayer.LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
                CreeperMinionRenderer.CHARGED_MODEL.setRotationAngles((CreeperMinionEntity)null, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                CreeperMinionRenderer.CHARGED_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
            }

            matrixStackIn.popPose();
        });
    }
}
