package fuzs.mutantmonsters.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class CreeperMinionRenderState extends LivingEntityRenderState implements PowerableRenderState {
    public boolean inSittingPose;
    public float flashIntensity;
    public boolean isPowered;

    @Override
    public boolean isPowered() {
        return this.isPowered;
    }
}
