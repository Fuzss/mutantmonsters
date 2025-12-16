package fuzs.mutantmonsters.world.entity.ai.goal;

import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class TrackSummonerGoal extends Goal {
    private final Zombie zombie;
    private MutantZombie mutantZombie;

    public TrackSummonerGoal(Zombie zombie, MutantZombie mutantZombie) {
        this.zombie = zombie;
        this.mutantZombie = mutantZombie;
    }

    @Override
    public boolean canUse() {
        return this.mutantZombie != null;
    }

    @Override
    public void tick() {
        if (this.zombie.getLastHurtByMob() == null && this.mutantZombie.getTarget() != null
                && this.zombie.getTarget() != this.mutantZombie.getTarget()) {
            this.zombie.setTarget(this.mutantZombie.getTarget());
        }

        this.zombie.setHomeTo(this.mutantZombie.blockPosition(), this.zombie.getTarget() == null ? 8 : 16);
    }

    @Override
    public void stop() {
        this.mutantZombie = null;
        this.zombie.setHomeTo(BlockPos.ZERO, -1);
    }
}
