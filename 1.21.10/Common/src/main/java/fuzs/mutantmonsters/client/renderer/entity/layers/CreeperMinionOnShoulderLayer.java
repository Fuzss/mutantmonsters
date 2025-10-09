package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import fuzs.mutantmonsters.client.renderer.entity.CreeperMinionRenderer;
import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionRenderState;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.client.renderer.v1.RenderStateExtraData;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Optional;

public class CreeperMinionOnShoulderLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final ContextKey<Optional<CreeperMinionRenderState>> LEFT_SHOULDER_CREEPER_MINION_KEY = new ContextKey<>(
            MutantMonsters.id("left_shoulder_creeper_minion"));
    private static final ContextKey<Optional<CreeperMinionRenderState>> RIGHT_SHOULDER_CREEPER_MINION_KEY = new ContextKey<>(
            MutantMonsters.id("right_shoulder_creeper_minion"));

    private final EquipmentAssetManager equipmentAssets;
    private final CreeperMinionModel model;
    private final CreeperMinionModel chargedModel;

    public CreeperMinionOnShoulderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> entityRenderer, EntityRendererProvider.Context context) {
        super(entityRenderer);
        this.equipmentAssets = context.getEquipmentAssets();
        this.model = new CreeperMinionModel(context.bakeLayer(ModModelLayers.CREEPER_MINION_SHOULDER));
        this.chargedModel = new CreeperMinionModel(context.bakeLayer(ModModelLayers.CREEPER_MINION_SHOULDER_ARMOR));
    }

    public static void addLivingEntityRenderLayers(EntityType<?> entityType, LivingEntityRenderer<?, ?, ?> entityRenderer, EntityRendererProvider.Context context) {
        if (entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
            avatarRenderer.addLayer(new CreeperMinionOnShoulderLayer(avatarRenderer, context));
        }
    }

    public static void onExtractRenderState(Entity entity, EntityRenderState renderState, float partialTick) {
        if (entity instanceof Avatar && renderState instanceof AvatarRenderState avatarRenderState) {
            RenderStateExtraData.set(renderState,
                    LEFT_SHOULDER_CREEPER_MINION_KEY,
                    copyRenderState(avatarRenderState,
                            ModRegistry.LEFT_SHOULDER_CREEPER_MINION_ATTACHMENT_TYPE.getOrDefault(entity,
                                    Optional.empty())));
            RenderStateExtraData.set(renderState,
                    RIGHT_SHOULDER_CREEPER_MINION_KEY,
                    copyRenderState(avatarRenderState,
                            ModRegistry.RIGHT_SHOULDER_CREEPER_MINION_ATTACHMENT_TYPE.getOrDefault(entity,
                                    Optional.empty())));
        }
    }

    private static Optional<CreeperMinionRenderState> copyRenderState(LivingEntityRenderState renderState, Optional<Boolean> isPowered) {
        if (isPowered.isEmpty()) {
            return Optional.empty();
        } else {
            CreeperMinionRenderState creeperMinionRenderState = new CreeperMinionRenderState();
            creeperMinionRenderState.ageInTicks = renderState.ageInTicks;
            creeperMinionRenderState.walkAnimationPos = renderState.walkAnimationPos;
            creeperMinionRenderState.walkAnimationSpeed = renderState.walkAnimationSpeed;
            creeperMinionRenderState.yRot = renderState.yRot;
            creeperMinionRenderState.xRot = renderState.xRot;
            creeperMinionRenderState.inSittingPose = true;
            creeperMinionRenderState.outlineColor = renderState.outlineColor;
            creeperMinionRenderState.isPowered = isPowered.get();
            return Optional.of(creeperMinionRenderState);
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot) {
        RenderStateExtraData.getOrDefault(renderState, LEFT_SHOULDER_CREEPER_MINION_KEY, Optional.empty())
                .ifPresent((CreeperMinionRenderState creeperMinionRenderState) -> this.renderOnShoulder(poseStack,
                        nodeCollector,
                        packedLight,
                        renderState,
                        creeperMinionRenderState,
                        true));
        RenderStateExtraData.getOrDefault(renderState, RIGHT_SHOULDER_CREEPER_MINION_KEY, Optional.empty())
                .ifPresent((CreeperMinionRenderState creeperMinionRenderState) -> this.renderOnShoulder(poseStack,
                        nodeCollector,
                        packedLight,
                        renderState,
                        creeperMinionRenderState,
                        false));
    }

    private void renderOnShoulder(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, AvatarRenderState avatarRenderState, CreeperMinionRenderState renderState, boolean isLeftShoulder) {
        poseStack.pushPose();
        poseStack.translate(0.42F * (isLeftShoulder ? 1.0F : -1.0F),
                avatarRenderState.isCrouching ? -0.55F : -0.75F,
                0.0F);
        if (this.hasLayer(avatarRenderState.chestEquipment, EquipmentClientInfo.LayerType.HUMANOID)) {
            poseStack.translate(0.0F, -0.053125F, 0.0F);
        }

        poseStack.scale(0.5F, 0.5F, 0.5F);
        nodeCollector.submitModel(this.model,
                renderState,
                poseStack,
                this.model.renderType(CreeperMinionRenderer.TEXTURE_LOCATION),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                avatarRenderState.outlineColor,
                null);
        if (renderState.isPowered) {
            RenderType renderType = ModRenderType.energySwirl(PowerableLayer.LIGHTNING_TEXTURE,
                    avatarRenderState.ageInTicks * 0.01F,
                    avatarRenderState.ageInTicks * 0.01F);
            int color = ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F);
            nodeCollector.submitModel(this.chargedModel,
                    renderState,
                    poseStack,
                    renderType,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    color,
                    null,
                    avatarRenderState.outlineColor,
                    null);
        }

        poseStack.popPose();
    }

    /**
     * @see net.minecraft.client.renderer.entity.layers.CapeLayer#hasLayer(ItemStack, EquipmentClientInfo.LayerType)
     */
    private boolean hasLayer(ItemStack itemStack, EquipmentClientInfo.LayerType layer) {
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && !equippable.assetId().isEmpty()) {
            EquipmentClientInfo equipmentClientInfo = this.equipmentAssets.get(equippable.assetId().get());
            return !equipmentClientInfo.getLayers(layer).isEmpty();
        } else {
            return false;
        }
    }
}
