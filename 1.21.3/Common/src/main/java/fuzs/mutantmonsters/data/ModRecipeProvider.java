package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CREEPER_MINION_TRACKER_ITEM.value()).define('I',
                Items.IRON_INGOT
        ).define('S', ModItems.CREEPER_SHARD_ITEM.value()).pattern(" I ").pattern("ISI").pattern(" I ").unlockedBy(
                getHasName(ModItems.CREEPER_SHARD_ITEM.value()), has(ModItems.CREEPER_SHARD_ITEM.value())).save(
                recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MUTANT_SKELETON_ARMS_ITEM.value())
                .define('S', ModItems.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value())
                .define('L', ModItems.MUTANT_SKELETON_LIMB_ITEM.value())
                .pattern("S S")
                .pattern("L L")
                .pattern("L L")
                .unlockedBy(getHasName(ModItems.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value(),
                        ModItems.MUTANT_SKELETON_LIMB_ITEM.value()
                ), has(ModItems.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value(),
                        ModItems.MUTANT_SKELETON_LIMB_ITEM.value()
                ))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MUTANT_SKELETON_BOOTS_ITEM.value()).define('L',
                ModItems.MUTANT_SKELETON_LIMB_ITEM.value()
        ).pattern("L L").unlockedBy(getHasName(ModItems.MUTANT_SKELETON_LIMB_ITEM.value()),
                has(ModItems.MUTANT_SKELETON_LIMB_ITEM.value())
        ).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM.value()).define(
                'A', ModItems.MUTANT_SKELETON_ARMS_ITEM.value()).define('R',
                ModItems.MUTANT_SKELETON_RIB_CAGE_ITEM.value()
        ).pattern("A").pattern("R").unlockedBy(getHasName(ModItems.MUTANT_SKELETON_ARMS_ITEM.value(),
                ModItems.MUTANT_SKELETON_RIB_CAGE_ITEM.value()
        ), has(ModItems.MUTANT_SKELETON_ARMS_ITEM.value(), ModItems.MUTANT_SKELETON_RIB_CAGE_ITEM.value())).save(
                recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MUTANT_SKELETON_LEGGINGS_ITEM.value())
                .define('P', ModItems.MUTANT_SKELETON_PELVIS_ITEM.value())
                .define('L', ModItems.MUTANT_SKELETON_LIMB_ITEM.value())
                .pattern(" P ")
                .pattern("L L")
                .unlockedBy(getHasName(ModItems.MUTANT_SKELETON_PELVIS_ITEM.value(),
                        ModItems.MUTANT_SKELETON_LIMB_ITEM.value()
                ), has(ModItems.MUTANT_SKELETON_PELVIS_ITEM.value(), ModItems.MUTANT_SKELETON_LIMB_ITEM.value()))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.MUTANT_SKELETON_RIB_CAGE_ITEM.value()).define('R',
                ModItems.MUTANT_SKELETON_RIB_ITEM.value()
        ).pattern("R R").pattern("R R").pattern("R R").unlockedBy(
                getHasName(ModItems.MUTANT_SKELETON_RIB_ITEM.value()),
                has(ModItems.MUTANT_SKELETON_RIB_ITEM.value())
        ).save(recipeOutput);
    }
}
