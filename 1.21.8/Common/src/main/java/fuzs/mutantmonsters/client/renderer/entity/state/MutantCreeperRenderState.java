package fuzs.mutantmonsters.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class MutantCreeperRenderState extends LivingEntityRenderState implements PowerableRenderState {
    public float attackTime;
    public boolean isJumpAttacking;
    public float overlayColor;
    public boolean isPowered;

    @Override
    public boolean isPowered() {
        return this.isPowered;
    }
}
