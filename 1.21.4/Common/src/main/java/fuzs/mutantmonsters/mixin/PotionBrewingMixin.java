package fuzs.mutantmonsters.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.world.item.ChemicalXItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PotionBrewing.class)
abstract class PotionBrewingMixin {

    @ModifyReturnValue(method = "mix", at = @At("RETURN"))
    public ItemStack mix(ItemStack itemStack) {
        return itemStack.is(ModItems.CHEMICAL_X_ITEM) ? ChemicalXItem.setDefaultPotionContents(itemStack) : itemStack;
    }
}
