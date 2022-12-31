package fuzs.mutantmonsters.block;

import fuzs.mutantmonsters.tileentity.MBSkullTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MBSkullBlock extends SkullBlock {
    public MBSkullBlock(SkullBlock.Type skullType, BlockBehaviour.Properties properties) {
        super(skullType, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MBSkullTileEntity(pPos, pState);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof MBSkullTileEntity && stack.hasTag()) {
            ((MBSkullTileEntity)tileentity).setItemData(stack.getTag());
        }

    }

    public static enum Types implements SkullBlock.Type {
        MUTANT_SKELETON;

        private Types() {
        }
    }
}
