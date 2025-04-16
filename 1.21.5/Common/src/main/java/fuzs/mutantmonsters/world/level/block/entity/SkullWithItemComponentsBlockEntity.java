package fuzs.mutantmonsters.world.level.block.entity;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkullWithItemComponentsBlockEntity extends SkullBlockEntity {
    static final String TAG_CUSTOM_NAME = "custom_name";
    static final String TAG_DAMAGE = "damage";
    static final String TAG_ENCHANTMENTS = "enchantments";
    static final String TAG_REPAIR_COST = "repair_cost";

    private int damage;
    private ItemEnchantments enchantments = ItemEnchantments.EMPTY;
    private int repairCost;

    public SkullWithItemComponentsBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.SKULL_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.saveAdditional(compoundTag, registries);
        compoundTag.putInt(TAG_DAMAGE, this.damage);
        if (!this.enchantments.isEmpty()) {
            compoundTag.store(TAG_ENCHANTMENTS, ItemEnchantments.CODEC, this.enchantments);
        }
        compoundTag.putInt(TAG_REPAIR_COST, this.repairCost);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.loadAdditional(compoundTag, registries);
        this.damage = compoundTag.getIntOr(TAG_DAMAGE, 0);
        this.enchantments = compoundTag.read(TAG_ENCHANTMENTS, ItemEnchantments.CODEC).orElse(ItemEnchantments.EMPTY);
        this.repairCost = compoundTag.getIntOr(TAG_REPAIR_COST, 0);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter dataComponentGetter) {
        super.applyImplicitComponents(dataComponentGetter);
        this.damage = dataComponentGetter.getOrDefault(DataComponents.DAMAGE, 0);
        this.enchantments = dataComponentGetter.get(DataComponents.ENCHANTMENTS);
        this.repairCost = dataComponentGetter.getOrDefault(DataComponents.REPAIR_COST, 0);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.DAMAGE, this.damage);
        components.set(DataComponents.ENCHANTMENTS, this.enchantments);
        components.set(DataComponents.REPAIR_COST, this.repairCost);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove(TAG_CUSTOM_NAME);
        tag.remove(TAG_DAMAGE);
        tag.remove(TAG_ENCHANTMENTS);
        tag.remove(TAG_REPAIR_COST);
    }
}
