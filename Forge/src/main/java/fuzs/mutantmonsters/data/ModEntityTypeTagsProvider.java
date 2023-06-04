package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModEntityTypeTagsProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagsProvider(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.get());
        this.tag(EntityTypeTags.SKELETONS).add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get());
    }
}
