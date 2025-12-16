package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.stream.StreamSupport;

public class SpawningPreventionHandler {
    private static final Object2IntMap<EntityType<?>> SPAWN_LIMITS_PER_ENTITY_TYPE;

    static {
        Object2IntMap<EntityType<?>> map = new Object2IntArrayMap<>();
        map.put(ModEntityTypes.BODY_PART_ENTITY_TYPE.value(), 128);
        map.put(ModEntityTypes.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value(), 128);
        map.put(ModEntityTypes.CREEPER_MINION_EGG_ENTITY_TYPE.value(), 32);
        SPAWN_LIMITS_PER_ENTITY_TYPE = Object2IntMaps.unmodifiable(map);
    }

    public static EventResult onEntitySpawn(Entity entity, ServerLevel serverLevel, boolean isNewlySpawned) {
        if (isNewlySpawned) {
            int spawnLimit = SPAWN_LIMITS_PER_ENTITY_TYPE.getOrDefault(entity.getType(), -1);
            if (spawnLimit != -1) {
                long entitiesOfType = StreamSupport.stream(serverLevel.getAllEntities().spliterator(), false)
                        .filter((Entity currentEntity) -> {
                            return currentEntity.getType() == entity.getType();
                        })
                        .count();
                if (entitiesOfType >= spawnLimit) {
                    return EventResult.INTERRUPT;
                }
            }
        }
        return EventResult.PASS;
    }
}
