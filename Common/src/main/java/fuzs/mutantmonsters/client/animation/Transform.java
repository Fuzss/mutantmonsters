package fuzs.mutantmonsters.client.animation;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;

public class Transform {
    private final Vector3f rotation = new Vector3f();
    private final Vector3f offset = new Vector3f();

    public Vector3f getRotation() {
        return this.rotation;
    }

    public Vector3f getOffset() {
        return this.offset;
    }

    public void rotate(ModelPart renderer, float multiplier) {
        this.rotation.mul(multiplier);
        renderer.xRot += this.rotation.x();
        renderer.yRot += this.rotation.y();
        renderer.zRot += this.rotation.z();
    }

    public void offset(ModelPart renderer, float multiplier) {
        this.offset.mul(multiplier);
        renderer.x += this.offset.x();
        renderer.y += this.offset.y();
        renderer.z += this.offset.z();
    }
}
