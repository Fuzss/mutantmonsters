package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.layers.EndersoulLayer;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantEndermanRenderer extends AlternateMobRenderer<MutantEnderman, EntityModel<MutantEnderman>> {
    private static final ResourceLocation TEXTURE = MutantMonstersClient.entityTexture("mutant_enderman/mutant_enderman");
    private static final ResourceLocation DEATH_TEXTURE = MutantMonstersClient.entityTexture("mutant_enderman/death");
    private static final RenderType EYES_RENDER_TYPE = MutantRenderTypes.eyes(MutantMonstersClient.entityTexture("mutant_enderman/eyes"));
    private static final RenderType DEATH_RENDER_TYPE = RenderType.entityDecal(TEXTURE);
    private final MutantEndermanModel endermanModel;
    private final EndermanModel<MutantEnderman> cloneModel;
    private boolean teleportAttack;

    public MutantEndermanRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantEndermanModel(context.bakeLayer(ClientModRegistry.MUTANT_ENDERMAN)), 0.8F);
        this.endermanModel = (MutantEndermanModel)this.model;
        this.cloneModel = new EndermanModel<>(context.bakeLayer(ClientModRegistry.ENDERMAN_CLONE));
        this.addLayer(new EyesLayer(this));
        this.addLayer(new SoulLayer(this));
        this.addLayer(new HeldBlocksLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public boolean shouldRender(MutantEnderman livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else if (livingEntityIn.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
            AABB teleportBoundingBox = livingEntityIn.getTeleportPosition().map(Vec3::atBottomCenterOf).map(livingEntityIn.getType().getDimensions()::makeBoundingBox).orElseThrow();
            return camera.isVisible(teleportBoundingBox);
        } else {
            return false;
        }
    }

    @Override
    public void render(MutantEnderman entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.isClone()) {
            this.model = this.cloneModel;
            this.cloneModel.creepy = entityIn.isAggressive();
            this.shadowRadius = 0.5F;
            this.shadowStrength = 0.5F;
        } else {
            this.model = this.endermanModel;
            this.shadowRadius = 0.8F;
            this.shadowStrength = entityIn.deathTime > 80 ? 1.0F - getDeathProgress(entityIn) : 1.0F;
        }

        this.teleportAttack = false;
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (entityIn.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
            this.teleportAttack = true;
            entityIn.getTeleportPosition().ifPresent((pos) -> {
                matrixStackIn.pushPose();
                double d0 = Mth.lerp((double)partialTicks, entityIn.xOld, entityIn.getX());
                double d1 = Mth.lerp((double)partialTicks, entityIn.yOld, entityIn.getY());
                double d2 = Mth.lerp((double)partialTicks, entityIn.zOld, entityIn.getZ());
                matrixStackIn.translate((double)pos.getX() + 0.5 - d0, (double)pos.getY() - d1, (double)pos.getZ() + 0.5 - d2);
                super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.popPose();
            });
        }

    }

    @Override
    protected boolean hasAlternateRender(MutantEnderman mob, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        if (mob.deathTime > 80) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.dragonExplosionAlpha(DEATH_TEXTURE));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, getDeathProgress(mob));
            VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(DEATH_RENDER_TYPE);
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Vec3 getRenderOffset(MutantEnderman entityIn, float partialTicks) {
        boolean stare = entityIn.getAnimation() == MutantEnderman.STARE_ANIMATION;
        boolean scream = entityIn.getAnimation() == MutantEnderman.SCREAM_ANIMATION;
        boolean clone = entityIn.isClone() && entityIn.isAggressive();
        boolean telesmash = entityIn.getAnimation() == MutantEnderman.TELESMASH_ANIMATION && entityIn.getAnimationTick() < 18;
        boolean death = entityIn.getAnimation() == MutantEnderman.DEATH_ANIMATION;
        if (!stare && !scream && !clone && !telesmash && !death) {
            return super.getRenderOffset(entityIn, partialTicks);
        } else {
            double shake = 0.03;
            if (clone) {
                shake = 0.02;
            } else if (death) {
                shake = entityIn.getAnimationTick() < 80 ? 0.019999999552965164 : 0.05000000074505806;
            } else if (entityIn.getAnimationTick() >= 40) {
                shake *= 0.5;
            }

            return new Vec3(entityIn.getRandom().nextGaussian() * shake, 0.0, entityIn.getRandom().nextGaussian() * shake);
        }
    }

    @Override
    protected float getFlipDegrees(MutantEnderman livingEntity) {
        return 0.0F;
    }

    @Override
    protected RenderType getRenderType(MutantEnderman livingEntity, boolean isVisible, boolean visibleToSpectator, boolean isGlowing) {
        return livingEntity.isClone() ? null : super.getRenderType(livingEntity, isVisible, visibleToSpectator, isGlowing);
    }

    private static float getDeathProgress(MutantEnderman mutantEnderman) {
        return (float)(mutantEnderman.deathTime - 80) / (float)(MutantEnderman.DEATH_ANIMATION.duration() - 80);
    }

    @Override
    public ResourceLocation getTextureLocation(MutantEnderman entity) {
        return TEXTURE;
    }

    static class HeldBlocksLayer extends RenderLayer<MutantEnderman, EntityModel<MutantEnderman>> {
        private final BlockRenderDispatcher blockRenderer;

        public HeldBlocksLayer(RenderLayerParent<MutantEnderman, EntityModel<MutantEnderman>> entityRendererIn, BlockRenderDispatcher blockRenderer) {
            super(entityRendererIn);
            this.blockRenderer = blockRenderer;
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MutantEnderman entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.getAnimation() != MutantEnderman.CLONE_ANIMATION) {
                for(int i = 0; i < 4; ++i) {
                    if (entity.getHeldBlock(i) > 0) {
                        matrixStackIn.pushPose();
                        ((MutantEndermanModel)this.getParentModel()).translateRotateArm(matrixStackIn, i);
                        matrixStackIn.translate(0.0, 1.2, 0.0);
                        float tick = (float)entity.tickCount + (float)(i + 1) * 2.0F * 3.1415927F + partialTicks;
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(tick * 10.0F));
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(tick * 8.0F));
                        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(tick * 6.0F));
                        matrixStackIn.scale(-0.75F, -0.75F, 0.75F);
                        matrixStackIn.translate(-0.5, -0.5, 0.5);
                        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
                        this.blockRenderer.renderSingleBlock(Block.stateById(entity.getHeldBlock(i)), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
                        matrixStackIn.popPose();
                    }
                }

            }
        }
    }

    class SoulLayer extends EndersoulLayer<MutantEnderman, EntityModel<MutantEnderman>> {
        public SoulLayer(RenderLayerParent<MutantEnderman, EntityModel<MutantEnderman>> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MutantEnderman entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            boolean teleport = entity.getAnimation() == MutantEnderman.TELEPORT_ANIMATION && entity.getAnimationTick() < 10;
            boolean scream = entity.getAnimation() == MutantEnderman.SCREAM_ANIMATION;
            boolean clone = entity.isClone();
            if (teleport || scream || clone) {
                float scale = 0.0F;
                if (teleport) {
                    scale = 1.2F + ((float)entity.getAnimationTick() + partialTicks) / 10.0F;
                    if (MutantEndermanRenderer.this.teleportAttack) {
                        scale = 2.2F - ((float)entity.getAnimationTick() + partialTicks) / 10.0F;
                    }
                }

                if (scream) {
                    if (entity.getAnimationTick() < 40) {
                        scale = 1.2F + ((float)entity.getAnimationTick() + partialTicks) / 40.0F;
                    } else if (entity.getAnimationTick() < 160) {
                        scale = 2.2F;
                    } else {
                        scale = Math.max(0.0F, 2.2F - ((float)entity.getAnimationTick() + partialTicks) / 10.0F);
                    }
                }

                matrixStackIn.pushPose();
                if (!clone) {
                    matrixStackIn.scale(scale, scale * 0.8F, scale);
                }

                super.render(matrixStackIn, bufferIn, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                matrixStackIn.popPose();
            }

        }

        @Override
        protected float getAlpha(MutantEnderman entity, float partialTicks) {
            float alpha = 1.0F;
            if (entity.getAnimation() == MutantEnderman.TELEPORT_ANIMATION) {
                if (!MutantEndermanRenderer.this.teleportAttack && entity.getAnimationTick() >= 8) {
                    alpha -= ((float)entity.getAnimationTick() - 8.0F + partialTicks) / 2.0F;
                }

                if (MutantEndermanRenderer.this.teleportAttack && entity.getAnimationTick() < 2) {
                    alpha = ((float)entity.getAnimationTick() + partialTicks) / 2.0F;
                }
            }

            if (entity.getAnimation() == MutantEnderman.SCREAM_ANIMATION) {
                if (entity.getAnimationTick() < 40) {
                    alpha = ((float)entity.getAnimationTick() + partialTicks) / 40.0F;
                } else if (entity.getAnimationTick() >= 160) {
                    alpha = 1.0F - ((float)entity.getAnimationTick() + partialTicks) / 40.0F;
                }
            }

            return alpha;
        }
    }

    static class EyesLayer extends RenderLayer<MutantEnderman, EntityModel<MutantEnderman>> {
        public EyesLayer(RenderLayerParent<MutantEnderman, EntityModel<MutantEnderman>> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, MutantEnderman entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isClone()) {
                VertexConsumer ivertexbuilder = bufferIn.getBuffer(MutantEndermanRenderer.EYES_RENDER_TYPE);
                this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, entity.deathTime > 80 ? 1.0F - MutantEndermanRenderer.getDeathProgress(entity) : 1.0F);
            }

        }
    }
}
