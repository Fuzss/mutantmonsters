package fuzs.mutantmonsters;

import fuzs.mutantmonsters.handler.EntityEventsHandler;
import fuzs.mutantmonsters.handler.PlayerEventsHandler;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModRegistryForge;
import fuzs.puzzleslib.core.CommonFactories;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(MutantMonsters.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MutantMonstersForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CommonFactories.INSTANCE.modConstructor(MutantMonsters.MOD_ID).accept(new MutantMonsters());
        ModRegistryForge.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final LivingHurtEvent evt) -> {
            EntityEventsHandler.onLivingHurt(evt.getEntity(), evt.getSource(), evt.getAmount());
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingEntityUseItemEvent.Tick evt) -> {
            PlayerEventsHandler.onItemUseTick(evt.getEntity(), evt.getItem(), evt.getDuration()).ifPresent(evt::setDuration);
        });
        MinecraftForge.EVENT_BUS.addListener((final ArrowLooseEvent evt) -> {
            PlayerEventsHandler.onArrowLoose(evt.getEntity(), evt.getBow(), evt.getLevel(), evt.getCharge(), evt.hasAmmo()).ifPresent(unit -> evt.setCanceled(true));
        });
        MinecraftForge.EVENT_BUS.addListener((final PlayerInteractEvent.EntityInteractSpecific evt) -> {
            InteractionResult result = EntityEventsHandler.onEntityInteract(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getTarget(), new EntityHitResult(evt.getTarget(), evt.getLocalPos().add(evt.getTarget().position())));
            if (result != InteractionResult.PASS) {
                evt.setCancellationResult(result);
                evt.setCanceled(true);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final TickEvent.PlayerTickEvent evt) -> {
            if (evt.phase == TickEvent.Phase.END) PlayerEventsHandler.onPlayerTick$End(evt.player);
        });
        MinecraftForge.EVENT_BUS.addListener((final ItemTossEvent evt) -> {
            PlayerEventsHandler.onItemToss(evt.getEntity(), evt.getPlayer()).ifPresent(unit -> evt.setCanceled(true));
        });
        MinecraftForge.EVENT_BUS.addListener((final EntityJoinLevelEvent evt) -> {
            if (evt.getLevel() instanceof ServerLevel level) EntityEventsHandler.onEntityJoinServerLevel(evt.getEntity(), level);
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingDropsEvent evt) -> {
            EntityEventsHandler.onLivingDrops(evt.getEntity(), evt.getSource(), evt.getDrops(), evt.getLootingLevel(), evt.isRecentlyHit()).ifPresent(unit -> evt.setCanceled(true));
        });
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        evt.put(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeleton.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 5.0).build());
        evt.put(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombie.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 4.0).build());
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        DataGenerator generator = evt.getGenerator();
        final ExistingFileHelper existingFileHelper = evt.getExistingFileHelper();
    }
}
