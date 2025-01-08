package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.PowerableLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.CreeperMinionRenderState;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CreeperMinionRenderer extends MobRenderer<CreeperMinion, CreeperMinionRenderState, CreeperMinionModel> {
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace(
            "textures/entity/creeper/creeper.png");

    public CreeperMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperMinionModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION)), 0.25F);
        this.addLayer(new PowerableLayer<>(this,
                new CreeperMinionModel(context.bakeLayer(ModelLayerLocations.CREEPER_MINION_ARMOR))));
    }

    @Override
    public void extractRenderState(CreeperMinion entity, CreeperMinionRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.inSittingPose = entity.isInSittingPose();
        reusedState.flashIntensity = entity.getFlashIntensity(partialTick);
        reusedState.isPowered = entity.isCharged();
    }

    @Override
    public CreeperMinionRenderState createRenderState() {
        return new CreeperMinionRenderState();
    }

    @Override
    protected float getWhiteOverlayProgress(CreeperMinionRenderState renderState) {
        float flashIntensity = renderState.flashIntensity;
        return (int) (flashIntensity * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(flashIntensity, 0.5F, 1.0F);
    }

    @Override
    protected void scale(CreeperMinionRenderState creeperMinion, PoseStack poseStack) {
        float flashIntensity = creeperMinion.flashIntensity;
        float f1 = 1.0F + Mth.sin(flashIntensity * 100.0F) * flashIntensity * 0.01F;
        flashIntensity = Mth.clamp(flashIntensity, 0.0F, 1.0F);
        flashIntensity *= flashIntensity;
        flashIntensity *= flashIntensity;
        float f2 = (1.0F + flashIntensity * 0.4F) * f1;
        float f3 = (1.0F + flashIntensity * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperMinionRenderState creeperMinion) {
        return TEXTURE_LOCATION;
    }
}
