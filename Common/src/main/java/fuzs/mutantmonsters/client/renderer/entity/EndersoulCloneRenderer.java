package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.layers.EndersoulLayer;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class EndersoulCloneRenderer extends MobRenderer<EndersoulClone, EndermanModel<EndersoulClone>> {
    public EndersoulCloneRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermanModel<>(context.bakeLayer(ClientModRegistry.ENDERSOUL_CLONE)), 0.5F);
        this.addLayer(new EndersoulLayer<>(this));
        this.shadowStrength = 0.5F;
    }

    @Override
    public void render(EndersoulClone entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        this.model.creepy = entityIn.isAggressive();
    }

    @Override
    public Vec3 getRenderOffset(EndersoulClone entityIn, float partialTicks) {
        return entityIn.isAggressive() ? new Vec3(entityIn.getRandom().nextGaussian() * 0.02, 0.0, entityIn.getRandom().nextGaussian() * 0.02) : super.getRenderOffset(entityIn, partialTicks);
    }

    @Override
    protected RenderType getRenderType(EndersoulClone p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        return null;
    }

    @Override
    protected float getFlipDegrees(EndersoulClone livingEntity) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(EndersoulClone entity) {
        return EndersoulLayer.TEXTURE;
    }
}
