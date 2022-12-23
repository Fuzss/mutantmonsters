package fuzs.mutantmonsters;

import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import fuzs.mutantmonsters.entity.EndersoulCloneEntity;
import fuzs.mutantmonsters.entity.mutant.*;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.item.ChemicalXItem;
import fuzs.mutantmonsters.packet.MBPacketHandler;
import fuzs.puzzleslib.core.ModConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutantMonsters implements ModConstructor {
    public static final String MOD_ID = "mutantbeasts";
    public static final String MOD_NAME = "Mutant Monsters";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
    }

    @Override
    public void onCommonSetup() {
        MBPacketHandler.register();
        BrewingRecipeRegistry.addRecipe(new ChemicalXItem.BrewingRecipe());
    }

    @Override
    public void onEntityAttributeCreation(EntityAttributesCreateContext context) {
        context.registerEntityAttributes(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), CreeperMinionEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), EndersoulCloneEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), MutantCreeperEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), MutantEndermanEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeletonEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get(), MutantSnowGolemEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombieEntity.registerAttributes());
        context.registerEntityAttributes(ModRegistry.SPIDER_PIG_ENTITY_TYPE.get(), SpiderPigEntity.registerAttributes());
    }

    @Override
    public void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
        context.registerSpawnPlacement(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MutantEndermanEntity::canSpawn);
        context.registerSpawnPlacement(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        context.registerSpawnPlacement(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        context.registerSpawnPlacement(ModRegistry.SPIDER_PIG_ENTITY_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, Animal::checkAnimalSpawnRules);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static ResourceLocation getEntityTexture(String name) {
        return prefix("textures/entity/" + name + ".png");
    }
}
