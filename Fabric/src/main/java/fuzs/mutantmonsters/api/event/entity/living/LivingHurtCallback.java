package fuzs.mutantmonsters.api.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

@FunctionalInterface
public interface LivingHurtCallback {
    Event<LivingHurtCallback> EVENT = EventFactory.createArrayBacked(LivingHurtCallback.class, listeners -> (LivingEntity entity, DamageSource source, float amount) -> {
        for (LivingHurtCallback event : listeners) {
            if (event.onLivingHurt(entity, source, amount).isPresent()) {
                return Optional.of(Unit.INSTANCE);
            }
        }
        return Optional.empty();
    });

    /**
     * Called right before any reduction on damage due to e.g. armor are done, cancelling prevents any damage/armor durability being taken.
     *
     * @param entity the entity being hurt
     * @param source damage source entity is hurt by
     * @param amount amount hurt
     * @return false to prevent this entity from being hurt, otherwise vanilla will continue to execute
     */
    Optional<Unit> onLivingHurt(LivingEntity entity, DamageSource source, float amount);
}
