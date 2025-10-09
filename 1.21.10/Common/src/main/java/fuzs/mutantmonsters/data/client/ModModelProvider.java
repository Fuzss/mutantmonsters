package fuzs.mutantmonsters.data.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.renderer.special.EndersoulHandSpecialRenderer;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.client.data.v2.models.ItemModelGenerationHelper;
import fuzs.puzzleslib.api.client.data.v2.models.ModelLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.impl.init.LegacySpawnEggItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModModelProvider extends AbstractModelProvider {
    public static final ResourceLocation TEMPLATE_SPAWN_EGG = ModelLocationHelper.getItemModel(MutantMonsters.id(
            "template_spawn_egg"));

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        ItemModelGenerationHelper.generateHead(ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value(),
                ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.value(),
                ModRegistry.MUTANT_SKELETON_SKULL_TYPE,
                blockModelGenerators);
    }

    @Override
    public void addItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateLayeredItem(TEMPLATE_SPAWN_EGG,
                ModelLocationHelper.getItemTexture(MutantMonsters.id("spawn_egg")),
                ModelLocationHelper.getItemTexture(MutantMonsters.id("spawn_egg_overlay")));
        ItemModelGenerationHelper.generateSpawnEgg(ModItems.CREEPER_MINION_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        itemModelGenerators.generateFlatItem(ModItems.CREEPER_MINION_TRACKER_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.CREEPER_SHARD_ITEM.value(),
                ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        this.generateHulkHammer(itemModelGenerators);
        this.generateEndersoulHand(itemModelGenerators);
        generateSpawnEgg(ModItems.MUTANT_CREEPER_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        generateSpawnEgg(ModItems.MUTANT_ENDERMAN_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        generateSpawnEgg(ModItems.MUTANT_SKELETON_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        generateSpawnEgg(ModItems.MUTANT_ZOMBIE_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        generateSpawnEgg(ModItems.MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        generateSpawnEgg(ModItems.SPIDER_PIG_SPAWN_EGG_ITEM.value(), itemModelGenerators);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_ARMS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_BOOTS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value(),
                ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_LIMB_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_PELVIS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_RIB_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_RIB_CAGE_ITEM.value(), ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value(),
                ModelTemplates.FLAT_ITEM);
        itemModelGenerators.generateFlatItem(ModItems.CHEMICAL_X_ITEM.value(), ModelTemplates.FLAT_ITEM);
    }

    public final void generateHulkHammer(ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(ModItems.HULK_HAMMER_ITEM.value(),
                ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked holdingModel = ItemModelUtils.plainModel(ModelLocationHelper.getItemModel(ModItems.HULK_HAMMER_ITEM.value(),
                "_in_hand"));
        itemModelGenerators.itemModelOutput.accept(ModItems.HULK_HAMMER_ITEM.value(),
                ItemModelGenerators.createFlatModelDispatch(itemModel, holdingModel));
    }

    public final void generateEndersoulHand(ItemModelGenerators itemModelGenerators) {
        ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(ModItems.ENDERSOUL_HAND_ITEM.value(),
                ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked holdingModel = ItemModelUtils.specialModel(ModelLocationHelper.getItemModel(ModItems.ENDERSOUL_HAND_ITEM.value(),
                "_in_hand"), new EndersoulHandSpecialRenderer.Unbaked());
        itemModelGenerators.itemModelOutput.accept(ModItems.ENDERSOUL_HAND_ITEM.value(),
                ItemModelGenerators.createFlatModelDispatch(itemModel, holdingModel));
    }

    public static void generateSpawnEgg(Item item, ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(item,
                ItemModelUtils.tintedModel(TEMPLATE_SPAWN_EGG,
                        ItemModelUtils.constantTint(((LegacySpawnEggItem) item).getBackgroundColor()),
                        ItemModelUtils.constantTint(((LegacySpawnEggItem) item).getHighlightColor())));
    }
}
