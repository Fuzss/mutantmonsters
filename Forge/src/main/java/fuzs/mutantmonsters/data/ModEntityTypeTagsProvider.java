package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public ModEntityTypeTagsProvider(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.get());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.get());
        this.tag(EntityTypeTags.SKELETONS).add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.get());
    }
}
