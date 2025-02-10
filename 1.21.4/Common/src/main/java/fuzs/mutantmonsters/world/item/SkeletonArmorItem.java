package fuzs.mutantmonsters.world.item;

import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import java.util.List;

public class SkeletonArmorItem extends ArmorItem {
    private final ArmorType armorType;

    public SkeletonArmorItem(ArmorMaterial material, ArmorType armorType, Properties properties) {
        super(material, armorType, properties);
        this.armorType = armorType;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.addAll(Proxy.INSTANCE.splitTooltipLines(this.getDescriptionComponent()));
    }

    public Component getDescriptionComponent() {
        return Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GOLD);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && slotId == this.armorType.getSlot().getIndex()) {
            if (player.getInventory().getArmor(slotId) == itemStack) {
                Holder<MobEffect> mobEffect = switch (this.armorType) {
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
