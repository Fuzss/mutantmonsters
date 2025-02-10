package fuzs.mutantmonsters.world.item;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class ChemicalXItem extends SplashPotionItem {

    public ChemicalXItem(Properties properties) {
        super(properties);
    }

    public static ItemStack setDefaultPotionContents(ItemStack itemStack) {
        itemStack.set(DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY.withEffectAdded(new MobEffectInstance(ModRegistry.CHEMICAL_X_MOB_EFFECT, 1)));
        return itemStack;
    }

    @Override
    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return itemStack.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> tooltipLines, TooltipFlag tooltipFlag) {
        // NO-OP
    }
}
