package fuzs.mutantmonsters.entity.ai.goal;

import fuzs.mutantmonsters.entity.mutant.MutantZombieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;

public class TrackSummonerGoal extends Goal {
    private final Zombie zombie;
    private MutantZombieEntity mutantZombie;

    public TrackSummonerGoal(Zombie zombie, MutantZombieEntity mutantZombie) {
        this.zombie = zombie;
        this.mutantZombie = mutantZombie;
    }

    @Override
    public boolean canUse() {
        return this.mutantZombie != null;
    }

    @Override
    public void tick() {
        if (this.zombie.getLastHurtByMob() == null && this.mutantZombie.getTarget() != null && this.zombie.getTarget() != this.mutantZombie.getTarget()) {
            this.zombie.setTarget(this.mutantZombie.getTarget());
        }

        this.zombie.restrictTo(this.mutantZombie.blockPosition(), this.zombie.getTarget() == null ? 8 : 16);
    }

    @Override
    public void stop() {
        this.mutantZombie = null;
        this.zombie.restrictTo(BlockPos.ZERO, -1);
    }
}
