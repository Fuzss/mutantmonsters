package fuzs.mutantmonsters.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.client.model.geom.FutureModelPart;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
abstract class ModelPartMixin implements FutureModelPart {
    @Unique
    public float mutantmonsters$xScale = 1.0F;
    @Unique
    public float mutantmonsters$yScale = 1.0F;
    @Unique
    public float mutantmonsters$zScale = 1.0F;
    @Unique
    public boolean mutantmonsters$skipDraw;

    @Override
    public void mutantmonsters$setXScale(float xScale) {
        this.mutantmonsters$xScale = xScale;
    }

    @Override
    public void mutantmonsters$setYScale(float yScale) {
        this.mutantmonsters$yScale = yScale;
    }

    @Override
    public void mutantmonsters$setZScale(float zScale) {
        this.mutantmonsters$zScale = zScale;
    }

    @Override
    public void mutantmonsters$setSkipDraw(boolean skipDraw) {
        this.mutantmonsters$skipDraw = skipDraw;
    }

    @Inject(method = "translateAndRotate", at = @At("TAIL"))
    public void translateAndRotate(PoseStack poseStack, CallbackInfo callback) {
        if (this.mutantmonsters$xScale != 1.0F || this.mutantmonsters$yScale != 1.0F || this.mutantmonsters$zScale != 1.0F) {
            poseStack.scale(this.mutantmonsters$xScale, this.mutantmonsters$yScale, this.mutantmonsters$zScale);
        }
    }

    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    private void compile(PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo callback) {
        if (this.mutantmonsters$skipDraw) callback.cancel();
    }
}
