package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.puzzleslib.api.util.v1.EntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

public abstract class MutantMonster extends Monster {

    protected MutantMonster(EntityType<? extends Monster> entityType, Level level) {
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
        if (this.isAlive() && this.getTarget() != null && this.level() instanceof ServerLevel serverLevel) {
            if (this.horizontalCollision && EntityHelper.isMobGriefingAllowed(serverLevel, this)) {
                boolean hasDestroyedBlock = false;
                AABB aabb = this.getBoundingBox().inflate(0.2D);

                for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aabb.minX),
                        Mth.floor(aabb.minY),
                        Mth.floor(aabb.minZ),
                        Mth.floor(aabb.maxX),
                        Mth.floor(aabb.maxY),
                        Mth.floor(aabb.maxZ))) {
                    if (this.level().getBlockState(blockPos).getBlock() instanceof LeavesBlock) {
                        hasDestroyedBlock |= this.level().destroyBlock(blockPos, true, this);
                    }
                }

                if (!hasDestroyedBlock && this.onGround()) {
                    this.jumpFromGround();
                }
            }
        }
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void blockedByItem(LivingEntity livingEntity) {
        livingEntity.hurtMarked = true;
    }

    protected void knockbackBlockedAttacker(LivingEntity livingEntity) {
        super.blockedByItem(livingEntity);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    protected void updateNoActionTime() {
        // NO-OP
    }
}
