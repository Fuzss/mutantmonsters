package fuzs.mutantmonsters.data.client;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractParticleProvider;
import net.minecraft.resources.Identifier;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModParticleProvider extends AbstractParticleProvider {

    public ModParticleProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addParticles() {
        this.add(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(),
                Identifier.withDefaultNamespace("generic"),
                7);
        this.add(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value());
    }
}
