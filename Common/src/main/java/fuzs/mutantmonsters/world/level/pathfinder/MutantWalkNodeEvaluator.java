package fuzs.mutantmonsters.world.level.pathfinder;

import fuzs.mutantmonsters.core.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class MutantWalkNodeEvaluator extends WalkNodeEvaluator {

    public static BlockPathTypes getBlockPathTypeStatic(BlockGetter blockReader, BlockPos.MutableBlockPos mutable) {
        int posX = mutable.getX();
        int posY = mutable.getY();
        int posZ = mutable.getZ();
        BlockPathTypes rawNode = getBlockPathTypeRaw(blockReader, mutable);
        if (rawNode == BlockPathTypes.OPEN && posY >= blockReader.getMinBuildHeight() + 1) {
            BlockPathTypes nodeBelow = getBlockPathTypeRaw(blockReader, mutable.set(posX, posY - 1, posZ));
            rawNode = nodeBelow != BlockPathTypes.WALKABLE && nodeBelow != BlockPathTypes.OPEN && nodeBelow != BlockPathTypes.WATER && nodeBelow != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            rawNode = switch (nodeBelow) {
                case DAMAGE_FIRE, DAMAGE_CACTUS, DAMAGE_OTHER, DANGER_OTHER, STICKY_HONEY -> nodeBelow;
                case POWDER_SNOW -> BlockPathTypes.DANGER_POWDER_SNOW;
                default -> rawNode;
            };
        }

        if (rawNode == BlockPathTypes.WALKABLE) {
            rawNode = checkNeighbourBlocks(blockReader, mutable.set(posX, posY, posZ), rawNode);
        }

        return rawNode;
    }

    public static BlockPathTypes checkNeighbourBlocks(BlockGetter blockReader, BlockPos.MutableBlockPos mutable, BlockPathTypes pathNodeType) {
        int x = mutable.getX();
        int y = mutable.getY();
        int z = mutable.getZ();

        for (int extraX = -1; extraX <= 1; ++extraX) {
            for (int extraY = -1; extraY <= 1; ++extraY) {
                for (int extraZ = -1; extraZ <= 1; ++extraZ) {
                    if (extraX != 0 || extraZ != 0) {
                        mutable.set(x + extraX, y + extraY, z + extraZ);
                        BlockPathTypes blockPathType = CommonAbstractions.INSTANCE.getAdjacentBlockPathType(blockReader, mutable, pathNodeType);
                        if (blockPathType != null) return blockPathType;
                        BlockPathTypes rawNode = getBlockPathTypeRaw(blockReader, mutable);
                        switch (rawNode) {
                            case DAMAGE_FIRE -> {
                                return BlockPathTypes.DANGER_FIRE;
                            }
                            case DAMAGE_CACTUS -> {
                                return BlockPathTypes.DANGER_CACTUS;
                            }
                            // includes more danger types
                            case DAMAGE_OTHER, DANGER_OTHER -> {
                                return BlockPathTypes.DANGER_OTHER;
                            }
                            case WATER -> {
                                return BlockPathTypes.WATER_BORDER;
                            }
                            case LAVA -> {
                                return BlockPathTypes.LAVA;
                            }
                        }
                    }
                }
            }
        }

        return pathNodeType;
    }

    protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter blockReader, BlockPos blockPos) {
        BlockState blockstate = blockReader.getBlockState(blockPos);
        Block block = blockstate.getBlock();

        if (blockstate.is(BlockTags.PORTALS)) return BlockPathTypes.DAMAGE_OTHER;
        if (block.getJumpFactor() < 1.0F) return BlockPathTypes.STICKY_HONEY;
        if (!(block.getSpeedFactor() < 1.0F) && !blockstate.is(Blocks.COBWEB) && !blockstate.is(BlockTags.PRESSURE_PLATES) && !blockstate.is(Blocks.TRIPWIRE) && !blockstate.is(Blocks.WITHER_ROSE)) {
            return BlockPathTypes.DANGER_OTHER;
        }

        return WalkNodeEvaluator.getBlockPathTypeRaw(blockReader, blockPos);
    }

    @Override
    protected BlockPathTypes evaluateBlockPathType(BlockGetter blockReader, boolean canOpenDoors, boolean canEnterDoors, BlockPos blockPos, BlockPathTypes pathNodeType) {
        if (pathNodeType == BlockPathTypes.DOOR_WOOD_CLOSED && canOpenDoors && canEnterDoors) {
            pathNodeType = BlockPathTypes.WALKABLE;
        }

        if (pathNodeType == BlockPathTypes.DOOR_OPEN && !canEnterDoors) {
            pathNodeType = BlockPathTypes.BLOCKED;
        }

        // don't be held back by rails

        if (pathNodeType == BlockPathTypes.LEAVES) {
            pathNodeType = BlockPathTypes.BLOCKED;
        }

        return pathNodeType;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z) {
        return getBlockPathTypeStatic(blockaccessIn, new BlockPos.MutableBlockPos(x, y, z));
    }
}