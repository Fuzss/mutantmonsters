package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.layers.*;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MutantEndermanRenderer extends MobRenderer<MutantEnderman, MutantEndermanRenderState, MutantEndermanModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_enderman/mutant_enderman.png");

    private final MutantEndermanTeleportLayer teleportLayer;

    public MutantEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantEndermanModel(context.bakeLayer(ModelLayerLocations.MUTANT_ENDERMAN)), 0.8F);
        // order is important here, death layer must come first, will break eyes rendering if not
        this.addLayer(new MutantEndermanDeathLayer(this));
        this.addLayer(new MutantEndermanEyesLayer(this));
        this.addLayer(new MutantEndermanCloneLayer(this, context.getModelSet()));
        this.teleportLayer = new MutantEndermanTeleportLayer(this);
        this.addLayer(this.teleportLayer);
        this.addLayer(new MutantEndermanScreamLayer(this));
        this.addLayer(new MutantEndermanHeldBlocksLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public boolean shouldRender(MutantEnderman mutantEnderman, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(mutantEnderman, camera, camX, camY, camZ)) {
            return true;
        } else if (mutantEnderman.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
            return mutantEnderman.getTeleportPosition()
                    .map(Vec3::atBottomCenterOf)
                    .map(mutantEnderman.getType().getDimensions()::makeBoundingBox)
                    .filter(camera::isVisible)
                    .isPresent();
        } else {
            return false;
        }
    }

    @Override
    public void render(MutantEndermanRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (renderState.isClone) {
            this.shadowRadius = 0.5F;
            this.shadowStrength = 0.5F;
        } else {
            this.shadowRadius = 0.8F;
            this.shadowStrength = renderState.deathTime > 80.0F ? 1.0F - getDeathProgress(renderState) : 1.0F;
        }

        super.render(renderState, poseStack, bufferSource, packedLight);

        if (renderState.animation == MutantEnderman.TELEPORT_ANIMATION && renderState.teleportPosition != null) {
            poseStack.pushPose();
            // tried moving this to a separate layer, pose stack manipulations don't add up anymore though when run from layer rendering
            poseStack.translate(renderState.teleportPosition.getX() + 0.5 - renderState.x,
                    renderState.teleportPosition.getY() - renderState.y,
                    renderState.teleportPosition.getZ() + 0.5 - renderState.z);
            this.teleportLayer.setShrinking(true);
            super.render(renderState, poseStack, bufferSource, packedLight);
            this.teleportLayer.setShrinking(false);
            poseStack.popPose();
        }
    }

    public static float getDeathProgress(LivingEntityRenderState renderState) {
        return (renderState.deathTime - 80.0F) / (MutantEnderman.DEATH_ANIMATION.duration() - 80.0F);
    }

    @Override
    public Vec3 getRenderOffset(MutantEndermanRenderState renderState) {
        return renderState.renderOffset != null ? renderState.renderOffset : super.getRenderOffset(renderState);
    }

    @Override
    public void extractRenderState(MutantEnderman entity, MutantEndermanRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.hasRedOverlay = entity.hurtTime > 0;
        reusedState.isCreepy = entity.isAggressive();
        reusedState.animationTime =
                entity.getAnimationTick() > 0 ? entity.getAnimationTick() + partialTick : entity.getAnimationTick();
        reusedState.animation = entity.getAnimation();
        reusedState.armScale = entity.getArmScale(partialTick);
        reusedState.isClone = entity.isClone();
        System.arraycopy(entity.heldBlocks, 0, reusedState.heldBlocks, 0, entity.heldBlocks.length);
        reusedState.activeArm = entity.getActiveArm();
        boolean hasTarget = entity.hasTargetTicks > 0;
        for (int i = 0; i < entity.heldBlockTicks.length; i++) {
            reusedState.heldBlockTicks[i] = entity.heldBlockTicks[i] +
                    (entity.heldBlockTicks[i] > 0 ? (hasTarget ? partialTick : -partialTick) : 0.0F);
        }
        reusedState.teleportPosition = entity.getTeleportPosition().orElse(null);
        reusedState.renderOffset = this.getRenderOffset(entity);
    }

    @Nullable
    private Vec3 getRenderOffset(MutantEnderman mutantEnderman) {
        boolean stare = mutantEnderman.getAnimation() == MutantEnderman.STARE_ANIMATION;
        boolean scream = mutantEnderman.getAnimation() == MutantEnderman.SCREAM_ANIMATION;
        boolean clone = mutantEnderman.isClone() && mutantEnderman.isAggressive();
        boolean telesmash = mutantEnderman.getAnimation() == MutantEnderman.TELESMASH_ANIMATION &&
                mutantEnderman.getAnimationTick() < 18.0F;
        boolean death = mutantEnderman.getAnimation() == MutantEnderman.DEATH_ANIMATION;
        if (stare || scream || clone || telesmash || death) {
            double shake = 0.03;
            if (clone) {
                shake = 0.02;
            } else if (death) {
                shake = mutantEnderman.getAnimationTick() < 80.0F ? 0.02 : 0.05;
            } else if (mutantEnderman.getAnimationTick() >= 40.0F) {
                shake *= 0.5;
            }

            return new Vec3(mutantEnderman.getRandom().nextGaussian() * shake,
                    0.0,
                    mutantEnderman.getRandom().nextGaussian() * shake);
        } else {
            return null;
        }
    }

    @Override
    public MutantEndermanRenderState createRenderState() {
        return new MutantEndermanRenderState();
    }

    @Override
    public ResourceLocation getTextureLocation(MutantEndermanRenderState renderState) {
        return TEXTURE_LOCATION;
    }

    @Override
    public RenderType getRenderType(MutantEndermanRenderState renderState, boolean bodyVisible, boolean translucent, boolean glowing) {
        // prevents the model from rendering, layers are still drawn
        if (renderState.isClone || renderState.deathTime > 80.0F) {
            return null;
        } else {
            return super.getRenderType(renderState, bodyVisible, translucent, glowing);
        }
    }

    @Override
    protected float getFlipDegrees() {
        return 0.0F;
    }

    @Override
    protected AABB getBoundingBoxForCulling(MutantEnderman mutantEnderman) {
        return super.getBoundingBoxForCulling(mutantEnderman).inflate(3.0);
    }
}
