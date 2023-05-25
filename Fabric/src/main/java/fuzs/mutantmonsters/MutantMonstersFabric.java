package fuzs.mutantmonsters;

import fuzs.mutantmonsters.init.ModRegistryFabric;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class MutantMonstersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModRegistryFabric.touch();
        ModConstructor.construct(MutantMonsters.MOD_ID, MutantMonsters::new, ContentRegistrationFlags.BIOME_MODIFICATIONS);
    }
}
