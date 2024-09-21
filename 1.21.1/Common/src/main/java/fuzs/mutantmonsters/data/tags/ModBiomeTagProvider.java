package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

public class ModBiomeTagProvider extends AbstractTagProvider<Biome> {

    public ModBiomeTagProvider(DataProviderContext context) {
        super(Registries.BIOME, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.add(ModRegistry.WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG);
        this.add(ModRegistry.WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG);
        this.add(ModRegistry.WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG);
        this.add(ModRegistry.WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG);
    }
}
