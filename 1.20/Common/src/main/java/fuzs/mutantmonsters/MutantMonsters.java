package fuzs.mutantmonsters;

import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.handler.EntityEventsHandler;
import fuzs.mutantmonsters.handler.PlayerEventsHandler;
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
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.context.*;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDropsCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.ArrowLooseCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.ItemTossCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.network.v2.MessageDirection;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
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

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).common(CommonConfig.class);
    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID);

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerMessages();
        registerHandlers();
    }

    private static void registerMessages() {
        NETWORK.register(C2SCreeperMinionTrackerMessage.class, C2SCreeperMinionTrackerMessage::new, MessageDirection.TO_SERVER);
        NETWORK.register(S2CMutantLevelParticlesMessage.class, S2CMutantLevelParticlesMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(C2SCreeperMinionNameMessage.class, C2SCreeperMinionNameMessage::new, MessageDirection.TO_SERVER);
        NETWORK.register(S2CAnimationMessage.class, S2CAnimationMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(S2CSeismicWaveFluidParticlesMessage.class, S2CSeismicWaveFluidParticlesMessage::new, MessageDirection.TO_CLIENT);
        NETWORK.register(S2CMutantEndermanHeldBlockMessage.class, S2CMutantEndermanHeldBlockMessage::new, MessageDirection.TO_CLIENT);
    }

    private static void registerHandlers() {
        LivingHurtCallback.EVENT.register(EntityEventsHandler::onLivingHurt);
        UseItemEvents.TICK.register(PlayerEventsHandler::onItemUseTick);
        ArrowLooseCallback.EVENT.register(PlayerEventsHandler::onArrowLoose);
        PlayerInteractEvents.USE_ENTITY.register(EntityEventsHandler::onEntityInteract);
        PlayerTickEvents.END.register(PlayerEventsHandler::onPlayerTick$End);
        ServerEntityLevelEvents.LOAD.register(EntityEventsHandler::onEntityJoinServerLevel);
        LivingDropsCallback.EVENT.register(EntityEventsHandler::onLivingDrops);
        ItemTossCallback.EVENT.register(PlayerEventsHandler::onItemToss);
    }

    @Override
    public void onCommonSetup(ModLifecycleContext context) {
        context.enqueueWork(() -> {
            PotionBrewingRegistry.INSTANCE.registerPotionRecipe(Potions.THICK, Ingredient.of(ModRegistry.ENDERSOUL_HAND_ITEM.get(), ModRegistry.HULK_HAMMER_ITEM.get(), ModRegistry.CREEPER_SHARD_ITEM.get(), ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get()), ModRegistry.CHEMICAL_X_POTION.get());
        });
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
            int spawnerDataWeight = Math.max(1, (int) (spawnerData.getWeight().asInt() * spawnWeight));
            spawnSettings.addSpawn(mobCategory, new MobSpawnSettings.SpawnerData(mutantEntityType, spawnerDataWeight, 1, 1));
        });
        MobSpawnSettings.MobSpawnCost mobSpawnCost = spawnSettings.getSpawnCost(entityType);
        if (mobSpawnCost != null) {
            // just add this with the same values as the vanilla mob, the spawn data weight is what matters most
            spawnSettings.setSpawnCost(mutantEntityType, mobSpawnCost.energyBudget(), mobSpawnCost.charge());
        }
    }

    @Override
    public void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
        context.registerCreativeModeTab(CreativeModeTabConfigurator.from(MOD_ID).icon(() -> new ItemStack(ModRegistry.ENDERSOUL_HAND_ITEM.get())).displayItems((itemDisplayParameters, output) -> {
            output.accept(ModRegistry.CREEPER_MINION_TRACKER_ITEM.get());
            output.accept(ModRegistry.CREEPER_SHARD_ITEM.get());
            output.accept(ModRegistry.ENDERSOUL_HAND_ITEM.get());
            output.accept(ModRegistry.HULK_HAMMER_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_ARMS_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_LIMB_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_RIB_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_RIB_CAGE_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.get());
            output.accept(ModRegistry.CREEPER_MINION_SPAWN_EGG_ITEM.get());
            output.accept(ModRegistry.MUTANT_CREEPER_SPAWN_EGG_ITEM.get());
            output.accept(ModRegistry.MUTANT_ENDERMAN_SPAWN_EGG_ITEM.get());
            output.accept(ModRegistry.MUTANT_SKELETON_SPAWN_EGG_ITEM.get());
            output.accept(ModRegistry.MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM.get());
            output.accept(ModRegistry.MUTANT_ZOMBIE_SPAWN_EGG_ITEM.get());
            output.accept(ModRegistry.SPIDER_PIG_SPAWN_EGG_ITEM.get());
        }).appendEnchantmentsAndPotions());
    }
}
