package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.mixin.accessor.LootContextParamSetsAccessor;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.mutantmonsters.world.level.block.SkullWithItemComponentsBlock;
import fuzs.mutantmonsters.world.level.block.WallSkullWithItemComponentsBlock;
import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemComponentsBlockEntity;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.api.item.v2.ItemEquipmentFactories;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ModRegistry {
    public static final SkullBlock.Type MUTANT_SKELETON_SKULL_TYPE = () -> "mutant_skeleton";

    static final RegistryManager REGISTRIES = RegistryManager.from(MutantMonsters.MOD_ID);
    public static final Holder.Reference<DataComponentType<Unit>> EXPLOSION_RESISTANT_DATA_COMPONENT_TYPE = REGISTRIES.registerDataComponentType(
            "explosion_resistant",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final Holder.Reference<ArmorMaterial> MUTANT_SKELETON_ARMOR_MATERIAL = REGISTRIES.registerArmorMaterial(
            "mutant_skeleton", ItemEquipmentFactories.toArmorTypeMap(2, 5, 6, 2), 9, SoundEvents.ARMOR_EQUIP_GENERIC,
            () -> Ingredient.of(Items.BONE_BLOCK), 0.0F, 0.0F
    );
    public static final Holder.Reference<Block> MUTANT_SKELETON_SKULL_BLOCK = REGISTRIES.registerBlock(
            "mutant_skeleton_skull", () -> new SkullWithItemComponentsBlock(MUTANT_SKELETON_SKULL_TYPE,
                    BlockBehaviour.Properties.of()
                            .instrument(NoteBlockInstrument.SKELETON)
                            .strength(1.0F)
                            .pushReaction(PushReaction.DESTROY)
            ));
    public static final Holder.Reference<Block> MUTANT_SKELETON_WALL_SKULL_BLOCK = REGISTRIES.registerBlock(
            "mutant_skeleton_wall_skull", () -> new WallSkullWithItemComponentsBlock(MUTANT_SKELETON_SKULL_TYPE,
                    BlockBehaviour.Properties.ofFullCopy(MUTANT_SKELETON_SKULL_BLOCK.value())
                            .dropsLike(MUTANT_SKELETON_SKULL_BLOCK.value())
            ));
    public static final Holder.Reference<BlockEntityType<SkullWithItemComponentsBlockEntity>> SKULL_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
            "skull", () -> BlockEntityType.Builder.of(SkullWithItemComponentsBlockEntity::new,
                    MUTANT_SKELETON_SKULL_BLOCK.value(), MUTANT_SKELETON_WALL_SKULL_BLOCK.value()
            ));
    public static final Holder.Reference<MobEffect> CHEMICAL_X_MOB_EFFECT = REGISTRIES.registerMobEffect("chemical_x",
            () -> new ChemicalXMobEffect(MobEffectCategory.HARMFUL, 0x000000)
    );
    public static final Holder.Reference<Potion> CHEMICAL_X_POTION = REGISTRIES.registerPotion("chemical_x",
            () -> new Potion(new MobEffectInstance(CHEMICAL_X_MOB_EFFECT, 1))
    );
    public static final Holder.Reference<SimpleParticleType> ENDERSOUL_PARTICLE_TYPE = REGISTRIES.register(
            Registries.PARTICLE_TYPE, "endersoul", () -> new SimpleParticleType(false));
    public static final Holder.Reference<SimpleParticleType> SKULL_SPIRIT_PARTICLE_TYPE = REGISTRIES.register(
            Registries.PARTICLE_TYPE, "skull_spirit", () -> new SimpleParticleType(true));

    public static final DataAttachmentType<Entity, List<SeismicWave>> SEISMIC_WAVE_ATTACHMENT_TYPE = DataAttachmentRegistry.<List<SeismicWave>>entityBuilder()
            .defaultValue(EntityType.PLAYER, Collections.emptyList())
            .build(MutantMonsters.id("seismic_waves"));

    static final TagFactory TAGS = TagFactory.make(MutantMonsters.MOD_ID);
    public static final TagKey<Block> MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG = TAGS.registerBlockTag(
            "mutant_enderman_holdable_immune");
    public static final TagKey<Block> ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG = TAGS.registerBlockTag(
            "endersoul_hand_holdable_immune");
    public static final TagKey<EntityType<?>> MUTANTS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("mutants");
    public static final TagKey<EntityType<?>> SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag(
            "spider_pig_targets");
    public static final TagKey<Biome> WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_creeper_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_enderman_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_skeleton_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_zombie_spawns");

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
    public static final LootContextParamSet BODY_PART_LOOT_CONTEXT_PARAM_SET = registerLootContextParamSet(
            MutantMonsters.id("body_part"), (builder) -> {
                builder.required(LootContextParams.THIS_ENTITY);
            });

    public static void touch() {
        ModEntityTypes.touch();
        ModItems.touch();
        ModSoundEvents.touch();
    }

    private static LootContextParamSet registerLootContextParamSet(ResourceLocation id, Consumer<LootContextParamSet.Builder> consumer) {
        LootContextParamSet.Builder builder = new LootContextParamSet.Builder();
        consumer.accept(builder);
        LootContextParamSet set = builder.build();
        LootContextParamSet other = LootContextParamSetsAccessor.mutantmonsters$getRegistry().put(id, set);
        if (other != null) {
            throw new IllegalStateException("Loot table parameter set " + id + " is already registered");
        } else {
            return set;
        }
    }
}
