package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.world.entity.mutant.MutantEnderman;
import fuzs.puzzleslib.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class S2CMutantEndermanHeldBlockMessage implements Message<S2CMutantEndermanHeldBlockMessage> {
    private int entityId;
    private int blockId;
    private byte index;

    public S2CMutantEndermanHeldBlockMessage() {

    }

    public S2CMutantEndermanHeldBlockMessage(MutantEnderman mutantEnderman, int blockId, int index) {
        this.entityId = mutantEnderman.getId();
        this.blockId = blockId;
        this.index = (byte)index;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.blockId);
        buf.writeByte(this.index);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.blockId = buf.readVarInt();
        this.index = buf.readByte();
    }

    @Override
    public MessageHandler<S2CMutantEndermanHeldBlockMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CMutantEndermanHeldBlockMessage message, Player player, Object gameInstance) {
                Level level = ((Minecraft) gameInstance).level;
                Entity entity = level.getEntity(message.entityId);
                if (entity instanceof MutantEnderman) {
                    ((MutantEnderman) entity).setHeldBlock(message.index, message.blockId, 0);
                }
            }
        };
    }
}
