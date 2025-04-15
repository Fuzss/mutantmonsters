package fuzs.mutantmonsters.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.SaddleableRenderState;

public class SpiderPigRenderState extends LivingEntityRenderState implements SaddleableRenderState {
    public float attackTime;
    public boolean isSaddled;

    @Override
    public boolean isSaddled() {
        return this.isSaddled;
    }
}
