package fuzs.mutantmonsters.core;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.S2CSeismicWaveFluidParticlesMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeismicWave extends BlockPos {
    private final boolean first;
    private final boolean affectsTerrain;

    public SeismicWave(int x, int y, int z, boolean first, boolean affectsTerrain) {
        super(x, y, z);
        this.first = first;
        this.affectsTerrain = affectsTerrain;
    }

    public boolean isFirst() {
        return this.first;
    }

    public boolean affectsTerrain() {
        return this.affectsTerrain;
    }

    public static void createWaves(Level world, List<SeismicWave> list, int x1, int z1, int x2, int z2, int y) {
        int deltaX = x2 - x1;
        int deltaZ = z2 - z1;
        int xStep = deltaX < 0 ? -1 : 1;
        int zStep = deltaZ < 0 ? -1 : 1;
        deltaX = Math.abs(deltaX);
        deltaZ = Math.abs(deltaZ);
        int x = x1;
        int z = z1;
        int deltaX2 = deltaX * 2;
        int deltaZ2 = deltaZ * 2;
        int firstY = ZombieResurrection.getSuitableGround(world, x1, y, z1, 3, false);
        SeismicWave wave = new SeismicWave(x1, y, z1, true, true);
        if (firstY != -1) {
            wave = new SeismicWave(x1, firstY, z1, true, true);
        }

        list.add(wave);
        int error;
        int i;
        if (deltaX2 >= deltaZ2) {
            error = deltaX;

            for(i = 0; i < deltaX; ++i) {
                x += xStep;
                error += deltaZ2;
                if (error > deltaX2) {
                    z += zStep;
                    error -= deltaX2;
                }

                addWave(world, list, x, y, z);
            }
        } else {
            error = deltaZ;

            for(i = 0; i < deltaZ; ++i) {
                z += zStep;
                error += deltaX2;
                if (error > deltaZ2) {
                    x += xStep;
                    error -= deltaZ2;
                }

                addWave(world, list, x, y, z);
            }
        }

    }

    @Nullable
    public static SeismicWave addWave(Level world, List<SeismicWave> list, int x, int y, int z) {
        y = ZombieResurrection.getSuitableGround(world, x, y, z, 3, false);
        SeismicWave wave = null;
        if (y != -1) {
            list.add(wave = new SeismicWave(x, y, z, false, true));
        }

        if (world.random.nextInt(2) == 0) {
            list.add(new SeismicWave(x, y + 1, z, false, false));
        }

        return wave;
    }

    public void affectBlocks(Level world, Entity entity) {
        if (this.affectsTerrain) {
            BlockPos posAbove = this.above();
            BlockState blockstate = world.getBlockState(this);
            Block block = blockstate.getBlock();
            Player playerEntity = entity instanceof Player ? (Player)entity : null;
            if (playerEntity != null && playerEntity.mayBuild() || world.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                if (blockstate.is(Blocks.GRASS_BLOCK) || blockstate.is(Blocks.DIRT_PATH) || blockstate.is(Blocks.FARMLAND) || blockstate.is(Blocks.PODZOL) || blockstate.is(Blocks.MYCELIUM)) {
                    world.setBlockAndUpdate(this, Blocks.DIRT.defaultBlockState());
                }

                BlockState blockstateAbove = world.getBlockState(posAbove);
                float hardness = blockstateAbove.getDestroySpeed(world, posAbove);
                if (blockstateAbove.getCollisionShape(world, posAbove).isEmpty() && hardness > -1.0F && hardness <= 1.0F) {
                    world.destroyBlock(posAbove, playerEntity != null);
                }

                if (block instanceof DoorBlock) {
                    if (DoorBlock.isWoodenDoor(blockstate)) {
                        world.levelEvent(LevelEvent.SOUND_ZOMBIE_WOODEN_DOOR, this, 0);
                    } else {
                        world.levelEvent(LevelEvent.SOUND_ZOMBIE_IRON_DOOR, this, 0);
                    }
                }

                if (block instanceof TntBlock) {
                    CommonAbstractions.INSTANCE.onBlockCaughtFire(block, blockstate, world, this, null, playerEntity);
                    world.removeBlock(this, false);
                }
            }

            if (block instanceof BellBlock) {
                ((BellBlock)block).onHit(world, blockstate, new BlockHitResult(Vec3.atLowerCornerOf(this), entity.getDirection(), this, false), playerEntity, true);
            }

            if (blockstate.is(Blocks.REDSTONE_ORE)) {
                block.stepOn(world, this, blockstate, entity);
            }

            if (blockstate.getFluidState().isEmpty()) {
                world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, posAbove, Block.getId(blockstate));
            } else {
                MutantMonsters.NETWORK.sendToAllNearExcept(new S2CSeismicWaveFluidParticlesMessage(this), null, this.getX() + 0.5, this.getY(), this.getZ() + 0.5, 1024.0, entity.level());
            }
        }
    }
}
