package fuzs.mutantmonsters.network.client;

import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.function.BiConsumer;

public record ServerboundCreeperMinionTrackerMessage(int entityId,
                                                     DataType dataType,
                                                     boolean optionValue) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundCreeperMinionTrackerMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ServerboundCreeperMinionTrackerMessage::entityId,
            ExtraStreamCodecs.fromEnum(DataType.class),
            ServerboundCreeperMinionTrackerMessage::dataType,
            ByteBufCodecs.BOOL,
            ServerboundCreeperMinionTrackerMessage::optionValue,
            ServerboundCreeperMinionTrackerMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                Entity entity = context.level().getEntity(ServerboundCreeperMinionTrackerMessage.this.entityId);
                if (entity instanceof CreeperMinion creeperMinion) {
                    ServerboundCreeperMinionTrackerMessage.this.dataType.valueConsumer.accept(creeperMinion,
                            ServerboundCreeperMinionTrackerMessage.this.optionValue);
                }
            }
        };
    }

    public enum DataType {
        CAN_DESTROY_BLOCKS(CreeperMinion::setDestroyBlocks),
        IS_CUSTOM_NAME_VISIBLE(CreeperMinion::setCustomNameVisible),
        CAN_RIDE_ON_SHOULDER(CreeperMinion::setCanRideOnShoulder);

        final BiConsumer<CreeperMinion, Boolean> valueConsumer;

        DataType(BiConsumer<CreeperMinion, Boolean> valueConsumer) {
            this.valueConsumer = valueConsumer;
        }
    }
}
