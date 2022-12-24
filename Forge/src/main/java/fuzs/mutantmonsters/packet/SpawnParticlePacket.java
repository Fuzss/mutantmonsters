package fuzs.mutantmonsters.packet;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class SpawnParticlePacket {
    private final ParticleOptions particleData;
    private final double posX;
    private final double posY;
    private final double posZ;
    private final double speedX;
    private final double speedY;
    private final double speedZ;
    private final int amount;

    public SpawnParticlePacket(ParticleOptions particleData, double posX, double posY, double posZ, double speedX, double speedY, double speedZ, int amount) {
        this.particleData = particleData;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        this.amount = amount;
    }

    void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(Registry.PARTICLE_TYPE.getId(this.particleData.getType()));
        this.particleData.writeToNetwork(buffer);
        buffer.writeDouble(this.posX);
        buffer.writeDouble(this.posY);
        buffer.writeDouble(this.posZ);
        buffer.writeDouble(this.speedX);
        buffer.writeDouble(this.speedY);
        buffer.writeDouble(this.speedZ);
        buffer.writeVarInt(this.amount);
    }

    SpawnParticlePacket(FriendlyByteBuf buffer) {
        ParticleType<?> particletype = Registry.PARTICLE_TYPE.byId(buffer.readVarInt());
        if (particletype == null) {
            particletype = ParticleTypes.BLOCK_MARKER;
        }

        this.particleData = this.readParticle(buffer, particletype);
        this.posX = buffer.readDouble();
        this.posY = buffer.readDouble();
        this.posZ = buffer.readDouble();
        this.speedX = buffer.readDouble();
        this.speedY = buffer.readDouble();
        this.speedZ = buffer.readDouble();
        this.amount = buffer.readVarInt();
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf packetBuffer, ParticleType<T> particleType) {
        return particleType.getDeserializer().fromNetwork(particleType, packetBuffer);
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<Level> optional = LogicalSidedProvider.CLIENTWORLD.get(context.get().getDirection().getReceptionSide());
            optional.ifPresent((world) -> {
                int i;
                if (this.particleData == ModRegistry.ENDERSOUL_PARTICLE_TYPE.get()) {
                    for(i = 0; i < this.amount; ++i) {
                        float f = (world.random.nextFloat() - 0.5F) * 1.8F;
                        float f1 = (world.random.nextFloat() - 0.5F) * 1.8F;
                        float f2 = (world.random.nextFloat() - 0.5F) * 1.8F;
                        double tempX = this.posX + (double)(world.random.nextFloat() - 0.5F) * this.speedX;
                        double tempY = this.posY + (double)(world.random.nextFloat() - 0.5F) * this.speedY + 0.5;
                        double tempZ = this.posZ + (double)(world.random.nextFloat() - 0.5F) * this.speedZ;
                        world.addAlwaysVisibleParticle(ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), true, tempX, tempY, tempZ, f, f1, f2);
                    }
                } else {
                    for(i = 0; i < this.amount; ++i) {
                        double d0 = world.random.nextGaussian() * 0.02;
                        double d1 = world.random.nextGaussian() * 0.02;
                        double d2 = world.random.nextGaussian() * 0.02;
                        world.addParticle(this.particleData, this.posX, this.posY, this.posZ, d0, d1, d2);
                    }
                }

            });
        });
        context.get().setPacketHandled(true);
    }
}
