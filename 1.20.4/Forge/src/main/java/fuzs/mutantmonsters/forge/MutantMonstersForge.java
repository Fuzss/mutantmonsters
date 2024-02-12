package fuzs.mutantmonsters.forge;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.forge.init.ForgeModRegistry;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(MutantMonsters.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MutantMonstersForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ForgeModRegistry.touch();
        ModConstructor.construct(MutantMonsters.MOD_ID, MutantMonsters::new);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        evt.put(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeleton.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 5.0).build());
        evt.put(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombie.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 4.0).build());
    }
}
