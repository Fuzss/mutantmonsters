package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.entity.MutantSkeletonBodyPart;
import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.mutantmonsters.world.level.block.SkullWithItemComponentsBlock;
import fuzs.mutantmonsters.world.level.block.WallSkullWithItemComponentsBlock;
import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemComponentsBlockEntity;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.init.v3.registry.ContentRegistrationHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ModRegistry {
    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder().add(Registries.DAMAGE_TYPE,
            ModRegistry::bootstrapDamageTypes);
    public static final SkullBlock.Type MUTANT_SKELETON_SKULL_TYPE = MutantMonsters.id("mutant_skeleton")::toString;
    static final RegistryManager REGISTRIES = RegistryManager.from(MutantMonsters.MOD_ID);
    public static final Holder.Reference<Block> MUTANT_SKELETON_SKULL_BLOCK = REGISTRIES.registerBlock(
            "mutant_skeleton_skull",
            (BlockBehaviour.Properties properties) -> new SkullWithItemComponentsBlock(MUTANT_SKELETON_SKULL_TYPE,
                    properties),
            () -> BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.SKELETON)
                    .strength(1.0F)
                    .pushReaction(PushReaction.DESTROY));
    public static final Holder.Reference<Block> MUTANT_SKELETON_WALL_SKULL_BLOCK = REGISTRIES.registerBlock(
            "mutant_skeleton_wall_skull",
            (BlockBehaviour.Properties properties) -> new WallSkullWithItemComponentsBlock(MUTANT_SKELETON_SKULL_TYPE,
                    properties),
            () -> BlockBehaviour.Properties.ofFullCopy(MUTANT_SKELETON_SKULL_BLOCK.value())
                    .overrideLootTable(MUTANT_SKELETON_SKULL_BLOCK.value().getLootTable())
                    .overrideDescription(MUTANT_SKELETON_SKULL_BLOCK.value().getDescriptionId()));
    public static final Holder.Reference<BlockEntityType<SkullWithItemComponentsBlockEntity>> SKULL_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
            "skull",
            SkullWithItemComponentsBlockEntity::new,
            () -> Set.of(MUTANT_SKELETON_SKULL_BLOCK.value(), MUTANT_SKELETON_WALL_SKULL_BLOCK.value()));
    public static final Holder.Reference<MobEffect> CHEMICAL_X_MOB_EFFECT = REGISTRIES.registerMobEffect("chemical_x",
            () -> new ChemicalXMobEffect(MobEffectCategory.HARMFUL, 0x000000));
    public static final Holder.Reference<CreativeModeTab> CREATIVE_MODE_TAB = REGISTRIES.registerCreativeModeTab(
            ModItems.ENDERSOUL_HAND_ITEM);
    public static final Holder.Reference<SimpleParticleType> ENDERSOUL_PARTICLE_TYPE = REGISTRIES.register(Registries.PARTICLE_TYPE,
            "endersoul",
            () -> new SimpleParticleType(false));
    public static final Holder.Reference<SimpleParticleType> SKULL_SPIRIT_PARTICLE_TYPE = REGISTRIES.register(Registries.PARTICLE_TYPE,
            "skull_spirit",
            () -> new SimpleParticleType(true));
    public static final Holder.Reference<EntityDataSerializer<MutantSkeletonBodyPart.BodyPart>> BODY_PART_ENTITY_DATA_SERIALIZER = REGISTRIES.registerEntityDataSerializer(
            "body_part",
            () -> EntityDataSerializer.forValueType(MutantSkeletonBodyPart.BodyPart.STREAM_CODEC));

    public static final DataAttachmentType<Entity, List<SeismicWave>> SEISMIC_WAVE_ATTACHMENT_TYPE = DataAttachmentRegistry.<List<SeismicWave>>entityBuilder()
            .defaultValue(EntityType.PLAYER, Collections.emptyList())
            .build(MutantMonsters.id("seismic_waves"));

    public static final ResourceKey<DamageType> PLAYER_SEISMIC_WAVE_DAMAGE_TYPE = REGISTRIES.registerDamageType(
            "player_seismic_wave");
    public static final ResourceKey<DamageType> MUTANT_SKELETON_SHATTER_DAMAGE_TYPE = REGISTRIES.registerDamageType(
            "mutant_skeleton_shatter");
    public static final ResourceKey<DamageType> MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE = REGISTRIES.registerDamageType(
            "mutant_zombie_seismic_wave");
    public static final ResourceKey<DamageType> PIERCING_MOB_ATTACK_DAMAGE_TYPE = REGISTRIES.registerDamageType(
            "piercing_mob_attack");
    public static final ResourceKey<DamageType> ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE = REGISTRIES.registerDamageType(
            "endersoul_fragment_explosion");

    public static final ResourceKey<LootTable> MUTANT_SKELETON_PELVIS_LOOT_TABLE = REGISTRIES.registerLootTable(
            "entities/mutant_skeleton/pelvis");
    public static final ResourceKey<LootTable> MUTANT_SKELETON_RIB_LOOT_TABLE = REGISTRIES.registerLootTable(
            "entities/mutant_skeleton/rib");
    public static final ResourceKey<LootTable> MUTANT_SKELETON_SKULL_LOOT_TABLE = REGISTRIES.registerLootTable(
            "entities/mutant_skeleton/skull");
    public static final ResourceKey<LootTable> MUTANT_SKELETON_LIMB_LOOT_TABLE = REGISTRIES.registerLootTable(
            "entities/mutant_skeleton/limb");
    public static final ResourceKey<LootTable> MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE = REGISTRIES.registerLootTable(
            "entities/mutant_skeleton/shoulder_pad");
    public static final ResourceKey<LootTable> MUTANT_ENDERMAN_CONTINUOUS_LOOT_TABLE = REGISTRIES.registerLootTable(
            "entities/mutant_enderman_continuous");
    public static final ResourceKey<LootTable> CHARGED_MUTANT_CREEPER_LOOT_TABLE = REGISTRIES.registerLootTable(
            "charged_mutant_creeper/root");
    public static final ResourceKey<LootTable> CHARGED_CREEPER_MINION_LOOT_TABLE = REGISTRIES.registerLootTable(
            "charged_creeper_minion/root");
    public static final ContextKeySet BODY_PART_LOOT_CONTEXT_PARAM_SET = ContentRegistrationHelper.registerContextKeySet(
            MutantMonsters.id("body_part"),
            (ContextKeySet.Builder builder) -> {
                builder.required(LootContextParams.THIS_ENTITY);
            });

    public static final DataAttachmentType<Entity, Optional<Boolean>> LEFT_SHOULDER_CREEPER_MINION_ATTACHMENT_TYPE = DataAttachmentRegistry.<Optional<Boolean>>entityBuilder()
            .defaultValue(EntityType.PLAYER, Optional.empty())
            .networkSynchronized(ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional),
                    // Do not sync to players to bypass this bug in Fabric: https://github.com/FabricMC/fabric-api/issues/4943
                    ModLoaderEnvironment.INSTANCE.getModLoader().isFabric() ?
                            (Entity entity) -> Function.identity()::apply : PlayerSet::nearEntity)
            .build(MutantMonsters.id("left_shoulder_creeper_minion"));
    public static final DataAttachmentType<Entity, Optional<Boolean>> RIGHT_SHOULDER_CREEPER_MINION_ATTACHMENT_TYPE = DataAttachmentRegistry.<Optional<Boolean>>entityBuilder()
            .defaultValue(EntityType.PLAYER, Optional.empty())
            .networkSynchronized(ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional),
                    // Do not sync to players to bypass this bug in Fabric: https://github.com/FabricMC/fabric-api/issues/4943
                    ModLoaderEnvironment.INSTANCE.getModLoader().isFabric() ?
                            (Entity entity) -> Function.identity()::apply : PlayerSet::nearEntity)
            .build(MutantMonsters.id("right_shoulder_creeper_minion"));

    public static void bootstrap() {
        ModEntityTypes.bootstrap();
        ModItems.bootstrap();
        ModSoundEvents.bootstrap();
        ModTags.bootstrap();
    }

    public static void bootstrapDamageTypes(BootstrapContext<DamageType> context) {
        context.register(PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("player", 0.1F));
        context.register(MUTANT_SKELETON_SHATTER_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(PIERCING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE, new DamageType("thrown", 0.1F));
    }
}
