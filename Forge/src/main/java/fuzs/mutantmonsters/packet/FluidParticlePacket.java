package fuzs.mutantmonsters.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class FluidParticlePacket {
    private final BlockPos blockPos;

    public FluidParticlePacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.blockPos);
    }

    FluidParticlePacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<Level> optional = LogicalSidedProvider.CLIENTWORLD.get(context.get().getDirection().getReceptionSide());
            optional.ifPresent((world) -> {
                BlockState blockState = world.getBlockState(this.blockPos);
                blockState.getFluidState().getShape(world, this.blockPos).forAllBoxes((p_228348_3_, p_228348_5_, p_228348_7_, p_228348_9_, p_228348_11_, p_228348_13_) -> {
                    double d1 = Math.min(1.0, p_228348_9_ - p_228348_3_);
                    double d2 = Math.min(1.0, p_228348_11_ - p_228348_5_);
                    double d3 = Math.min(1.0, p_228348_13_ - p_228348_7_);
                    int i = Math.max(2, Mth.ceil(d1 / 0.25));
                    int j = Math.max(2, Mth.ceil(d2 / 0.25));
                    int k = Math.max(2, Mth.ceil(d3 / 0.25));

                    for(int l = 0; l < i; ++l) {
                        for(int i1 = 0; i1 < j; ++i1) {
                            for(int j1 = 0; j1 < k; ++j1) {
                                double d4 = ((double)l + 0.5) / (double)i;
                                double d5 = ((double)i1 + 0.5) / (double)j;
                                double d6 = ((double)j1 + 0.5) / (double)k;
                                double d7 = d4 * d1 + p_228348_3_;
                                double d8 = d5 * d2 + p_228348_5_;
                                double d9 = d6 * d3 + p_228348_7_;
                                world.addAlwaysVisibleParticle((new BlockParticleOption(ParticleTypes.BLOCK, blockState)).setPos(this.blockPos), true, (double)this.blockPos.getX() + d7, (double)this.blockPos.getY() + d8, (double)this.blockPos.getZ() + d9, d4 - 0.5, d5 - 0.5, d6 - 0.5);
                            }
                        }
                    }

                });
            });
        });
        context.get().setPacketHandled(true);
    }
}
