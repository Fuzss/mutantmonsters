package fuzs.mutantmonsters.api.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.OptionalInt;

public final class LivingEntityUseItemEvents {
    public static final Event<Tick> TICK = EventFactory.createArrayBacked(Tick.class, callbacks -> (LivingEntity entity, ItemStack item, int duration) -> {
        for (Tick callback : callbacks) {
            OptionalInt optional = callback.onUseItemTick(entity, item, duration);
            if (optional.isPresent()) return optional;
        }
        return OptionalInt.empty();
    });

    @FunctionalInterface
    public interface Tick {
        /**
         * Fired every tick an entity is using an item.
         *
         * @param entity the entity using the item
         * @param item the item stack being used
         * @param duration the duration the stack has already been in use for
         * @return new duration or -1 if nothing changed
         */
        OptionalInt onUseItemTick(LivingEntity entity, ItemStack item, int duration);
    }
}
