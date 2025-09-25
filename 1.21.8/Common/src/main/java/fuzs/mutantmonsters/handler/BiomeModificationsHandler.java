package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.biome.v1.SpawnerDataBuilder;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.math.Fraction;

import java.util.function.DoubleSupplier;

public final class BiomeModificationsHandler {

    private BiomeModificationsHandler() {
        // NO-OP
    }

    public static void onRegisterBiomeModifications(BiomeModificationsContext context) {
        registerMutantSpawn(context,
                ModTags.WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG,
                () -> MutantMonsters.CONFIG.get(CommonConfig.class).mutantCreeperSpawnWeight,
                EntityType.CREEPER.builtInRegistryHolder(),
                ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE);
        registerMutantSpawn(context,
                ModTags.WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG,
                () -> MutantMonsters.CONFIG.get(CommonConfig.class).mutantEndermanSpawnWeight,
                EntityType.ENDERMAN.builtInRegistryHolder(),
                ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE);
        registerMutantSpawn(context,
                ModTags.WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG,
                () -> MutantMonsters.CONFIG.get(CommonConfig.class).mutantSkeletonSpawnWeight,
                EntityType.SKELETON.builtInRegistryHolder(),
                ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE);
        registerMutantSpawn(context,
                ModTags.WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG,
                () -> MutantMonsters.CONFIG.get(CommonConfig.class).mutantZombieSpawnWeight,
                EntityType.ZOMBIE.builtInRegistryHolder(),
                ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE);
    }

    private static void registerMutantSpawn(BiomeModificationsContext context, TagKey<Biome> withoutSpawnsTag, DoubleSupplier spawnWeightSupplier, Holder.Reference<? extends EntityType<?>> vanillaEntityType, Holder.Reference<? extends EntityType<?>> mutantEntityType) {
        context.registerBiomeModification(BiomeLoadingPhase.ADDITIONS, (BiomeLoadingContext biomeLoadingContext) -> {
            return !biomeLoadingContext.is(withoutSpawnsTag);
        }, (BiomeModificationContext biomeModificationContext) -> {
            SpawnerDataBuilder.create(biomeModificationContext.mobSpawnSettings(), vanillaEntityType.value())
                    .setWeight(Fraction.getFraction(1, Math.round(1.0F / (float) spawnWeightSupplier.getAsDouble())))
                    .setMinCount(1)
                    .setMaxCount(1)
                    .apply(mutantEntityType.value());
        });
    }
}
