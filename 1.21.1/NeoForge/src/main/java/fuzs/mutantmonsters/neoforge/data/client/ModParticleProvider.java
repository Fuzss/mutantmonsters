package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractParticleDescriptionProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.resources.ResourceLocation;

public class ModParticleProvider extends AbstractParticleDescriptionProvider {

    public ModParticleProvider(ForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addParticleDescriptions() {
        this.add(ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), new ResourceLocation("generic"), 7);
        this.add(ModRegistry.SKULL_SPIRIT_PARTICLE_TYPE.value());
    }
}
