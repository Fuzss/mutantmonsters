package fuzs.mutantmonsters.pathfinding;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;

public class MBGroundPathNavigator extends GroundPathNavigation {
    public MBGroundPathNavigator(Mob entitylivingIn, Level worldIn) {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder createPathFinder(int i) {
        this.nodeEvaluator = new MBWalkNodeProcessor();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, i);
    }

    protected void trimPath() {
        super.trimPath();
        if (this.mob.isSensitiveToWater()) {
            if (this.level.isRainingAt(this.mob.blockPosition())) {
                return;
            }

            for(int i = 0; i < this.path.getNodeCount(); ++i) {
                Node pathpoint = this.path.getNode(i);
                if (this.level.isRainingAt(pathpoint.asBlockPos())) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }

    }

    public void tick() {
        super.tick();
    }
}
