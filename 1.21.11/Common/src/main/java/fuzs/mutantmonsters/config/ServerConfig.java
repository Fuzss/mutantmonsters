package fuzs.mutantmonsters.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.puzzleslib.api.config.v3.serialization.ConfigDataSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ServerConfig implements ConfigCore {
    @Config(description = "Maximum distance the endersoul hand can be used to travel to.")
    @Config.IntRange(min = 0, max = 512)
    public int endersoulHandTeleportDistance = 256;
    @Config(name = "mutant_x_conversions", description = {"When infested with a Mutant X potion, what mutant mob should the target transform into. Otherwise the target will ony explode and take damage.", "Format for every entry is \"<namespace>:<path>,<namespace>:<path>\" with the second id representing the mutant. Namespace may be omitted to use \"minecraft\" by default."})
    List<String> mutantXConversionsRaw = Lists.newArrayList("minecraft:creeper,mutantmonsters:mutant_creeper", "minecraft:enderman,mutantmonsters:mutant_enderman", "minecraft:skeleton,mutantmonsters:mutant_skeleton", "minecraft:snow_golem,mutantmonsters:mutant_snow_golem", "minecraft:zombie,mutantmonsters:mutant_zombie", "minecraft:pig,mutantmonsters:spider_pig");

    public Map<EntityType<?>, EntityType<?>> mutantXConversions;

    @Override
    public void afterConfigReload() {
        record MutantXConversion(EntityType<?> entityType, @Nullable Identifier convertsTo) {

            public boolean isValid() {
                if (this.convertsTo != null && BuiltInRegistries.ENTITY_TYPE.containsKey(this.convertsTo)) {
                    return true;
                } else {
                    MutantMonsters.LOGGER.warn("Unable to parse mutated variant for entry {}", BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType));
                    return false;
                }
            }

            public EntityType<?> convertsToType() {
                return BuiltInRegistries.ENTITY_TYPE.getValue(this.convertsTo);
            }
        };
        ConfigDataSet<EntityType<?>> configDataSet = ConfigDataSet.from(Registries.ENTITY_TYPE, this.mutantXConversionsRaw, (integer, o) -> true, String.class);
        this.mutantXConversions = configDataSet.toMap().entrySet().stream()
                .map(data -> new MutantXConversion(data.getKey(), Identifier.tryParse((String) data.getValue()[0])))
                .filter(MutantXConversion::isValid)
                .collect(ImmutableMap.toImmutableMap(MutantXConversion::entityType, MutantXConversion::convertsToType));
    }
}
