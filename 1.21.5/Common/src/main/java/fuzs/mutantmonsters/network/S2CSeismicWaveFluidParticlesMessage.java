package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.services.CommonAbstractions;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class S2CSeismicWaveFluidParticlesMessage implements WritableMessage<S2CSeismicWaveFluidParticlesMessage> {
    private final BlockPos blockPos;

    public S2CSeismicWaveFluidParticlesMessage(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public S2CSeismicWaveFluidParticlesMessage(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.blockPos);
    }

    @Override
    public MessageHandler<S2CSeismicWaveFluidParticlesMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CSeismicWaveFluidParticlesMessage message, Player player, Object gameInstance) {
                Level level = ((Minecraft) gameInstance).level;
                BlockState blockState = level.getBlockState(message.blockPos);
                blockState.getFluidState()
                        .getShape(level, message.blockPos)
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
                                        double d4 = ((double) l + 0.5) / (double) i;
                                        double d5 = ((double) i1 + 0.5) / (double) j;
                                        double d6 = ((double) j1 + 0.5) / (double) k;
                                        double d7 = d4 * d1 + minX;
                                        double d8 = d5 * d2 + minY;
                                        double d9 = d6 * d3 + minZ;
                                        level.addAlwaysVisibleParticle(CommonAbstractions.INSTANCE.createBlockParticle(
                                                        ParticleTypes.BLOCK,
                                                        blockState,
                                                        S2CSeismicWaveFluidParticlesMessage.this.blockPos),
                                                true,
                                                (double) message.blockPos.getX() + d7,
                                                (double) message.blockPos.getY() + d8,
                                                (double) message.blockPos.getZ() + d9,
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
