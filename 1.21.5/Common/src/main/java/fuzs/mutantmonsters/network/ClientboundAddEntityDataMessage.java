package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.entity.animation.AdditionalSpawnDataEntity;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;

public record ClientboundAddEntityDataMessage(ClientboundAddEntityPacket packet,
                                              CompoundTag compoundTag) implements ClientboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddEntityDataMessage> STREAM_CODEC = StreamCodec.composite(
            ClientboundAddEntityPacket.STREAM_CODEC,
            ClientboundAddEntityDataMessage::packet,
            ByteBufCodecs.TRUSTED_COMPOUND_TAG,
            ClientboundAddEntityDataMessage::compoundTag,
            ClientboundAddEntityDataMessage::new);

    public ClientboundAddEntityDataMessage(Entity entity, ServerEntity serverEntity, CompoundTag compoundTag) {
        this(new ClientboundAddEntityPacket(entity, serverEntity), compoundTag);
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                ClientboundAddEntityPacket packet = ClientboundAddEntityDataMessage.this.packet;
                context.packetListener().handleAddEntity(packet);
                Entity entity = context.level().getEntity(packet.getId());
                if (entity instanceof AdditionalSpawnDataEntity spawnDataEntity) {
                    spawnDataEntity.readAdditionalAddEntityData(ClientboundAddEntityDataMessage.this.compoundTag);
                } else {
                    MutantMonsters.LOGGER.warn("Skipping additional add entity data for entity with id {}",
                            packet.getType());
                }
            }
        };
    }
}
