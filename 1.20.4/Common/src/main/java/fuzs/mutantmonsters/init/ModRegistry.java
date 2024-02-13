package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.capability.SeismicWavesCapability;
import fuzs.mutantmonsters.mixin.accessor.LootContextParamSetsAccessor;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.entity.*;
import fuzs.mutantmonsters.world.entity.mutant.*;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.entity.projectile.ThrowableBlock;
import fuzs.mutantmonsters.world.item.*;
import fuzs.mutantmonsters.world.level.block.SkullWithItemTagBlock;
import fuzs.mutantmonsters.world.level.block.WallSkullWithItemTagBlock;
import fuzs.mutantmonsters.world.level.block.entity.SkullWithItemTagBlockEntity;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import fuzs.puzzleslib.api.item.v2.ItemEquipmentFactories;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Consumer;

public class ModRegistry {
    public static final SkullBlock.Type MUTANT_SKELETON_SKULL_TYPE = () -> "mutant_skeleton";
    public static final ArmorMaterial MUTANT_SKELETON_ARMOR_MATERIAL = ItemEquipmentFactories.registerArmorMaterial(MutantMonsters.id(
            "mutant_skeleton"), 15, new int[]{2, 5, 6, 2}, 9, () -> Ingredient.of(Items.BONE_BLOCK));

    static final RegistryManager REGISTRY = RegistryManager.from(MutantMonsters.MOD_ID);
    public static final Holder.Reference<Block> MUTANT_SKELETON_SKULL_BLOCK = REGISTRY.registerBlock("mutant_skeleton_skull", () -> new SkullWithItemTagBlock(MUTANT_SKELETON_SKULL_TYPE, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final Holder.Reference<Block> MUTANT_SKELETON_WALL_SKULL_BLOCK = REGISTRY.registerBlock("mutant_skeleton_wall_skull", () -> new WallSkullWithItemTagBlock(MUTANT_SKELETON_SKULL_TYPE, BlockBehaviour.Properties.ofFullCopy(MUTANT_SKELETON_SKULL_BLOCK.value()).dropsLike(MUTANT_SKELETON_SKULL_BLOCK.value())));
    public static final Holder.Reference<EntityType<CreeperMinion>> CREEPER_MINION_ENTITY_TYPE = REGISTRY.registerEntityType("creeper_minion", () -> EntityType.Builder.of(CreeperMinion::new, MobCategory.MISC).sized(0.3F, 0.85F));
    public static final Holder.Reference<EntityType<MutantCreeper>> MUTANT_CREEPER_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_creeper", () -> EntityType.Builder.of(MutantCreeper::new, MobCategory.MONSTER).sized(1.99F, 2.8F));
    public static final Holder.Reference<EntityType<MutantEnderman>> MUTANT_ENDERMAN_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_enderman", () -> EntityType.Builder.<MutantEnderman>of(MutantEnderman::new, MobCategory.MONSTER).sized(1.2F, 4.2F));
    public static final Holder.Reference<EntityType<MutantSkeleton>> MUTANT_SKELETON_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_skeleton", () -> EntityType.Builder.<MutantSkeleton>of(MutantSkeleton::new, MobCategory.MONSTER).sized(1.2F, 3.6F));
    public static final Holder.Reference<EntityType<MutantSnowGolem>> MUTANT_SNOW_GOLEM_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_snow_golem", () -> EntityType.Builder.of(MutantSnowGolem::new, MobCategory.MISC).sized(1.15F, 2.3F));
    public static final Holder.Reference<EntityType<MutantZombie>> MUTANT_ZOMBIE_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_zombie", () -> EntityType.Builder.<MutantZombie>of(MutantZombie::new, MobCategory.MONSTER).sized(1.8F, 3.2F));
    public static final Holder.Reference<EntityType<SpiderPig>> SPIDER_PIG_ENTITY_TYPE = REGISTRY.registerEntityType("spider_pig", () -> EntityType.Builder.of(SpiderPig::new, MobCategory.CREATURE).sized(1.4F, 0.9F));
    public static final Holder.Reference<EntityType<MutantSkeletonBodyPart>> BODY_PART_ENTITY_TYPE = REGISTRY.registerEntityType("body_part", () -> EntityType.Builder.<MutantSkeletonBodyPart>of(MutantSkeletonBodyPart::new, MobCategory.MISC).clientTrackingRange(4).updateInterval(10).sized(0.7F, 0.7F));
    public static final Holder.Reference<EntityType<CreeperMinionEgg>> CREEPER_MINION_EGG_ENTITY_TYPE = REGISTRY.registerEntityType("creeper_minion_egg", () -> EntityType.Builder.<CreeperMinionEgg>of(CreeperMinionEgg::new, MobCategory.MISC).clientTrackingRange(10).updateInterval(20).sized(0.5625F, 0.75F));
    public static final Holder.Reference<EntityType<EndersoulClone>> ENDERSOUL_CLONE_ENTITY_TYPE = REGISTRY.registerEntityType("endersoul_clone", () -> EntityType.Builder.of(EndersoulClone::new, MobCategory.MONSTER).sized(0.6F, 2.9F));
    public static final Holder.Reference<EntityType<EndersoulFragment>> ENDERSOUL_FRAGMENT_ENTITY_TYPE = REGISTRY.registerEntityType("endersoul_fragment", () -> EntityType.Builder.<EndersoulFragment>of(EndersoulFragment::new, MobCategory.MISC).clientTrackingRange(4).updateInterval(10).sized(0.75F, 0.75F));
    public static final Holder.Reference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.registerLazily(Registries.ENTITY_TYPE, "mutant_arrow");
    public static final Holder.Reference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.registerLazily(Registries.ENTITY_TYPE, "skull_spirit");
    public static final Holder.Reference<EntityType<ThrowableBlock>> THROWABLE_BLOCK_ENTITY_TYPE = REGISTRY.registerEntityType("throwable_block", () -> EntityType.Builder.<ThrowableBlock>of(ThrowableBlock::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(6).updateInterval(Integer.MAX_VALUE));
    public static final Holder.Reference<Item> CREEPER_MINION_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(CREEPER_MINION_ENTITY_TYPE, 894731, 12040119);
    public static final Holder.Reference<Item> MUTANT_CREEPER_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(MUTANT_CREEPER_ENTITY_TYPE, 5349438, 11013646);
    public static final Holder.Reference<Item> MUTANT_ENDERMAN_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(MUTANT_ENDERMAN_ENTITY_TYPE, 1447446, 8860812);
    public static final Holder.Reference<Item> MUTANT_SKELETON_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(MUTANT_SKELETON_ENTITY_TYPE, 12698049, 6310217);
    public static final Holder.Reference<Item> MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(MUTANT_SNOW_GOLEM_ENTITY_TYPE, 15073279, 16753434);
    public static final Holder.Reference<Item> MUTANT_ZOMBIE_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(MUTANT_ZOMBIE_ENTITY_TYPE, 7969893, 44975);
    public static final Holder.Reference<Item> SPIDER_PIG_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem(SPIDER_PIG_ENTITY_TYPE, 3419431, 15771042);
    public static final Holder.Reference<Item> CREEPER_MINION_TRACKER_ITEM = REGISTRY.registerItem("creeper_minion_tracker", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final Holder.Reference<Item> CREEPER_SHARD_ITEM = REGISTRY.registerItem("creeper_shard", () -> new CreeperShardItem(new Item.Properties().durability(32).rarity(Rarity.UNCOMMON)));
    public static final Holder.Reference<Item> ENDERSOUL_HAND_ITEM = REGISTRY.whenOnFabricLike().registerItem("endersoul_hand", () -> new EndersoulHandItem(new Item.Properties().durability(240).rarity(Rarity.EPIC)));
    public static final Holder.Reference<Item> HULK_HAMMER_ITEM = REGISTRY.whenOnFabricLike().registerItem("hulk_hammer", () -> new HulkHammerItem(new Item.Properties().durability(64).rarity(Rarity.UNCOMMON)));
    public static final Holder.Reference<Item> MUTANT_SKELETON_ARMS_ITEM = REGISTRY.registerItem("mutant_skeleton_arms", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_LIMB_ITEM = REGISTRY.registerItem("mutant_skeleton_limb", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_PELVIS_ITEM = REGISTRY.registerItem("mutant_skeleton_pelvis", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_RIB_ITEM = REGISTRY.registerItem("mutant_skeleton_rib", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_RIB_CAGE_ITEM = REGISTRY.registerItem("mutant_skeleton_rib_cage", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_SHOULDER_PAD_ITEM = REGISTRY.registerItem("mutant_skeleton_shoulder_pad", () -> new Item(new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_SKULL_ITEM = REGISTRY.whenOnFabricLike().registerItem("mutant_skeleton_skull", () -> new ArmorBlockItem(
            MUTANT_SKELETON_ARMOR_MATERIAL, ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value(), ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.value(), new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final Holder.Reference<Item> MUTANT_SKELETON_CHESTPLATE_ITEM = REGISTRY.registerItem("mutant_skeleton_chestplate", () -> new SkeletonArmorItem(
            MUTANT_SKELETON_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_LEGGINGS_ITEM = REGISTRY.registerItem("mutant_skeleton_leggings", () -> new SkeletonArmorItem(
            MUTANT_SKELETON_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final Holder.Reference<Item> MUTANT_SKELETON_BOOTS_ITEM = REGISTRY.registerItem("mutant_skeleton_boots", () -> new SkeletonArmorItem(
            MUTANT_SKELETON_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));
    public static final Holder.Reference<BlockEntityType<SkullWithItemTagBlockEntity>> SKULL_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityType("skull", () -> BlockEntityType.Builder.of(SkullWithItemTagBlockEntity::new, MUTANT_SKELETON_SKULL_BLOCK.value(), MUTANT_SKELETON_WALL_SKULL_BLOCK.value()));
    public static final Holder.Reference<MobEffect> CHEMICAL_X_MOB_EFFECT = REGISTRY.registerMobEffect("chemical_x", () -> new ChemicalXMobEffect(MobEffectCategory.HARMFUL, 0x000000));
    public static final Holder.Reference<Potion> CHEMICAL_X_POTION = REGISTRY.registerPotion("chemical_x", () -> new Potion(new MobEffectInstance(CHEMICAL_X_MOB_EFFECT.value(), 1)));
    public static final Holder.Reference<SimpleParticleType> ENDERSOUL_PARTICLE_TYPE = REGISTRY.register(Registries.PARTICLE_TYPE, "endersoul", () -> new SimpleParticleType(false));
    public static final Holder.Reference<SimpleParticleType> SKULL_SPIRIT_PARTICLE_TYPE = REGISTRY.register(Registries.PARTICLE_TYPE, "skull_spirit", () -> new SimpleParticleType(true));
    public static final Holder.Reference<SoundEvent> ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.ambient");
    public static final Holder.Reference<SoundEvent> ENTITY_CREEPER_MINION_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.death");
    public static final Holder.Reference<SoundEvent> ENTITY_CREEPER_MINION_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.hurt");
    public static final Holder.Reference<SoundEvent> ENTITY_CREEPER_MINION_PRIMED_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.primed");
    public static final Holder.Reference<SoundEvent> ENTITY_CREEPER_MINION_EGG_HATCH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion_egg.hatch");
    public static final Holder.Reference<SoundEvent> ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.endersoul_clone.teleport");
    public static final Holder.Reference<SoundEvent> ENTITY_ENDERSOUL_CLONE_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.endersoul_clone.death");
    public static final Holder.Reference<SoundEvent> ENTITY_ENDERSOUL_FRAGMENT_EXPLODE_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.endersoul_fragment.explode");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_CREEPER_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.ambient");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_CREEPER_CHARGE_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.charge");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_CREEPER_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.death");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_CREEPER_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.hurt");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_CREEPER_PRIMED_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.primed");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.ambient");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.death");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.hurt");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_MORPH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.morph");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_SCREAM_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.scream");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_STARE_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.stare");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ENDERMAN_TELEPORT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.teleport");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_SKELETON_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.ambient");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_SKELETON_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.death");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_SKELETON_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.hurt");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_SKELETON_STEP_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.step");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_SNOW_GOLEM_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_snow_golem.death");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_SNOW_GOLEM_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_snow_golem.hurt");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ZOMBIE_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.ambient");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.attack");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ZOMBIE_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.death");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ZOMBIE_GRUNT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.grunt");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ZOMBIE_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.hurt");
    public static final Holder.Reference<SoundEvent> ENTITY_MUTANT_ZOMBIE_ROAR_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.roar");
    public static final Holder.Reference<SoundEvent> ENTITY_SPIDER_PIG_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.spider_pig.ambient");
    public static final Holder.Reference<SoundEvent> ENTITY_SPIDER_PIG_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.spider_pig.death");
    public static final Holder.Reference<SoundEvent> ENTITY_SPIDER_PIG_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.spider_pig.hurt");

    private static final CapabilityController CAPABILITY = CapabilityController.from(MutantMonsters.MOD_ID);
    public static final EntityCapabilityKey<Player, SeismicWavesCapability> SEISMIC_WAVES_CAPABILITY = CAPABILITY.registerEntityCapability("seismic_waves", SeismicWavesCapability.class,
            SeismicWavesCapability::new, Player.class);

    static final BoundTagFactory TAGS = BoundTagFactory.make(MutantMonsters.MOD_ID);
    public static final TagKey<Block> MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG = TAGS.registerBlockTag("mutant_enderman_holdable_immune");
    public static final TagKey<Block> ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG = TAGS.registerBlockTag("endersoul_hand_holdable_immune");
    public static final TagKey<EntityType<?>> MUTANTS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("mutants");
    public static final TagKey<Biome> WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag("without_mutant_creeper_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag("without_mutant_enderman_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag("without_mutant_skeleton_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag("without_mutant_zombie_spawns");

    public static final ResourceKey<DamageType> PLAYER_SEISMIC_WAVE_DAMAGE_TYPE = REGISTRY.registerDamageType("player_seismic_wave");
    public static final ResourceKey<DamageType> ARMOR_BYPASSING_MOB_ATTACK_DAMAGE_TYPE = REGISTRY.registerDamageType("armor_bypassing_mob_attack");
    public static final ResourceKey<DamageType> EFFECTS_BYPASSING_MOB_ATTACK_DAMAGE_TYPE = REGISTRY.registerDamageType("effects_bypassing_mob_attack");
    public static final ResourceKey<DamageType> PIERCING_MOB_ATTACK_DAMAGE_TYPE = REGISTRY.registerDamageType("piercing_mob_attack");
    public static final ResourceKey<DamageType> MUTANT_ARROW_DAMAGE_TYPE = REGISTRY.registerDamageType("mutant_arrow");
    public static final ResourceKey<DamageType> ARMOR_BYPASSING_THROWN_DAMAGE_TYPE = REGISTRY.registerDamageType("armor_bypassing_thrown");

    public static final ResourceLocation MUTANT_SKELETON_PELVIS_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/pelvis");
    public static final ResourceLocation MUTANT_SKELETON_RIB_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/rib");
    public static final ResourceLocation MUTANT_SKELETON_SKULL_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/skull");
    public static final ResourceLocation MUTANT_SKELETON_LIMB_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/limb");
    public static final ResourceLocation MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/shoulder_pad");
    public static final ResourceLocation MUTANT_ENDERMAN_CONTINUOUS_LOOT_TABLE = MutantMonsters.id("entities/mutant_enderman_continuous");
    public static final LootContextParamSet BODY_PART_LOOT_CONTEXT_PARAM_SET = registerLootContextParamSet(MutantMonsters.id("body_part"), (builder) -> {
        builder.required(LootContextParams.THIS_ENTITY);
    });

    public static void touch() {

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
