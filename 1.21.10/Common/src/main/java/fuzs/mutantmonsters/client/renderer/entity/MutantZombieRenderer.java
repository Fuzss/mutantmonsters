package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.MutantZombieModel;
import fuzs.mutantmonsters.client.renderer.entity.state.AnimatedEntityRenderState;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantZombieRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantZombieRenderer extends MobRenderer<MutantZombie, MutantZombieRenderState, MutantZombieModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_zombie.png");

    public MutantZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantZombieModel(context.bakeLayer(ModModelLayers.MUTANT_ZOMBIE)), 1.0F);
    }

    @Override
    public Vec3 getRenderOffset(MutantZombieRenderState renderState) {
        return new Vec3(0.0, -0.0975, 0.0);
    }

    @Override
    public void extractRenderState(MutantZombie entity, MutantZombieRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.hasRedOverlay = entity.hurtTime > 0 || entity.deathTime > 0 && entity.getLives() <= 0;
        AnimatedEntityRenderState.extractAnimatedEntityRenderState(entity,
                reusedState,
                partialTick,
                this.itemModelResolver);
        reusedState.vanishTime = entity.vanishTime > 0 ? entity.vanishTime + partialTick : entity.vanishTime;
        reusedState.throwHitTime = entity.throwHitTick == -1 ? -1.0F : entity.throwHitTick + partialTick;
        reusedState.throwFinishTime = entity.throwFinishTick == -1 ? -1.0F : entity.throwFinishTick + partialTick;
    }

    @Override
    public MutantZombieRenderState createRenderState() {
        return new MutantZombieRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(MutantZombieRenderState renderState) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected RenderType getRenderType(MutantZombieRenderState renderState, boolean bodyVisible, boolean translucent, boolean glowing) {
        return super.getRenderType(renderState, bodyVisible, translucent || renderState.vanishTime > 0.0F, glowing);
    }

    @Override
    protected void setupRotations(MutantZombieRenderState renderState, PoseStack poseStack, float bodyRot, float scale) {
        if (renderState.deathTime > 0.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
            float deathAmount = Math.min(20.0F, renderState.deathTime);
            boolean reviving = false;
            if (renderState.deathTime > 100.0F) {
                deathAmount = 140.0F - renderState.deathTime;
                reviving = true;
            }

            if (deathAmount > 0.0F) {
                float flipAmount;
                if (reviving) {
                    flipAmount = deathAmount / 40.0F * 1.6F;
                } else {
                    flipAmount = (deathAmount - 1.0F) / 20.0F * 1.6F;
                }

                flipAmount = Mth.sqrt(flipAmount);
                if (flipAmount > 1.0F) {
                    flipAmount = 1.0F;
                }

                poseStack.mulPose(Axis.XN.rotationDegrees(flipAmount * this.getFlipDegrees()));
            }
        } else {
            super.setupRotations(renderState, poseStack, bodyRot, scale);
        }
    }

    @Override
    protected float getFlipDegrees() {
        return 80.0F;
    }

    @Override
    protected void scale(MutantZombieRenderState renderState, PoseStack poseStack) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }

    @Override
    protected int getModelTint(MutantZombieRenderState renderState) {
        if (renderState.vanishTime > 0.0F) {
            float alpha = 1.0F - renderState.vanishTime / (float) MutantZombie.MAX_VANISH_TIME * 0.6F;
            return ARGB.white(Mth.clamp(alpha, 0.0F, 1.0F));
        } else {
            return super.getModelTint(renderState);
        }
    }

    @Override
    protected AABB getBoundingBoxForCulling(MutantZombie mutantZombie) {
        return super.getBoundingBoxForCulling(mutantZombie).inflate(1.0);
    }
}
