package fuzs.mutantmonsters.fabric.core;

import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;
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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.Nullable;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public BlockPathTypes getAdjacentBlockPathType(BlockGetter blockReader, BlockPos.MutableBlockPos mutable, BlockPathTypes pathNodeType) {
        return LandPathNodeTypesRegistry.getPathNodeType(blockReader.getBlockState(mutable), blockReader, mutable, true);
    }

    @Override
    public void onBlockCaughtFire(Block block, BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        // NO-OP
    }

    @Override
    public boolean onAnimalTame(Animal animal, Player tamer) {
        return FabricLivingEvents.ANIMAL_TAME.invoker().onAnimalTame(animal, tamer).isInterrupt();
    }

    @Override
    public BlockParticleOption setBlockParticlePos(BlockParticleOption particleOption, BlockPos pos) {
        return particleOption;
    }

    @Override
    public AbstractArrow getCustomArrowShotFromBow(BowItem bow, AbstractArrow arrow, ItemStack arrowStack) {
        return arrow;
    }

    @Override
    public boolean shouldRiderSit(Entity vehicle) {
        return true;
    }
}
