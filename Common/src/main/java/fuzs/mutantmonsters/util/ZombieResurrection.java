package fuzs.mutantmonsters.util;

import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.entity.ai.goal.TrackSummonerGoal;
import fuzs.mutantmonsters.entity.mutant.MutantZombieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.Scoreboard;

public class ZombieResurrection extends BlockPos {
    private int tick;

    public ZombieResurrection(Level world, int x, int y, int z) {
        super(x, y, z);
        this.tick = 100 + world.random.nextInt(40);
    }

    public ZombieResurrection(Level world, BlockPos pos, int tick) {
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

            if ((!checkDay || world.getFluidState(startPos).is(FluidTags.LAVA)) && !world.getFluidState(startPos).isEmpty()) {
                break;
            }

            if (world.isEmptyBlock(startPos)) {
                --i;
            } else {
                if (!world.isEmptyBlock(startPos) && world.isEmptyBlock(posUp) && blockState.getCollisionShape(world, startPos).isEmpty()) {
                    --i;
                    break;
                }

                if (world.isEmptyBlock(startPos) || world.isEmptyBlock(posUp) || world.getBlockState(posUp).getCollisionShape(world, posUp).isEmpty()) {
                    break;
                }

                ++i;
            }
        }

        if (checkDay && world.isDay()) {
            BlockPos lightPos = new BlockPos(x, y + 1, z);
            float f = world.getPathfindingCostFromLightLevels(lightPos);
            if (f > 0.0F && world.canSeeSkyFromBelowWater(lightPos) && world.random.nextInt(3) != 0) {
                return -1;
            }
        }

        return i;
    }

    public static EntityType<? extends Zombie> getZombieByLocation(Level world, BlockPos pos) {
        Holder<Biome> biome = world.getBiome(pos);
        int chance = world.random.nextInt(100);
        // TODO replace with proper tags
        if (biome.is(BiomeTags.ONLY_ALLOWS_SNOW_AND_GOLD_RABBITS)) {
            return chance < 80 && world.canSeeSky(pos) ? EntityType.HUSK : (chance < 1 ? EntityType.ZOMBIE_VILLAGER : EntityType.ZOMBIE);
        } else if ((biome.is(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING)) && world.isWaterAt(pos)) {
            return EntityType.DROWNED;
        } else {
            return chance < 95 ? EntityType.ZOMBIE : EntityType.ZOMBIE_VILLAGER;
        }
    }

    public int getTick() {
        return this.tick;
    }

    public boolean update(MutantZombieEntity mutantZombie) {
        Level world = mutantZombie.level;
        BlockPos abovePos = this.above();
        if (!world.isEmptyBlock(this) && world.isEmptyBlock(abovePos)) {
            if (mutantZombie.getRandom().nextInt(15) == 0) {
                world.levelEvent(2001, abovePos, Block.getId(world.getBlockState(this)));
            }

            if (--this.tick <= 0) {
                Zombie zombieEntity = (Zombie) getZombieByLocation(world, abovePos).create(world);
                if (world instanceof ServerLevelAccessor) {
                    SpawnGroupData ilivingentitydata = zombieEntity.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(this), MobSpawnType.MOB_SUMMONED, null, null);
                    if (ilivingentitydata instanceof Zombie.ZombieGroupData) {
                        new Zombie.ZombieGroupData(((Zombie.ZombieGroupData) ilivingentitydata).isBaby, false);
                    }
                }

                zombieEntity.setHealth(zombieEntity.getMaxHealth() * (0.6F + 0.4F * zombieEntity.getRandom().nextFloat()));
                zombieEntity.playAmbientSound();
                world.levelEvent(2001, abovePos, Block.getId(world.getBlockState(this)));
                if (!world.isClientSide) {
                    zombieEntity.moveTo(abovePos, mutantZombie.getYRot(), 0.0F);
                    CommonAbstractions.INSTANCE.getGoalSelector(zombieEntity).addGoal(0, new TrackSummonerGoal(zombieEntity, mutantZombie));
                    CommonAbstractions.INSTANCE.getGoalSelector(zombieEntity).addGoal(3, new MoveTowardsRestrictionGoal(zombieEntity, 1.0));
                    world.addFreshEntity(zombieEntity);
                }

                if (mutantZombie.getTeam() != null) {
                    Scoreboard scoreboard = world.getScoreboard();
                    scoreboard.addPlayerToTeam(zombieEntity.getScoreboardName(), scoreboard.getPlayerTeam(mutantZombie.getTeam().getName()));
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
