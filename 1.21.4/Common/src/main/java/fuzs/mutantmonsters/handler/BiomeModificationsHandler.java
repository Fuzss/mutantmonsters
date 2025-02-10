package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.function.ToDoubleFunction;

public final class BiomeModificationsHandler {

    private BiomeModificationsHandler() {
        // NO-OP
    }

    public static void onRegisterBiomeModifications(BiomeModificationsContext context) {
        registerMutantSpawn(context, ModTags.WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantCreeperSpawnWeight;
        }, EntityType.CREEPER.builtInRegistryHolder(), ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE);
        registerMutantSpawn(context, ModTags.WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantEndermanSpawnWeight;
        }, EntityType.ENDERMAN.builtInRegistryHolder(), ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE);
        registerMutantSpawn(context, ModTags.WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantSkeletonSpawnWeight;
        }, EntityType.SKELETON.builtInRegistryHolder(), ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE);
        registerMutantSpawn(context, ModTags.WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantZombieSpawnWeight;
        }, EntityType.ZOMBIE.builtInRegistryHolder(), ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE);
    }

    private static void registerMutantSpawn(BiomeModificationsContext context, TagKey<Biome> withoutSpawnsTag, ToDoubleFunction<CommonConfig> spawnWeightGetter, Holder.Reference<? extends EntityType<?>> vanillaEntityType, Holder.Reference<? extends EntityType<?>> mutantEntityType) {
        context.registerBiomeModification(BiomeLoadingPhase.ADDITIONS, (BiomeLoadingContext biomeLoadingContext) -> {
            return !biomeLoadingContext.is(withoutSpawnsTag);
        }, (BiomeModificationContext biomeModificationContext) -> {
            addMutantSpawn(biomeModificationContext.mobSpawnSettings(),
                    spawnWeightGetter.applyAsDouble(MutantMonsters.CONFIG.get(CommonConfig.class)),
                    vanillaEntityType.value(),
                    mutantEntityType.value());
        });
    }

    private static void addMutantSpawn(MobSpawnSettingsContext spawnSettings, double spawnWeight, EntityType<?> entityType, EntityType<?> mutantEntityType) {
        if (spawnWeight > 0.0) {
            spawnSettings.getSpawnerData(MobCategory.MONSTER)
                    .stream()
                    .filter(data -> data.type == entityType)
                    .findAny()
                    .ifPresent(spawnerData -> {
                        int spawnerDataWeight = Math.max(1, (int) (spawnerData.getWeight().asInt() * spawnWeight));
                        spawnSettings.addSpawn(MobCategory.MONSTER,
                                new MobSpawnSettings.SpawnerData(mutantEntityType, spawnerDataWeight, 1, 1));
                    });
            MobSpawnSettings.MobSpawnCost mobSpawnCost = spawnSettings.getSpawnCost(entityType);
            if (mobSpawnCost != null) {
                // just add this with the same values as the vanilla mob, the spawn data weight is what matters most
                spawnSettings.setSpawnCost(mutantEntityType, mobSpawnCost.energyBudget(), mobSpawnCost.charge());
            }
        }
    }
}
