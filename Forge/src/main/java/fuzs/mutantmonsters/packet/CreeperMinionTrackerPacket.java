package fuzs.mutantmonsters.packet;

import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CreeperMinionTrackerPacket {
    private final int entityId;
    private final byte optionsId;
    private final boolean setOption;

    public CreeperMinionTrackerPacket(CreeperMinionEntity creeperMinion, int optionsId, boolean setOption) {
        this.entityId = creeperMinion.getId();
        this.optionsId = (byte)optionsId;
        this.setOption = setOption;
    }

    void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
        buffer.writeByte(this.optionsId);
        buffer.writeBoolean(this.setOption);
    }

    CreeperMinionTrackerPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readVarInt();
        this.optionsId = buffer.readByte();
        this.setOption = buffer.readBoolean();
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            CreeperMinionEntity creeperMinion = (CreeperMinionEntity) context.get().getSender().level.getEntity(this.entityId);
            if (this.optionsId == 0) {
                creeperMinion.setDestroyBlocks(this.setOption);
            } else if (this.optionsId == 1) {
                creeperMinion.setCustomNameVisible(this.setOption);
            } else if (this.optionsId == 2) {
                creeperMinion.setCanRideOnShoulder(this.setOption);
            }

        });
        context.get().setPacketHandled(true);
    }
}
