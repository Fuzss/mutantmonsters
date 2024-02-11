package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.item.ArmorBlockForgeItem;
import fuzs.mutantmonsters.world.item.EndersoulHandForgeItem;
import fuzs.mutantmonsters.world.item.HulkHammerForgeItem;
import fuzs.mutantmonsters.world.item.MutantSkeletonArmorMaterial;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static fuzs.mutantmonsters.init.ModRegistry.REGISTRY;

public class ModRegistryForge {
    public static final RegistryReference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_arrow", () -> EntityType.Builder.<MutantArrow>of(MutantArrow::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(false).noSave());
    public static final RegistryReference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.registerEntityType("skull_spirit", () -> EntityType.Builder.<SkullSpirit>of(SkullSpirit::new, MobCategory.MISC).clientTrackingRange(10).updateInterval(20).setShouldReceiveVelocityUpdates(false).sized(0.1F, 0.1F));
    public static final RegistryReference<Item> ENDERSOUL_HAND_ITEM = REGISTRY.registerItem("endersoul_hand", () -> new EndersoulHandForgeItem(new Item.Properties().durability(240).rarity(Rarity.EPIC)));
    public static final RegistryReference<Item> HULK_HAMMER_ITEM = REGISTRY.registerItem("hulk_hammer", () -> new HulkHammerForgeItem(new Item.Properties().durability(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryReference<Item> MUTANT_SKELETON_SKULL_ITEM = REGISTRY.registerItem("mutant_skeleton_skull", () -> new ArmorBlockForgeItem(MutantSkeletonArmorMaterial.INSTANCE, ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.get(), ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static void touch() {

    }
}
