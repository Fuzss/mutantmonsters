package fuzs.mutantmonsters.world.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

public class SkeletonArmorItem extends Item {

    public SkeletonArmorItem(ArmorMaterial armorMaterial, ArmorType armorType, Properties properties) {
        super(skeletonProperties(armorMaterial, armorType, properties));
    }

    private static Item.Properties skeletonProperties(ArmorMaterial armorMaterial, ArmorType armorType, Properties properties) {
        properties.humanoidArmor(armorMaterial, armorType);
        switch (armorType) {
            case BOOTS ->
                    properties.attributes(withJumpStrength(armorType, armorMaterial.createAttributes(armorType), 2));
            case LEGGINGS ->
                    properties.attributes(withMovementSpeed(armorType, armorMaterial.createAttributes(armorType), 2));
        }
        return properties;
    }

    private static ItemAttributeModifiers withJumpStrength(ArmorType armorType, ItemAttributeModifiers itemAttributeModifiers, int amplifier) {
        EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(armorType.getSlot());
        ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("armor." + armorType.getName());
        return itemAttributeModifiers.withModifierAdded(Attributes.JUMP_STRENGTH,
                        new AttributeModifier(resourceLocation, 0.1 * amplifier, AttributeModifier.Operation.ADD_VALUE),
                        equipmentSlotGroup)
                .withModifierAdded(Attributes.SAFE_FALL_DISTANCE,
                        new AttributeModifier(resourceLocation, 1.0 * amplifier, AttributeModifier.Operation.ADD_VALUE),
                        equipmentSlotGroup);
    }

    private static ItemAttributeModifiers withMovementSpeed(ArmorType armorType, ItemAttributeModifiers itemAttributeModifiers, int amplifier) {
        EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(armorType.getSlot());
        ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("armor." + armorType.getName());
        return itemAttributeModifiers.withModifierAdded(Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(resourceLocation,
                                0.2 * amplifier,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        equipmentSlotGroup)
                .withModifierAdded(Attributes.STEP_HEIGHT,
                        new AttributeModifier(resourceLocation,
                                0.25 * amplifier,
                                AttributeModifier.Operation.ADD_VALUE),
                        equipmentSlotGroup);
    }

    public Component getDescriptionComponent() {
        return Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GOLD);
    }
}
