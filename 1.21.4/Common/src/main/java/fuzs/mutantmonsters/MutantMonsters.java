package fuzs.mutantmonsters;

import fuzs.mutantmonsters.config.CommonConfig;
import fuzs.mutantmonsters.config.ServerConfig;
import fuzs.mutantmonsters.handler.BiomeModificationsHandler;
import fuzs.mutantmonsters.handler.EntityEventsHandler;
import fuzs.mutantmonsters.handler.PlayerEventsHandler;
import fuzs.mutantmonsters.handler.SpawningPreventionHandler;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.network.*;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.EndersoulClone;
import fuzs.mutantmonsters.world.entity.mutant.*;
import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingDropsCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingHurtCallback;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.ArrowLooseCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.ItemEntityEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTickEvents;
import fuzs.puzzleslib.api.event.v1.level.ExplosionEvents;
import fuzs.puzzleslib.api.event.v1.server.RegisterPotionBrewingMixesCallback;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutantMonsters implements ModConstructor {
    public static final String MOD_ID = "mutantmonsters";
    public static final String MOD_NAME = "Mutant Monsters";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerLegacyServerbound(C2SCreeperMinionTrackerMessage.class, C2SCreeperMinionTrackerMessage::new)
            .registerLegacyClientbound(S2CMutantLevelParticlesMessage.class, S2CMutantLevelParticlesMessage::new)
            .registerLegacyClientbound(S2CAddEntityDataMessage.class, S2CAddEntityDataMessage::new)
            .registerLegacyServerbound(C2SCreeperMinionNameMessage.class, C2SCreeperMinionNameMessage::new)
            .registerLegacyClientbound(S2CAnimationMessage.class, S2CAnimationMessage::new)
            .registerLegacyClientbound(S2CSeismicWaveFluidParticlesMessage.class,
                    S2CSeismicWaveFluidParticlesMessage::new)
            .registerLegacyClientbound(S2CMutantEndermanHeldBlockMessage.class, S2CMutantEndermanHeldBlockMessage::new);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID)
            .common(CommonConfig.class)
            .server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        LivingHurtCallback.EVENT.register(EntityEventsHandler::onLivingHurt);
        UseItemEvents.TICK.register(PlayerEventsHandler::onItemUseTick);
        ArrowLooseCallback.EVENT.register(PlayerEventsHandler::onArrowLoose);
        PlayerInteractEvents.USE_ENTITY.register(EntityEventsHandler::onEntityInteract);
        PlayerTickEvents.END.register(PlayerEventsHandler::onEndPlayerTick);
        ServerEntityLevelEvents.LOAD.register(EntityEventsHandler::onEntityLoad);
        LivingDropsCallback.EVENT.register(EntityEventsHandler::onLivingDrops);
        ItemEntityEvents.TOSS.register(PlayerEventsHandler::onItemToss);
        ServerEntityLevelEvents.SPAWN.register(SpawningPreventionHandler::onEntitySpawn);
        ExplosionEvents.DETONATE.register(MutatedExplosionHelper::onExplosionDetonate);
        RegisterPotionBrewingMixesCallback.EVENT.register(MutantMonsters::registerPotionRecipes);
    }

    private static void registerPotionRecipes(RegisterPotionBrewingMixesCallback.Builder builder) {
        // this is required so the item can be put into brewing stand slots
        builder.registerPotionContainer((PotionItem) ModItems.CHEMICAL_X_ITEM.value());
        builder.registerContainerRecipe((PotionItem) Items.SPLASH_POTION,
                Ingredient.of(ModItems.ENDERSOUL_HAND_ITEM.value(),
                        ModItems.HULK_HAMMER_ITEM.value(),
                        ModItems.CREEPER_SHARD_ITEM.value(),
                        ModItems.MUTANT_SKELETON_SKULL_ITEM.value()),
                (PotionItem) ModItems.CHEMICAL_X_ITEM.value());
    }

    @Override
    public void onCommonSetup() {
        SkullBlock.Type.TYPES.put(ModRegistry.MUTANT_SKELETON_SKULL_TYPE.getSerializedName(),
                ModRegistry.MUTANT_SKELETON_SKULL_TYPE);
    }

    @Override
    public void onEntityAttributeCreation(EntityAttributesCreateContext context) {
        context.registerEntityAttributes(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(),
                CreeperMinion.createAttributes());
        context.registerEntityAttributes(ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(),
                EndersoulClone.createAttributes());
        context.registerEntityAttributes(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(),
                MutantCreeper.createAttributes());
        context.registerEntityAttributes(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                MutantEnderman.createAttributes());
        context.registerEntityAttributes(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                MutantSnowGolem.createAttributes());
        context.registerEntityAttributes(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(), SpiderPig.createAttributes());
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            context.registerEntityAttributes(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(),
                    MutantSkeleton.createAttributes());
            context.registerEntityAttributes(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value(),
                    MutantZombie.createAttributes());
        }
    }

    @Override
    public void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        context.registerSpawnPlacement(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                MutantEnderman::checkMutantEndermanSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING,
                Animal::checkAnimalSpawnRules);
    }

    @Override
    public void onRegisterBiomeModifications(BiomeModificationsContext context) {
        BiomeModificationsHandler.onRegisterBiomeModifications(context);
    }

    public static ResourceLocation id(String name) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, name);
    }
}
