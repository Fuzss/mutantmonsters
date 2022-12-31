package fuzs.mutantmonsters.api.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public final class PlayerTickEvents {
    public static final Event<StartTick> START_TICK = EventFactory.createArrayBacked(StartTick.class, callbacks -> (Player player) -> {
        for (StartTick callback : callbacks) {
            callback.onStartTick(player);
        }
    });

    public static final Event<EndTick> END_TICK = EventFactory.createArrayBacked(EndTick.class, callbacks -> (Player player) -> {
        for (EndTick callback : callbacks) {
            callback.onEndTick(player);
        }
    });

    @FunctionalInterface
    public interface StartTick {
        /**
         * fired at the beginning of {@link Player#tick()}
         * @param player the ticking player
         */
        void onStartTick(Player player);
    }

    @FunctionalInterface
    public interface EndTick {
        /**
         * fired at the end of {@link Player#tick()}
         * @param player the ticking player
         */
        void onEndTick(Player player);
    }
}
