package fuzs.mutantmonsters;

import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.network.S2CAnimationMessage;
import fuzs.mutantmonsters.network.S2CMutantEndermanHeldBlockMessage;
import fuzs.mutantmonsters.network.S2CMutantLevelParticlesMessage;
import fuzs.mutantmonsters.network.S2CSeismicWaveFluidParticlesMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import fuzs.mutantmonsters.world.entity.mutant.*;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.core.ModLoaderEnvironment;
import fuzs.puzzleslib.init.PotionBrewingRegistry;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutantMonsters implements ModConstructor {
    public static final String MOD_ID = "mutantmonsters";
    public static final String MOD_NAME = "Mutant Monsters";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder CONFIG = CommonFactories.INSTANCE.commonConfig(CommonConfig.class, () -> new CommonConfig());
    public static final NetworkHandler NETWORK = CommonFactories.INSTANCE.network(MOD_ID);

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    @Override
    public void onConstructMod() {
        CONFIG.bakeConfigs(MOD_ID);
        ModRegistry.touch();
        registerMessages();
    }

    @Override
    public void onCommonSetup() {
        PotionBrewingRegistry.INSTANCE.registerPotionRecipe(Potions.THICK, Ingredient.of(ModRegistry.ENDERSOUL_HAND_ITEM.get(), ModRegistry.HULK_HAMMER_ITEM.get(), ModRegistry.CREEPER_SHARD_ITEM.get(), ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get()), ModRegistry.CHEMICAL_X_POTION.get());
    }

    private static void registerMessages() {
        NETWORK.register(C2SCreeperMinionTrackerMessage.class, C2SCreeperMinionTrackerMessage::new, MessageDirection.TO_SERVER);
        NETWORK.register(S2CMutantLevelParticlesMessage.class, S2CMutantLevelParticlesMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(C2SCreeperMinionNameMessage.class, C2SCreeperMinionNameMessage::new, MessageDirection.TO_SERVER);
        NETWORK.register(S2CAnimationMessage.class, S2CAnimationMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(S2CSeismicWaveFluidParticlesMessage.class, S2CSeismicWaveFluidParticlesMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(S2CMutantEndermanHeldBlockMessage.class, S2CMutantEndermanHeldBlockMessage::new, MessageDirection.TO_CLIENT);
    }

    @Override
    public void onEntityAttributeCreation(EntityAttributesCreateContext context) {
        context.registerEntityAttributes(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), CreeperMinion.registerAttributes());
        context.registerEntityAttributes(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), EndersoulClone.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), MutantCreeper.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), MutantEnderman.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get(), MutantSnowGolem.registerAttributes());
        context.registerEntityAttributes(ModRegistry.SPIDER_PIG_ENTITY_TYPE.get(), SpiderPig.registerAttributes());
        if (!ModLoaderEnvironment.INSTANCE.getModLoader().isForge()) {
            context.registerEntityAttributes(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeleton.registerAttributes());
            context.registerEntityAttributes(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombie.registerAttributes());
        }
    }

    @Override
    public void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        context.registerSpawnPlacement(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MutantEnderman::canSpawn);
        context.registerSpawnPlacement(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.SPIDER_PIG_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);
    }

    @Override
    public void onRegisterBiomeModifications(BiomeModificationsContext context) {
        context.register(BiomeLoadingPhase.ADDITIONS, biomeLoadingContext -> {
            return true;
//            return !CONFIG.get(CommonConfig.class).biomeBlacklist.contains(biomeLoadingContext.getBiome());
        }, biomeModificationContext -> {
            MobSpawnSettingsContext spawnSettings = biomeModificationContext.mobSpawnSettings();
            CommonConfig config = CONFIG.get(CommonConfig.class);
            addMutantSpawn(spawnSettings, config.mutantCreeperSpawnWeight, MobCategory.MONSTER, EntityType.CREEPER, ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get());
            addMutantSpawn(spawnSettings, config.mutantEndermanSpawnWeight, MobCategory.MONSTER, EntityType.ENDERMAN, ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get());
            addMutantSpawn(spawnSettings, config.mutantSkeletonSpawnWeight, MobCategory.MONSTER, EntityType.SKELETON, ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get());
            addMutantSpawn(spawnSettings, config.mutantZombieSpawnWeight, MobCategory.MONSTER, EntityType.ZOMBIE, ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get());
        });
    }

    private static void addMutantSpawn(MobSpawnSettingsContext spawnSettings, double spawnWeight, MobCategory mobCategory, EntityType<?> entityType, EntityType<?> mutantEntityType) {
        if (spawnWeight == 0.0) return;
        spawnSettings.getSpawnerData(mobCategory).stream().filter(data -> data.type == entityType).findAny().ifPresent(spawnerData -> {
            spawnSettings.addSpawn(mobCategory, new MobSpawnSettings.SpawnerData(mutantEntityType, Math.max(1, (int) (spawnerData.getWeight().asInt() * spawnWeight)), 1, 1));
        });
        MobSpawnSettings.MobSpawnCost mobSpawnCost = spawnSettings.getSpawnCost(entityType);
        if (mobSpawnCost != null) {
            spawnSettings.setSpawnCost(mutantEntityType, mobSpawnCost.getCharge() / spawnWeight, mobSpawnCost.getEnergyBudget() * spawnWeight);
        }
    }
}
