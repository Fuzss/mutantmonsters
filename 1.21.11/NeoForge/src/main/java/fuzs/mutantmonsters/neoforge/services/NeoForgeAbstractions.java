package fuzs.mutantmonsters.neoforge.services;

import fuzs.mutantmonsters.services.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.jspecify.annotations.Nullable;

public class NeoForgeAbstractions implements CommonAbstractions {

    @Override
    public void onBlockCaughtFire(Block block, BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        block.onCaughtFire(state, level, pos, direction, igniter);
    }

    @Override
    public boolean onAnimalTame(Animal animal, Player tamer) {
        return EventHooks.onAnimalTame(animal, tamer);
    }

    @Override
    public BlockParticleOption createBlockParticle(ParticleType<BlockParticleOption> particleType, BlockState blockState, BlockPos blockPos) {
        return new BlockParticleOption(particleType, blockState, blockPos);
    }
}
