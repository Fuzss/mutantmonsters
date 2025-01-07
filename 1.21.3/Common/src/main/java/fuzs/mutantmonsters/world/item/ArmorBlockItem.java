package fuzs.mutantmonsters.world.item;

import com.google.common.base.Suppliers;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.function.Supplier;

public class ArmorBlockItem extends StandingAndWallBlockItem implements Equipable {
    private final Holder<ArmorMaterial> material;
    private final Supplier<ItemAttributeModifiers> defaultModifiers;

    public ArmorBlockItem(Holder<ArmorMaterial> material, Block floorBlock, Block wallBlock, Item.Properties properties) {
        super(floorBlock, wallBlock, properties, Direction.DOWN);
        this.material = material;
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        this.defaultModifiers = Suppliers.memoize(() -> {
            return getDefaultArmorItemModifiers(material, ArmorItem.Type.HELMET);
        });
    }

    static ItemAttributeModifiers getDefaultArmorItemModifiers(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        int defense = material.value().getDefense(type);
        float toughness = material.value().toughness();
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
        ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("armor." + type.getName());
        builder.add(Attributes.ARMOR,
                new AttributeModifier(resourceLocation, defense, AttributeModifier.Operation.ADD_VALUE),
                equipmentSlotGroup
        );
        builder.add(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(resourceLocation, toughness, AttributeModifier.Operation.ADD_VALUE),
                equipmentSlotGroup
        );
        float knockbackResistance = material.value().knockbackResistance();
        if (knockbackResistance > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(resourceLocation, knockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup
            );
        }

        return builder.build();
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.value().enchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return this.material.value().repairIngredient().get().test(repair);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        return this.defaultModifiers.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(itemStack);
        ItemStack itemStack2 = player.getItemBySlot(equipmentSlot);
        if (itemStack2.isEmpty()) {
            player.setItemSlot(equipmentSlot, itemStack.copy());
            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }

            itemStack.setCount(0);
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}
