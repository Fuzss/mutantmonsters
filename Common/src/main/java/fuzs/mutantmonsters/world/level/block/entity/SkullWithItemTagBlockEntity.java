package fuzs.mutantmonsters.world.level.block.entity;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SkullWithItemTagBlockEntity extends SkullBlockEntity {
    private static  final String ITEM_TAG_KEY = "ItemTag";

    @Nullable
    private CompoundTag itemTag;

    public SkullWithItemTagBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.SKULL_BLOCK_ENTITY_TYPE.get();
    }

    public void setItemData(@Nullable CompoundTag skullData) {
        this.itemTag = skullData;
        this.setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.itemTag != null) {
            compound.put(ITEM_TAG_KEY, this.itemTag);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains(ITEM_TAG_KEY, 10)) {
            this.itemTag = compound.getCompound(ITEM_TAG_KEY);
        }
    }
}
