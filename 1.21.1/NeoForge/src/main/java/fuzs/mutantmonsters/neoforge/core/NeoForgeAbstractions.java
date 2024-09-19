package fuzs.mutantmonsters.neoforge.core;

import fuzs.mutantmonsters.core.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

public class NeoForgeAbstractions implements CommonAbstractions {

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
    public boolean onAnimalTame(Animal animal, Player tamer) {
        return EventHooks.onAnimalTame(animal, tamer);
    }

    @Override
    public BlockParticleOption setBlockParticlePos(BlockParticleOption particleOption, BlockPos pos) {
        return particleOption.setPos(pos);
    }

    @Override
    public AbstractArrow getCustomArrowShotFromBow(BowItem bow, AbstractArrow arrow, ItemStack arrowStack) {
        return bow.customArrow(arrow, arrowStack);
    }

    @Override
    public boolean shouldRiderSit(Entity vehicle) {
        return vehicle.shouldRiderSit();
    }
}
