package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.EndersoulFragmentModel;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class EndersoulFragmentRenderer extends EntityRenderer<EndersoulFragment> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul_fragment.png");

    private final EndersoulFragmentModel model;

    public EndersoulFragmentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new EndersoulFragmentModel(context.bakeLayer(ClientModRegistry.ENDERSOUL_FRAGMENT));
        this.shadowRadius = 0.3F;
        this.shadowStrength = 0.5F;
    }

    @Override
    public void render(EndersoulFragment endersoulFragment, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(endersoulFragment, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(0.0, -1.9, 0.0);
        poseStack.scale(1.6F, 1.6F, 1.6F);
        float ageInTicks = (float) endersoulFragment.tickCount + partialTick;
        this.model.setupAnim(endersoulFragment, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(
                MutantRenderTypes.energySwirl(TEXTURE_LOCATION, ageInTicks * 0.008F, ageInTicks * 0.008F));
        int color = FastColor.ARGB32.colorFromFloat(1.0F, 0.9F, 0.3F, 1.0F);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EndersoulFragment endersoulFragment) {
        return TEXTURE_LOCATION;
    }
}
