package fuzs.mutantmonsters.network.client;

import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ServerboundCreeperMinionNameMessage(int entityId, String name) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundCreeperMinionNameMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundCreeperMinionNameMessage::entityId,
            ByteBufCodecs.STRING_UTF8,
            ServerboundCreeperMinionNameMessage::name,
            ServerboundCreeperMinionNameMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                Entity entity = context.level().getEntity(ServerboundCreeperMinionNameMessage.this.entityId);
                if (entity instanceof CreeperMinion) {
                    entity.setCustomName(Component.literal(ServerboundCreeperMinionNameMessage.this.name));
                }
            }
        };
    }
}
