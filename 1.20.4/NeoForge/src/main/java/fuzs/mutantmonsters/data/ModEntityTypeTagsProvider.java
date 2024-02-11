package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.get());
        this.tag(EntityTypeTags.SKELETONS).add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get());
    }
}
