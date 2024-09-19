package fuzs.mutantmonsters.fabric;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.fabric.init.FabricModRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class MutantMonstersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricModRegistry.touch();
        ModConstructor.construct(MutantMonsters.MOD_ID, MutantMonsters::new);
    }
}
