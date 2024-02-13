package fuzs.mutantmonsters.neoforge;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.data.client.ModModelProvider;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.neoforge.data.ModDamageTypeProvider;
import fuzs.mutantmonsters.data.ModRecipeProvider;
import fuzs.mutantmonsters.data.client.ModLanguageProvider;
import fuzs.mutantmonsters.neoforge.data.client.ModParticleProvider;
import fuzs.mutantmonsters.data.loot.ModBlockLootProvider;
import fuzs.mutantmonsters.data.loot.ModBodyPartLootProvider;
import fuzs.mutantmonsters.data.loot.ModEntityLootProvider;
import fuzs.mutantmonsters.data.loot.ModEntityTypeLootProvider;
import fuzs.mutantmonsters.data.tags.*;
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
        DataProviderHelper.registerDataProviders(MutantMonsters.MOD_ID,
                ModBlockLootProvider::new,
                ModBodyPartLootProvider::new,
                ModEntityLootProvider::new,
                ModEntityTypeLootProvider::new,
                ModBiomeTagProvider::new,
                ModBlockTagProvider::new,
                ModDamageTypeTagProvider::new,
                ModEntityTypeTagProvider::new,
                ModItemTagProvider::new,
                ModDamageTypeProvider::new,
                ModRecipeProvider::new,
                ModLanguageProvider::new,
                ModParticleProvider::new,
                ModModelProvider::new
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
