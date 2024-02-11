package fuzs.mutantmonsters.world.level.block;

import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemTagBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SkullWithItemTagBlock extends SkullBlock {

    public SkullWithItemTagBlock(SkullBlock.Type skullType, BlockBehaviour.Properties properties) {
        super(skullType, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SkullWithItemTagBlockEntity(pPos, pState);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof SkullWithItemTagBlockEntity && stack.hasTag()) {
            ((SkullWithItemTagBlockEntity) tileentity).setItemData(stack.getTag());
        }
    }
}
