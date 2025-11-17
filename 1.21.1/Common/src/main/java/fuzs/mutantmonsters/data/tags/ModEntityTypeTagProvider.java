package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModRegistry;
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
        this.add(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value());
        this.add(EntityTypeTags.IMPACT_PROJECTILES).add(ModEntityTypes.THROWABLE_BLOCK_ENTITY_TYPE.value());
        this.add(EntityTypeTags.ZOMBIES).add(ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value());
        this.add(EntityTypeTags.SKELETONS).add(ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value());
        this.add(ModRegistry.MUTANTS_ENTITY_TYPE_TAG)
                .add(ModEntityTypes.MUTANT_CREEPER_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_SKELETON_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(),
                        ModEntityTypes.MUTANT_ZOMBIE_ENTITY_TYPE.value());
        this.add(ModRegistry.SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG).add(EntityType.SPIDER, EntityType.PIG);
        this.add("enderzoology:concussion_immune")
                .add(ModEntityTypes.MUTANT_ENDERMAN_ENTITY_TYPE.value(),
                        ModEntityTypes.ENDERSOUL_CLONE_ENTITY_TYPE.value());
        this.add(EntityTypeTags.ARTHROPOD).add(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value());
        this.add(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(ModEntityTypes.SPIDER_PIG_ENTITY_TYPE.value())
                .addTag(ModRegistry.MUTANTS_ENTITY_TYPE_TAG);
    }
}
