package fuzs.mutantmonsters.data.loot;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class ModEntityLootProvider extends AbstractLootProvider.Simple {

    public ModEntityLootProvider(DataProviderContext context) {
        super(LootContextParamSets.ENTITY, context);
    }

    @Override
    public void addLootTables() {
        this.skipValidation(ModRegistry.CHARGED_MUTANT_CREEPER_LOOT_TABLE);
        this.skipValidation(ModRegistry.CHARGED_CREEPER_MINION_LOOT_TABLE);
        this.add(ModRegistry.MUTANT_ENDERMAN_CONTINUOUS_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.ENDER_PEARL))
                                .add(LootItem.lootTableItem(Items.ENDER_EYE))));
        this.add(ModRegistry.CHARGED_MUTANT_CREEPER_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(NestedLootTable.lootTableReference(BuiltInLootTables.CHARGED_CREEPER))));
        this.add(ModRegistry.CHARGED_CREEPER_MINION_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(NestedLootTable.lootTableReference(BuiltInLootTables.CHARGED_CREEPER))));
    }
}
