package fuzs.mutantmonsters.data.loot;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractLootProviderV2;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModEntityTypeLootProvider extends AbstractLootProviderV2.EntityTypes {

    public ModEntityTypeLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.CREEPER_MINION_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.GUNPOWDER)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                        .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F,
                                                1.0F
                                        )))))
        );
        this.add(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.SNOWBALL)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(32.0F, 48.0F)))))
        );
        this.add(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModRegistry.HULK_HAMMER_ITEM.value())))
        );
        this.skipValidation(ModRegistry.SPIDER_PIG_ENTITY_TYPE.value());
        this.add(ModRegistry.SPIDER_PIG_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootTableReference.lootTableReference(EntityType.PIG.getDefaultLootTable()))
                                .add(LootTableReference.lootTableReference(EntityType.SPIDER.getDefaultLootTable())))
        );
    }

    @Override
    public void skipValidation(EntityType<?> entityType) {
        // TODO remove again when fixed in Puzzles Lib
        this.skipValidation(entityType.getDefaultLootTable());
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> entityType) {
        return entityType == ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value() || entityType == ModRegistry.CREEPER_MINION_ENTITY_TYPE.value() || super.canHaveLootTable(entityType);
    }
}
