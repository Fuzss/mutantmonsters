package fuzs.mutantmonsters.world.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SkeletonArmorItem extends ArmorItem {

    public SkeletonArmorItem(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && slotId == this.type.getSlot().getIndex()) {
            if (player.getInventory().getArmor(slotId) == itemStack) {
                Holder<MobEffect> mobEffect = switch (this.type) {
                    case BOOTS -> MobEffects.JUMP;
                    case LEGGINGS -> MobEffects.MOVEMENT_SPEED;
                    default -> null;
                };
                if (mobEffect != null) {
                    player.addEffect(new MobEffectInstance(mobEffect, 100, 1, false, false));
                }
            }
        }
    }
}
