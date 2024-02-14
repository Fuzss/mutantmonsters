package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTagProvider extends AbstractTagProvider.EntityTypes {

    public ModEntityTypeTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.SKELETONS).add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value());
        this.tag(ModRegistry.MUTANTS_ENTITY_TYPE_TAG)
                .add(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.value(),
                        ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                        ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value(),
                        ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                        ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value()
                );
        this.tag(ModRegistry.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG).add(EntityType.SPIDER, EntityType.PIG);
        this.tag("enderzoology:concussion_immune").add(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value(), ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.value());
    }
}
