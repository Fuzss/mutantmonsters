package fuzs.mutantmonsters;

import fuzs.mutantmonsters.entity.mutant.MutantSkeletonEntity;
import fuzs.mutantmonsters.entity.mutant.MutantZombieEntity;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModRegistryForge;
import fuzs.puzzleslib.core.CommonFactories;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(MutantMonsters.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MutantMonstersForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CommonFactories.INSTANCE.modConstructor(MutantMonsters.MOD_ID).accept(new MutantMonsters());
        ModRegistryForge.touch();
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        evt.put(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeletonEntity.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 5.0).build());
        evt.put(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombieEntity.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 4.0).build());
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator generator = evt.getGenerator();
        final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
//        generator.addProvider(true, new ModLanguageProvider(generator, MutantMonsters.MOD_ID));
    }

    public static class BrewingRecipe implements IBrewingRecipe {

        @Override
        public boolean isInput(ItemStack input) {
            return input.getItem() == Items.SPLASH_POTION && PotionUtils.getPotion(input) == Potions.THICK;
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            Item item = ingredient.getItem();
            return item == ModRegistry.ENDERSOUL_HAND_ITEM.get() || item == ModRegistry.HULK_HAMMER_ITEM.get() || item == ModRegistry.CREEPER_SHARD_ITEM.get() || item == ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get();
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            return this.isInput(input) && this.isIngredient(ingredient) ? new ItemStack(ModRegistry.CHEMICAL_X_ITEM.get()) : ItemStack.EMPTY;
        }
    }
}
