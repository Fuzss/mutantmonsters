package fuzs.mutantmonsters.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModLootTableProvider extends LootTableProvider {
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList.of(Pair.of(BodyPartLoot::new, ModRegistry.BODY_PART_LOOT_CONTEXT_PARAM_SET));

    public ModLootTableProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return this.subProviders;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {

    }

    private static class BodyPartLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(ModRegistry.MUTANT_SKELETON_PELVIS_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.get()));
            consumer.accept(ModRegistry.MUTANT_SKELETON_RIB_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_RIB_ITEM.get()));
            consumer.accept(ModRegistry.MUTANT_SKELETON_SKULL_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get()));
            consumer.accept(ModRegistry.MUTANT_SKELETON_LIMB_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_LIMB_ITEM.get()));
            consumer.accept(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE, getBodyPartBuilder(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.get()));
        }

        private static LootTable.Builder getBodyPartBuilder(Item item) {
            return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))));
        }
    }
}
