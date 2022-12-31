package fuzs.mutantmonsters.world.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (this.slot == EquipmentSlot.LEGS && !livingEntity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 1, false, false));
        }

        if (this.slot == EquipmentSlot.FEET && !livingEntity.hasEffect(MobEffects.JUMP)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.JUMP, 1, livingEntity.isSprinting() ? 1 : 0, false, false));
        }
    }
}
