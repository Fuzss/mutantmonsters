package fuzs.mutantmonsters.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.block.MBSkullBlock;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.client.core.ClientFactories;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterEntitySpectatorShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = MutantMonsters.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MutantMonstersForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientFactories.INSTANCE.clientModConstructor(MutantMonsters.MOD_ID).accept(new MutantMonstersClient());
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        SkullBlockRenderer.SKIN_BY_TYPE.put(MBSkullBlock.Types.MUTANT_SKELETON, MutantMonsters.prefix("textures/entity/mutant_skeleton.png"));
    }

    @SubscribeEvent
    public static void onCreateSkullModels(final EntityRenderersEvent.CreateSkullModels evt) {
        evt.registerSkullModel(MBSkullBlock.Types.MUTANT_SKELETON, new SkullModel(evt.getEntityModelSet().bakeLayer(ClientModRegistry.MUTANT_SKELETON_SKULL)));
    }

    @SubscribeEvent
    public static void onRegisterEntitySpectatorShaders(final RegisterEntitySpectatorShadersEvent evt) {
        evt.register(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/creeper.json"));
        evt.register(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/invert.json"));
        evt.register(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/creeper.json"));
        evt.register(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.get(), new ResourceLocation("shaders/post/invert.json"));
    }
}
