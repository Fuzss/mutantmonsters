package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.world.item.*;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {
    public static final Holder.Reference<Item> CREEPER_MINION_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.CREEPER_MINION_ENTITY_TYPE, 894731, 12040119);
    public static final Holder.Reference<Item> MUTANT_CREEPER_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE, 5349438, 11013646);
    public static final Holder.Reference<Item> MUTANT_ENDERMAN_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE, 1447446, 8860812);
    public static final Holder.Reference<Item> MUTANT_SKELETON_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE, 12698049, 6310217);
    public static final Holder.Reference<Item> MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE, 15073279, 16753434);
    public static final Holder.Reference<Item> MUTANT_ZOMBIE_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE, 7969893, 44975);
    public static final Holder.Reference<Item> SPIDER_PIG_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.SPIDER_PIG_ENTITY_TYPE, 3419431, 15771042);
    public static final Holder.Reference<Item> CREEPER_MINION_TRACKER_ITEM = ModRegistry.REGISTRIES.registerItem(
            "creeper_minion_tracker", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Holder.Reference<Item> CREEPER_SHARD_ITEM = ModRegistry.REGISTRIES.registerItem("creeper_shard",
            () -> new CreeperShardItem(new Item.Properties().durability(16)
                    .rarity(Rarity.UNCOMMON)
                    .attributes(CreeperShardItem.createAttributes())
                    .component(ModRegistry.EXPLOSION_RESISTANT_DATA_COMPONENT_TYPE.value(), Unit.INSTANCE))
    );
    public static final Holder.Reference<Item> ENDERSOUL_HAND_ITEM = ModRegistry.REGISTRIES.registerItem(
            "endersoul_hand", () -> new EndersoulHandItem(new Item.Properties().durability(240)
                    .rarity(Rarity.EPIC)
                    .attributes(EndersoulHandItem.createAttributes())));
    public static final Holder.Reference<Item> HULK_HAMMER_ITEM = ModRegistry.REGISTRIES.whenOnFabricLike()
            .registerItem("hulk_hammer", () -> new HulkHammerItem(new Item.Properties().durability(64)
                    .rarity(Rarity.UNCOMMON)
                    .attributes(HulkHammerItem.createAttributes())
                    .component(DataComponents.TOOL, HulkHammerItem.createToolProperties())));
    public static final Holder.Reference<Item> MUTANT_SKELETON_ARMS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_arms", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_LIMB_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_limb", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_PELVIS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_pelvis", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_RIB_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_rib", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_RIB_CAGE_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_rib_cage", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_SHOULDER_PAD_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_shoulder_pad", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_SKULL_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_skull", () -> new ArmorBlockItem(ModRegistry.MUTANT_SKELETON_ARMOR_MATERIAL,
                    ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value(),
                    ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.value(),
                    new Item.Properties().rarity(Rarity.UNCOMMON).durability(ArmorItem.Type.HELMET.getDurability(15))
            ));
    public static final Holder.Reference<Item> MUTANT_SKELETON_CHESTPLATE_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_chestplate",
            () -> new SkeletonArmorItem(ModRegistry.MUTANT_SKELETON_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15))
            )
    );
    public static final Holder.Reference<Item> MUTANT_SKELETON_LEGGINGS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_leggings",
            () -> new SkeletonArmorItem(ModRegistry.MUTANT_SKELETON_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(15))
            )
    );
    public static final Holder.Reference<Item> MUTANT_SKELETON_BOOTS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_boots",
            () -> new SkeletonArmorItem(ModRegistry.MUTANT_SKELETON_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(15))
            )
    );

    public static void touch() {
        // NO-OP
    }
}
