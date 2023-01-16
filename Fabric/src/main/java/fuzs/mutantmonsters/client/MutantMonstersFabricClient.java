package fuzs.mutantmonsters.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.core.ContentRegistrationFlags;
import net.fabricmc.api.ClientModInitializer;

public class MutantMonstersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientFactories.INSTANCE.clientModConstructor(MutantMonsters.MOD_ID, ContentRegistrationFlags.BUILT_IN_ITEM_MODEL_RENDERERS).accept(new MutantMonstersClient());
    }
}
