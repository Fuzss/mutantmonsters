package fuzs.mutantmonsters.neoforge.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;

public class ModItemTagProvider extends AbstractTagProvider.Items {

    public ModItemTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        // Fabric Api doesn't have those :(
        this.tag("forge:armors/boots").add(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.value());
        this.tag("forge:armors/chestplates").add(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.value());
        this.tag("forge:armors/helmets").add(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.value());
        this.tag("forge:armors/leggings").add(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.value());
    }
}
