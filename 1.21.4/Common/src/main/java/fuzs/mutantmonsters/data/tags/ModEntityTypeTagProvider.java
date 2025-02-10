package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeTagProvider extends AbstractTagProvider<EntityType<?>> {

    public ModEntityTypeTagProvider(DataProviderContext context) {
        super(Registries.ENTITY_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.IMPACT_PROJECTILES).add(ModEntityTypes.THROWABLE_BLOCK_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.SKELETONS).add(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value());
        this.tag(ModTags.MUTANTS_ENTITY_TYPE_TAG)
                .add(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value());
        this.tag(ModTags.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG).add(EntityType.SPIDER, EntityType.PIG);
        this.tag("enderzoology:concussion_immune")
                .add(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                        ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.UNDEAD)
                .add(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.ARTHROPOD).add(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value());
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value())
                .addTag(ModTags.MUTANTS_ENTITY_TYPE_TAG);
    }
}
