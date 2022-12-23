package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EndersoulLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation TEXTURE = MutantMonsters.prefix("textures/item/endersoul.png");

    public EndersoulLayer(RenderLayerParent<T, M> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getParentModel().prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.energySwirl(TEXTURE, ageInTicks * 0.008F, ageInTicks * 0.008F));
        this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.9F, 0.3F, 1.0F, this.getAlpha(entity, partialTicks));
    }

    protected float getAlpha(T entity, float partialTicks) {
        return 1.0F;
    }
}
