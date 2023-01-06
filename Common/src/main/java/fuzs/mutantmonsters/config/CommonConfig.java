package fuzs.mutantmonsters.config;

import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.ValueCallback;
import fuzs.puzzleslib.config.core.AbstractConfigBuilder;
import fuzs.puzzleslib.config.serialization.ConfigDataSet;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

public class CommonConfig implements ConfigCore {
    public int mutantCreeperSpawnWeight;
    public int mutantEndermanSpawnWeight;
    public int mutantSkeletonSpawnWeight;
    public int mutantZombieSpawnWeight;
//    public ConfigDataSet<Biome> biomeBlacklist;

    @Override
    public void addToBuilder(AbstractConfigBuilder builder, ValueCallback callback) {
        callback.accept(builder.comment("Mutant Creeper spawn weight.").worldRestart().defineInRange("mutant_creeper_spawn_weight", 5, 0, 100), v -> this.mutantCreeperSpawnWeight = v);
        callback.accept(builder.comment("Mutant Enderman spawn weight.").worldRestart().defineInRange("mutant_enderman_spawn_weight", 5, 0, 100), v -> this.mutantEndermanSpawnWeight = v);
        callback.accept(builder.comment("Mutant Skeleton spawn weight.").worldRestart().defineInRange("mutant_skeleton_spawn_weight", 5, 0, 100), v -> this.mutantSkeletonSpawnWeight = v);
        callback.accept(builder.comment("Mutant Zombie spawn weight.").worldRestart().defineInRange("mutant_zombie_spawn_weight", 5, 0, 100), v -> this.mutantZombieSpawnWeight = v);
//        callback.accept(builder.comment("Mutants will not spawn in biomes present in this blacklist.", ConfigDataSet.CONFIG_DESCRIPTION).worldRestart().define("biome_blacklist", ConfigDataSet.toString(Registry.BIOME_REGISTRY)), v -> this.biomeBlacklist = ConfigDataSet.of(Registry.BIOME_REGISTRY, v));
    }
}
