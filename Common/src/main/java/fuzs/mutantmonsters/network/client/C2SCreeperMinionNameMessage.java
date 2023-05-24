package fuzs.mutantmonsters.network.client;

import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class C2SCreeperMinionNameMessage implements MessageV2<C2SCreeperMinionNameMessage> {
    private int entityId;
    private String name;

    public C2SCreeperMinionNameMessage() {

    }

    public C2SCreeperMinionNameMessage(Entity entity, String name) {
        this.entityId = entity.getId();
        this.name = name;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeUtf(this.name);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.name = buf.readUtf();
    }

    @Override
    public MessageHandler<C2SCreeperMinionNameMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(C2SCreeperMinionNameMessage message, Player player, Object gameInstance) {
                Entity entity = player.level.getEntity(message.entityId);
                if (entity instanceof CreeperMinion) {
                    entity.setCustomName(Component.literal(message.name));
                }
            }
        };
    }
}
