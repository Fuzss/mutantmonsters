package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class CustomMobRenderer<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends MobRenderer<T, S, M> {
    @Nullable
    private PoseStack poseStack;
    @Nullable
    private MultiBufferSource bufferSource;
    @Nullable
    private Integer packedLight;

    protected CustomMobRenderer(EntityRendererProvider.Context context, M entityModel, float shadowRadius) {
        super(context, entityModel, shadowRadius);
    }

    @Override
    public void render(S renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        super.render(renderState, poseStack, bufferSource, packedLight);
        this.poseStack = null;
        this.bufferSource = null;
        this.packedLight = null;
    }

    @Override
    protected @Nullable RenderType getRenderType(S renderState, boolean isVisible, boolean renderTranslucent, boolean appearsGlowing) {
        Objects.requireNonNull(this.poseStack, "pose stack is null");
        Objects.requireNonNull(this.bufferSource, "buffer source is null");
        Objects.requireNonNull(this.packedLight, "packed light is null");
        if (this.renderModel(renderState, this.poseStack, this.bufferSource, this.packedLight)) {
            return null;
        } else {
            return super.getRenderType(renderState, isVisible, renderTranslucent, appearsGlowing);
        }
    }

    protected boolean renderModel(S renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        return false;
    }
}
