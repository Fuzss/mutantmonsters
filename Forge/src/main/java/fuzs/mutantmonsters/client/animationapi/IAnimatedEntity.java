package fuzs.mutantmonsters.client.animationapi;

import fuzs.mutantmonsters.packet.AnimationPacket;
import fuzs.mutantmonsters.packet.MBPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.ArrayUtils;

public interface IAnimatedEntity extends IEntityAdditionalSpawnData {
    Animation getAnimation();

    void setAnimation(Animation var1);

    Animation[] getAnimations();

    int getAnimationTick();

    void setAnimationTick(int var1);

    default boolean isAnimationPlaying() {
        return this.getAnimation() != Animation.NONE;
    }

    default void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(ArrayUtils.indexOf(this.getAnimations(), this.getAnimation()));
        buffer.writeInt(this.getAnimationTick());
    }

    default void readSpawnData(FriendlyByteBuf additionalData) {
        int animationId = additionalData.readInt();
        this.setAnimation(animationId < 0 ? Animation.NONE : this.getAnimations()[animationId]);
        this.setAnimationTick(additionalData.readInt());
    }

    static <T extends Entity & IAnimatedEntity> void sendAnimationPacket(T entity, Animation animation) {
        if (!entity.level.isClientSide) {
            ((IAnimatedEntity)entity).setAnimation(animation);
            ((IAnimatedEntity)entity).setAnimationTick(0);
            MBPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {
                return entity;
            }), new AnimationPacket(entity.getId(), ArrayUtils.indexOf(((IAnimatedEntity)entity).getAnimations(), animation)));
        }

    }
}