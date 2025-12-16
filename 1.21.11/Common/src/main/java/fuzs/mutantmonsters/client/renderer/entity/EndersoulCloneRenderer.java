package fuzs.mutantmonsters.client.renderer.entity;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.state.EndersoulCloneRenderState;
import fuzs.mutantmonsters.client.renderer.rendertype.ModRenderTypes;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public class EndersoulCloneRenderer extends MobRenderer<EndersoulClone, EndersoulCloneRenderState, EndermanModel<EndersoulCloneRenderState>> {
    public static final Identifier TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul.png");

    public EndersoulCloneRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermanModel<>(context.bakeLayer(ModModelLayers.ENDERSOUL_CLONE)), 0.5F);
        this.shadowStrength = 0.5F;
    }

    @Override
    public void extractRenderState(EndersoulClone entity, EndersoulCloneRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, reusedState, partialTick, this.itemModelResolver);
        reusedState.isCreepy = entity.isAggressive();
        reusedState.renderOffset = new Vec3(entity.getRandom().nextGaussian() * 0.02,
                0.0,
                entity.getRandom().nextGaussian() * 0.02);
    }

    @Override
    public EndersoulCloneRenderState createRenderState() {
        return new EndersoulCloneRenderState();
    }

    @Override
    public Vec3 getRenderOffset(EndersoulCloneRenderState renderState) {
        return renderState.isCreepy ? renderState.renderOffset : super.getRenderOffset(renderState);
    }

    @Override
    protected RenderType getRenderType(EndersoulCloneRenderState renderState, boolean bodyVisible, boolean translucent, boolean glowing) {
        return ModRenderTypes.energySwirl(this.getTextureLocation(renderState),
                renderState.ageInTicks * 0.008F,
                renderState.ageInTicks * 0.008F);
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    public Identifier getTextureLocation(EndersoulCloneRenderState renderState) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected int getModelTint(EndersoulCloneRenderState renderState) {
        return ARGB.colorFromFloat(1.0F, 0.9F, 0.3F, 1.0F);
    }
}
