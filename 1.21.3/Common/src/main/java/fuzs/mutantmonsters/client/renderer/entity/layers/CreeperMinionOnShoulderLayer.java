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
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CreeperMinionOnShoulderLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    static final RenderPropertyKey<Optional<Boolean>> CREEPER_ON_LEFT_SHOULDER_RENDER_PROPERTY_KEY = new RenderPropertyKey<>(
            MutantMonsters.id("creeper_on_left_shoulder"));
    static final RenderPropertyKey<Optional<Boolean>> CREEPER_ON_RIGHT_SHOULDER_RENDER_PROPERTY_KEY = new RenderPropertyKey<>(
            MutantMonsters.id("creeper_on_right_shoulder"));

    private final CreeperMinionModel model;
    private final CreeperMinionModel chargedModel;
    private final CreeperMinionRenderState renderState = new CreeperMinionRenderState();

    public CreeperMinionOnShoulderLayer(RenderLayerParent<PlayerRenderState, PlayerModel> entityRenderer, EntityRendererProvider.Context context) {
        super(entityRenderer);
        this.model = new CreeperMinionModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_SHOULDER));
        this.chargedModel = new CreeperMinionModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_SHOULDER_ARMOR));
        this.renderState.inSittingPose = true;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, PlayerRenderState renderState, float yRot, float xRot) {
        RenderPropertyKey.getRenderProperty(renderState, CREEPER_ON_LEFT_SHOULDER_RENDER_PROPERTY_KEY)
                .ifPresent((Boolean isPowered) -> this.renderOnShoulder(poseStack,
                        bufferSource,
                        packedLight,
                        renderState,
                        yRot,
                        xRot,
                        true,
                        isPowered));
        RenderPropertyKey.getRenderProperty(renderState, CREEPER_ON_RIGHT_SHOULDER_RENDER_PROPERTY_KEY)
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
        poseStack.translate(isLeftShoulder ? 0.42 : -0.42, renderState.isCrouching ? -0.55 : -0.75, 0.0);
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

    private void extractRenderState(PlayerRenderState renderState, float yRot, float xRot) {
        this.renderState.ageInTicks = renderState.ageInTicks;
        this.renderState.walkAnimationPos = renderState.walkAnimationPos;
        this.renderState.walkAnimationSpeed = renderState.walkAnimationSpeed;
        this.renderState.yRot = yRot;
        this.renderState.xRot = xRot;
    }

    public static void onExtractRenderState(Entity entity, EntityRenderState entityRenderState, float partialTick) {
        if (entity instanceof Player player && entityRenderState instanceof PlayerRenderState) {
            RenderPropertyKey.setRenderProperty(entityRenderState,
                    CREEPER_ON_LEFT_SHOULDER_RENDER_PROPERTY_KEY,
                    getCreeperMinionOnShoulder(player, true));
            RenderPropertyKey.setRenderProperty(entityRenderState,
                    CREEPER_ON_RIGHT_SHOULDER_RENDER_PROPERTY_KEY,
                    getCreeperMinionOnShoulder(player, false));
        }
    }

    private static Optional<Boolean> getCreeperMinionOnShoulder(Player player, boolean isLeftShoulder) {
        CompoundTag compoundTag = getEntityOnShoulder(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(),
                player,
                isLeftShoulder);
        return compoundTag != null ? Optional.of(compoundTag.getBoolean("Powered")) : Optional.empty();
    }

    @Nullable
    private static CompoundTag getEntityOnShoulder(EntityType<?> entityType, Player player, boolean isLeftShoulder) {
        CompoundTag compoundTag = isLeftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        return EntityType.byString(compoundTag.getString("id")).filter((EntityType<?> entityTypeX) -> {
            return entityType == entityTypeX;
        }).isPresent() ? compoundTag : null;
    }
}
