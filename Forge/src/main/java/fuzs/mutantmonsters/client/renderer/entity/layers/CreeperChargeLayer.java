package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CreeperChargeLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final M model;

    public CreeperChargeLayer(RenderLayerParent<T, M> renderer, M model) {
        super(renderer);
        this.model = model;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof MutantCreeperEntity && ((MutantCreeperEntity)entity).isCharged() || entity instanceof CreeperMinionEntity && ((CreeperMinionEntity)entity).isCharged()) {
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().copyPropertiesTo(this.model);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.energySwirl(LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }

    }
}
