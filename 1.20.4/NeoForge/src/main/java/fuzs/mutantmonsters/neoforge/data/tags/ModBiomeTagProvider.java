package fuzs.mutantmonsters.neoforge.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeTagProvider extends AbstractTagProvider.Simple<Biome> {

    public ModBiomeTagProvider(DataProviderContext context) {
        super(Registries.BIOME, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG);
        this.tag(ModRegistry.WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG);
        this.tag(ModRegistry.WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG);
        this.tag(ModRegistry.WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG);
    }
}
