package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class S2CMutantLevelParticlesMessage implements MessageV2<S2CMutantLevelParticlesMessage> {
    private ParticleOptions particleData;
    private double posX;
    private double posY;
    private double posZ;
    private double speedX;
    private double speedY;
    private double speedZ;
    private int amount;

    public S2CMutantLevelParticlesMessage() {

    }

    public S2CMutantLevelParticlesMessage(ParticleOptions particleData, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int amount) {
        this.particleData = particleData;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.amount = amount;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(BuiltInRegistries.PARTICLE_TYPE.getId(this.particleData.getType()));
        this.particleData.writeToNetwork(buf);
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        buf.writeDouble(this.speedX);
        buf.writeDouble(this.speedY);
        buf.writeDouble(this.speedZ);
        buf.writeVarInt(this.amount);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        ParticleType<?> particletype = BuiltInRegistries.PARTICLE_TYPE.byId(buf.readVarInt());
        this.particleData = this.readParticle(buf, particletype);
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.speedX = buf.readDouble();
        this.speedY = buf.readDouble();
        this.speedZ = buf.readDouble();
        this.amount = buf.readVarInt();
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf packetBuffer, ParticleType<T> particleType) {
        return particleType.getDeserializer().fromNetwork(particleType, packetBuffer);
    }

    @Override
    public MessageHandler<S2CMutantLevelParticlesMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CMutantLevelParticlesMessage message, Player player, Object gameInstance) {
                Level level = (((Minecraft) gameInstance)).level;
                if (message.particleData == ModRegistry.ENDERSOUL_PARTICLE_TYPE.value()) {
                    for (int i = 0; i < message.amount; ++i) {
                        float f = (level.random.nextFloat() - 0.5F) * 1.8F;
                        float f1 = (level.random.nextFloat() - 0.5F) * 1.8F;
                        float f2 = (level.random.nextFloat() - 0.5F) * 1.8F;
                        double tempX = message.posX + (double) (level.random.nextFloat() - 0.5F) * message.speedX;
                        double tempY = message.posY + (double) (level.random.nextFloat() - 0.5F) * message.speedY + 0.5;
                        double tempZ = message.posZ + (double) (level.random.nextFloat() - 0.5F) * message.speedZ;
                        level.addAlwaysVisibleParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), true, tempX, tempY, tempZ, f, f1, f2);
                    }
                } else {
                    for (int i = 0; i < message.amount; ++i) {
                        double d0 = level.random.nextGaussian() * 0.02;
                        double d1 = level.random.nextGaussian() * 0.02;
                        double d2 = level.random.nextGaussian() * 0.02;
                        level.addParticle(message.particleData, message.posX, message.posY, message.posZ, d0, d1, d2);
                    }
                }
            }
        };
    }
}
