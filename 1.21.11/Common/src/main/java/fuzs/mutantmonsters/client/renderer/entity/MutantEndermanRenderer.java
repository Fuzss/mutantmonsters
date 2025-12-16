package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.entity.layers.*;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class MutantEndermanRenderer extends MobRenderer<MutantEnderman, MutantEndermanRenderState, MutantEndermanModel> {
    public static final Identifier TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_enderman/mutant_enderman.png");

    private final MutantEndermanTeleportLayer teleportLayer;

    public MutantEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantEndermanModel(context.bakeLayer(ModModelLayers.MUTANT_ENDERMAN)), 0.8F);
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
    public void submit(MutantEndermanRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
        if (renderState.animation == MutantEnderman.TELEPORT_ANIMATION && renderState.teleportPosition != null) {
            poseStack.pushPose();
            // tried moving this to a separate layer, pose stack manipulations don't add up anymore though when run from layer rendering
            poseStack.translate(renderState.teleportPosition.getX() + 0.5 - renderState.x,
                    renderState.teleportPosition.getY() - renderState.y,
                    renderState.teleportPosition.getZ() + 0.5 - renderState.z);
            this.teleportLayer.setShrinking(true);
            super.submit(renderState, poseStack, nodeCollector, cameraRenderState);
            this.teleportLayer.setShrinking(false);
            poseStack.popPose();
        }
    }

    @Override
    protected float getShadowRadius(MutantEndermanRenderState renderState) {
        return renderState.isClone ? 0.5F : 0.8F;
    }

    @Override
    protected float getShadowStrength(MutantEndermanRenderState renderState) {
        return renderState.isClone ? 0.5F : renderState.deathTime > 80.0F ? 1.0F - getDeathProgress(renderState) : 1.0F;
    }

    public static float getDeathProgress(LivingEntityRenderState renderState) {
        return (renderState.deathTime - 80.0F) / (MutantEnderman.DEATH_ANIMATION.duration() - 80.0F);
    }

    @Override
    public Vec3 getRenderOffset(MutantEndermanRenderState renderState) {
        return renderState.renderOffset != null ? renderState.renderOffset : super.getRenderOffset(renderState);
    }

    @Override
    public void extractRenderState(MutantEnderman mutantEnderman, MutantEndermanRenderState reusedState, float partialTick) {
        super.extractRenderState(mutantEnderman, reusedState, partialTick);
        reusedState.hasRedOverlay = mutantEnderman.hurtTime > 0;
        reusedState.isCreepy = mutantEnderman.isAggressive();
        reusedState.animationTime =
                mutantEnderman.getAnimationTick() > 0 ? mutantEnderman.getAnimationTick() + partialTick :
                        mutantEnderman.getAnimationTick();
        reusedState.animation = mutantEnderman.getAnimation();
        reusedState.armScale = mutantEnderman.getArmScale(partialTick);
        reusedState.isClone = mutantEnderman.isClone();
        for (int i = 0; i < reusedState.heldBlocks.length; i++) {
            reusedState.heldBlocks[i] = mutantEnderman.getHeldBlock(i).orElse(null);
        }
        reusedState.activeArm = mutantEnderman.getActiveArm();
        boolean hasTarget = mutantEnderman.hasTargetTicks > 0;
        for (int i = 0; i < mutantEnderman.heldBlockTicks.length; i++) {
            reusedState.heldBlockTicks[i] = mutantEnderman.heldBlockTicks[i] + (mutantEnderman.heldBlockTicks[i] > 0 ?
                    (hasTarget ? partialTick : -partialTick) : 0.0F);
        }
        reusedState.teleportPosition = mutantEnderman.getTeleportPosition().orElse(null);
        reusedState.renderOffset = this.getRenderOffset(mutantEnderman);
    }

    @Nullable
    private Vec3 getRenderOffset(MutantEnderman mutantEnderman) {
        boolean stare = mutantEnderman.getAnimation() == MutantEnderman.STARE_ANIMATION;
        boolean scream = mutantEnderman.getAnimation() == MutantEnderman.SCREAM_ANIMATION;
        boolean clone = mutantEnderman.isClone() && mutantEnderman.isAggressive();
        boolean telesmash = mutantEnderman.getAnimation() == MutantEnderman.TELESMASH_ANIMATION
                && mutantEnderman.getAnimationTick() < 18.0F;
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
    public Identifier getTextureLocation(MutantEndermanRenderState renderState) {
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
