package fuzs.mutantmonsters;

import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.config.ServerConfig;
import fuzs.mutantmonsters.handler.EntityEventsHandler;
import fuzs.mutantmonsters.handler.PlayerEventsHandler;
import fuzs.mutantmonsters.handler.SpawningPreventionHandler;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.network.*;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import fuzs.mutantmonsters.world.entity.mutant.*;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.MobSpawnSettingsContext;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDropsCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.ArrowLooseCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.ItemEntityEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.event.v1.level.ExplosionEvents;
import fuzs.puzzleslib.api.init.v3.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.api.network.v2.NetworkHandlerV2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.ToDoubleFunction;

public class MutantMonsters implements ModConstructor {
    public static final String MOD_ID = "mutantmonsters";
    public static final String MOD_NAME = "Mutant Monsters";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV2 NETWORK = NetworkHandlerV2.build(MOD_ID, false);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).common(CommonConfig.class).server(ServerConfig.class);

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
        NETWORK.registerServerbound(C2SCreeperMinionTrackerMessage.class, C2SCreeperMinionTrackerMessage::new);
        NETWORK.registerClientbound(S2CMutantLevelParticlesMessage.class, S2CMutantLevelParticlesMessage::new);
        NETWORK.registerClientbound(S2CAddEntityDataMessage.class, S2CAddEntityDataMessage::new);
        NETWORK.registerServerbound(C2SCreeperMinionNameMessage.class, C2SCreeperMinionNameMessage::new);
        NETWORK.registerClientbound(S2CAnimationMessage.class, S2CAnimationMessage::new);
        NETWORK.registerClientbound(S2CSeismicWaveFluidParticlesMessage.class, S2CSeismicWaveFluidParticlesMessage::new);
        NETWORK.registerClientbound(S2CMutantEndermanHeldBlockMessage.class, S2CMutantEndermanHeldBlockMessage::new);
    }

    private static void registerHandlers() {
        LivingHurtCallback.EVENT.register(EntityEventsHandler::onLivingHurt);
        UseItemEvents.TICK.register(PlayerEventsHandler::onItemUseTick);
        ArrowLooseCallback.EVENT.register(PlayerEventsHandler::onArrowLoose);
        PlayerInteractEvents.USE_ENTITY.register(EntityEventsHandler::onEntityInteract);
        PlayerTickEvents.END.register(PlayerEventsHandler::onPlayerTick$End);
        ServerEntityLevelEvents.LOAD.register(EntityEventsHandler::onEntityLoad);
        LivingDropsCallback.EVENT.register(EntityEventsHandler::onLivingDrops);
        ItemEntityEvents.TOSS.register(PlayerEventsHandler::onItemToss);
        ServerEntityLevelEvents.SPAWN.register(SpawningPreventionHandler::onEntitySpawn);
        ExplosionEvents.DETONATE.register(MutatedExplosionHelper::onExplosionDetonate);
    }

    @Override
    public void onCommonSetup() {
        PotionBrewingRegistry.INSTANCE.registerPotionRecipe(Potions.THICK, Ingredient.of(ModRegistry.ENDERSOUL_HAND_ITEM.value(), ModRegistry.HULK_HAMMER_ITEM.value(), ModRegistry.CREEPER_SHARD_ITEM.value(), ModRegistry.MUTANT_SKELETON_SKULL_ITEM.value()), ModRegistry.CHEMICAL_X_POTION.value());
    }

    @Override
    public void onEntityAttributeCreation(EntityAttributesCreateContext context) {
        context.registerEntityAttributes(ModRegistry.CREEPER_MINION_ENTITY_TYPE.value(), CreeperMinion.registerAttributes());
        context.registerEntityAttributes(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.value(), EndersoulClone.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.value(), MutantCreeper.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value(), MutantEnderman.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(), MutantSnowGolem.registerAttributes());
        context.registerEntityAttributes(ModRegistry.SPIDER_PIG_ENTITY_TYPE.value(), SpiderPig.registerAttributes());
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            context.registerEntityAttributes(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value(), MutantSkeleton.registerAttributes());
            context.registerEntityAttributes(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value(), MutantZombie.registerAttributes());
        }
    }

    @Override
    public void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        context.registerSpawnPlacement(ModRegistry.CREEPER_MINION_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MutantEnderman::canSpawn);
        context.registerSpawnPlacement(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.SPIDER_PIG_ENTITY_TYPE.value(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);
    }

    @Override
    public void onRegisterBiomeModifications(BiomeModificationsContext context) {
        registerMutantSpawn(context, ModRegistry.WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantCreeperSpawnWeight;
        }, EntityType.CREEPER, ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.value());
        registerMutantSpawn(context, ModRegistry.WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantEndermanSpawnWeight;
        }, EntityType.ENDERMAN, ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value());
        registerMutantSpawn(context, ModRegistry.WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantSkeletonSpawnWeight;
        }, EntityType.SKELETON, ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value());
        registerMutantSpawn(context, ModRegistry.WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG, (CommonConfig config) -> {
            return config.mutantZombieSpawnWeight;
        }, EntityType.ZOMBIE, ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value());
    }

    private static void registerMutantSpawn(BiomeModificationsContext context, TagKey<Biome> withoutSpawnsTag, ToDoubleFunction<CommonConfig> spawnWeightGetter, EntityType<?> vanillaEntityType, EntityType<?> mutantEntityType) {
        context.register(BiomeLoadingPhase.ADDITIONS, biomeLoadingContext -> {
            return !biomeLoadingContext.is(withoutSpawnsTag);
        }, biomeModificationContext -> {
            addMutantSpawn(biomeModificationContext.mobSpawnSettings(), spawnWeightGetter.applyAsDouble(CONFIG.get(CommonConfig.class)),
                    vanillaEntityType, mutantEntityType
            );
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
                                new MobSpawnSettings.SpawnerData(mutantEntityType, spawnerDataWeight, 1, 1)
                        );
                    });
            MobSpawnSettings.MobSpawnCost mobSpawnCost = spawnSettings.getSpawnCost(entityType);
            if (mobSpawnCost != null) {
                // just add this with the same values as the vanilla mob, the spawn data weight is what matters most
                spawnSettings.setSpawnCost(mutantEntityType, mobSpawnCost.energyBudget(), mobSpawnCost.charge());
            }
        }
    }

    @Override
    public void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
        context.registerCreativeModeTab(CreativeModeTabConfigurator.from(MOD_ID).icon(() -> new ItemStack(ModRegistry.ENDERSOUL_HAND_ITEM.value())).displayItems((itemDisplayParameters, output) -> {
            output.accept(ModRegistry.CREEPER_MINION_TRACKER_ITEM.value());
            output.accept(ModRegistry.CREEPER_SHARD_ITEM.value());
            output.accept(ModRegistry.ENDERSOUL_HAND_ITEM.value());
            output.accept(ModRegistry.HULK_HAMMER_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_ARMS_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_LIMB_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_RIB_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_RIB_CAGE_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.value());
            output.accept(ModRegistry.CREEPER_MINION_SPAWN_EGG_ITEM.value());
            output.accept(ModRegistry.MUTANT_CREEPER_SPAWN_EGG_ITEM.value());
            output.accept(ModRegistry.MUTANT_ENDERMAN_SPAWN_EGG_ITEM.value());
            output.accept(ModRegistry.MUTANT_SKELETON_SPAWN_EGG_ITEM.value());
            output.accept(ModRegistry.MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM.value());
            output.accept(ModRegistry.MUTANT_ZOMBIE_SPAWN_EGG_ITEM.value());
            output.accept(ModRegistry.SPIDER_PIG_SPAWN_EGG_ITEM.value());
        }).appendEnchantmentsAndPotions());
    }

    @Override
    public ContentRegistrationFlags[] getContentRegistrationFlags() {
        return new ContentRegistrationFlags[]{ContentRegistrationFlags.BIOME_MODIFICATIONS};
    }
}
