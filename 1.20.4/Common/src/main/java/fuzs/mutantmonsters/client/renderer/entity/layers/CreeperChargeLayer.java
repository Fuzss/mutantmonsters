package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
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
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity instanceof MutantCreeper mutantCreeper && mutantCreeper.isCharged() || entity instanceof CreeperMinion creeperMinion && creeperMinion.isCharged()) {
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
            this.getParentModel().copyPropertiesTo(this.model);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(MutantRenderTypes.energySwirl(LIGHTNING_TEXTURE, ageInTicks * 0.01F, ageInTicks * 0.01F));
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
        }

    }
}
