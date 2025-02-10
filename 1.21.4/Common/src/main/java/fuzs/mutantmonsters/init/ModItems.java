package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.item.*;
import fuzs.puzzleslib.api.item.v2.ArmorMaterialBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DamageResistant;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

public class ModItems {
    public static final ArmorMaterial MUTANT_SKELETON_ARMOR_MATERIAL = ArmorMaterialBuilder.of(MutantMonsters.id(
                    "mutant_skeleton"), ModTags.REPAIRS_SKELETON_ARMOR_ITEM_TAG)
            .setDurability(15)
            .setDefense(2, 5, 6, 2)
            .setEnchantmentValue(9)
            .build();
    public static final ArmorMaterial MUTANT_SKELETON_HELMET_ARMOR_MATERIAL = ArmorMaterialBuilder.copyOf(
            MUTANT_SKELETON_ARMOR_MATERIAL).setNoAssetId().build();

    public static final Holder.Reference<Item> CREEPER_MINION_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.CREEPER_MINION_ENTITY_TYPE,
            894731,
            12040119);
    public static final Holder.Reference<Item> MUTANT_CREEPER_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE,
            5349438,
            11013646);
    public static final Holder.Reference<Item> MUTANT_ENDERMAN_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE,
            1447446,
            8860812);
    public static final Holder.Reference<Item> MUTANT_SKELETON_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE,
            12698049,
            6310217);
    public static final Holder.Reference<Item> MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE,
            15073279,
            16753434);
    public static final Holder.Reference<Item> MUTANT_ZOMBIE_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE,
            7969893,
            44975);
    public static final Holder.Reference<Item> SPIDER_PIG_SPAWN_EGG_ITEM = ModRegistry.REGISTRIES.registerSpawnEggItem(
            ModEntityTypes.SPIDER_PIG_ENTITY_TYPE,
            3419431,
            15771042);
    public static final Holder.Reference<Item> CREEPER_MINION_TRACKER_ITEM = ModRegistry.REGISTRIES.registerItem(
            "creeper_minion_tracker",
            () -> new Item.Properties().stacksTo(1));
    public static final Holder.Reference<Item> CREEPER_SHARD_ITEM = ModRegistry.REGISTRIES.registerItem("creeper_shard",
            CreeperShardItem::new,
            () -> new Item.Properties().durability(16)
                    .rarity(Rarity.UNCOMMON)
                    .attributes(CreeperShardItem.createAttributes())
                    .component(DataComponents.DAMAGE_RESISTANT, new DamageResistant(DamageTypeTags.IS_EXPLOSION)));
    public static final Holder.Reference<Item> ENDERSOUL_HAND_ITEM = ModRegistry.REGISTRIES.registerItem(
            "endersoul_hand",
            EndersoulHandItem::new,
            () -> new Item.Properties().durability(240)
                    .rarity(Rarity.EPIC)
                    .enchantable(20)
                    .attributes(EndersoulHandItem.createAttributes()));
    public static final Holder.Reference<Item> HULK_HAMMER_ITEM = ModRegistry.REGISTRIES.whenOnFabricLike()
            .registerItem("hulk_hammer",
                    HulkHammerItem::new,
                    () -> new Item.Properties().durability(64)
                            .rarity(Rarity.UNCOMMON)
                            .attributes(HulkHammerItem.createAttributes())
                            .component(DataComponents.TOOL, HulkHammerItem.createToolProperties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_ARMS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_arms");
    public static final Holder.Reference<Item> MUTANT_SKELETON_LIMB_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_limb");
    public static final Holder.Reference<Item> MUTANT_SKELETON_PELVIS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_pelvis");
    public static final Holder.Reference<Item> MUTANT_SKELETON_RIB_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_rib");
    public static final Holder.Reference<Item> MUTANT_SKELETON_RIB_CAGE_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_rib_cage");
    public static final Holder.Reference<Item> MUTANT_SKELETON_SHOULDER_PAD_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_shoulder_pad");
    public static final Holder.Reference<Item> MUTANT_SKELETON_SKULL_ITEM = ModRegistry.REGISTRIES.registerBlockItem(
            ModRegistry.MUTANT_SKELETON_SKULL_BLOCK,
            (Block block, Item.Properties properties) -> new ArmorBlockItem(block,
                    ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.value(),
                    MUTANT_SKELETON_HELMET_ARMOR_MATERIAL,
                    properties),
            () -> new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Holder.Reference<Item> MUTANT_SKELETON_CHESTPLATE_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_chestplate",
            (Item.Properties properties) -> new SkeletonArmorItem(MUTANT_SKELETON_ARMOR_MATERIAL,
                    ArmorType.CHESTPLATE,
                    properties));
    public static final Holder.Reference<Item> MUTANT_SKELETON_LEGGINGS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_leggings",
            (Item.Properties properties) -> new SkeletonArmorItem(MUTANT_SKELETON_ARMOR_MATERIAL,
                    ArmorType.LEGGINGS,
                    properties));
    public static final Holder.Reference<Item> MUTANT_SKELETON_BOOTS_ITEM = ModRegistry.REGISTRIES.registerItem(
            "mutant_skeleton_boots",
            (Item.Properties properties) -> new SkeletonArmorItem(MUTANT_SKELETON_ARMOR_MATERIAL,
                    ArmorType.BOOTS,
                    properties));
    public static final Holder.Reference<Item> CHEMICAL_X_ITEM = ModRegistry.REGISTRIES.registerItem("chemical_x",
            ChemicalXItem::new,
            () -> new Item.Properties().stacksTo(16)
                    .useCooldown(0.5F)
                    .component(DataComponents.POTION_CONTENTS,
                            PotionContents.EMPTY.withEffectAdded(new MobEffectInstance(ModRegistry.CHEMICAL_X_MOB_EFFECT,
                                    1)))
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

    public static void bootstrap() {
        // NO-OP
    }
}
