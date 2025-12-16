package fuzs.mutantmonsters.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class CommonConfig implements ConfigCore {
    private static final String SPAWN_WEIGHT_DESCRIPTION = "Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.";

    @Config(description = {
            "Weight for mutant creeper spawns as a percentage of the vanilla creeper spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0)
    public double mutantCreeperSpawnWeight = 0.05;
    @Config(description = {
            "Weight for mutant enderman spawns as a percentage of the vanilla enderman spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0)
    public double mutantEndermanSpawnWeight = 0.05;
    @Config(description = {
            "Weight for mutant skeleton spawns as a percentage of the vanilla skeleton spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0)
    public double mutantSkeletonSpawnWeight = 0.05;
    @Config(description = {
            "Weight for mutant zombie spawns as a percentage of the vanilla zombie spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0)
    public double mutantZombieSpawnWeight = 0.05;
}
