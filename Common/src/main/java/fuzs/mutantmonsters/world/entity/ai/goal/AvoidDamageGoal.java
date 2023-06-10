package fuzs.mutantmonsters.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;

public class AvoidDamageGoal extends PanicGoal {
    private final BooleanSupplier avoidsAttacker;

    public AvoidDamageGoal(PathfinderMob creature, double speed) {
        this(creature, speed, () -> {
            return false;
        });
    }

    public AvoidDamageGoal(PathfinderMob creature, double speed, BooleanSupplier avoidsAttacker) {
        super(creature, speed);
        this.avoidsAttacker = avoidsAttacker;
    }

    public boolean canUse() {
        if (this.mob.isOnFire() && !this.mob.isSensitiveToWater()) {
            if (this.mob.level().isRaining()) {
                for(int i = 0; i < 10; ++i) {
                    BlockPos blockpos1 = this.mob.blockPosition().offset(this.mob.getRandom().nextInt(20) - 10, this.mob.getRandom().nextInt(6) - 3, this.mob.getRandom().nextInt(20) - 10);
                    if (this.mob.level().isRainingAt(blockpos1) && this.mob.getWalkTargetValue(blockpos1) >= 0.0F) {
                        return this.hasPosition(Vec3.atBottomCenterOf(blockpos1));
                    }
                }
            }

            BlockPos blockpos = this.lookForWater(this.mob.level(), this.mob, 15);
            return blockpos != null && this.mob.getNavigation().createPath(blockpos, 0) != null && this.hasPosition(Vec3.atLowerCornerOf(blockpos)) || this.findRandomPosition();
        } else if (this.avoidsAttacker.getAsBoolean() && this.mob.getLastHurtByMob() != null) {
            return this.hasPosition(DefaultRandomPos.getPosAway(this.mob, 10, 9, this.mob.getLastHurtByMob().position()));
        } else if (this.mob.getLastDamageSource() != null && this.shouldAvoidDamage(this.mob.getLastDamageSource())) {
            Vec3 damageVec = this.mob.getLastDamageSource().getSourcePosition();
            return damageVec != null ? this.hasPosition(DefaultRandomPos.getPosAway(this.mob, 8, 5, damageVec)) : this.findRandomPosition();
        } else {
            return false;
        }
    }

    private boolean hasPosition(Vec3 vec3d) {
        if (vec3d == null) {
            return false;
        } else {
            this.posX = vec3d.x;
            this.posY = vec3d.y;
            this.posZ = vec3d.z;
            return true;
        }
    }

    protected boolean shouldAvoidDamage(DamageSource source) {
        if (source.getEntity() != null) {
            return false;
        } else if (source.is(DamageTypeTags.WITCH_RESISTANT_TO) && source.getDirectEntity() == null) {
            return false;
        } else {
            return !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.STARVE) && !source.is(DamageTypes.FELL_OUT_OF_WORLD);
        }
    }
}
