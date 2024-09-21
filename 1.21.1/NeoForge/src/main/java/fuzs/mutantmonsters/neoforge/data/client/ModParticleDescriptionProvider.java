package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractParticleDescriptionProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;

public class ModParticleDescriptionProvider extends AbstractParticleDescriptionProvider {

    public ModParticleDescriptionProvider(NeoForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addParticleDescriptions() {
        this.add(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), ResourceLocationHelper.withDefaultNamespace("generic"),
                7
        );
        this.add(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value());
    }
}
