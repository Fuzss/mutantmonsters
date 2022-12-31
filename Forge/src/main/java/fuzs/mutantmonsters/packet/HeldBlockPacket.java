package fuzs.mutantmonsters.packet;

import fuzs.mutantmonsters.entity.mutant.MutantEndermanEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class HeldBlockPacket {
    private final int entityId;
    private final int blockId;
    private final byte index;

    public HeldBlockPacket(MutantEndermanEntity mutantEnderman, int blockId, int index) {
        this.entityId = mutantEnderman.getId();
        this.blockId = blockId;
        this.index = (byte)index;
    }

    void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
        buffer.writeVarInt(this.blockId);
        buffer.writeByte(this.index);
    }

    HeldBlockPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readVarInt();
        this.blockId = buffer.readVarInt();
        this.index = buffer.readByte();
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<Level> optionalWorld = LogicalSidedProvider.CLIENTWORLD.get(context.get().getDirection().getReceptionSide());
            optionalWorld.ifPresent((world) -> {
                Entity entity = world.getEntity(this.entityId);
                if (entity instanceof MutantEndermanEntity) {
                    ((MutantEndermanEntity)entity).setHeldBlock(this.index, this.blockId, 0);
                }

            });
        });
        context.get().setPacketHandled(true);
    }
}
