package fuzs.mutantmonsters.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.ValueCallback;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CommonConfig implements ConfigCore {
    public double mutantCreeperSpawnWeight;
    public double mutantEndermanSpawnWeight;
    public double mutantSkeletonSpawnWeight;
    public double mutantZombieSpawnWeight;
//    public ConfigDataSet<Biome> biomeBlacklist;
    @Config(name = "mutant_x_conversions", description = {"When infested with a Mutant X potion, what mutant mob should the target transform into. Otherwise the target will ony explode and take damage.", "Format for every entry is \"<namespace>:<path>,<namespace>:<path>\" with the second id representing the mutant. Namespace may be omitted to use \"minecraft\" by default."})
    List<String> mutantXConversionsRaw = Lists.newArrayList("minecraft:creeper,mutantmonsters:mutant_creeper", "minecraft:enderman,mutantmonsters:mutant_enderman", "minecraft:skeleton,mutantmonsters:mutant_skeleton", "minecraft:snow_golem,mutantmonsters:mutant_snow_golem", "minecraft:zombie,mutantmonsters:mutant_zombie", "minecraft:pig,mutantmonsters:spider_pig");

    public Map<EntityType<?>, EntityType<?>> mutantXConversions;

    @Override
    public void addToBuilder(ForgeConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("Weight for mutant creeper spawns as a percentage of the vanilla creeper spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_creeper_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantCreeperSpawnWeight = v);
        callback.accept(builder.comment("Weight for mutant enderman spawns as a percentage of the vanilla enderman spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_enderman_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantEndermanSpawnWeight = v);
        callback.accept(builder.comment("Weight for mutant skeleton spawns as a percentage of the vanilla skeleton spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_skeleton_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantSkeletonSpawnWeight = v);
        callback.accept(builder.comment("Weight for mutant zombie spawns as a percentage of the vanilla zombie spawn weight. Mutants can spawn in every biome and dimension where their vanilla counterparts can be found.").worldRestart().defineInRange("mutant_zombie_spawn_weight", 0.05, 0.0, 20.0), v -> this.mutantZombieSpawnWeight = v);
//        callback.accept(builder.comment("Mutants will not spawn in biomes present in this blacklist.", ConfigDataSet.CONFIG_DESCRIPTION).worldRestart().define("biome_blacklist", ConfigDataSet.toString(Registry.BIOME_REGISTRY)), v -> this.biomeBlacklist = ConfigDataSet.of(Registry.BIOME_REGISTRY, v));
    }

    @Override
    public void afterConfigReload() {
        record MutantXConversion(EntityType<?> entityType, @Nullable ResourceLocation convertsTo) {

            public boolean isValid() {
                if (this.convertsTo != null && BuiltInRegistries.ENTITY_TYPE.containsKey(this.convertsTo)) {
                    return true;
                } else {
                    MutantMonsters.LOGGER.warn("Unable to parse mutated variant for entry {}", BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType));
                    return false;
                }
            }

            public EntityType<?> convertsToType() {
                return BuiltInRegistries.ENTITY_TYPE.get(this.convertsTo);
            }
        };
        ConfigDataSet<EntityType<?>> configDataSet = ConfigDataSet.from(Registries.ENTITY_TYPE, this.mutantXConversionsRaw, (integer, o) -> true, String.class);
        this.mutantXConversions = configDataSet.toMap().entrySet().stream()
                .map(data -> new MutantXConversion(data.getKey(), ResourceLocation.tryParse((String) data.getValue()[0])))
                .filter(MutantXConversion::isValid)
                .collect(ImmutableMap.toImmutableMap(MutantXConversion::entityType, MutantXConversion::convertsToType));
    }
}
