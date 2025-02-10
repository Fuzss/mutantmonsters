package fuzs.mutantmonsters.fabric.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class MutantMonstersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(MutantMonsters.MOD_ID, MutantMonstersClient::new);
    }
}
