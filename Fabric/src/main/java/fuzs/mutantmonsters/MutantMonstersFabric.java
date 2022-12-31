package fuzs.mutantmonsters;

import fuzs.mutantmonsters.api.event.entity.PlayerTickEvents;
import fuzs.mutantmonsters.api.event.entity.item.ItemTossCallback;
import fuzs.mutantmonsters.api.event.entity.living.LivingDropsCallback;
import fuzs.mutantmonsters.api.event.entity.living.LivingEntityUseItemEvents;
import fuzs.mutantmonsters.api.event.entity.living.LivingHurtCallback;
import fuzs.mutantmonsters.api.event.entity.player.ArrowLooseCallback;
import fuzs.mutantmonsters.init.ModRegistryFabric;
import fuzs.puzzleslib.core.CommonFactories;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class MutantMonstersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonFactories.INSTANCE.modConstructor(MutantMonsters.MOD_ID).accept(new MutantMonsters());
        ModRegistryFabric.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        LivingHurtCallback.EVENT.register(EventHandler::onLivingHurt);
        LivingEntityUseItemEvents.TICK.register(EventHandler::onItemUseTick);
        ArrowLooseCallback.EVENT.register(EventHandler::onArrowLoose);
        UseEntityCallback.EVENT.register(EventHandler::onEntityInteract);
        PlayerTickEvents.END_TICK.register(EventHandler::onPlayerTick$End);
        ServerEntityEvents.ENTITY_LOAD.register(EventHandler::onEntityJoinServerLevel);
        ItemTossCallback.EVENT.register(EventHandler::onItemToss);
        LivingDropsCallback.EVENT.register(EventHandler::onLivingDrops);
    }
}
