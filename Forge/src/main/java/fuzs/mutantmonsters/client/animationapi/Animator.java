package fuzs.mutantmonsters.client.animationapi;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Animator {
    private int tempTick;
    private int prevTempTick;
    private boolean correctAnim;
    private IAnimatedEntity animEntity;
    private final Map<ModelPart, Transform> transformMap = new HashMap();
    private final Map<ModelPart, Transform> prevTransformMap = new HashMap();
    private float partialTick;

    public Animator() {
    }

    public IAnimatedEntity getEntity() {
        return this.animEntity;
    }

    public void update(IAnimatedEntity entity, float partialTick) {
        this.tempTick = this.prevTempTick = 0;
        this.correctAnim = false;
        this.animEntity = entity;
        this.transformMap.clear();
        this.prevTransformMap.clear();
        this.partialTick = partialTick;
    }

    public boolean setAnimation(Animation animation) {
        this.tempTick = this.prevTempTick = 0;
        this.correctAnim = this.animEntity.getAnimation() == animation;
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
        return (Transform)this.transformMap.computeIfAbsent(box, (b) -> {
            return new Transform();
        });
    }

    public void endPhase() {
        this.endPhase(false);
    }

    private void endPhase(boolean stationary) {
        if (this.correctAnim) {
            int animTick = this.animEntity.getAnimationTick();
            if (animTick >= this.prevTempTick && animTick < this.tempTick) {
                if (stationary) {
                    Iterator var9 = this.prevTransformMap.keySet().iterator();

                    while(var9.hasNext()) {
                        ModelPart model = (ModelPart)var9.next();
                        Transform transform = (Transform)this.prevTransformMap.get(model);
                        transform.rotate(model, 1.0F);
                        transform.offset(model, 1.0F);
                    }
                } else {
                    float tick = ((float)(animTick - this.prevTempTick) + this.partialTick) / (float)(this.tempTick - this.prevTempTick);
                    float inc = Mth.sin(tick * 3.1415927F / 2.0F);
                    float dec = 1.0F - inc;
                    Iterator var6 = this.prevTransformMap.keySet().iterator();

                    ModelPart model;
                    Transform transform;
                    while(var6.hasNext()) {
                        model = (ModelPart)var6.next();
                        transform = (Transform)this.prevTransformMap.get(model);
                        transform.rotate(model, dec);
                        transform.offset(model, dec);
                    }

                    var6 = this.transformMap.keySet().iterator();

                    while(var6.hasNext()) {
                        model = (ModelPart)var6.next();
                        transform = (Transform)this.transformMap.get(model);
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

    public static void addRotationAngle(ModelPart model, float x, float y, float z) {
        model.xRot += x;
        model.yRot += y;
        model.zRot += z;
    }

    public static void resetAngles(ModelPart... boxes) {
        ModelPart[] var1 = boxes;
        int var2 = boxes.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ModelPart box = var1[var3];
            resetAngles(box);
        }

    }

    public static void resetAngles(ModelPart box) {
        box.xRot = 0.0F;
        box.yRot = 0.0F;
        box.zRot = 0.0F;
    }
}
