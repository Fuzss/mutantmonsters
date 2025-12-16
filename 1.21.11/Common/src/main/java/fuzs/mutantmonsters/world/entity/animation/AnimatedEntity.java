package fuzs.mutantmonsters.world.entity.animation;

import fuzs.mutantmonsters.network.ClientboundAnimationMessage;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.nbt.CompoundTag;
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
    default void writeAdditionalAddEntityData(CompoundTag compoundTag) {
        compoundTag.putByte("animation_index", (byte) ArrayUtils.indexOf(this.getAnimations(), this.getAnimation()));
        compoundTag.putInt("animation_tick", this.getAnimationTick());
    }

    @Override
    default void readAdditionalAddEntityData(CompoundTag compoundTag) {
        int animationId = compoundTag.getByteOr("animation_index", (byte) 0);
        this.setAnimation(animationId < 0 ? EntityAnimation.NONE : this.getAnimations()[animationId]);
        this.setAnimationTick(compoundTag.getIntOr("animation_tick", 0));
    }

    static <T extends Entity & AnimatedEntity> void sendAnimationPacket(T entity, EntityAnimation animation) {
        if (!entity.level().isClientSide()) {
            entity.setAnimation(animation);
            entity.setAnimationTick(0);
            PlayerSet playerSet = PlayerSet.nearEntity(entity);
            MessageSender.broadcast(playerSet,
                    new ClientboundAnimationMessage(entity.getId(),
                            ArrayUtils.indexOf(entity.getAnimations(), animation)));
        }
    }
}
