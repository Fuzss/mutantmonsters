package fuzs.mutantmonsters.config;

import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig implements ConfigCore {
    public double mutantCreeperSpawnWeight;
    public double mutantEndermanSpawnWeight;
    public double mutantSkeletonSpawnWeight;
    public double mutantZombieSpawnWeight;

    @Override
    public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Weight for mutant creeper spawns as a percentage of the vanilla creeper spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_creeper_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantCreeperSpawnWeight = v);
        callback.accept(builder.comment("Weight for mutant enderman spawns as a percentage of the vanilla enderman spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_enderman_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantEndermanSpawnWeight = v);
        callback.accept(builder.comment("Weight for mutant skeleton spawns as a percentage of the vanilla skeleton spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_skeleton_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantSkeletonSpawnWeight = v);
        callback.accept(builder.comment("Weight for mutant zombie spawns as a percentage of the vanilla zombie spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_zombie_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantZombieSpawnWeight = v);
    }
}
