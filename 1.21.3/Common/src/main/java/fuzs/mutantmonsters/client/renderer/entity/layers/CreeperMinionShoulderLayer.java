package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.client.renderer.entity.CreeperMinionRenderer;
import fuzs.mutantmonsters.init.ModEntityTypes;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;
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
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderCreeperMinion(poseStack, multiBufferSource, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch, true);
        this.renderCreeperMinion(poseStack, multiBufferSource, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch, false);
    }

    private void renderCreeperMinion(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, boolean leftShoulder) {
        CompoundTag tag = leftShoulder ? livingEntity.getShoulderEntityLeft() : livingEntity.getShoulderEntityRight();
        EntityType.byString(tag.getString("id")).filter(
                ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value()::equals).ifPresent((entityType) -> {
            poseStack.pushPose();
            poseStack.translate(leftShoulder ? 0.42 : -0.42, livingEntity.isCrouching() ? -0.55 : -0.75, 0.0);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(CreeperMinionRenderer.TEXTURE_LOCATION));
            this.model.setupAnim(true, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            if (tag.getBoolean("Powered")) {
                vertexConsumer = multiBufferSource.getBuffer(MutantRenderTypes.energySwirl(CreeperChargeLayer.LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
                this.chargedModel.setupAnim(true, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                int color = FastColor.ARGB32.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F);
                this.chargedModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            }
            poseStack.popPose();
        });
    }
}
