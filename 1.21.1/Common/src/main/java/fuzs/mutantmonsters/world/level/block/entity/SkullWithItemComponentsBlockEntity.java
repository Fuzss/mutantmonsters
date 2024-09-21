package fuzs.mutantmonsters.world.level.block.entity;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SkullWithItemComponentsBlockEntity extends SkullBlockEntity {
    static final String TAG_CUSTOM_NAME = "custom_name";
    static final String TAG_DAMAGE = "damage";
    static final String TAG_ENCHANTMENTS = "enchantments";
    static final String TAG_REPAIR_COST = "repair_cost";

    @Nullable
    private Component customName;
    private int damage;
    @Nullable
    private ItemEnchantments enchantments;
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
        if (this.customName != null) {
            ComponentSerialization.FLAT_CODEC.encodeStart(NbtOps.INSTANCE, this.customName).ifSuccess(tag -> {
                compoundTag.put(TAG_CUSTOM_NAME, tag);
            });
        }
        compoundTag.putInt(TAG_DAMAGE, this.damage);
        if (this.enchantments != null) {
            ItemEnchantments.CODEC.encodeStart(NbtOps.INSTANCE, this.enchantments).ifSuccess(tag -> {
                compoundTag.put(TAG_ENCHANTMENTS, tag);
            });
        }
        compoundTag.putInt(TAG_REPAIR_COST, this.repairCost);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registries) {
        super.loadAdditional(compoundTag, registries);
        this.customName = ComponentSerialization.FLAT_CODEC.parse(NbtOps.INSTANCE, compoundTag.get(TAG_CUSTOM_NAME))
                .result()
                .orElse(null);
        this.damage = compoundTag.getInt(TAG_DAMAGE);
        this.enchantments = ItemEnchantments.CODEC.parse(NbtOps.INSTANCE, compoundTag.get(TAG_ENCHANTMENTS))
                .result()
                .orElse(null);
        this.repairCost = compoundTag.getInt(TAG_REPAIR_COST);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.customName = componentInput.get(DataComponents.CUSTOM_NAME);
        this.damage = componentInput.getOrDefault(DataComponents.DAMAGE, 0);
        this.enchantments = componentInput.get(DataComponents.ENCHANTMENTS);
        this.repairCost = componentInput.getOrDefault(DataComponents.REPAIR_COST, 0);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (this.customName != null) {
            components.set(DataComponents.CUSTOM_NAME, this.customName);
        }
        components.set(DataComponents.DAMAGE, this.damage);
        if (this.enchantments != null) {
            components.set(DataComponents.ENCHANTMENTS, this.enchantments);
        }
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
