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
    public void render(EndersoulClone endersoulClone, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super.render(endersoulClone, entityYaw, partialTick, poseStack, multiBufferSource, packedLight);
        this.model.creepy = endersoulClone.isAggressive();
    }

    @Override
    public Vec3 getRenderOffset(EndersoulClone endersoulClone, float partialTick) {
        return endersoulClone.isAggressive() ? new Vec3(endersoulClone.getRandom().nextGaussian() * 0.02, 0.0, endersoulClone.getRandom().nextGaussian() * 0.02) : super.getRenderOffset(endersoulClone, partialTick);
    }

    @Override
    protected RenderType getRenderType(EndersoulClone endersoulClone, boolean bodyVisible, boolean translucent, boolean glowing) {
        return null;
    }

    @Override
    protected float getFlipDegrees(EndersoulClone endersoulClone) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(EndersoulClone endersoulClone) {
        return EndersoulLayer.TEXTURE_LOCATION;
    }
}
