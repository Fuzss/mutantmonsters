package fuzs.mutantmonsters.packet;

import fuzs.mutantmonsters.client.animationapi.Animation;
import fuzs.mutantmonsters.client.animationapi.IAnimatedEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class AnimationPacket {
    private final int entityId;
    private final int index;

    public AnimationPacket(int entityId, int index) {
        this.entityId = entityId;
        this.index = index;
    }

    void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.entityId);
        buffer.writeVarInt(this.index);
    }

    AnimationPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readVarInt();
        this.index = buffer.readVarInt();
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Optional<Level> optional = LogicalSidedProvider.CLIENTWORLD.get(context.get().getDirection().getReceptionSide());
            optional.ifPresent((world) -> {
                Entity entity = world.getEntity(this.entityId);
                if (entity instanceof IAnimatedEntity animatedEntity) {
                    if (this.index == -1) {
                        animatedEntity.setAnimation(Animation.NONE);
                    } else {
                        animatedEntity.setAnimation(animatedEntity.getAnimations()[this.index]);
                    }

                    animatedEntity.setAnimationTick(0);
                }

            });
        });
        context.get().setPacketHandled(true);
    }
}
