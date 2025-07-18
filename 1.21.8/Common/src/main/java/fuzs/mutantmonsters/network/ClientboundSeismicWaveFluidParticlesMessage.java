package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public record ClientboundSeismicWaveFluidParticlesMessage(BlockPos blockPos) implements ClientboundPlayMessage {
    public static final StreamCodec<ByteBuf, ClientboundSeismicWaveFluidParticlesMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundSeismicWaveFluidParticlesMessage::blockPos,
            ClientboundSeismicWaveFluidParticlesMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                BlockPos blockPos = ClientboundSeismicWaveFluidParticlesMessage.this.blockPos;
                BlockState blockState = context.level().getBlockState(blockPos);
                blockState.getFluidState()
                        .getShape(context.level(), blockPos)
                        .forAllBoxes((double minX, double minY, double minZ, double maxX, double maxY, double maxZ) -> {
                            double d1 = Math.min(1.0, maxX - minX);
                            double d2 = Math.min(1.0, maxY - minY);
                            double d3 = Math.min(1.0, maxZ - minZ);
                            int i = Math.max(2, Mth.ceil(d1 / 0.25));
                            int j = Math.max(2, Mth.ceil(d2 / 0.25));
                            int k = Math.max(2, Mth.ceil(d3 / 0.25));

                            for (int l = 0; l < i; ++l) {
                                for (int i1 = 0; i1 < j; ++i1) {
                                    for (int j1 = 0; j1 < k; ++j1) {
                                        double d4 = (l + 0.5) / i;
                                        double d5 = (i1 + 0.5) / j;
                                        double d6 = (j1 + 0.5) / k;
                                        double d7 = d4 * d1 + minX;
                                        double d8 = d5 * d2 + minY;
                                        double d9 = d6 * d3 + minZ;
                                        context.level()
                                                .addAlwaysVisibleParticle(CommonAbstractions.INSTANCE.createBlockParticle(
                                                                ParticleTypes.BLOCK,
                                                                blockState,
                                                                blockPos),
                                                        true,
                                                        blockPos.getX() + d7,
                                                        blockPos.getY() + d8,
                                                        blockPos.getZ() + d9,
                                                        d4 - 0.5,
                                                        d5 - 0.5,
                                                        d6 - 0.5);
                                    }
                                }
                            }
                        });
            }
        };
    }
}
