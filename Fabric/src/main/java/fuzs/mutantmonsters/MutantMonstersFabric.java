package fuzs.mutantmonsters;

import fuzs.mutantmonsters.api.event.entity.PlayerTickEvents;
import fuzs.mutantmonsters.api.event.entity.item.ItemTossCallback;
import fuzs.mutantmonsters.api.event.entity.living.LivingDropsCallback;
import fuzs.mutantmonsters.api.event.entity.living.LivingEntityUseItemEvents;
import fuzs.mutantmonsters.api.event.entity.living.LivingHurtCallback;
import fuzs.mutantmonsters.api.event.entity.player.ArrowLooseCallback;
import fuzs.mutantmonsters.handler.EntityEventsHandler;
import fuzs.mutantmonsters.handler.PlayerEventsHandler;
import fuzs.mutantmonsters.init.ModRegistryFabric;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ContentRegistrationFlags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class MutantMonstersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModRegistryFabric.touch();
        CommonFactories.INSTANCE.modConstructor(MutantMonsters.MOD_ID, ContentRegistrationFlags.BIOMES).accept(new MutantMonsters());
        registerHandlers();
    }

    private static void registerHandlers() {
        LivingHurtCallback.EVENT.register(EntityEventsHandler::onLivingHurt);
        LivingEntityUseItemEvents.TICK.register(PlayerEventsHandler::onItemUseTick);
        ArrowLooseCallback.EVENT.register(PlayerEventsHandler::onArrowLoose);
        UseEntityCallback.EVENT.register(EntityEventsHandler::onEntityInteract);
        PlayerTickEvents.END_TICK.register(PlayerEventsHandler::onPlayerTick$End);
        ServerEntityEvents.ENTITY_LOAD.register(EntityEventsHandler::onEntityJoinServerLevel);
        ItemTossCallback.EVENT.register(PlayerEventsHandler::onItemToss);
        LivingDropsCallback.EVENT.register(EntityEventsHandler::onLivingDrops);
    }
}
