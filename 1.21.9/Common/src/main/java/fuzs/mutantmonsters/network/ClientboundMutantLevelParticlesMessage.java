package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public record ClientboundMutantLevelParticlesMessage(ParticleOptions particle,
                                                     double posX,
                                                     double posY,
                                                     double posZ,
                                                     double speedX,
                                                     double speedY,
                                                     double speedZ,
                                                     int amount) implements ClientboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMutantLevelParticlesMessage> STREAM_CODEC = StreamCodec.composite(
            ParticleTypes.STREAM_CODEC,
            ClientboundMutantLevelParticlesMessage::particle,
            ByteBufCodecs.DOUBLE,
            ClientboundMutantLevelParticlesMessage::posX,
            ByteBufCodecs.DOUBLE,
            ClientboundMutantLevelParticlesMessage::posY,
            ByteBufCodecs.DOUBLE,
            ClientboundMutantLevelParticlesMessage::posZ,
            ByteBufCodecs.DOUBLE,
            ClientboundMutantLevelParticlesMessage::speedX,
            ByteBufCodecs.DOUBLE,
            ClientboundMutantLevelParticlesMessage::speedY,
            ByteBufCodecs.DOUBLE,
            ClientboundMutantLevelParticlesMessage::speedZ,
            ByteBufCodecs.INT,
            ClientboundMutantLevelParticlesMessage::amount,
            ClientboundMutantLevelParticlesMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                Level level = context.level();
                RandomSource randomSource = level.random;
                if (ClientboundMutantLevelParticlesMessage.this.particle ==
                        ModRegistry.ENDERSOUL_PARTICLE_TYPE.value()) {
                    for (int i = 0; i < ClientboundMutantLevelParticlesMessage.this.amount; ++i) {
                        float f = (randomSource.nextFloat() - 0.5F) * 1.8F;
                        float f1 = (randomSource.nextFloat() - 0.5F) * 1.8F;
                        float f2 = (randomSource.nextFloat() - 0.5F) * 1.8F;
                        double x = ClientboundMutantLevelParticlesMessage.this.posX +
                                (double) (randomSource.nextFloat() - 0.5F) *
                                        ClientboundMutantLevelParticlesMessage.this.speedX;
                        double y = ClientboundMutantLevelParticlesMessage.this.posY +
                                (double) (randomSource.nextFloat() - 0.5F) *
                                        ClientboundMutantLevelParticlesMessage.this.speedY + 0.5;
                        double z = ClientboundMutantLevelParticlesMessage.this.posZ +
                                (double) (randomSource.nextFloat() - 0.5F) *
                                        ClientboundMutantLevelParticlesMessage.this.speedZ;
                        level.addAlwaysVisibleParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(),
                                true,
                                x,
                                y,
                                z,
                                f,
                                f1,
                                f2);
                    }
                } else {
                    for (int i = 0; i < ClientboundMutantLevelParticlesMessage.this.amount; ++i) {
                        double d0 = randomSource.nextGaussian() * 0.02;
                        double d1 = randomSource.nextGaussian() * 0.02;
                        double d2 = randomSource.nextGaussian() * 0.02;
                        level.addParticle(ClientboundMutantLevelParticlesMessage.this.particle,
                                ClientboundMutantLevelParticlesMessage.this.posX,
                                ClientboundMutantLevelParticlesMessage.this.posY,
                                ClientboundMutantLevelParticlesMessage.this.posZ,
                                d0,
                                d1,
                                d2);
                    }
                }
            }
        };
    }
}
