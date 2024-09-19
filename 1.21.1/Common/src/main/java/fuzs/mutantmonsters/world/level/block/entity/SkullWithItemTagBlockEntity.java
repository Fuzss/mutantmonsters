package fuzs.mutantmonsters.world.level.block.entity;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SkullWithItemTagBlockEntity extends SkullBlockEntity {
    public static final String TAG_ITEM = "ItemTag";

    @Nullable
    private CompoundTag itemTag;

    public SkullWithItemTagBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.SKULL_BLOCK_ENTITY_TYPE.value();
    }

    public void setItemData(@Nullable CompoundTag skullData) {
        this.itemTag = skullData;
        this.setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.itemTag != null) {
            compound.put(TAG_ITEM, this.itemTag);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains(TAG_ITEM, 10)) {
            this.itemTag = compound.getCompound(TAG_ITEM);
        }
    }
}
