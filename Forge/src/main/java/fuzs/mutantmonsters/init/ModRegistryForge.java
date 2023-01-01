package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.item.ArmorBlockForgeItem;
import fuzs.mutantmonsters.world.item.EndersoulHandForgeItem;
import fuzs.mutantmonsters.world.item.HulkHammerForgeItem;
import fuzs.mutantmonsters.world.item.MutantSkeletonArmorMaterial;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class ModRegistryForge {
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(MutantMonsters.MOD_ID);
    public static final RegistryReference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.registerEntityTypeBuilder("mutant_arrow", () -> EntityType.Builder.<MutantArrow>of(MutantArrow::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).noSave());
    public static final RegistryReference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.registerEntityTypeBuilder("skull_spirit", () -> EntityType.Builder.<SkullSpirit>of(SkullSpirit::new, MobCategory.MISC).clientTrackingRange(10).updateInterval(20).setShouldReceiveVelocityUpdates(false).sized(0.1F, 0.1F));
    public static final RegistryReference<Item> CREEPER_MINION_SPAWN_EGG_ITEM = REGISTRY.registerItem("creeper_minion_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.CREEPER_MINION_ENTITY_TYPE::get, 894731, 12040119, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> MUTANT_CREEPER_SPAWN_EGG_ITEM = REGISTRY.registerItem("mutant_creeper_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE::get, 5349438, 11013646, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> MUTANT_ENDERMAN_SPAWN_EGG_ITEM = REGISTRY.registerItem("mutant_enderman_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE::get, 1447446, 8860812, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> MUTANT_SKELETON_SPAWN_EGG_ITEM = REGISTRY.registerItem("mutant_skeleton_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE::get, 12698049, 6310217, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM = REGISTRY.registerItem("mutant_snow_golem_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE::get, 15073279, 16753434, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> MUTANT_ZOMBIE_SPAWN_EGG_ITEM = REGISTRY.registerItem("mutant_zombie_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE::get, 7969893, 44975, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> SPIDER_PIG_SPAWN_EGG_ITEM = REGISTRY.registerItem("spider_pig_spawn_egg", () -> new ForgeSpawnEggItem(ModRegistry.SPIDER_PIG_ENTITY_TYPE::get, 3419431, 15771042, new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB)));
    public static final RegistryReference<Item> ENDERSOUL_HAND_ITEM = REGISTRY.registerItem("endersoul_hand", () -> new EndersoulHandForgeItem(new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB).durability(240).rarity(Rarity.EPIC)));
    public static final RegistryReference<Item> HULK_HAMMER_ITEM = REGISTRY.registerItem("hulk_hammer", () -> new HulkHammerForgeItem(new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB).durability(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryReference<Item> MUTANT_SKELETON_SKULL_ITEM = REGISTRY.registerItem("mutant_skeleton_skull", () -> new ArmorBlockForgeItem(MutantSkeletonArmorMaterial.INSTANCE, ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.get(), ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.get(), new Item.Properties().tab(ModRegistry.CREATIVE_MODE_TAB).rarity(Rarity.UNCOMMON)));

    public static void touch() {

    }
}
