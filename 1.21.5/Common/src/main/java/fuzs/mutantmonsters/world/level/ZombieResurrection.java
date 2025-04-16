package fuzs.mutantmonsters.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.mutantmonsters.world.entity.ai.goal.TrackSummonerGoal;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.Scoreboard;

import java.util.List;

public class ZombieResurrection extends BlockPos {
    public static final Codec<ZombieResurrection> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockPos.CODEC.fieldOf(
                            "block_pos").forGetter(ZombieResurrection::getBlockPos),
                    Codec.INT.fieldOf("current_tick").forGetter(ZombieResurrection::getCurrentTick))
            .apply(instance, ZombieResurrection::new));
    public static final Codec<List<ZombieResurrection>> LIST_CODEC = CODEC.listOf();

    private int tick;

    public ZombieResurrection(Level level, int x, int y, int z) {
        super(x, y, z);
        this.tick = 100 + level.random.nextInt(40);
    }

    public ZombieResurrection(BlockPos pos, int tick) {
        super(pos);
        this.tick = tick;
    }

    public static int getSuitableGround(Level world, int x, int y, int z) {
        return getSuitableGround(world, x, y, z, 4, true);
    }

    public static int getSuitableGround(Level world, int x, int y, int z, int range, boolean checkDay) {
        int i = y;

        while (true) {
            if (Math.abs(y - i) > range) {
                return -1;
            }

            BlockPos startPos = new BlockPos(x, i, z);
            BlockPos posUp = startPos.above();
            BlockState blockState = world.getBlockState(startPos);
            if (blockState.is(BlockTags.FIRE)) {
                return -1;
            }

            if ((!checkDay || world.getFluidState(startPos).is(FluidTags.LAVA)) &&
                    !world.getFluidState(startPos).isEmpty()) {
                break;
            }

            if (world.isEmptyBlock(startPos)) {
                --i;
            } else {
                if (!world.isEmptyBlock(startPos) && world.isEmptyBlock(posUp) &&
                        blockState.getCollisionShape(world, startPos).isEmpty()) {
                    --i;
                    break;
                }

                if (world.isEmptyBlock(startPos) || world.isEmptyBlock(posUp) ||
                        world.getBlockState(posUp).getCollisionShape(world, posUp).isEmpty()) {
                    break;
                }

                ++i;
            }
        }

        if (checkDay && world.isBrightOutside()) {
            BlockPos lightPos = new BlockPos(x, y + 1, z);
            float f = world.getPathfindingCostFromLightLevels(lightPos);
            if (f > 0.0F && world.canSeeSkyFromBelowWater(lightPos) && world.random.nextInt(3) != 0) {
                return -1;
            }
        }

        return i;
    }

    public static EntityType<? extends Zombie> getZombieByLocation(Level level, BlockPos pos) {
        if ((level.getBiome(pos).is(BiomeTags.IS_OCEAN) || level.getBiome(pos).is(BiomeTags.IS_RIVER)) &&
                level.isWaterAt(pos)) {
            return EntityType.DROWNED;
        } else if (level.isBrightOutside() && level.canSeeSky(pos)) {
            return EntityType.HUSK;
        } else {
            return level.random.nextFloat() < 0.05F ? EntityType.ZOMBIE_VILLAGER : EntityType.ZOMBIE;
        }
    }

    public int getCurrentTick() {
        return this.tick;
    }

    private BlockPos getBlockPos() {
        return this;
    }

    public boolean update(MutantZombie mutantZombie) {
        Level level = mutantZombie.level();
        BlockPos abovePos = this.above();
        if (!level.isEmptyBlock(this) && level.isEmptyBlock(abovePos)) {
            if (mutantZombie.getRandom().nextInt(15) == 0) {
                level.levelEvent(2001, abovePos, Block.getId(level.getBlockState(this)));
            }

            if (--this.tick <= 0) {
                Zombie zombieEntity = (Zombie) getZombieByLocation(level, abovePos).create(level,
                        EntitySpawnReason.MOB_SUMMONED);
                if (level instanceof ServerLevelAccessor) {
                    SpawnGroupData ilivingentitydata = zombieEntity.finalizeSpawn((ServerLevelAccessor) level,
                            level.getCurrentDifficultyAt(this),
                            EntitySpawnReason.MOB_SUMMONED,
                            null);
                    if (ilivingentitydata instanceof Zombie.ZombieGroupData) {
                        new Zombie.ZombieGroupData(((Zombie.ZombieGroupData) ilivingentitydata).isBaby, false);
                    }
                }

                zombieEntity.setHealth(
                        zombieEntity.getMaxHealth() * (0.6F + 0.4F * zombieEntity.getRandom().nextFloat()));
                zombieEntity.playAmbientSound();
                level.levelEvent(2001, abovePos, Block.getId(level.getBlockState(this)));
                if (!level.isClientSide) {
                    zombieEntity.snapTo(abovePos, mutantZombie.getYRot(), 0.0F);
                    zombieEntity.goalSelector.addGoal(0, new TrackSummonerGoal(zombieEntity, mutantZombie));
                    zombieEntity.goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(zombieEntity, 1.0));
                    level.addFreshEntity(zombieEntity);
                }

                if (mutantZombie.getTeam() != null) {
                    Scoreboard scoreboard = level.getScoreboard();
                    scoreboard.addPlayerToTeam(zombieEntity.getScoreboardName(),
                            scoreboard.getPlayerTeam(mutantZombie.getTeam().getName()));
                }

                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
