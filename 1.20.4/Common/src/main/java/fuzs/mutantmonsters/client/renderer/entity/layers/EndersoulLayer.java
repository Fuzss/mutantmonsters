package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.renderer.MutantRenderTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EndersoulLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul.png");

    public EndersoulLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getParentModel().prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
        this.getParentModel().setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(MutantRenderTypes.energySwirl(TEXTURE_LOCATION,
                ageInTicks * 0.008F,
                ageInTicks * 0.008F
        ));
        this.getParentModel()
                .renderToBuffer(poseStack,
                        vertexConsumer,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        0.9F,
                        0.3F,
                        1.0F,
                        this.getAlpha(livingEntity, partialTicks)
                );
    }

    protected float getAlpha(T livingEntity, float partialTick) {
        return 1.0F;
    }
}
