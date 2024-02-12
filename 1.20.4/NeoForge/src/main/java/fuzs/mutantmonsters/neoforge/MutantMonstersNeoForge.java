package fuzs.mutantmonsters.neoforge;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.neoforge.data.ModDamageTypeProvider;
import fuzs.mutantmonsters.neoforge.data.ModRecipeProvider;
import fuzs.mutantmonsters.neoforge.data.loot.ModBodyPartLootProvider;
import fuzs.mutantmonsters.neoforge.data.loot.ModEntityLootProvider;
import fuzs.mutantmonsters.neoforge.data.loot.ModEntityTypeLootProvider;
import fuzs.mutantmonsters.neoforge.data.tags.*;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.neoforge.init.NeoForgeModRegistry;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@Mod(MutantMonsters.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MutantMonstersNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        NeoForgeModRegistry.touch();
        ModConstructor.construct(MutantMonsters.MOD_ID, MutantMonsters::new);
        DataProviderHelper.registerDataProviders(MutantMonsters.MOD_ID, ModBodyPartLootProvider::new,
                ModEntityLootProvider::new, ModEntityTypeLootProvider::new, ModBiomeTagProvider::new, ModBlockTagProvider::new,
                ModDamageTypeTagProvider::new, ModEntityTypeTagProvider::new, ModItemTagProvider::new,
                ModDamageTypeProvider::new, ModRecipeProvider::new
        );
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        evt.put(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value(),
                MutantSkeleton.registerAttributes().add(NeoForgeMod.SWIM_SPEED.value(), 5.0).build()
        );
        evt.put(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value(),
                MutantZombie.registerAttributes().add(NeoForgeMod.SWIM_SPEED.value(), 4.0).build()
        );
    }
}
