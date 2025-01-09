package fuzs.mutantmonsters.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.CreeperRenderState;

public class CreeperMinionRenderState extends CreeperRenderState implements PowerableRenderState {
    public boolean inSittingPose;

    @Override
    public boolean isPowered() {
        return this.isPowered;
    }
}
