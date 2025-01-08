package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
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
        this.add(ItemTags.HEAD_ARMOR).add(ModItems.MUTANT_SKELETON_SKULL_ITEM.value());
        this.add(ItemTags.CHEST_ARMOR).add(ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value());
        this.add(ItemTags.LEG_ARMOR).add(ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value());
        this.add(ItemTags.FOOT_ARMOR).add(ModItems.MUTANT_SKELETON_BOOTS_ITEM.value());
        this.add(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.CREEPER_SHARD_ITEM.value(),
                        ModItems.HULK_HAMMER_ITEM.value(),
                        ModItems.ENDERSOUL_HAND_ITEM.value(),
                        ModItems.MUTANT_SKELETON_SKULL_ITEM.value(),
                        ModItems.ENDERSOUL_HAND_ITEM.value());
        this.add(ItemTags.SKULLS).add(ModItems.MUTANT_SKELETON_SKULL_ITEM.value());
        this.add(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(ModItems.HULK_HAMMER_ITEM.value());
        this.add(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(ModItems.HULK_HAMMER_ITEM.value());
        this.add(ItemTags.TRIMMABLE_ARMOR)
                .remove(ModItems.MUTANT_SKELETON_SKULL_ITEM.value(),
                        ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value(),
                        ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value(),
                        ModItems.MUTANT_SKELETON_BOOTS_ITEM.value());
        this.add(ModRegistry.REPAIRS_SKELETON_ARMOR_ITEM_TAG).add(Items.BONE_BLOCK);
    }
}
