package fuzs.mutantmonsters.data.loot;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.puzzleslib.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModEntityTypeLootProvider extends AbstractLootProvider.EntityTypes {

    public ModEntityTypeLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.add(ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.GUNPOWDER)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries(),
                                                UniformGenerator.between(0.0F, 1.0F))))));
        this.add(ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(), LootTable.lootTable());
        this.add(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.SNOWBALL)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(32.0F,
                                                48.0F))))));
        this.add(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool().add(LootItem.lootTableItem(ModItems.HULK_HAMMER_ITEM.value()))));
        this.skipValidation(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value());
        this.add(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .add(NestedLootTable.lootTableReference(EntityType.PIG.getDefaultLootTable()
                                        .orElseThrow()))
                                .add(NestedLootTable.lootTableReference(EntityType.SPIDER.getDefaultLootTable()
                                        .orElseThrow()))));
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> entityType) {
        return entityType == ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value() ||
                entityType == ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value() || super.canHaveLootTable(entityType);
    }
}
