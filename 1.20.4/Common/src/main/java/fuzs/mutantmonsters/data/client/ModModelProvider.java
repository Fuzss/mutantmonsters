package fuzs.mutantmonsters.data.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.client.data.v2.ItemModelProperties;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class ModModelProvider extends AbstractModelProvider {
    public static final ModelTemplate MUTANT_SPAWN_EGG = createItemModelTemplate(decorateItemModelLocation(
            MutantMonsters.id("template_spawn_egg")));

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelGenerators builder) {
        builder.blockEntityModels(decorateBlockModelLocation(MutantMonsters.id("skull")), Blocks.BONE_BLOCK)
                .createWithCustomBlockItemModel(ModelTemplates.SKULL_INVENTORY,
                        ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value()
                )
                .createWithoutBlockItem(ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.value());
    }

    @Override
    public void addItemModels(ItemModelGenerators builder) {
        builder.generateLayeredItem(decorateItemModelLocation(MutantMonsters.id("template_spawn_egg")),
                decorateItemModelLocation(MutantMonsters.id("spawn_egg")),
                decorateItemModelLocation(MutantMonsters.id("spawn_egg_overlay"))
        );
        createPotionItem(builder, Items.POTION, "chemical_x");
        createPotionItem(builder, Items.SPLASH_POTION, "splash_chemical_x");
        createPotionItem(builder, Items.LINGERING_POTION, "lingering_chemical_x");
        builder.generateFlatItem(ModRegistry.CREEPER_MINION_SPAWN_EGG_ITEM.value(), SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.CREEPER_MINION_TRACKER_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.CREEPER_SHARD_ITEM.value(), ModelTemplates.FLAT_HANDHELD_ROD_ITEM);
        builder.generateFlatItem(ModRegistry.ENDERSOUL_HAND_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.HULK_HAMMER_ITEM.value(), ModelTemplates.FLAT_HANDHELD_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_CREEPER_SPAWN_EGG_ITEM.value(), MUTANT_SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.MUTANT_ENDERMAN_SPAWN_EGG_ITEM.value(), MUTANT_SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_SPAWN_EGG_ITEM.value(), MUTANT_SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.MUTANT_ZOMBIE_SPAWN_EGG_ITEM.value(), MUTANT_SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM.value(), MUTANT_SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.SPIDER_PIG_SPAWN_EGG_ITEM.value(), MUTANT_SPAWN_EGG);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_ARMS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_LIMB_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_RIB_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_RIB_CAGE_ITEM.value(), ModelTemplates.FLAT_ITEM);
        builder.generateFlatItem(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value(), ModelTemplates.FLAT_ITEM);
    }

    private static void createPotionItem(ItemModelGenerators builder, Item item, String modelOverride) {
        builder.generateLayeredItem(decorateItemModelLocation(MutantMonsters.id(modelOverride)),
                decorateItemModelLocation(MutantMonsters.id("potion_overlay")),
                decorateItemModelLocation(MutantMonsters.id(modelOverride))
        );
        ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layered(ModelLocationUtils.decorateItemModelLocation("potion_overlay"),
                        ModelLocationUtils.getModelLocation(item)
                ),
                builder.output, ItemModelProperties.overridesFactory(ModelTemplates.TWO_LAYERED_ITEM,
                        ItemModelProperties.singleOverride(decorateItemModelLocation(MutantMonsters.id(modelOverride)),
                                MutantMonstersClient.CHEMICAL_X_MODEL_PROPERTY,
                                1.0F
                        )
                )
        );
    }
}
