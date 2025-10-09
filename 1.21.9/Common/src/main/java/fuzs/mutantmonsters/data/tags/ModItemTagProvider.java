package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ModItemTagProvider extends AbstractTagProvider<Item> {

    public ModItemTagProvider(DataProviderContext context) {
        super(Registries.ITEM, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.HEAD_ARMOR).add(ModItems.MUTANT_SKELETON_SKULL_ITEM.value());
        this.tag(ItemTags.CHEST_ARMOR).add(ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value());
        this.tag(ItemTags.LEG_ARMOR).add(ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value());
        this.tag(ItemTags.FOOT_ARMOR).add(ModItems.MUTANT_SKELETON_BOOTS_ITEM.value());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.CREEPER_SHARD_ITEM.value(),
                        ModItems.HULK_HAMMER_ITEM.value(),
                        ModItems.ENDERSOUL_HAND_ITEM.value(),
                        ModItems.MUTANT_SKELETON_SKULL_ITEM.value(),
                        ModItems.ENDERSOUL_HAND_ITEM.value());
        this.tag(ItemTags.SKULLS).add(ModItems.MUTANT_SKELETON_SKULL_ITEM.value());
        this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(ModItems.HULK_HAMMER_ITEM.value());
        this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(ModItems.HULK_HAMMER_ITEM.value());
        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .remove(ModItems.MUTANT_SKELETON_SKULL_ITEM.value(),
                        ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value(),
                        ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value(),
                        ModItems.MUTANT_SKELETON_BOOTS_ITEM.value());
        this.tag(ModTags.REPAIRS_SKELETON_ARMOR_ITEM_TAG).add(Items.BONE_BLOCK);
        this.tag(ModTags.SPIDER_PIG_FOOD_ITEM_TAG)
                .add(Items.CARROT, Items.POTATO, Items.BEETROOT, Items.PORKCHOP, Items.SPIDER_EYE);
    }
}
