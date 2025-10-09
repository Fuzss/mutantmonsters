package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.CreeperMinionRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionRenderState;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Optional;

public class CreeperMinionOnShoulderLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    static final RenderPropertyKey<Optional<Boolean>> CREEPER_ON_LEFT_SHOULDER_RENDER_PROPERTY_KEY = new RenderPropertyKey<>(
            MutantMonsters.id("creeper_on_left_shoulder"));
    static final RenderPropertyKey<Optional<Boolean>> CREEPER_ON_RIGHT_SHOULDER_RENDER_PROPERTY_KEY = new RenderPropertyKey<>(
            MutantMonsters.id("creeper_on_right_shoulder"));

    private final EquipmentAssetManager equipmentAssets;
    private final CreeperMinionModel model;
    private final CreeperMinionModel chargedModel;
    private final CreeperMinionRenderState renderState = new CreeperMinionRenderState();

    public CreeperMinionOnShoulderLayer(RenderLayerParent<PlayerRenderState, PlayerModel> entityRenderer, EntityRendererProvider.Context context) {
        super(entityRenderer);
        this.equipmentAssets = context.getEquipmentAssets();
        this.model = new CreeperMinionModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_SHOULDER));
        this.chargedModel = new CreeperMinionModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_SHOULDER_ARMOR));
        this.renderState.inSittingPose = true;
    }

    public static void addLivingEntityRenderLayers(EntityType<?> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, EntityRendererProvider.Context context) {
        if (entityRenderer instanceof PlayerRenderer playerRenderer) {
            playerRenderer.addLayer(new CreeperMinionOnShoulderLayer(playerRenderer, context));
        }
    }

    public static void onExtractRenderState(Entity entity, EntityRenderState entityRenderState, float partialTick) {
        if (entity instanceof AbstractClientPlayer player && entityRenderState instanceof PlayerRenderState) {
            RenderPropertyKey.set(entityRenderState,
                    CREEPER_ON_LEFT_SHOULDER_RENDER_PROPERTY_KEY,
                    getMinionOnShoulder(player, true));
            RenderPropertyKey.set(entityRenderState,
                    CREEPER_ON_RIGHT_SHOULDER_RENDER_PROPERTY_KEY,
                    getMinionOnShoulder(player, false));
        }
    }

    private static Optional<Boolean> getMinionOnShoulder(AbstractClientPlayer player, boolean leftShoulder) {
        CompoundTag compoundTag = leftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        if (compoundTag.isEmpty()) {
            return Optional.empty();
        } else {
            EntityType<?> entityType = compoundTag.read("id", EntityType.CODEC).orElse(null);
            return entityType == ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value() ? compoundTag.getBoolean("Powered") :
                    Optional.empty();
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, PlayerRenderState renderState, float yRot, float xRot) {
        RenderPropertyKey.getOrDefault(renderState, CREEPER_ON_LEFT_SHOULDER_RENDER_PROPERTY_KEY, Optional.empty())
                .ifPresent((Boolean isPowered) -> this.renderOnShoulder(poseStack,
                        bufferSource,
                        packedLight,
                        renderState,
                        yRot,
                        xRot,
                        true,
                        isPowered));
        RenderPropertyKey.getOrDefault(renderState, CREEPER_ON_RIGHT_SHOULDER_RENDER_PROPERTY_KEY, Optional.empty())
                .ifPresent((Boolean isPowered) -> this.renderOnShoulder(poseStack,
                        bufferSource,
                        packedLight,
                        renderState,
                        yRot,
                        xRot,
                        false,
                        isPowered));
    }

    private void renderOnShoulder(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, PlayerRenderState renderState, float yRot, float xRot, boolean isLeftShoulder, boolean isPowered) {
        this.extractRenderState(renderState, yRot, xRot);
        poseStack.pushPose();
        poseStack.translate(0.42F * (isLeftShoulder ? 1.0F : -1.0F), renderState.isCrouching ? -0.55F : -0.75F, 0.0F);
        if (this.hasLayer(renderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
            poseStack.translate(0.0F, -0.053125F, 0.0F);
        }
        poseStack.scale(0.5F, 0.5F, 0.5F);
        this.model.setupAnim(this.renderState);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(CreeperMinionRenderer.TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        if (isPowered) {
            vertexConsumer = bufferSource.getBuffer(ModRenderType.energySwirl(PowerableLayer.LIGHTNING_TEXTURE,
                    this.renderState.ageInTicks * 0.01F,
                    this.renderState.ageInTicks * 0.01F));
            this.chargedModel.setupAnim(this.renderState);
            int color = ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F);
            this.chargedModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        }
        poseStack.popPose();
    }

    private boolean hasLayer(ItemStack stack, EquipmentClientInfo.LayerType layer) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && !equippable.assetId().isEmpty()) {
            EquipmentClientInfo equipmentClientInfo = this.equipmentAssets.get((ResourceKey<EquipmentAsset>)equippable.assetId().get());
            return !equipmentClientInfo.getLayers(layer).isEmpty();
        } else {
            return false;
        }
    }

    private void extractRenderState(PlayerRenderState renderState, float yRot, float xRot) {
        this.renderState.ageInTicks = renderState.ageInTicks;
        this.renderState.walkAnimationPos = renderState.walkAnimationPos;
        this.renderState.walkAnimationSpeed = renderState.walkAnimationSpeed;
        this.renderState.yRot = yRot;
        this.renderState.xRot = xRot;
    }
}
