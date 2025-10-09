package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.world.entity.*;
import fuzs.mutantmonsters.world.entity.mutant.*;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {
    public static final Holder.Reference<EntityType<CreeperMinion>> CREEPER_MINION_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "creeper_minion",
            () -> EntityType.Builder.of(CreeperMinion::new, MobCategory.MISC).sized(0.3F, 0.85F));
    public static final Holder.Reference<EntityType<MutantCreeper>> MUTANT_CREEPER_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "mutant_creeper",
            () -> EntityType.Builder.of(MutantCreeper::new, MobCategory.MONSTER).sized(1.99F, 2.8F).eyeHeight(2.6F));
    public static final Holder.Reference<EntityType<MutantEnderman>> MUTANT_ENDERMAN_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "mutant_enderman",
            () -> EntityType.Builder.of(MutantEnderman::new, MobCategory.MONSTER).sized(1.2F, 4.2F).eyeHeight(3.9F));
    public static final Holder.Reference<EntityType<MutantSkeleton>> MUTANT_SKELETON_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "mutant_skeleton",
            () -> EntityType.Builder.of(MutantSkeleton::new, MobCategory.MONSTER).sized(1.2F, 3.6F).eyeHeight(3.25F));
    public static final Holder.Reference<EntityType<MutantSnowGolem>> MUTANT_SNOW_GOLEM_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "mutant_snow_golem",
            () -> EntityType.Builder.of(MutantSnowGolem::new, MobCategory.MISC).sized(1.15F, 2.3F).eyeHeight(2.0F));
    public static final Holder.Reference<EntityType<MutantZombie>> MUTANT_ZOMBIE_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "mutant_zombie",
            () -> EntityType.Builder.of(MutantZombie::new, MobCategory.MONSTER).sized(1.8F, 3.2F).eyeHeight(2.8F));
    public static final Holder.Reference<EntityType<SpiderPig>> SPIDER_PIG_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "spider_pig",
            () -> EntityType.Builder.of(SpiderPig::new, MobCategory.CREATURE).sized(1.4F, 0.9F).eyeHeight(0.675F));
    public static final Holder.Reference<EntityType<MutantSkeletonBodyPart>> BODY_PART_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "body_part",
            () -> EntityType.Builder.<MutantSkeletonBodyPart>of(MutantSkeletonBodyPart::new, MobCategory.MISC)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .sized(0.7F, 0.7F));
    public static final Holder.Reference<EntityType<CreeperMinionEgg>> CREEPER_MINION_EGG_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "creeper_minion_egg",
            () -> EntityType.Builder.<CreeperMinionEgg>of(CreeperMinionEgg::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .sized(0.5625F, 0.75F)
                    .ridingOffset(0.2F)
                    .passengerAttachments(0.55F));
    public static final Holder.Reference<EntityType<EndersoulClone>> ENDERSOUL_CLONE_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "endersoul_clone",
            () -> EntityType.Builder.of(EndersoulClone::new, MobCategory.MONSTER).sized(0.6F, 2.9F).eyeHeight(2.55F));
    public static final Holder.Reference<EntityType<EndersoulFragment>> ENDERSOUL_FRAGMENT_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "endersoul_fragment",
            () -> EntityType.Builder.<EndersoulFragment>of(EndersoulFragment::new, MobCategory.MISC)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .sized(0.75F, 0.75F));
    public static final Holder.Reference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = ModRegistry.REGISTRIES.registerLazily(
            Registries.ENTITY_TYPE,
            "mutant_arrow");
    public static final Holder.Reference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = ModRegistry.REGISTRIES.registerLazily(
            Registries.ENTITY_TYPE,
            "skull_spirit");
    public static final Holder.Reference<EntityType<ThrowableBlock>> THROWABLE_BLOCK_ENTITY_TYPE = ModRegistry.REGISTRIES.registerEntityType(
            "throwable_block",
            () -> EntityType.Builder.<ThrowableBlock>of(ThrowableBlock::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(6)
                    .updateInterval(Integer.MAX_VALUE));

    public static void bootstrap() {
        // NO-OP
    }
}
