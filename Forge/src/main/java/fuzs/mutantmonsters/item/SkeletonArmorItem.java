package fuzs.mutantmonsters.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkeletonArmorItem extends ArmorItem {
    public SkeletonArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (this.slot == EquipmentSlot.LEGS && !player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 1, false, false));
        }

        if (this.slot == EquipmentSlot.FEET && !player.hasEffect(MobEffects.JUMP)) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1, player.isSprinting() ? 1 : 0, false, false));
        }

    }
}
