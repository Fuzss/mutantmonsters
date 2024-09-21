package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.CreeperMinionModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperChargeLayer;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CreeperMinionRenderer extends MobRenderer<CreeperMinion, CreeperMinionModel> {
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocationHelper.withDefaultNamespace("textures/entity/creeper/creeper.png");

    public CreeperMinionRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperMinionModel(context.bakeLayer(ClientModRegistry.CREEPER_MINION)), 0.25F);
        this.addLayer(new CreeperChargeLayer<>(this,
                new CreeperMinionModel(context.bakeLayer(ClientModRegistry.CREEPER_MINION_ARMOR))
        ));
    }

    @Override
    protected float getWhiteOverlayProgress(CreeperMinion creeperMinion, float partialTick) {
        float f = creeperMinion.getFlashIntensity(partialTick);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    protected void scale(CreeperMinion creeperMinion, PoseStack poseStack, float partialTick) {
        float f = creeperMinion.getFlashIntensity(partialTick);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    public ResourceLocation getTextureLocation(CreeperMinion creeperMinion) {
        return TEXTURE_LOCATION;
    }
}
