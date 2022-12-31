package fuzs.mutantmonsters.api.event.entity.living;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;
import java.util.Optional;

public interface LivingDropsCallback {
    Event<LivingDropsCallback> EVENT = EventFactory.createArrayBacked(LivingDropsCallback.class, listeners -> (LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) -> {
        for (LivingDropsCallback event : listeners) {
            if (event.onLivingDrops(entity, source, drops, lootingLevel, recentlyHit).isPresent()) {
                return Optional.of(Unit.INSTANCE);
            }
        }
        return Optional.empty();
    });

    /**
     * called right before drops from a killed entity are spawned into the world
     *
     * @param entity        the entity that has been killed
     * @param source        damage source that killed the entity
     * @param drops         all drops, including equipment; this can be modified
     * @param lootingLevel  looting level of killer weapon
     * @param recentlyHit   does this count as a player kill
     * @return              is present when the callback is cancelled, allows for manual handling the spawning of drops
     */
    Optional<Unit> onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit);
}
