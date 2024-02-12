package fuzs.mutantmonsters.world.level.block;

import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemTagBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WallSkullWithItemTagBlock extends WallSkullBlock {

    public WallSkullWithItemTagBlock(SkullBlock.Type iSkullType, BlockBehaviour.Properties properties) {
        super(iSkullType, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SkullWithItemTagBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof SkullWithItemTagBlockEntity blockEntity && stack.hasTag()) {
            blockEntity.setItemData(stack.getTag());
        }
    }
}
