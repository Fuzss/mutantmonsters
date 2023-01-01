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

public class EndersoulFragmentRenderer extends EntityRenderer<EndersoulFragment> {
    private static final ResourceLocation TEXTURE = MutantMonsters.entityTexture("endersoul_fragment");
    private final EndersoulFragmentModel model;

    public EndersoulFragmentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new EndersoulFragmentModel(context.bakeLayer(ClientModRegistry.ENDERSOUL_FRAGMENT));
        this.shadowRadius = 0.3F;
        this.shadowStrength = 0.5F;
    }

    @Override
    public void render(EndersoulFragment entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0, -1.9, 0.0);
        matrixStackIn.scale(1.6F, 1.6F, 1.6F);
        float ageInTicks = (float)entityIn.tickCount + partialTicks;
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(MutantRenderTypes.energySwirl(TEXTURE, ageInTicks * 0.008F, ageInTicks * 0.008F));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.9F, 0.3F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EndersoulFragment entity) {
        return TEXTURE;
    }
}
