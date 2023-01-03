package fuzs.mutantmonsters.network.client;

import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class C2SCreeperMinionTrackerMessage implements Message<C2SCreeperMinionTrackerMessage> {
    private int entityId;
    private byte optionsId;
    private boolean setOption;

    public C2SCreeperMinionTrackerMessage() {

    }

    public C2SCreeperMinionTrackerMessage(CreeperMinion creeperMinion, int optionsId, boolean setOption) {
        this.entityId = creeperMinion.getId();
        this.optionsId = (byte)optionsId;
        this.setOption = setOption;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeByte(this.optionsId);
        buf.writeBoolean(this.setOption);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.optionsId = buf.readByte();
        this.setOption = buf.readBoolean();
    }

    @Override
    public MessageHandler<C2SCreeperMinionTrackerMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SCreeperMinionTrackerMessage message, Player player, Object gameInstance) {
                Entity entity = player.level.getEntity(message.entityId);
                if (entity instanceof CreeperMinion creeperMinion) {
                    if (message.optionsId == 0) {
                        creeperMinion.setDestroyBlocks(message.setOption);
                    } else if (message.optionsId == 1) {
                        creeperMinion.setCustomNameVisible(message.setOption);
                    } else if (message.optionsId == 2) {
                        creeperMinion.setCanRideOnShoulder(message.setOption);
                    }
                }
            }
        };
    }
}
