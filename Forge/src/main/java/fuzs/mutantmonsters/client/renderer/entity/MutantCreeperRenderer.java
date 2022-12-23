package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.layers.CreeperChargeLayer;
import fuzs.mutantmonsters.client.renderer.entity.model.MutantCreeperModel;
import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MutantCreeperRenderer extends AlternateMobRenderer<MutantCreeperEntity, MutantCreeperModel> {
    private static final ResourceLocation TEXTURE = MutantMonsters.getEntityTexture("mutant_creeper");

    public MutantCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantCreeperModel(context.bakeLayer(ClientModRegistry.MUTANT_CREEPER)), 1.5F);
        this.addLayer(new CreeperChargeLayer<>(this, new MutantCreeperModel(context.bakeLayer(ClientModRegistry.MUTANT_CREEPER_ARMOR))));
    }

    @Override
    protected void scale(MutantCreeperEntity livingEntity, PoseStack matrixStackIn, float partialTickTime) {
        float scale = 1.2F;
        this.shadowRadius = 1.5F;
        if (livingEntity.deathTime > 0) {
            float f = (float)livingEntity.deathTime / 100.0F;
            scale -= f * 0.4F;
            this.shadowRadius -= f * 0.4F;
        }

        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    protected float getWhiteOverlayProgress(MutantCreeperEntity livingEntityIn, float partialTicks) {
        float f = livingEntityIn.getOverlayColor(partialTicks);
        return livingEntityIn.isJumpAttacking() && livingEntityIn.deathTime == 0 ? ((int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F)) : f;
    }

    @Override
    protected float getFlipDegrees(MutantCreeperEntity livingEntity) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantCreeperEntity entity) {
        return TEXTURE;
    }
}
