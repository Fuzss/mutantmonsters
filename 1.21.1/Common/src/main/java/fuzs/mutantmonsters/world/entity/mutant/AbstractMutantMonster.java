package fuzs.mutantmonsters.world.entity.mutant;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;

public abstract class AbstractMutantMonster extends Monster {

    protected AbstractMutantMonster(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getPathfindingMalus(BlockPathTypes nodeType) {
        if (nodeType == BlockPathTypes.LEAVES && this.getTarget() != null) return 0.0F;
        return super.getPathfindingMalus(nodeType);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.getTarget() != null) {
            if (this.horizontalCollision && CommonAbstractions.INSTANCE.getMobGriefingRule(this.level(), this)) {
                boolean flag = false;
                AABB aabb = this.getBoundingBox().inflate(0.2D);

                for(BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                    if (block instanceof LeavesBlock) {
                        flag = this.level().destroyBlock(blockpos, true, this) || flag;
                    }
                }

                if (!flag && this.onGround()) {
                    this.jumpFromGround();
                }
            }
        }
    }
}
