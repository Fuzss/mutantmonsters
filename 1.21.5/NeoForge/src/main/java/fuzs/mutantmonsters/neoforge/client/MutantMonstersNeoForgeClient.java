package fuzs.mutantmonsters.neoforge.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.MutantMonstersClient;
import fuzs.mutantmonsters.data.client.ModLanguageProvider;
import fuzs.mutantmonsters.data.client.ModModelProvider;
import fuzs.mutantmonsters.neoforge.data.client.ModEquipmentProvider;
import fuzs.mutantmonsters.neoforge.data.client.ModParticleProvider;
import fuzs.mutantmonsters.neoforge.data.client.ModSoundProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = MutantMonsters.MOD_ID, dist = Dist.CLIENT)
public class MutantMonstersNeoForgeClient {

    public MutantMonstersNeoForgeClient() {
        ClientModConstructor.construct(MutantMonsters.MOD_ID, MutantMonstersClient::new);
        DataProviderHelper.registerDataProviders(MutantMonsters.MOD_ID,
                ModLanguageProvider::new,
                ModParticleProvider::new,
                ModModelProvider::new,
                ModSoundProvider::new,
                ModEquipmentProvider::new);
    }
}
