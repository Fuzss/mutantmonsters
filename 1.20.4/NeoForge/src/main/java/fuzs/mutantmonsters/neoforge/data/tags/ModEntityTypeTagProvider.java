package fuzs.mutantmonsters.neoforge.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;

public class ModEntityTypeTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.SKELETONS).add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value());
    }
}
