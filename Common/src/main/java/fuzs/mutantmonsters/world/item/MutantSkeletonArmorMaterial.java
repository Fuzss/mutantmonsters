package fuzs.mutantmonsters.world.item;

import com.google.common.base.Suppliers;
import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public final class MutantSkeletonArmorMaterial implements ArmorMaterial {
    private static final int[] DEFENSE_FOR_SLOT_VALUES = {2, 5, 6, 2};
    private static final int[] DURABILITY_FOR_SLOT_VALUES = {13, 15, 16, 11};
    public static final ArmorMaterial INSTANCE = new MutantSkeletonArmorMaterial();

    private MutantSkeletonArmorMaterial() {

    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return DURABILITY_FOR_SLOT_VALUES[slot.getIndex()] * 15;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return DEFENSE_FOR_SLOT_VALUES[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 9;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    @Override
    public String getName() {
        return "mutant_skeleton";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return ((Supplier<Ingredient>) Suppliers.memoize(() -> Ingredient.EMPTY)).get();
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
