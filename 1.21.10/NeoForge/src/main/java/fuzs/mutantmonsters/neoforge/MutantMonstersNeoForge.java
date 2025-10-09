package fuzs.mutantmonsters.neoforge;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.data.ModAdvancementProvider;
import fuzs.mutantmonsters.data.ModRecipeProvider;
import fuzs.mutantmonsters.data.loot.ModBlockLootProvider;
import fuzs.mutantmonsters.data.loot.ModBodyPartLootProvider;
import fuzs.mutantmonsters.data.loot.ModEntityLootProvider;
import fuzs.mutantmonsters.data.loot.ModEntityTypeLootProvider;
import fuzs.mutantmonsters.data.tags.*;
import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.neoforge.init.NeoForgeModRegistry;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(MutantMonsters.MOD_ID)
public class MutantMonstersNeoForge {

    public MutantMonstersNeoForge(ModContainer modContainer) {
        NeoForgeModRegistry.bootstrap();
        ModConstructor.construct(MutantMonsters.MOD_ID, MutantMonsters::new);
        registerLoadingHandlers(modContainer.getEventBus());
        DataProviderHelper.registerDataProviders(MutantMonsters.MOD_ID,
                ModRegistry.REGISTRY_SET_BUILDER,
                ModBlockLootProvider::new,
                ModBodyPartLootProvider::new,
                ModEntityLootProvider::new,
                ModEntityTypeLootProvider::new,
                ModBiomeTagProvider::new,
                ModBlockTagProvider::new,
                ModDamageTypeTagProvider::new,
                ModEntityTypeTagProvider::new,
                ModItemTagProvider::new,
                ModRecipeProvider::new,
                ModAdvancementProvider::new);
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        eventBus.addListener((final EntityAttributeCreationEvent evt) -> {
            evt.put(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(),
                    MutantSkeleton.createAttributes().add(NeoForgeMod.SWIM_SPEED, 5.0).build());
            evt.put(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value(),
                    MutantZombie.createAttributes().add(NeoForgeMod.SWIM_SPEED, 4.0).build());
        });
    }
}
