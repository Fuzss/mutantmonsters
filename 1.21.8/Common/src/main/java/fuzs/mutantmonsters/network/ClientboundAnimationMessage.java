package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.world.entity.animation.AnimatedEntity;
import fuzs.mutantmonsters.world.entity.animation.EntityAnimation;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record ClientboundAnimationMessage(int entityId, int index) implements ClientboundPlayMessage {
    public static final StreamCodec<ByteBuf, ClientboundAnimationMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ClientboundAnimationMessage::entityId,
            ByteBufCodecs.VAR_INT,
            ClientboundAnimationMessage::index,
            ClientboundAnimationMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                Entity entity = context.level().getEntity(ClientboundAnimationMessage.this.entityId);
                if (entity instanceof AnimatedEntity animatedEntity) {
                    if (ClientboundAnimationMessage.this.index == -1) {
                        animatedEntity.setAnimation(EntityAnimation.NONE);
                    } else {
                        animatedEntity.setAnimation(animatedEntity.getAnimations()[ClientboundAnimationMessage.this.index]);
                    }
                    animatedEntity.setAnimationTick(0);
                }
            }
        };
    }
}
