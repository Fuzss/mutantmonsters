package fuzs.mutantmonsters.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import net.fabricmc.api.ClientModInitializer;

public class MutantMonstersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(MutantMonsters.MOD_ID, MutantMonstersClient::new, ContentRegistrationFlags.BUILT_IN_ITEM_MODEL_RENDERERS);
    }
}
