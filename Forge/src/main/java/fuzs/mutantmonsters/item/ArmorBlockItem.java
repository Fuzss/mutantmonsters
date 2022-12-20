package fuzs.mutantmonsters.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.UUID;

public class ArmorBlockItem extends StandingAndWallBlockItem implements Wearable {
    private static final UUID ARMOR_MODIFIER = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;
    private final ArmorMaterial material;

    public ArmorBlockItem(ArmorMaterial material, Block floorBlock, Block wallBlockIn, Item.Properties propertiesIn) {
        super(floorBlock, wallBlockIn, propertiesIn.defaultDurability(material.getDurabilityForSlot(EquipmentSlot.HEAD)));
        this.material = material;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER, "Armor modifier", (double)material.getDefenseForSlot(EquipmentSlot.HEAD), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIER, "Armor toughness", (double)material.getToughness(), AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return armorType == EquipmentSlot.HEAD;
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return this.material.getRepairIngredient().test(repair);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.category == EnchantmentCategory.ARMOR || enchantment.category == EnchantmentCategory.ARMOR_HEAD;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return slot == EquipmentSlot.HEAD ? this.attributeModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult actionresulttype = this.place(new BlockPlaceContext(context));
        return actionresulttype != InteractionResult.SUCCESS ? this.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult() : actionresulttype;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        EquipmentSlot equipmentslottype = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = playerIn.getItemBySlot(equipmentslottype);
        if (itemstack1.isEmpty()) {
            playerIn.setItemSlot(equipmentslottype, itemstack.copy());
            itemstack.setCount(0);
            return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }
}
