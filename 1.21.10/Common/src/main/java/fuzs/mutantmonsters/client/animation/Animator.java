package fuzs.mutantmonsters.client.animation;

import fuzs.mutantmonsters.client.renderer.entity.state.AnimatedEntityRenderState;
import fuzs.mutantmonsters.world.entity.animation.EntityAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class Animator {
    private int tempTick;
    private int prevTempTick;
    private boolean correctAnim;
    private AnimatedEntityRenderState renderState;
    private final Map<ModelPart, Transform> transformMap = new HashMap<>();
    private final Map<ModelPart, Transform> prevTransformMap = new HashMap<>();

    public AnimatedEntityRenderState getRenderState() {
        return this.renderState;
    }

    public void update(AnimatedEntityRenderState renderState) {
        this.tempTick = this.prevTempTick = 0;
        this.correctAnim = false;
        this.renderState = renderState;
        this.transformMap.clear();
        this.prevTransformMap.clear();
    }

    public boolean setAnimation(EntityAnimation animation) {
        this.tempTick = this.prevTempTick = 0;
        this.correctAnim = this.renderState.animation == animation;
        return this.correctAnim;
    }

    public void startPhase(int duration) {
        if (this.correctAnim) {
            this.prevTempTick = this.tempTick;
            this.tempTick += duration;
        }
    }

    public void setStationaryPhase(int duration) {
        this.startPhase(duration);
        this.endPhase(true);
    }

    public void resetPhase(int duration) {
        this.startPhase(duration);
        this.endPhase();
    }

    public void rotate(ModelPart box, float x, float y, float z) {
        if (this.correctAnim) {
            this.getTransform(box).getRotation().add(x, y, z);
        }

    }

    public void move(ModelPart box, float x, float y, float z) {
        if (this.correctAnim) {
            this.getTransform(box).getOffset().add(x, y, z);
        }

    }

    private Transform getTransform(ModelPart box) {
        return this.transformMap.computeIfAbsent(box, (b) -> {
            return new Transform();
        });
    }

    public void endPhase() {
        this.endPhase(false);
    }

    private void endPhase(boolean stationary) {
        if (this.correctAnim) {
            float animTick = this.renderState.animationTime;
            if (animTick >= this.prevTempTick && animTick < this.tempTick) {
                if (stationary) {
                    for (ModelPart modelPart : this.prevTransformMap.keySet()) {
                        Transform transform = this.prevTransformMap.get(modelPart);
                        transform.rotate(modelPart, 1.0F);
                        transform.offset(modelPart, 1.0F);
                    }
                } else {
                    float tick = (animTick - this.prevTempTick) / (float) (this.tempTick - this.prevTempTick);
                    float inc = Mth.sin(tick * 3.1415927F / 2.0F);
                    float dec = 1.0F - inc;
                    ModelPart model;
                    Transform transform;
                    for (ModelPart modelPart : this.prevTransformMap.keySet()) {
                        model = modelPart;
                        transform = this.prevTransformMap.get(model);
                        transform.rotate(model, dec);
                        transform.offset(model, dec);
                    }

                    for (ModelPart modelPart : this.transformMap.keySet()) {
                        model = modelPart;
                        transform = this.transformMap.get(model);
                        transform.rotate(model, inc);
                        transform.offset(model, inc);
                    }
                }
            }

            if (!stationary) {
                this.prevTransformMap.clear();
                this.prevTransformMap.putAll(this.transformMap);
                this.transformMap.clear();
            }
        }
    }

    public static void addRotationAngle(ModelPart modelPart, float x, float y, float z) {
        modelPart.xRot += x;
        modelPart.yRot += y;
        modelPart.zRot += z;
    }

    @Deprecated(forRemoval = true)
    public static void resetAngles(ModelPart... modelParts) {
        for (ModelPart box : modelParts) {
            resetAngles(box);
        }
    }

    @Deprecated(forRemoval = true)
    public static void resetAngles(ModelPart modelPart) {
        modelPart.xRot = 0.0F;
        modelPart.yRot = 0.0F;
        modelPart.zRot = 0.0F;
    }

    public static void setScale(ModelPart modelPart, float scale) {
        modelPart.xScale = modelPart.yScale = modelPart.zScale = scale;
    }
}
