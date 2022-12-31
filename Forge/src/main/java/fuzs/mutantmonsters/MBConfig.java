package fuzs.mutantmonsters;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MBConfig {
    static final ForgeConfigSpec COMMON_SPEC;
    public static final MBConfig COMMON;
    public final ForgeConfigSpec.IntValue mutantCreeperSpawnWeight;
    public final ForgeConfigSpec.IntValue mutantEndermanSpawnWeight;
    public final ForgeConfigSpec.IntValue mutantSkeletonSpawnWeight;
    public final ForgeConfigSpec.IntValue mutantZombieSpawnWeight;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeWhitelist;

    private MBConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Common configuration settings").push("common");
        this.mutantCreeperSpawnWeight = builder.comment("Mutant Creeper spawn weight", "Requires game restart").worldRestart().defineInRange("mutantCreeperSpawnWeight", 4, 0, 100);
        this.mutantEndermanSpawnWeight = builder.comment("Mutant Enderman spawn weight", "Requires game restart").worldRestart().defineInRange("mutantEndermanSpawnWeight", 3, 0, 100);
        this.mutantSkeletonSpawnWeight = builder.comment("Mutant Skeleton spawn weight", "Requires game restart").worldRestart().defineInRange("mutantSkeletonSpawnWeight", 4, 0, 100);
        this.mutantZombieSpawnWeight = builder.comment("Mutant Zombie spawn weight", "Requires game restart").worldRestart().defineInRange("mutantZombieSpawnWeight", 4, 0, 100);
        ForgeConfigSpec.Builder var10001 = builder.comment("Mutants will only spawn in the biomes from the given mod IDs", "Example - \"minecraft\", \"midnight\"", "You can see a mod's ID by clicking the 'Mods' button on the main screen and clicking on the mod's name on the left", "Requires game restart");
        this.biomeWhitelist = var10001.defineList("biomeWhitelist", List.of("minecraft"), String.class::isInstance);
        builder.pop();
    }

    static {
        Pair<MBConfig, ForgeConfigSpec> commonPair = (new ForgeConfigSpec.Builder()).configure(MBConfig::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();
    }
}
