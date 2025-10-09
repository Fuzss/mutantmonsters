package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.PowerableLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionRenderState;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CreeperMinionRenderer extends MobRenderer<CreeperMinion, CreeperRenderState, CreeperMinionModel> {
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace(
            "textures/entity/creeper/creeper.png");

    public CreeperMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperMinionModel(context.bakeLayer(ModModelLayers.CREEPER_MINION)), 0.25F);
        this.addLayer(new PowerableLayer<>(this,
                new CreeperMinionModel(context.bakeLayer(ModModelLayers.CREEPER_MINION_ARMOR))));
    }

    @Override
    public void extractRenderState(CreeperMinion entity, CreeperRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.swelling = entity.getSwelling(partialTick);
        reusedState.isPowered = entity.isCharged();
        ((CreeperMinionRenderState) reusedState).inSittingPose = entity.isInSittingPose();
    }

    @Override
    public CreeperRenderState createRenderState() {
        return new CreeperMinionRenderState();
    }

    @Override
    protected float getWhiteOverlayProgress(CreeperRenderState renderState) {
        float flashIntensity = renderState.swelling;
        return (int) (flashIntensity * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(flashIntensity, 0.5F, 1.0F);
    }

    @Override
    protected void scale(CreeperRenderState renderState, PoseStack poseStack) {
        float flashIntensity = renderState.swelling;
        float f1 = 1.0F + Mth.sin(flashIntensity * 100.0F) * flashIntensity * 0.01F;
        flashIntensity = Mth.clamp(flashIntensity, 0.0F, 1.0F);
        flashIntensity *= flashIntensity;
        flashIntensity *= flashIntensity;
        float f2 = (1.0F + flashIntensity * 0.4F) * f1;
        float f3 = (1.0F + flashIntensity * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
