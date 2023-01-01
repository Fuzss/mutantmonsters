package fuzs.mutantmonsters.world.level.pathfinder;

import fuzs.mutantmonsters.core.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class MBWalkNodeProcessor extends WalkNodeEvaluator {
    public MBWalkNodeProcessor() {
    }

    protected BlockPathTypes evaluateBlockPathType(BlockGetter blockReader, boolean canOpenDoors, boolean canEnterDoors, BlockPos blockPos, BlockPathTypes pathNodeType) {
        if (pathNodeType == BlockPathTypes.DOOR_WOOD_CLOSED && canOpenDoors && canEnterDoors) {
            pathNodeType = BlockPathTypes.WALKABLE;
        }

        if (pathNodeType == BlockPathTypes.DOOR_OPEN && !canEnterDoors) {
            pathNodeType = BlockPathTypes.BLOCKED;
        }

        if (pathNodeType == BlockPathTypes.LEAVES) {
            pathNodeType = BlockPathTypes.BLOCKED;
        }

        return pathNodeType;
    }

    public BlockPathTypes getBlockPathType(BlockGetter blockaccessIn, int x, int y, int z) {
        return getBlockPathTypeStatic(blockaccessIn, new BlockPos.MutableBlockPos(x, y, z));
    }

    public static BlockPathTypes getBlockPathTypeStatic(BlockGetter blockReader, BlockPos.MutableBlockPos mutable) {
        int i = mutable.getX();
        int j = mutable.getY();
        int k = mutable.getZ();
        BlockPathTypes rawNode = getBlockPathTypeRaw(blockReader, mutable);
        if (rawNode == BlockPathTypes.OPEN && j >= 1) {
            BlockPathTypes nodeBelow = getBlockPathTypeRaw(blockReader, mutable.set(i, j - 1, k));
            rawNode = nodeBelow != BlockPathTypes.WALKABLE && nodeBelow != BlockPathTypes.OPEN && nodeBelow != BlockPathTypes.WATER && nodeBelow != BlockPathTypes.LAVA ? BlockPathTypes.WALKABLE : BlockPathTypes.OPEN;
            switch (nodeBelow) {
                case DAMAGE_FIRE:
                case DAMAGE_CACTUS:
                case DAMAGE_OTHER:
                case DANGER_OTHER:
                case STICKY_HONEY:
                    rawNode = nodeBelow;
            }
        }

        if (rawNode == BlockPathTypes.WALKABLE) {
            rawNode = checkNeighbourBlocks(blockReader, mutable.set(i, j, k), rawNode);
        }

        return rawNode;
    }

    public static BlockPathTypes checkNeighbourBlocks(BlockGetter blockReader, BlockPos.MutableBlockPos mutable, BlockPathTypes pathNodeType) {
        int x = mutable.getX();
        int y = mutable.getY();
        int z = mutable.getZ();

        for(int extraX = -1; extraX <= 1; ++extraX) {
            for(int extraY = -1; extraY <= 1; ++extraY) {
                for(int extraZ = -1; extraZ <= 1; ++extraZ) {
                    if (extraX != 0 || extraZ != 0) {
                        mutable.set(x + extraX, y + extraY, z + extraZ);
                        BlockPathTypes rawNode = getBlockPathTypeRaw(blockReader, mutable);
                        switch (rawNode) {
                            case DAMAGE_FIRE:
                                return BlockPathTypes.DANGER_FIRE;
                            case DAMAGE_CACTUS:
                                return BlockPathTypes.DANGER_CACTUS;
                            case DAMAGE_OTHER:
                            case DANGER_OTHER:
                                return BlockPathTypes.DANGER_OTHER;
                            case STICKY_HONEY:
                            default:
                                break;
                            case WATER:
                                return BlockPathTypes.WATER_BORDER;
                            case LAVA:
                                return rawNode;
                        }
                    }
                }
            }
        }

        return pathNodeType;
    }

    protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter blockReader, BlockPos blockPos) {
        BlockState blockstate = blockReader.getBlockState(blockPos);
        BlockPathTypes type = null;
        if (type != null) {
            return type;
        } else {
            Block block = blockstate.getBlock();
            if (blockstate.isAir()) {
                return BlockPathTypes.OPEN;
            } else if (!blockstate.is(BlockTags.TRAPDOORS) && !blockstate.is(Blocks.LILY_PAD)) {
                if (blockstate.is(Blocks.CACTUS)) {
                    return BlockPathTypes.DAMAGE_CACTUS;
                } else if (!blockstate.is(Blocks.SWEET_BERRY_BUSH) && !blockstate.is(BlockTags.PORTALS)) {
                    if (block.getJumpFactor() < 1.0F) {
                        return BlockPathTypes.STICKY_HONEY;
                    } else if (!(block.getSpeedFactor() < 1.0F) && !blockstate.is(Blocks.COBWEB) && !blockstate.is(BlockTags.PRESSURE_PLATES) && !blockstate.is(Blocks.TRIPWIRE) && !blockstate.is(Blocks.WITHER_ROSE)) {
                        if (blockstate.is(Blocks.COCOA)) {
                            return BlockPathTypes.COCOA;
                        } else {
                            FluidState fluidstate = blockReader.getFluidState(blockPos);
                            if (fluidstate.is(FluidTags.WATER)) {
                                return BlockPathTypes.WATER;
                            } else if (fluidstate.is(FluidTags.LAVA)) {
                                return BlockPathTypes.LAVA;
                            } else if (!blockstate.is(BlockTags.FIRE) && !blockstate.is(Blocks.LAVA) && !blockstate.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockstate)) {
                                if (DoorBlock.isWoodenDoor(blockstate) && !(Boolean)blockstate.getValue(DoorBlock.OPEN)) {
                                    return BlockPathTypes.DOOR_WOOD_CLOSED;
                                } else if (block instanceof DoorBlock && blockstate.getMaterial() == Material.METAL && !(Boolean)blockstate.getValue(DoorBlock.OPEN)) {
                                    return BlockPathTypes.DOOR_IRON_CLOSED;
                                } else if (block instanceof DoorBlock && blockstate.getValue(DoorBlock.OPEN)) {
                                    return BlockPathTypes.DOOR_OPEN;
                                } else if (block instanceof BaseRailBlock) {
                                    return BlockPathTypes.RAIL;
                                } else if (block instanceof LeavesBlock) {
                                    return BlockPathTypes.LEAVES;
                                } else if (!blockstate.is(BlockTags.FENCES) && !blockstate.is(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.getValue(FenceGateBlock.OPEN))) {
                                    return !blockstate.isPathfindable(blockReader, blockPos, PathComputationType.LAND) ? BlockPathTypes.BLOCKED : BlockPathTypes.OPEN;
                                } else {
                                    return BlockPathTypes.FENCE;
                                }
                            } else {
                                return BlockPathTypes.DAMAGE_FIRE;
                            }
                        }
                    } else {
                        return BlockPathTypes.DANGER_OTHER;
                    }
                } else {
                    return BlockPathTypes.DAMAGE_OTHER;
                }
            } else {
                return BlockPathTypes.TRAPDOOR;
            }
        }
    }
}