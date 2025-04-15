package fuzs.mutantmonsters.data.loot;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {

    public ModBlockLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value(), (Block block) -> {
            return LootTable.lootTable().withPool(this.applyExplosionCondition(block, LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(
                                    CopyComponentsFunction.Source.BLOCK_ENTITY)
                            .include(DataComponents.CUSTOM_NAME)
                            .include(DataComponents.DAMAGE)
                            .include(DataComponents.ENCHANTMENTS)
                            .include(DataComponents.REPAIR_COST)))));
        });
    }
}
