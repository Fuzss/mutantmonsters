package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.S2CAnimationMessage;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;

public interface AnimatedEntity extends AdditionalSpawnDataEntity {

    EntityAnimation getAnimation();

    void setAnimation(EntityAnimation var1);

    EntityAnimation[] getAnimations();

    int getAnimationTick();

    void setAnimationTick(int var1);

    default boolean isAnimationPlaying() {
        return this.getAnimation() != EntityAnimation.NONE;
    }

    @Override
    default void writeAdditionalAddEntityData(FriendlyByteBuf buffer) {
        buffer.writeInt(ArrayUtils.indexOf(this.getAnimations(), this.getAnimation()));
        buffer.writeInt(this.getAnimationTick());
    }

    @Override
    default void readAdditionalAddEntityData(FriendlyByteBuf additionalData) {
        int animationId = additionalData.readInt();
        this.setAnimation(animationId < 0 ? EntityAnimation.NONE : this.getAnimations()[animationId]);
        this.setAnimationTick(additionalData.readInt());
    }

    static <T extends Entity & AnimatedEntity> void sendAnimationPacket(T entity, EntityAnimation animation) {
        if (!entity.level().isClientSide) {
            entity.setAnimation(animation);
            entity.setAnimationTick(0);
            PlayerSet playerSet = PlayerSet.nearEntity(entity);
            MutantMonsters.NETWORK.sendMessage(playerSet, new S2CAnimationMessage(entity.getId(),
                    ArrayUtils.indexOf(entity.getAnimations(), animation)
            ).toClientboundMessage());
        }
    }
}