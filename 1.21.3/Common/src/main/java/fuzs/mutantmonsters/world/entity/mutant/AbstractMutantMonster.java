package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;

public abstract class AbstractMutantMonster extends Monster {

    protected AbstractMutantMonster(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigation(this, level) {
            @Override
            protected PathFinder createPathFinder(int maxVisitedNodes) {
                this.nodeEvaluator = new WalkNodeEvaluator();
                this.nodeEvaluator.setCanPassDoors(true);
                return new PathFinder(this.nodeEvaluator, maxVisitedNodes) {
                    @Override
                    protected float distance(Node first, Node second) {
                        return first.distanceToXZ(second);
                    }
                };
            }
        };
    }

    @Override
    public float getPathfindingMalus(PathType nodeType) {
        if (nodeType == PathType.LEAVES && this.getTarget() != null) {
            return 0.0F;
        } else {
            return super.getPathfindingMalus(nodeType);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.getTarget() != null) {
            if (this.horizontalCollision && CommonAbstractions.INSTANCE.getMobGriefingRule(this.level(), this)) {
                boolean hasDestroyedBlock = false;
                AABB aabb = this.getBoundingBox().inflate(0.2D);

                for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY),
                        Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)
                )) {
                    if (this.level().getBlockState(blockpos).getBlock() instanceof LeavesBlock) {
                        hasDestroyedBlock |= this.level().destroyBlock(blockpos, true, this);
                    }
                }

                if (!hasDestroyedBlock && this.onGround()) {
                    this.jumpFromGround();
                }
            }
        }
    }
}
