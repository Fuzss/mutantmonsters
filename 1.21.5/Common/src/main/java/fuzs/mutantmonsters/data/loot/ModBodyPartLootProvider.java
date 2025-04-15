package fuzs.mutantmonsters.data.loot;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBodyPartLootProvider extends AbstractLootProvider.Simple {

    public ModBodyPartLootProvider(DataProviderContext context) {
        super(ModRegistry.BODY_PART_LOOT_CONTEXT_PARAM_SET, context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.MUTANT_SKELETON_PELVIS_LOOT_TABLE,
                getBodyPartBuilder(ModItems.MUTANT_SKELETON_PELVIS_ITEM.value())
        );
        this.add(ModRegistry.MUTANT_SKELETON_RIB_LOOT_TABLE,
                getBodyPartBuilder(ModItems.MUTANT_SKELETON_RIB_ITEM.value())
        );
        this.add(ModRegistry.MUTANT_SKELETON_SKULL_LOOT_TABLE,
                getBodyPartBuilder(ModItems.MUTANT_SKELETON_SKULL_ITEM.value())
        );
        this.add(ModRegistry.MUTANT_SKELETON_LIMB_LOOT_TABLE,
                getBodyPartBuilder(ModItems.MUTANT_SKELETON_LIMB_ITEM.value())
        );
        this.add(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE,
                getBodyPartBuilder(ModItems.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value())
        );
    }

    private static LootTable.Builder getBodyPartBuilder(Item item) {
        return LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))));
    }
}
