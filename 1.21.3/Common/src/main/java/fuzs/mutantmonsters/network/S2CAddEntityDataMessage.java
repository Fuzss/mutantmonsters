package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.entity.AdditionalSpawnDataEntity;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class S2CAddEntityDataMessage implements WritableMessage<S2CAddEntityDataMessage> {
    private final ClientboundAddEntityPacket vanillaPacket;
    private final byte[] additionalData;

    public S2CAddEntityDataMessage(ClientboundAddEntityPacket vanillaPacket, byte[] additionalData) {
        this.vanillaPacket = vanillaPacket;
        this.additionalData = additionalData;
    }

    public S2CAddEntityDataMessage(FriendlyByteBuf friendlyByteBuf) {
        this.vanillaPacket = ClientboundAddEntityPacket.STREAM_CODEC.decode((RegistryFriendlyByteBuf) friendlyByteBuf);
        this.additionalData = friendlyByteBuf.readByteArray();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        ClientboundAddEntityPacket.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.vanillaPacket);
        friendlyByteBuf.writeByteArray(this.additionalData);
    }

    @Override
    public MessageHandler<S2CAddEntityDataMessage> makeHandler() {
        return new MessageHandler<>() {
            @Override
            public void handle(S2CAddEntityDataMessage message, Player player, Object instance) {
                Minecraft minecraft = (Minecraft) instance;
                minecraft.getConnection().handleAddEntity(message.vanillaPacket);
                Entity entity = minecraft.level.getEntity(message.vanillaPacket.getId());
                if (entity instanceof AdditionalSpawnDataEntity spawnDataEntity) {
                    FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(
                            Unpooled.wrappedBuffer(message.additionalData));
                    try {
                        spawnDataEntity.readAdditionalAddEntityData(friendlyByteBuf);
                    } finally {
                        friendlyByteBuf.release();
                    }
                } else {
                    EntityType<?> entitytype = message.vanillaPacket.getType();
                    MutantMonsters.LOGGER.warn("Skipping additional add entity data for entity with id {}", entitytype);
                }
            }
        };
    }
}
