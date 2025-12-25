package fuzs.mutantmonsters.world.entity.animation;

import fuzs.mutantmonsters.network.ClientboundAddEntityDataMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;

/**
 * An interface attached to {@link Entity} to send additional data to clients when the entity is added to the client
 * level.
 * <p>
 * Using this interface requires overriding {@link Entity#getAddEntityPacket(ServerEntity)} and returning
 * {@link #getPacket(Entity, ServerEntity)}
 */
public interface AdditionalSpawnDataEntity {

    /**
     * Create a custom packet to be returned from {@link Entity#getAddEntityPacket(ServerEntity)}
     *
     * @param entity the entity to read data from
     * @param <T>    entity type
     * @return the vanilla packet to be sent
     */
    @SuppressWarnings("unchecked")
    static <T extends Entity & AdditionalSpawnDataEntity> Packet<ClientGamePacketListener> getPacket(T entity, ServerEntity serverEntity) {
        CompoundTag compoundTag = new CompoundTag();
        entity.writeAdditionalAddEntityData(compoundTag);
        return (Packet<ClientGamePacketListener>) (Packet<?>) new ClientboundAddEntityDataMessage(entity,
                serverEntity,
                compoundTag).toPacket();
    }

    /**
     * Read additional entity data from a buffer.
     *
     * @param compoundTag byte buffer to read from
     */
    void readAdditionalAddEntityData(CompoundTag compoundTag);

    /**
     * Write additional entity data to a buffer.
     *
     * @param compoundTag byte buffer to write to
     */
    void writeAdditionalAddEntityData(CompoundTag compoundTag);
}
