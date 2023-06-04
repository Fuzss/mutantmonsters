package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractLootProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModLootTableProvider extends AbstractLootProvider.Simple {

    public ModLootTableProvider(DataGenerator packOutput) {
        super(packOutput, "", ModRegistry.BODY_PART_LOOT_CONTEXT_PARAM_SET);
    }

    @Override
    public void generate() {
        this.add(ModRegistry.MUTANT_SKELETON_PELVIS_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.get()));
        this.add(ModRegistry.MUTANT_SKELETON_RIB_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_RIB_ITEM.get()));
        this.add(ModRegistry.MUTANT_SKELETON_SKULL_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get()));
        this.add(ModRegistry.MUTANT_SKELETON_LIMB_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_LIMB_ITEM.get()));
        this.add(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.get()));
    }

    private static LootTable.Builder getBodyPartBuilder(Item item) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))));
    }
}
