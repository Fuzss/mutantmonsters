package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantSnowGolemModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.world.entity.mutant.MutantSnowGolem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class MutantSnowGolemRenderer extends MobRenderer<MutantSnowGolem, MutantSnowGolemModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_snow_golem/mutant_snow_golem.png");
    public static final ResourceLocation JACK_O_LANTERN_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/entity/mutant_snow_golem/jack_o_lantern.png");
    private static final RenderType GLOW_RENDER_TYPE = MutantRenderTypes.eyes(
            MutantMonsters.id("textures/entity/mutant_snow_golem/glow.png"));

    public MutantSnowGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantSnowGolemModel(context.bakeLayer(ClientModRegistry.MUTANT_SNOW_GOLEM)), 0.7F);
        this.addLayer(new JackOLanternLayer(this, context.getModelSet()));
        this.addLayer(new HeldBlockLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public void render(MutantSnowGolem mutantSnowGolem, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(mutantSnowGolem, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
        Player owner = mutantSnowGolem.getOwner();
        if (owner != null) {
            poseStack.pushPose();
            if (this.shouldShowName(mutantSnowGolem)) {
                poseStack.translate(0.0, 0.259, 0.0);
            }

            this.renderNameTag(mutantSnowGolem, owner.getDisplayName().copy().withStyle((Style style) -> {
                return style.withItalic(true);
            }), poseStack, multiBufferSource, packedLight, partialTick);
            poseStack.popPose();
        }

    }

    @Override
    public ResourceLocation getTextureLocation(MutantSnowGolem mutantSnowGolem) {
        return TEXTURE_LOCATION;
    }

    static class HeldBlockLayer extends RenderLayer<MutantSnowGolem, MutantSnowGolemModel> {
        private final BlockRenderDispatcher blockRenderer;

        public HeldBlockLayer(RenderLayerParent<MutantSnowGolem, MutantSnowGolemModel> renderer, BlockRenderDispatcher blockRenderer) {
            super(renderer);
            this.blockRenderer = blockRenderer;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, MutantSnowGolem mutantSnowGolem, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (mutantSnowGolem.isThrowing() && mutantSnowGolem.getThrowingTick() < 7) {
                poseStack.pushPose();
                boolean leftHanded = mutantSnowGolem.isLeftHanded();
                float scale = Math.min(0.8F, ((float) mutantSnowGolem.getThrowingTick() + partialTick) / 7.0F);
                poseStack.translate(leftHanded ? -0.4 : 0.4, 0.0, 0.0);
                this.getParentModel().translateArm(leftHanded, poseStack);
                poseStack.translate(0.0, 0.9, 0.0);
                poseStack.scale(-scale, -scale, scale);
                poseStack.translate(-0.5, -0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                this.blockRenderer.renderSingleBlock(Blocks.ICE.defaultBlockState(), poseStack, multiBufferSource,
                        packedLight, OverlayTexture.NO_OVERLAY
                );
                poseStack.popPose();
            }
        }
    }

    static class JackOLanternLayer extends RenderLayer<MutantSnowGolem, MutantSnowGolemModel> {
        private final MutantSnowGolemModel headModel;

        public JackOLanternLayer(RenderLayerParent<MutantSnowGolem, MutantSnowGolemModel> renderer, EntityModelSet entityModelSet) {
            super(renderer);
            this.headModel = new MutantSnowGolemModel(
                    entityModelSet.bakeLayer(ClientModRegistry.MUTANT_SNOW_GOLEM_HEAD)).setRenderHeadOnly();
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, MutantSnowGolem mutantSnowGolem, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (mutantSnowGolem.hasJackOLantern()) {
                if (!mutantSnowGolem.isInvisible()) {
                    this.getParentModel().copyPropertiesTo(this.headModel);
                    renderColoredCutoutModel(this.headModel, MutantSnowGolemRenderer.JACK_O_LANTERN_TEXTURE_LOCATION,
                            poseStack, multiBufferSource, packedLight, mutantSnowGolem, -1
                    );
                }

                float green = Math.max(0.0F, 0.8F + 0.05F * Mth.cos(ageInTicks * 0.15F));
                float blue = Math.max(0.0F, 0.15F + 0.2F * Mth.cos(ageInTicks * 0.1F));
                VertexConsumer vertexConsumer = multiBufferSource.getBuffer(MutantSnowGolemRenderer.GLOW_RENDER_TYPE);
                int color = FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, green, blue);
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY,
                        color
                );
            }
        }
    }
}
