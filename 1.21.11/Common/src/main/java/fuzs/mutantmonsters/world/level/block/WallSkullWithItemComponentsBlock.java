package fuzs.mutantmonsters.world.level.block;

import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemComponentsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WallSkullWithItemComponentsBlock extends WallSkullBlock {

    public WallSkullWithItemComponentsBlock(SkullBlock.Type skullType, BlockBehaviour.Properties properties) {
        super(skullType, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SkullWithItemComponentsBlockEntity(blockPos, blockState);
    }
}
