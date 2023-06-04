package fuzs.mutantmonsters;

import fuzs.mutantmonsters.capability.SeismicWavesCapability;
import fuzs.mutantmonsters.data.ModEntityTypeTagsProvider;
import fuzs.mutantmonsters.data.ModItemTagsProvider;
import fuzs.mutantmonsters.data.ModLootTableProvider;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModRegistryForge;
import fuzs.mutantmonsters.world.entity.mutant.MutantSkeleton;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(MutantMonsters.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MutantMonstersForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(MutantMonsters.MOD_ID, MutantMonsters::new, ContentRegistrationFlags.BIOME_MODIFICATIONS);
        ModRegistryForge.touch();
        registerCapabilities();
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.SEISMIC_WAVES_CAPABILITY, new CapabilityToken<SeismicWavesCapability>() {});
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(final EntityAttributeCreationEvent evt) {
        evt.put(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get(), MutantSkeleton.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 5.0).build());
        evt.put(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.get(), MutantZombie.registerAttributes().add(ForgeMod.SWIM_SPEED.get(), 4.0).build());
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        final DataGenerator dataGenerator = evt.getGenerator();
        final ExistingFileHelper fileHelper = evt.getExistingFileHelper();
        dataGenerator.addProvider(new ModEntityTypeTagsProvider(dataGenerator, MutantMonsters.MOD_ID, fileHelper));
        dataGenerator.addProvider(new ModItemTagsProvider(dataGenerator, MutantMonsters.MOD_ID, fileHelper));
        dataGenerator.addProvider(new ModLootTableProvider(dataGenerator));
    }
}
