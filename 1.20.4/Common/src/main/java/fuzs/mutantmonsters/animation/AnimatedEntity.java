package fuzs.mutantmonsters.animation;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.S2CAnimationMessage;
import fuzs.mutantmonsters.world.entity.AdditionalSpawnDataEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;

public interface AnimatedEntity extends AdditionalSpawnDataEntity {

    Animation getAnimation();

    void setAnimation(Animation var1);

    Animation[] getAnimations();

    int getAnimationTick();

    void setAnimationTick(int var1);

    default boolean isAnimationPlaying() {
        return this.getAnimation() != Animation.NONE;
    }

    @Override
    default void writeAdditionalAddEntityData(FriendlyByteBuf buffer) {
        buffer.writeInt(ArrayUtils.indexOf(this.getAnimations(), this.getAnimation()));
        buffer.writeInt(this.getAnimationTick());
    }

    @Override
    default void readAdditionalAddEntityData(FriendlyByteBuf additionalData) {
        int animationId = additionalData.readInt();
        this.setAnimation(animationId < 0 ? Animation.NONE : this.getAnimations()[animationId]);
        this.setAnimationTick(additionalData.readInt());
    }

    static <T extends Entity & AnimatedEntity> void sendAnimationPacket(T entity, Animation animation) {
        if (!entity.level().isClientSide) {
            entity.setAnimation(animation);
            entity.setAnimationTick(0);
            MutantMonsters.NETWORK.sendToAllTracking(new S2CAnimationMessage(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation)), entity);
        }
    }
}