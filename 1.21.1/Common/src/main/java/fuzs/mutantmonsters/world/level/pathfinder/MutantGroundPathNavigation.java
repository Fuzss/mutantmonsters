package fuzs.mutantmonsters.world.level.pathfinder;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;

public class MutantGroundPathNavigation extends GroundPathNavigation {

    public MutantGroundPathNavigation(Mob entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    @Override
    protected PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new MutantWalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, i);
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.mob.isSensitiveToWater()) {
            if (this.level.isRainingAt(this.mob.blockPosition())) {
                return;
            }

            for (int i = 0; i < this.path.getNodeCount(); ++i) {
                Node pathpoint = this.path.getNode(i);
                if (this.level.isRainingAt(pathpoint.asBlockPos())) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }

    }
}
