package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.CreeperMinionEggModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.layers.PowerableLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionEggRenderState;
import fuzs.mutantmonsters.client.renderer.rendertype.ModRenderTypes;
import fuzs.mutantmonsters.world.entity.CreeperMinionEgg;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Unit;

public class CreeperMinionEggRenderer extends EntityRenderer<CreeperMinionEgg, CreeperMinionEggRenderState> {
    public static final Identifier TEXTURE_LOCATION = MutantMonsters.id("textures/entity/creeper_minion_egg.png");

    private final CreeperMinionEggModel model;
    private final CreeperMinionEggModel armorModel;

    public CreeperMinionEggRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CreeperMinionEggModel(context.bakeLayer(ModModelLayers.CREEPER_MINION_EGG));
        this.armorModel = new CreeperMinionEggModel(context.bakeLayer(ModModelLayers.CREEPER_MINION_EGG_ARMOR));
        this.shadowRadius = 0.4F;
    }

    @Override
    public void submit(CreeperMinionEggRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        nodeCollector.submitModel(this.model,
                Unit.INSTANCE,
                poseStack,
                this.model.renderType(TEXTURE_LOCATION),
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                renderState.outlineColor,
                null);
        if (renderState.isCharged) {
            RenderType renderType = ModRenderTypes.energySwirl(PowerableLayer.LIGHTNING_TEXTURE,
                    renderState.ageInTicks * 0.01F,
                    renderState.ageInTicks * 0.01F);
            int color = ARGB.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F);
            nodeCollector.submitModel(this.armorModel,
                    Unit.INSTANCE,
                    poseStack,
                    renderType,
                    renderState.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    color,
                    null,
                    renderState.outlineColor,
                    null);
        }

        poseStack.popPose();
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
