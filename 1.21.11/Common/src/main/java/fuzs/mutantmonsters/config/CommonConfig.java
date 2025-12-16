package fuzs.mutantmonsters.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class CommonConfig implements ConfigCore {
    private static final String SPAWN_WEIGHT_DESCRIPTION = "Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.";
    private static final String VALUES_EXAMPLE_DESCRIPTION = "Example: 0.05 = 5%, 1.0 = 100%, 30.0 = 3000%";

    @Config(description = {
            "Weight for mutant creeper spawns as a percentage of the vanilla creeper spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION,
            VALUES_EXAMPLE_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0, max = 1024.0)
    public double mutantCreeperSpawnWeight = 0.05;
    @Config(description = {
            "Weight for mutant enderman spawns as a percentage of the vanilla enderman spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION,
            VALUES_EXAMPLE_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0, max = 1024.0)
    public double mutantEndermanSpawnWeight = 0.05;
    @Config(description = {
            "Weight for mutant skeleton spawns as a percentage of the vanilla skeleton spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION,
            VALUES_EXAMPLE_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0, max = 1024.0)
    public double mutantSkeletonSpawnWeight = 0.05;
    @Config(description = {
            "Weight for mutant zombie spawns as a percentage of the vanilla zombie spawn weight.",
            SPAWN_WEIGHT_DESCRIPTION,
            VALUES_EXAMPLE_DESCRIPTION
    }, worldRestart = true)
    @Config.DoubleRange(min = 0.0, max = 1024.0)
    public double mutantZombieSpawnWeight = 0.05;
}
