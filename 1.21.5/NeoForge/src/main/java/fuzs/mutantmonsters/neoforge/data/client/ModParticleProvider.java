package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.neoforge.api.client.data.v2.AbstractParticleProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;

public class ModParticleProvider extends AbstractParticleProvider {

    public ModParticleProvider(NeoForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addParticles() {
        this.add(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(),
                ResourceLocationHelper.withDefaultNamespace("generic"),
                7);
        this.add(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value());
    }
}
