package fuzs.mutantmonsters.network;

import fuzs.mutantmonsters.animation.AnimatedEntity;
import fuzs.mutantmonsters.animation.Animation;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class S2CAnimationMessage implements MessageV2<S2CAnimationMessage> {
    private int entityId;
    private int index;

    public S2CAnimationMessage() {

    }

    public S2CAnimationMessage(int entityId, int index) {
        this.entityId = entityId;
        this.index = index;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.index);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.index = buf.readVarInt();
    }

    @Override
    public MessageHandler<S2CAnimationMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(S2CAnimationMessage message, Player player, Object gameInstance) {
                Level world = ((Minecraft) gameInstance).level;
                Entity entity = world.getEntity(message.entityId);
                if (entity instanceof AnimatedEntity animatedEntity) {
                    if (message.index == -1) {
                        animatedEntity.setAnimation(Animation.NONE);
                    } else {
                        animatedEntity.setAnimation(animatedEntity.getAnimations()[message.index]);
                    }
                    animatedEntity.setAnimationTick(0);
                }
            }
        };
    }
}
