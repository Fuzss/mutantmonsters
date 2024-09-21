package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class S2CMutantLevelParticlesMessage implements MessageV2<S2CMutantLevelParticlesMessage> {
    private ParticleOptions particle;
    private double posX;
    private double posY;
    private double posZ;
    private double speedX;
    private double speedY;
    private double speedZ;
    private int amount;

    public S2CMutantLevelParticlesMessage() {
        // NO-OP
    }

    public S2CMutantLevelParticlesMessage(ParticleOptions particle, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int amount) {
        this.particle = particle;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.amount = amount;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        ParticleTypes.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.particle);
        friendlyByteBuf.writeDouble(this.posX);
        friendlyByteBuf.writeDouble(this.posY);
        friendlyByteBuf.writeDouble(this.posZ);
        friendlyByteBuf.writeDouble(this.speedX);
        friendlyByteBuf.writeDouble(this.speedY);
        friendlyByteBuf.writeDouble(this.speedZ);
        friendlyByteBuf.writeVarInt(this.amount);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.particle = ParticleTypes.STREAM_CODEC.decode((RegistryFriendlyByteBuf) friendlyByteBuf);
        this.posX = friendlyByteBuf.readDouble();
        this.posY = friendlyByteBuf.readDouble();
        this.posZ = friendlyByteBuf.readDouble();
        this.speedX = friendlyByteBuf.readDouble();
        this.speedY = friendlyByteBuf.readDouble();
        this.speedZ = friendlyByteBuf.readDouble();
        this.amount = friendlyByteBuf.readVarInt();
    }

    @Override
    public MessageHandler<S2CMutantLevelParticlesMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CMutantLevelParticlesMessage message, Player player, Object gameInstance) {
                Level level = (((Minecraft) gameInstance)).level;
                if (message.particle == ModRegistry.ENDERSOUL_PARTICLE_TYPE.value()) {
                    for (int i = 0; i < message.amount; ++i) {
                        float f = (level.random.nextFloat() - 0.5F) * 1.8F;
                        float f1 = (level.random.nextFloat() - 0.5F) * 1.8F;
                        float f2 = (level.random.nextFloat() - 0.5F) * 1.8F;
                        double tempX = message.posX + (double) (level.random.nextFloat() - 0.5F) * message.speedX;
                        double tempY = message.posY + (double) (level.random.nextFloat() - 0.5F) * message.speedY + 0.5;
                        double tempZ = message.posZ + (double) (level.random.nextFloat() - 0.5F) * message.speedZ;
                        level.addAlwaysVisibleParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), true, tempX, tempY,
                                tempZ, f, f1, f2
                        );
                    }
                } else {
                    for (int i = 0; i < message.amount; ++i) {
                        double d0 = level.random.nextGaussian() * 0.02;
                        double d1 = level.random.nextGaussian() * 0.02;
                        double d2 = level.random.nextGaussian() * 0.02;
                        level.addParticle(message.particle, message.posX, message.posY, message.posZ, d0, d1, d2);
                    }
                }
            }
        };
    }
}
