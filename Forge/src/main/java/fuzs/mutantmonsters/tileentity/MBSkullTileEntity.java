package fuzs.mutantmonsters.tileentity;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class MBSkullTileEntity extends SkullBlockEntity {
    @Nullable
    private CompoundTag itemTag;

    public MBSkullTileEntity(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModRegistry.SKULL_BLOCK_ENTITY_TYPE.get();
    }

    @Nullable
    public CompoundTag getItemTag() {
        return this.itemTag;
    }

    public void setItemData(@Nullable CompoundTag skullData) {
        this.itemTag = skullData;
        this.setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.itemTag != null) {
            compound.put("ItemTag", this.itemTag);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("ItemTag", 10)) {
            this.itemTag = compound.getCompound("ItemTag");
        } else if (compound.contains("Item", 10)) {
            this.itemTag = compound.getCompound("Item");
        }

    }
}
