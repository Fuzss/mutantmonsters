package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperChargeLayer;
import fuzs.mutantmonsters.client.model.MutantCreeperModel;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MutantCreeperRenderer extends AlternateMobRenderer<MutantCreeper, MutantCreeperModel> {
    private static final ResourceLocation TEXTURE_LOCATION = MutantMonstersClient.entityTexture("mutant_creeper");

    public MutantCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantCreeperModel(context.bakeLayer(ClientModRegistry.MUTANT_CREEPER)), 1.5F);
        this.addLayer(new CreeperChargeLayer<>(this, new MutantCreeperModel(context.bakeLayer(ClientModRegistry.MUTANT_CREEPER_ARMOR))));
    }

    @Override
    protected void scale(MutantCreeper mutantCreeper, PoseStack poseStack, float partialTickTime) {
        float scale = 1.2F;
        this.shadowRadius = 1.5F;
        if (mutantCreeper.deathTime > 0) {
            float f = (float)mutantCreeper.deathTime / 100.0F;
            scale -= f * 0.4F;
            this.shadowRadius -= f * 0.4F;
        }

        poseStack.scale(scale, scale, scale);
    }

    @Override
    protected float getWhiteOverlayProgress(MutantCreeper mutantCreeper, float partialTicks) {
        float f = mutantCreeper.getOverlayColor(partialTicks);
        return mutantCreeper.isJumpAttacking() && mutantCreeper.deathTime == 0 ? ((int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F)) : f;
    }

    @Override
    protected float getFlipDegrees(MutantCreeper mutantCreeper) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantCreeper mutantCreeper) {
        return TEXTURE_LOCATION;
    }
}
