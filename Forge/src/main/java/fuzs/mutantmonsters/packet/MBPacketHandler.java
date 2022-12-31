package fuzs.mutantmonsters.packet;

import fuzs.mutantmonsters.MutantMonsters;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class MBPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(MutantMonsters.id("main"), () -> {
        return "1";
    }, "1"::equals, "1"::equals);

    public MBPacketHandler() {
    }

    public static void register() {
        INSTANCE.registerMessage(0, CreeperMinionTrackerPacket.class, CreeperMinionTrackerPacket::encode, CreeperMinionTrackerPacket::new, CreeperMinionTrackerPacket::handle);
        INSTANCE.registerMessage(1, SpawnParticlePacket.class, SpawnParticlePacket::encode, SpawnParticlePacket::new, SpawnParticlePacket::handle);
        INSTANCE.registerMessage(2, AnimationPacket.class, AnimationPacket::encode, AnimationPacket::new, AnimationPacket::handle);
        INSTANCE.registerMessage(3, FluidParticlePacket.class, FluidParticlePacket::encode, FluidParticlePacket::new, FluidParticlePacket::handle);
        INSTANCE.registerMessage(4, HeldBlockPacket.class, HeldBlockPacket::encode, HeldBlockPacket::new, HeldBlockPacket::handle);
    }
}
