package fuzs.mutantmonsters.fabric.services;

import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FabricAbstractions implements CommonAbstractions {

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
    public boolean shouldRiderSit(Entity vehicle) {
        return true;
    }
}
