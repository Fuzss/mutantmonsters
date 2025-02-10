package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.S2CAddEntityDataMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;

/**
 * An interface attached to {@link Entity} to send additional data to clients when the entity is added to the client
 * level.
 * <p>Using this interface requires overriding {@link Entity#getAddEntityPacket()} and returning
 * {@link #getPacket(Entity)}
 */
public interface AdditionalSpawnDataEntity {

    /**
     * Read additional entity data from a buffer.
     *
     * @param buf byte buffer to read from
     */
    void readAdditionalAddEntityData(FriendlyByteBuf buf);

    /**
     * Create a custom packet to be returned from {@link Entity#getAddEntityPacket()}
     *
     * @param entity the entity to read data from
     * @param <T>    entity type
     * @return the vanilla packet to be sent
     */
    @SuppressWarnings("unchecked")
    static <T extends Entity & AdditionalSpawnDataEntity> Packet<ClientGamePacketListener> getPacket(T entity, ServerEntity serverEntity) {
        ClientboundAddEntityPacket vanillaPacket = new ClientboundAddEntityPacket(entity, serverEntity);
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        try {
            entity.writeAdditionalAddEntityData(friendlyByteBuf);
            Packet<?> packet = MutantMonsters.NETWORK.toClientboundPacket(
                    new S2CAddEntityDataMessage(vanillaPacket, friendlyByteBuf.array()).toClientboundMessage());
            return (Packet<ClientGamePacketListener>) packet;
        } finally {
            friendlyByteBuf.release();
        }
    }

    /**
     * Write additional entity data to a buffer.
     *
     * @param buf byte buffer to write to
     */
    void writeAdditionalAddEntityData(FriendlyByteBuf buf);
}
