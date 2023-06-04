package fuzs.mutantmonsters.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FleeRainGoal extends FleeSunGoal {
    public FleeRainGoal(PathfinderMob creatureEntity, double movementSpeedIn) {
        super(creatureEntity, movementSpeedIn);
    }

    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            return false;
        } else {
            return this.mob.level.isRainingAt(this.mob.blockPosition()) && this.setWantedPos();
        }
    }

    @Nullable
    protected Vec3 getHidePos() {
        Random random = this.mob.getRandom();
        BlockPos blockpos = this.mob.blockPosition();

        for(int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (!this.mob.level.isRainingAt(blockpos1) && this.mob.getWalkTargetValue(blockpos1) >= 0.0F) {
                return Vec3.atBottomCenterOf(blockpos1);
            }
        }

        return null;
    }
}
