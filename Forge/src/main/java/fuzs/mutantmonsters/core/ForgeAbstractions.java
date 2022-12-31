package fuzs.mutantmonsters.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public BlockPathTypes getAdjacentBlockPathType(BlockGetter blockReader, BlockPos.MutableBlockPos mutable, BlockPathTypes pathNodeType) {
        BlockState blockstate = blockReader.getBlockState(mutable);
        BlockPathTypes blockPathType = blockstate.getAdjacentBlockPathType(blockReader, mutable, null, pathNodeType);
        if (blockPathType != null) return blockPathType;
        FluidState fluidState = blockstate.getFluidState();
        return fluidState.getAdjacentBlockPathType(blockReader, mutable, null, pathNodeType);
    }

    @Override
    public void onBlockCaughtFire(Block block, BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        block.onCaughtFire(state, level, pos, direction, igniter);
    }

    @Override
    public boolean getMobGriefingEvent(Level level, Entity entity) {
        return ForgeEventFactory.getMobGriefingEvent(level, entity);
    }

    @Override
    public boolean onAnimalTame(Animal animal, Player tamer) {
        return ForgeEventFactory.onAnimalTame(animal, tamer);
    }

    @Override
    public BlockParticleOption setBlockParticlePos(BlockParticleOption particleOption, BlockPos pos) {
        return particleOption.setPos(pos);
    }

    @Override
    public GoalSelector getGoalSelector(Mob mob) {
        return mob.goalSelector;
    }

    @Override
    public boolean onExplosionStart(Level level, Explosion explosion) {
        return ForgeEventFactory.onExplosionStart(level, explosion);
    }

    @Override
    public void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
        ForgeEventFactory.onExplosionDetonate(level, explosion, list, diameter);
    }

    @Override
    public @Nullable Entity getExplosionExploder(Explosion explosion) {
        return explosion.getExploder();
    }

    @Override
    public Vec3 getExplosionPosition(Explosion explosion) {
        return explosion.getPosition();
    }

    @Override
    public boolean isArrowInfinite(ArrowItem arrow, ItemStack projectile, ItemStack bow, Player player) {
        return arrow.isInfinite(projectile, bow, player);
    }

    @Override
    public AbstractArrow getCustomArrowShotFromBow(BowItem bow, AbstractArrow arrow) {
        return bow.customArrow(arrow);
    }

    @Override
    public boolean shouldRiderSit(Entity vehicle) {
        return vehicle.shouldRiderSit();
    }
}
