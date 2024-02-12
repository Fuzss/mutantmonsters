package fuzs.mutantmonsters.forge.world.item;

import fuzs.mutantmonsters.world.item.HulkHammerItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HulkHammerForgeItem extends HulkHammerItem {

    public HulkHammerForgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }
}
