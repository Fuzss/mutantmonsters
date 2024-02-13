package fuzs.mutantmonsters.data.loot;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemTagBlockEntity;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {

    public ModBlockLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value(), (Block block) -> {
            return LootTable.lootTable()
                    .withPool(this.applyExplosionCondition(block,
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(block)
                                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                                            .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                                                    .copy(SkullWithItemTagBlockEntity.TAG_ITEM + "." +
                                                            ItemStack.TAG_DAMAGE, ItemStack.TAG_DAMAGE)
                                                    .copy(SkullWithItemTagBlockEntity.TAG_ITEM + "." +
                                                            ItemStack.TAG_ENCH, ItemStack.TAG_ENCH)
                                                    .copy(SkullWithItemTagBlockEntity.TAG_ITEM + "." + "RepairCost",
                                                            "RepairCost"
                                                    )
                                                    .copy(SkullWithItemTagBlockEntity.TAG_ITEM + "." +
                                                                    ItemStack.TAG_DISPLAY + "." + ItemStack.TAG_DISPLAY_NAME,
                                                            ItemStack.TAG_DISPLAY + "." + ItemStack.TAG_DISPLAY_NAME
                                                    )))
                    ));
        });
    }
}
