package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.capability.SeismicWavesCapability;
import fuzs.mutantmonsters.capability.SeismicWavesCapabilityImpl;
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
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.PlayerCapabilityKey;
import fuzs.puzzleslib.api.capability.v2.data.PlayerRespawnStrategy;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Consumer;

public class ModRegistry {
    public static final SkullBlock.Type MUTANT_SKELETON_SKULL_TYPE = new SkullBlock.Type() {};
    
    static final RegistryManager REGISTRY = RegistryManager.instant(MutantMonsters.MOD_ID);
    public static final RegistryReference<Block> MUTANT_SKELETON_SKULL_BLOCK = REGISTRY.registerBlock("mutant_skeleton_skull", () -> new SkullWithItemTagBlock(MUTANT_SKELETON_SKULL_TYPE, BlockBehaviour.Properties.of(Material.DECORATION).strength(1.0F)));
    public static final RegistryReference<Block> MUTANT_SKELETON_WALL_SKULL_BLOCK = REGISTRY.registerBlock("mutant_skeleton_wall_skull", () -> new WallSkullWithItemTagBlock(MUTANT_SKELETON_SKULL_TYPE, BlockBehaviour.Properties.copy(MUTANT_SKELETON_SKULL_BLOCK.get()).dropsLike(MUTANT_SKELETON_SKULL_BLOCK.get())));
    public static final RegistryReference<EntityType<CreeperMinion>> CREEPER_MINION_ENTITY_TYPE = REGISTRY.registerEntityType("creeper_minion", () -> EntityType.Builder.of(CreeperMinion::new, MobCategory.MISC).sized(0.3F, 0.85F));
    public static final RegistryReference<EntityType<MutantCreeper>> MUTANT_CREEPER_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_creeper", () -> EntityType.Builder.of(MutantCreeper::new, MobCategory.MONSTER).sized(1.99F, 2.8F));
    public static final RegistryReference<EntityType<MutantEnderman>> MUTANT_ENDERMAN_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_enderman", () -> EntityType.Builder.<MutantEnderman>of(MutantEnderman::new, MobCategory.MONSTER).sized(1.2F, 4.2F));
    public static final RegistryReference<EntityType<MutantSkeleton>> MUTANT_SKELETON_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_skeleton", () -> EntityType.Builder.<MutantSkeleton>of(MutantSkeleton::new, MobCategory.MONSTER).sized(1.2F, 3.6F));
    public static final RegistryReference<EntityType<MutantSnowGolem>> MUTANT_SNOW_GOLEM_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_snow_golem", () -> EntityType.Builder.of(MutantSnowGolem::new, MobCategory.MISC).sized(1.15F, 2.3F));
    public static final RegistryReference<EntityType<MutantZombie>> MUTANT_ZOMBIE_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_zombie", () -> EntityType.Builder.<MutantZombie>of(MutantZombie::new, MobCategory.MONSTER).sized(1.8F, 3.2F));
    public static final RegistryReference<EntityType<SpiderPig>> SPIDER_PIG_ENTITY_TYPE = REGISTRY.registerEntityType("spider_pig", () -> EntityType.Builder.of(SpiderPig::new, MobCategory.CREATURE).sized(1.4F, 0.9F));
    public static final RegistryReference<EntityType<MutantSkeletonBodyPart>> BODY_PART_ENTITY_TYPE = REGISTRY.registerEntityType("body_part", () -> EntityType.Builder.<MutantSkeletonBodyPart>of(MutantSkeletonBodyPart::new, MobCategory.MISC).clientTrackingRange(4).updateInterval(10).sized(0.7F, 0.7F));
    public static final RegistryReference<EntityType<CreeperMinionEgg>> CREEPER_MINION_EGG_ENTITY_TYPE = REGISTRY.registerEntityType("creeper_minion_egg", () -> EntityType.Builder.<CreeperMinionEgg>of(CreeperMinionEgg::new, MobCategory.MISC).clientTrackingRange(10).updateInterval(20).sized(0.5625F, 0.75F));
    public static final RegistryReference<EntityType<EndersoulClone>> ENDERSOUL_CLONE_ENTITY_TYPE = REGISTRY.registerEntityType("endersoul_clone", () -> EntityType.Builder.of(EndersoulClone::new, MobCategory.MONSTER).sized(0.6F, 2.9F));
    public static final RegistryReference<EntityType<EndersoulFragment>> ENDERSOUL_FRAGMENT_ENTITY_TYPE = REGISTRY.registerEntityType("endersoul_fragment", () -> EntityType.Builder.<EndersoulFragment>of(EndersoulFragment::new, MobCategory.MISC).clientTrackingRange(4).updateInterval(10).sized(0.75F, 0.75F));
    public static final RegistryReference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.placeholder(Registries.ENTITY_TYPE, "mutant_arrow");
    public static final RegistryReference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.placeholder(Registries.ENTITY_TYPE, "skull_spirit");
    public static final RegistryReference<EntityType<ThrowableBlock>> THROWABLE_BLOCK_ENTITY_TYPE = REGISTRY.registerEntityType("throwable_block", () -> EntityType.Builder.<ThrowableBlock>of(ThrowableBlock::new, MobCategory.MISC).clientTrackingRange(4).updateInterval(100).sized(1.0F, 1.0F));
    public static final RegistryReference<Item> CREEPER_MINION_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) CREEPER_MINION_ENTITY_TYPE, 894731, 12040119);
    public static final RegistryReference<Item> MUTANT_CREEPER_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) MUTANT_CREEPER_ENTITY_TYPE, 5349438, 11013646);
    public static final RegistryReference<Item> MUTANT_ENDERMAN_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) MUTANT_ENDERMAN_ENTITY_TYPE, 1447446, 8860812);
    public static final RegistryReference<Item> MUTANT_SKELETON_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) MUTANT_SKELETON_ENTITY_TYPE, 12698049, 6310217);
    public static final RegistryReference<Item> MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) MUTANT_SNOW_GOLEM_ENTITY_TYPE, 15073279, 16753434);
    public static final RegistryReference<Item> MUTANT_ZOMBIE_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) MUTANT_ZOMBIE_ENTITY_TYPE, 7969893, 44975);
    public static final RegistryReference<Item> SPIDER_PIG_SPAWN_EGG_ITEM = REGISTRY.registerSpawnEggItem((RegistryReference<EntityType<? extends Mob>>) (RegistryReference<?>) SPIDER_PIG_ENTITY_TYPE, 3419431, 15771042);
    public static final RegistryReference<Item> CREEPER_MINION_TRACKER_ITEM = REGISTRY.registerItem("creeper_minion_tracker", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryReference<Item> CREEPER_SHARD_ITEM = REGISTRY.registerItem("creeper_shard", () -> new CreeperShardItem(new Item.Properties().durability(32).rarity(Rarity.UNCOMMON)));
    public static final RegistryReference<Item> ENDERSOUL_HAND_ITEM = REGISTRY.whenNotOn(ModLoader.FORGE).registerItem("endersoul_hand", () -> new EndersoulHandItem(new Item.Properties().durability(240).rarity(Rarity.EPIC)));
    public static final RegistryReference<Item> HULK_HAMMER_ITEM = REGISTRY.whenNotOn(ModLoader.FORGE).registerItem("hulk_hammer", () -> new HulkHammerItem(new Item.Properties().durability(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryReference<Item> MUTANT_SKELETON_ARMS_ITEM = REGISTRY.registerItem("mutant_skeleton_arms", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_LIMB_ITEM = REGISTRY.registerItem("mutant_skeleton_limb", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_PELVIS_ITEM = REGISTRY.registerItem("mutant_skeleton_pelvis", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_RIB_ITEM = REGISTRY.registerItem("mutant_skeleton_rib", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_RIB_CAGE_ITEM = REGISTRY.registerItem("mutant_skeleton_rib_cage", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_SHOULDER_PAD_ITEM = REGISTRY.registerItem("mutant_skeleton_shoulder_pad", () -> new Item(new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_SKULL_ITEM = REGISTRY.placeholder(Registries.ITEM, "mutant_skeleton_skull");
    public static final RegistryReference<Item> MUTANT_SKELETON_CHESTPLATE_ITEM = REGISTRY.registerItem("mutant_skeleton_chestplate", () -> new SkeletonArmorItem(MutantSkeletonArmorMaterial.INSTANCE, EquipmentSlot.CHEST, new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_LEGGINGS_ITEM = REGISTRY.registerItem("mutant_skeleton_leggings", () -> new SkeletonArmorItem(MutantSkeletonArmorMaterial.INSTANCE, EquipmentSlot.LEGS, new Item.Properties()));
    public static final RegistryReference<Item> MUTANT_SKELETON_BOOTS_ITEM = REGISTRY.registerItem("mutant_skeleton_boots", () -> new SkeletonArmorItem(MutantSkeletonArmorMaterial.INSTANCE, EquipmentSlot.FEET, new Item.Properties()));
    public static final RegistryReference<BlockEntityType<SkullWithItemTagBlockEntity>> SKULL_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityType("skull", () -> BlockEntityType.Builder.of(SkullWithItemTagBlockEntity::new, MUTANT_SKELETON_SKULL_BLOCK.get(), MUTANT_SKELETON_WALL_SKULL_BLOCK.get()));
    public static final RegistryReference<MobEffect> CHEMICAL_X_MOB_EFFECT = REGISTRY.registerMobEffect("chemical_x", () -> new ChemicalXMobEffect(MobEffectCategory.HARMFUL, 0x000000));
    public static final RegistryReference<Potion> CHEMICAL_X_POTION = REGISTRY.registerPotion("chemical_x", () -> new Potion(new MobEffectInstance(CHEMICAL_X_MOB_EFFECT.get(), 1)));
    public static final RegistryReference<SimpleParticleType> ENDERSOUL_PARTICLE_TYPE = REGISTRY.register(Registries.PARTICLE_TYPE, "endersoul", () -> new SimpleParticleType(false));
    public static final RegistryReference<SimpleParticleType> SKULL_SPIRIT_PARTICLE_TYPE = REGISTRY.register(Registries.PARTICLE_TYPE, "skull_spirit", () -> new SimpleParticleType(true));
    public static final RegistryReference<SoundEvent> ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.ambient");
    public static final RegistryReference<SoundEvent> ENTITY_CREEPER_MINION_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.death");
    public static final RegistryReference<SoundEvent> ENTITY_CREEPER_MINION_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.hurt");
    public static final RegistryReference<SoundEvent> ENTITY_CREEPER_MINION_PRIMED_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion.primed");
    public static final RegistryReference<SoundEvent> ENTITY_CREEPER_MINION_EGG_HATCH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.creeper_minion_egg.hatch");
    public static final RegistryReference<SoundEvent> ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.endersoul_clone.teleport");
    public static final RegistryReference<SoundEvent> ENTITY_ENDERSOUL_CLONE_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.endersoul_clone.death");
    public static final RegistryReference<SoundEvent> ENTITY_ENDERSOUL_FRAGMENT_EXPLODE_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.endersoul_fragment.explode");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_CREEPER_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.ambient");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_CREEPER_CHARGE_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.charge");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_CREEPER_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.death");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_CREEPER_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.hurt");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_CREEPER_PRIMED_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_creeper.primed");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.ambient");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.death");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.hurt");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_MORPH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.morph");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_SCREAM_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.scream");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_STARE_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.stare");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ENDERMAN_TELEPORT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_enderman.teleport");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_SKELETON_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.ambient");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_SKELETON_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.death");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_SKELETON_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.hurt");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_SKELETON_STEP_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_skeleton.step");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_SNOW_GOLEM_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_snow_golem.death");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_SNOW_GOLEM_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_snow_golem.hurt");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ZOMBIE_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.ambient");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.attack");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ZOMBIE_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.death");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ZOMBIE_GRUNT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.grunt");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ZOMBIE_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.hurt");
    public static final RegistryReference<SoundEvent> ENTITY_MUTANT_ZOMBIE_ROAR_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.mutant_zombie.roar");
    public static final RegistryReference<SoundEvent> ENTITY_SPIDER_PIG_AMBIENT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.spider_pig.ambient");
    public static final RegistryReference<SoundEvent> ENTITY_SPIDER_PIG_DEATH_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.spider_pig.death");
    public static final RegistryReference<SoundEvent> ENTITY_SPIDER_PIG_HURT_SOUND_EVENT = REGISTRY.registerSoundEvent("entity.spider_pig.hurt");

    private static final CapabilityController CAPABILITY = CapabilityController.from(MutantMonsters.MOD_ID);
    public static final PlayerCapabilityKey<SeismicWavesCapability> SEISMIC_WAVES_CAPABILITY = CAPABILITY.registerPlayerCapability("seismic_waves", SeismicWavesCapability.class, player -> new SeismicWavesCapabilityImpl(), PlayerRespawnStrategy.NEVER);

    public static final TagKey<Block> MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG = REGISTRY.createBlockTag("mutant_enderman_holdable_immune");
    public static final TagKey<Block> ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG = REGISTRY.createBlockTag("endersoul_hand_holdable_immune");

    public static final ResourceLocation MUTANT_SKELETON_PELVIS_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/pelvis");
    public static final ResourceLocation MUTANT_SKELETON_RIB_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/rib");
    public static final ResourceLocation MUTANT_SKELETON_SKULL_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/skull");
    public static final ResourceLocation MUTANT_SKELETON_LIMB_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/limb");
    public static final ResourceLocation MUTANT_SKELETON_SHOULDER_PAD_LOOT_TABLE = MutantMonsters.id("entities/mutant_skeleton/shoulder_pad");
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
