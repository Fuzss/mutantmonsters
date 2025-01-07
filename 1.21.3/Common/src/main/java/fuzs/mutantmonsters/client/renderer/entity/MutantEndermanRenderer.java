package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.client.renderer.entity.layers.EndersoulLayer;
import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class MutantEndermanRenderer extends AlternateMobRenderer<MutantEnderman, EntityModel<MutantEnderman>> {
    private static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_enderman/mutant_enderman.png");
    private static final ResourceLocation DEATH_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_enderman/death.png");
    private static final RenderType EYES_RENDER_TYPE = MutantRenderTypes.eyes(
            MutantMonsters.id("textures/entity/mutant_enderman/eyes.png"));
    private static final RenderType DEATH_RENDER_TYPE = RenderType.entityDecal(TEXTURE_LOCATION);

    private final MutantEndermanModel endermanModel;
    private final EndermanModel<MutantEnderman> cloneModel;
    private boolean teleportAttack;

    public MutantEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantEndermanModel(context.bakeLayer(ClientModRegistry.MUTANT_ENDERMAN)), 0.8F);
        this.endermanModel = (MutantEndermanModel) this.model;
        this.cloneModel = new EndermanModel<>(context.bakeLayer(ClientModRegistry.ENDERMAN_CLONE));
        this.addLayer(new EyesLayer(this));
        this.addLayer(new SoulLayer(this));
        this.addLayer(new HeldBlocksLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public boolean shouldRender(MutantEnderman mutantEnderman, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(mutantEnderman, camera, camX, camY, camZ)) {
            return true;
        } else if (mutantEnderman.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
            return mutantEnderman.getTeleportPosition().map(Vec3::atBottomCenterOf).map(
                    mutantEnderman.getType().getDimensions()::makeBoundingBox).filter(camera::isVisible).isPresent();
        } else {
            return false;
        }
    }

    @Override
    public void render(MutantEnderman mutantEnderman, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        if (mutantEnderman.isClone()) {
            this.model = this.cloneModel;
            this.cloneModel.creepy = mutantEnderman.isAggressive();
            this.shadowRadius = 0.5F;
            this.shadowStrength = 0.5F;
        } else {
            this.model = this.endermanModel;
            this.shadowRadius = 0.8F;
            this.shadowStrength = mutantEnderman.deathTime > 80 ? 1.0F - getDeathProgress(mutantEnderman) : 1.0F;
        }

        this.teleportAttack = false;
        super.render(mutantEnderman, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
        if (mutantEnderman.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
            this.teleportAttack = true;
            mutantEnderman.getTeleportPosition().ifPresent((pos) -> {
                poseStack.pushPose();
                double d0 = Mth.lerp((double) partialTick, mutantEnderman.xOld, mutantEnderman.getX());
                double d1 = Mth.lerp((double) partialTick, mutantEnderman.yOld, mutantEnderman.getY());
                double d2 = Mth.lerp((double) partialTick, mutantEnderman.zOld, mutantEnderman.getZ());
                poseStack.translate((double) pos.getX() + 0.5 - d0, (double) pos.getY() - d1,
                        (double) pos.getZ() + 0.5 - d2
                );
                super.render(mutantEnderman, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
                poseStack.popPose();
            });
        }

    }

    private static float getDeathProgress(MutantEnderman mutantEnderman) {
        return (float) (mutantEnderman.deathTime - 80) / (float) (MutantEnderman.DEATH_ANIMATION.duration() - 80);
    }

    @Override
    protected boolean hasAlternateRender(MutantEnderman mutantEnderman, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        if (mutantEnderman.deathTime > 80) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(
                    RenderType.dragonExplosionAlpha(DEATH_TEXTURE_LOCATION));
            int color = FastColor.ARGB32.colorFromFloat(getDeathProgress(mutantEnderman), 1.0F, 1.0F, 1.0F);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            vertexConsumer = multiBufferSource.getBuffer(DEATH_RENDER_TYPE);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Vec3 getRenderOffset(MutantEnderman mutantEnderman, float partialTick) {
        boolean stare = mutantEnderman.getAnimation() == MutantEnderman.STARE_ANIMATION;
        boolean scream = mutantEnderman.getAnimation() == MutantEnderman.SCREAM_ANIMATION;
        boolean clone = mutantEnderman.isClone() && mutantEnderman.isAggressive();
        boolean telesmash = mutantEnderman.getAnimation() == MutantEnderman.TELESMASH_ANIMATION &&
                mutantEnderman.getAnimationTick() < 18;
        boolean death = mutantEnderman.getAnimation() == MutantEnderman.DEATH_ANIMATION;
        if (!stare && !scream && !clone && !telesmash && !death) {
            return super.getRenderOffset(mutantEnderman, partialTick);
        } else {
            double shake = 0.03;
            if (clone) {
                shake = 0.02;
            } else if (death) {
                shake = mutantEnderman.getAnimationTick() < 80 ? 0.02 : 0.05;
            } else if (mutantEnderman.getAnimationTick() >= 40) {
                shake *= 0.5;
            }

            return new Vec3(mutantEnderman.getRandom().nextGaussian() * shake, 0.0,
                    mutantEnderman.getRandom().nextGaussian() * shake
            );
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MutantEnderman mutantEnderman) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected RenderType getRenderType(MutantEnderman mutantEnderman, boolean bodyVisible, boolean translucent, boolean glowing) {
        return mutantEnderman.isClone() ? null : super.getRenderType(mutantEnderman, bodyVisible, translucent, glowing);
    }

    @Override
    protected float getFlipDegrees(MutantEnderman mutantEnderman) {
        return 0.0F;
    }

    static class HeldBlocksLayer extends RenderLayer<MutantEnderman, EntityModel<MutantEnderman>> {
        private final BlockRenderDispatcher blockRenderer;

        public HeldBlocksLayer(RenderLayerParent<MutantEnderman, EntityModel<MutantEnderman>> renderer, BlockRenderDispatcher blockRenderer) {
            super(renderer);
            this.blockRenderer = blockRenderer;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, MutantEnderman mutantEnderman, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (mutantEnderman.getAnimation() != MutantEnderman.CLONE_ANIMATION &&
                    this.getParentModel() instanceof MutantEndermanModel model) {
                for (int i = 0; i < 4; ++i) {
                    if (mutantEnderman.getHeldBlock(i) > 0) {
                        poseStack.pushPose();
                        model.translateRotateArm(poseStack, i);
                        poseStack.translate(0.0, 1.2, 0.0);
                        float tick = (float) mutantEnderman.tickCount + (float) (i + 1) * 2.0F * (float) Math.PI +
                                partialTick;
                        poseStack.mulPose(Axis.XP.rotationDegrees(tick * 10.0F));
                        poseStack.mulPose(Axis.YP.rotationDegrees(tick * 8.0F));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(tick * 6.0F));
                        poseStack.scale(-0.75F, -0.75F, 0.75F);
                        poseStack.translate(-0.5, -0.5, 0.5);
                        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                        this.blockRenderer.renderSingleBlock(Block.stateById(mutantEnderman.getHeldBlock(i)), poseStack,
                                multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY
                        );
                        poseStack.popPose();
                    }
                }

            }
        }
    }

    static class EyesLayer extends RenderLayer<MutantEnderman, EntityModel<MutantEnderman>> {

        public EyesLayer(RenderLayerParent<MutantEnderman, EntityModel<MutantEnderman>> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, MutantEnderman mutantEnderman, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!mutantEnderman.isClone()) {
                VertexConsumer ivertexbuilder = multiBufferSource.getBuffer(MutantEndermanRenderer.EYES_RENDER_TYPE);
                float alpha = mutantEnderman.deathTime > 80 ? 1.0F - MutantEndermanRenderer.getDeathProgress(
                        mutantEnderman) : 1.0F;
                int color = FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F);
                this.getParentModel().renderToBuffer(poseStack, ivertexbuilder, 0xF00000, OverlayTexture.NO_OVERLAY,
                        color
                );
            }

        }
    }

    class SoulLayer extends EndersoulLayer<MutantEnderman, EntityModel<MutantEnderman>> {

        public SoulLayer(RenderLayerParent<MutantEnderman, EntityModel<MutantEnderman>> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, MutantEnderman mutantEnderman, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            boolean teleport = mutantEnderman.getAnimation() == MutantEnderman.TELEPORT_ANIMATION &&
                    mutantEnderman.getAnimationTick() < 10;
            boolean scream = mutantEnderman.getAnimation() == MutantEnderman.SCREAM_ANIMATION;
            boolean clone = mutantEnderman.isClone();
            if (teleport || scream || clone) {
                float scale = 0.0F;
                if (teleport) {
                    scale = 1.2F + ((float) mutantEnderman.getAnimationTick() + partialTick) / 10.0F;
                    if (MutantEndermanRenderer.this.teleportAttack) {
                        scale = 2.2F - ((float) mutantEnderman.getAnimationTick() + partialTick) / 10.0F;
                    }
                }

                if (scream) {
                    if (mutantEnderman.getAnimationTick() < 40) {
                        scale = 1.2F + ((float) mutantEnderman.getAnimationTick() + partialTick) / 40.0F;
                    } else if (mutantEnderman.getAnimationTick() < 160) {
                        scale = 2.2F;
                    } else {
                        scale = Math.max(0.0F,
                                2.2F - ((float) mutantEnderman.getAnimationTick() + partialTick) / 10.0F
                        );
                    }
                }

                poseStack.pushPose();
                if (!clone) {
                    poseStack.scale(scale, scale * 0.8F, scale);
                }

                super.render(poseStack, multiBufferSource, packedLight, mutantEnderman, limbSwing, limbSwingAmount,
                        partialTick, ageInTicks, netHeadYaw, headPitch
                );
                poseStack.popPose();
            }

        }

        @Override
        protected float getAlpha(MutantEnderman mutantEnderman, float partialTick) {
            float alpha = 1.0F;
            if (mutantEnderman.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
                if (!MutantEndermanRenderer.this.teleportAttack && mutantEnderman.getAnimationTick() >= 8) {
                    alpha -= ((float) mutantEnderman.getAnimationTick() - 8.0F + partialTick) / 2.0F;
                }

                if (MutantEndermanRenderer.this.teleportAttack && mutantEnderman.getAnimationTick() < 2) {
                    alpha = ((float) mutantEnderman.getAnimationTick() + partialTick) / 2.0F;
                }
            }

            if (mutantEnderman.getAnimation() == MutantEnderman.SCREAM_ANIMATION) {
                if (mutantEnderman.getAnimationTick() < 40) {
                    alpha = ((float) mutantEnderman.getAnimationTick() + partialTick) / 40.0F;
                } else if (mutantEnderman.getAnimationTick() >= 160) {
                    alpha = 1.0F - ((float) mutantEnderman.getAnimationTick() + partialTick) / 40.0F;
                }
            }

            return alpha;
        }
    }
}
