package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.MutantCreeperModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.PowerableLayer;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantCreeperRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MutantCreeperRenderer extends MobRenderer<MutantCreeper, MutantCreeperRenderState, MutantCreeperModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_creeper.png");

    public MutantCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantCreeperModel(context.bakeLayer(ModModelLayers.MUTANT_CREEPER)), 1.5F);
        this.addLayer(new PowerableLayer<>(this,
                new MutantCreeperModel(context.bakeLayer(ModModelLayers.MUTANT_CREEPER_ARMOR))));
    }

    @Override
    public void extractRenderState(MutantCreeper entity, MutantCreeperRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.attackTime = entity.getAttackAnim(partialTick);
        reusedState.isJumpAttacking = entity.isJumpAttacking();
        reusedState.overlayColor = entity.getOverlayColor(partialTick);
        reusedState.isPowered = entity.isCharged();
    }

    @Override
    public MutantCreeperRenderState createRenderState() {
        return new MutantCreeperRenderState();
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    protected float getWhiteOverlayProgress(MutantCreeperRenderState renderState) {
        return renderState.isJumpAttacking && renderState.deathTime == 0 ?
                ((int) (renderState.overlayColor * 10.0F) % 2 == 0 ? 0.0F :
                        Mth.clamp(renderState.overlayColor, 0.5F, 1.0F)) : renderState.overlayColor;
    }

    @Override
    protected void scale(MutantCreeperRenderState mutantCreeper, PoseStack poseStack) {
        float scale = 1.2F;
        this.shadowRadius = 1.5F;
        if (mutantCreeper.deathTime > 0) {
            float deathProgress = mutantCreeper.deathTime / 100.0F;
            scale -= deathProgress * 0.4F;
            this.shadowRadius -= deathProgress * 0.4F;
        }

        poseStack.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(MutantCreeperRenderState renderState) {
        return TEXTURE_LOCATION;
    }
}
