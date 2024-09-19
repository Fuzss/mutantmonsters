package fuzs.mutantmonsters.data.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.gui.screens.CreeperMinionTrackerScreen;
import fuzs.mutantmonsters.data.ModAdvancementProvider;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addCreativeModeTab(MutantMonsters.MOD_ID, MutantMonsters.MOD_NAME);
        builder.add(ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.value(), "Mutant Skeleton Skull");
        builder.add(ModRegistry.BODY_PART_ENTITY_TYPE.value(), "Body Part");
        builder.add(ModRegistry.CREEPER_MINION_ENTITY_TYPE.value(), "Creeper Minion");
        builder.add(ModRegistry.CREEPER_MINION_EGG_ENTITY_TYPE.value(), "Creeper Minion Egg");
        builder.add(ModRegistry.ENDERSOUL_CLONE_ENTITY_TYPE.value(), "Endersoul Clone");
        builder.add(ModRegistry.ENDERSOUL_FRAGMENT_ENTITY_TYPE.value(), "Endersoul Fragment");
        builder.add(ModRegistry.MUTANT_ARROW_ENTITY_TYPE.value(), "Mutant Arrow");
        builder.add(ModRegistry.MUTANT_CREEPER_ENTITY_TYPE.value(), "Mutant Creeper");
        builder.add(ModRegistry.MUTANT_ENDERMAN_ENTITY_TYPE.value(), "Mutant Enderman");
        builder.add(ModRegistry.MUTANT_SKELETON_ENTITY_TYPE.value(), "Mutant Skeleton");
        builder.add(ModRegistry.MUTANT_SNOW_GOLEM_ENTITY_TYPE.value(), "Mutant Snow Golem");
        builder.add(ModRegistry.MUTANT_ZOMBIE_ENTITY_TYPE.value(), "Mutant Zombie");
        builder.add(ModRegistry.SKULL_SPIRIT_ENTITY_TYPE.value(), "Skull Spirit");
        builder.add(ModRegistry.SPIDER_PIG_ENTITY_TYPE.value(), "Spider Pig");
        builder.add(ModRegistry.THROWABLE_BLOCK_ENTITY_TYPE.value(), "Throwable Block");
        builder.add(CreeperMinionTrackerScreen.HEALTH_COMPONENT, "Health");
        builder.add(CreeperMinionTrackerScreen.EXPLOSION_COMPONENT, "Explosion");
        builder.add(CreeperMinionTrackerScreen.CONTINUOUS_EXPLOSION_COMPONENT, "Continuous");
        builder.add(CreeperMinionTrackerScreen.ONE_TIME_EXPLOSION_COMPONENT, "One-Time");
        builder.add(CreeperMinionTrackerScreen.BLAST_RADIUS_COMPONENT, "Blast Radius");
        builder.add(CreeperMinionTrackerScreen.SHOW_NAME_COMPONENT, "Always Show Name");
        builder.add(CreeperMinionTrackerScreen.DESTROY_BLOCKS_COMPONENT, "Destroy Blocks");
        builder.add(CreeperMinionTrackerScreen.RIDE_ON_SHOULDER_COMPONENT, "Ride On Shoulder");
        builder.addSpawnEgg(ModRegistry.CREEPER_MINION_SPAWN_EGG_ITEM.value(), "Creeper Minion");
        builder.addSpawnEgg(ModRegistry.MUTANT_CREEPER_SPAWN_EGG_ITEM.value(), "Mutant Creeper");
        builder.addSpawnEgg(ModRegistry.MUTANT_ENDERMAN_SPAWN_EGG_ITEM.value(), "Mutant Enderman");
        builder.addSpawnEgg(ModRegistry.MUTANT_SKELETON_SPAWN_EGG_ITEM.value(), "Mutant Skeleton");
        builder.addSpawnEgg(ModRegistry.MUTANT_SNOW_GOLEM_SPAWN_EGG_ITEM.value(), "Mutant Snow Golem");
        builder.addSpawnEgg(ModRegistry.MUTANT_ZOMBIE_SPAWN_EGG_ITEM.value(), "Mutant Zombie");
        builder.addSpawnEgg(ModRegistry.SPIDER_PIG_SPAWN_EGG_ITEM.value(), "Spider Pig");
        builder.add(ModRegistry.CREEPER_MINION_TRACKER_ITEM.value(), "Creeper Minion Tracker");
        builder.add(ModRegistry.CREEPER_MINION_TRACKER_ITEM.value(), "tame_success", "%1$s was tamed by %2$s");
        builder.add(ModRegistry.CREEPER_SHARD_ITEM.value(), "Creeper Shard");
        builder.add(ModRegistry.HULK_HAMMER_ITEM.value(), "Hulk Hammer");
        builder.add(ModRegistry.ENDERSOUL_HAND_ITEM.value(), "Endersoul Hand");
        builder.add(ModRegistry.ENDERSOUL_HAND_ITEM.value(), "teleport_failed", "Unable to teleport to location");
        builder.add(ModRegistry.MUTANT_SKELETON_ARMS_ITEM.value(), "Mutant Skeleton Arms");
        builder.add(ModRegistry.MUTANT_SKELETON_LIMB_ITEM.value(), "Mutant Skeleton Limb");
        builder.add(ModRegistry.MUTANT_SKELETON_PELVIS_ITEM.value(), "Mutant Skeleton Pelvis");
        builder.add(ModRegistry.MUTANT_SKELETON_RIB_CAGE_ITEM.value(), "Mutant Skeleton Rib Cage");
        builder.add(ModRegistry.MUTANT_SKELETON_RIB_ITEM.value(), "Mutant Skeleton Rib");
        builder.add(ModRegistry.MUTANT_SKELETON_SHOULDER_PAD_ITEM.value(), "Mutant Skeleton Shoulder Pad");
        builder.add(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.value(), "Mutant Skeleton Chestplate");
        builder.add(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.value(), "Mutant Skeleton Leggings");
        builder.add(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.value(), "Mutant Skeleton Boots");
        builder.add(ModRegistry.CHEMICAL_X_MOB_EFFECT.value(), "Chemical X");
        builder.add(ModRegistry.CHEMICAL_X_POTION.value(), "Chemical X");
        builder.add(ModRegistry.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.value(), "Creeper Minion hisses");
        builder.add(ModRegistry.ENTITY_CREEPER_MINION_DEATH_SOUND_EVENT.value(), "Creeper Minion dies");
        builder.add(ModRegistry.ENTITY_CREEPER_MINION_HURT_SOUND_EVENT.value(), "Creeper Minion hurts");
        builder.add(ModRegistry.ENTITY_CREEPER_MINION_PRIMED_SOUND_EVENT.value(), "Creeper Minion primes");
        builder.add(ModRegistry.ENTITY_CREEPER_MINION_EGG_HATCH_SOUND_EVENT.value(), "Creeper Minion Egg hatches");
        builder.add(ModRegistry.ENTITY_ENDERSOUL_CLONE_DEATH_SOUND_EVENT.value(), "Endersoul Clone dies");
        builder.add(ModRegistry.ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT.value(), "Endersoul Clone teleports");
        builder.add(ModRegistry.ENTITY_ENDERSOUL_FRAGMENT_EXPLODE_SOUND_EVENT.value(), "Endersoul Fragment explodes");
        builder.add(ModRegistry.ENTITY_MUTANT_CREEPER_AMBIENT_SOUND_EVENT.value(), "Mutant Creeper hisses");
        builder.add(ModRegistry.ENTITY_MUTANT_CREEPER_CHARGE_SOUND_EVENT.value(), "Mutant Creeper charges");
        builder.add(ModRegistry.ENTITY_MUTANT_CREEPER_DEATH_SOUND_EVENT.value(), "Mutant Creeper dies");
        builder.add(ModRegistry.ENTITY_MUTANT_CREEPER_HURT_SOUND_EVENT.value(), "Mutant Creeper hurts");
        builder.add(ModRegistry.ENTITY_MUTANT_CREEPER_PRIMED_SOUND_EVENT.value(), "Mutant Creeper primes");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_AMBIENT_SOUND_EVENT.value(), "Mutant Enderman breathes");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_DEATH_SOUND_EVENT.value(), "Mutant Enderman dies");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_HURT_SOUND_EVENT.value(), "Mutant Enderman hurts");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_MORPH_SOUND_EVENT.value(), "Mutant Enderman morphs");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_SCREAM_SOUND_EVENT.value(), "Mutant Enderman screams");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_STARE_SOUND_EVENT.value(), "Mutant Enderman cries out");
        builder.add(ModRegistry.ENTITY_MUTANT_ENDERMAN_TELEPORT_SOUND_EVENT.value(), "Mutant Enderman teleports");
        builder.add(ModRegistry.ENTITY_MUTANT_SKELETON_AMBIENT_SOUND_EVENT.value(), "Mutant Skeleton rattles");
        builder.add(ModRegistry.ENTITY_MUTANT_SKELETON_DEATH_SOUND_EVENT.value(), "Mutant Skeleton dies");
        builder.add(ModRegistry.ENTITY_MUTANT_SKELETON_HURT_SOUND_EVENT.value(), "Mutant Skeleton hurts");
        builder.add(ModRegistry.ENTITY_MUTANT_SKELETON_STEP_SOUND_EVENT.value(), "Footsteps");
        builder.add(ModRegistry.ENTITY_MUTANT_SNOW_GOLEM_DEATH_SOUND_EVENT.value(), "Mutant Snow Golem dies");
        builder.add(ModRegistry.ENTITY_MUTANT_SNOW_GOLEM_HURT_SOUND_EVENT.value(), "Mutant Snow Golem hurts");
        builder.add(ModRegistry.ENTITY_MUTANT_ZOMBIE_AMBIENT_SOUND_EVENT.value(), "Mutant Zombie grumbles");
        builder.add(ModRegistry.ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT.value(), "Mutant Zombie attacks");
        builder.add(ModRegistry.ENTITY_MUTANT_ZOMBIE_DEATH_SOUND_EVENT.value(), "Mutant Zombie dies");
        builder.add(ModRegistry.ENTITY_MUTANT_ZOMBIE_GRUNT_SOUND_EVENT.value(), "Mutant Zombie grunts");
        builder.add(ModRegistry.ENTITY_MUTANT_ZOMBIE_HURT_SOUND_EVENT.value(), "Mutant Zombie hurts");
        builder.add(ModRegistry.ENTITY_MUTANT_ZOMBIE_ROAR_SOUND_EVENT.value(), "Mutant Zombie roars");
        builder.add(ModRegistry.ENTITY_SPIDER_PIG_AMBIENT_SOUND_EVENT.value(), "Spider Pig oinks");
        builder.add(ModRegistry.ENTITY_SPIDER_PIG_DEATH_SOUND_EVENT.value(), "Spider Pig dies");
        builder.add(ModRegistry.ENTITY_SPIDER_PIG_HURT_SOUND_EVENT.value(), "Spider Pig hurts");
        builder.add(ModAdvancementProvider.ROOT_ADVANCEMENT.title(), MutantMonsters.MOD_NAME);
        builder.add(ModAdvancementProvider.ROOT_ADVANCEMENT.description(),
                "Mutant creatures and beasts to face off against. Will you triumph, or will you perish?"
        );
        builder.add(ModAdvancementProvider.BURN_ZOMBIE_BURN_ADVANCEMENT.title(), "Burn Zombie Burn");
        builder.add(ModAdvancementProvider.BURN_ZOMBIE_BURN_ADVANCEMENT.description(),
                "Use a flint and steel to light a mutant zombie on fire when it's down"
        );
        builder.add(ModAdvancementProvider.FROSTY_THE_SNOW_GOLEM_ADVANCEMENT.title(), "Frosty the Snow Golem");
        builder.add(ModAdvancementProvider.FROSTY_THE_SNOW_GOLEM_ADVANCEMENT.description(),
                "Create a Mutant Snow Golem using Chemical X"
        );
        builder.add(ModAdvancementProvider.GUNPOWDER_SPICE_ADVANCEMENT.title(), "Gunpowder, spice...");
        builder.add(ModAdvancementProvider.GUNPOWDER_SPICE_ADVANCEMENT.description(),
                "...and everything nice. Obtain a potion of Chemical X."
        );
        builder.add(ModAdvancementProvider.HULK_SMASH_ADVANCEMENT.title(), "Hulk Smash!");
        builder.add(ModAdvancementProvider.HULK_SMASH_ADVANCEMENT.description(),
                "Kill a Mutant Zombie using a Hulk Hammer"
        );
        builder.add(ModAdvancementProvider.NO_BONES_ABOUT_IT_ADVANCEMENT.title(), "No Bones About It");
        builder.add(ModAdvancementProvider.NO_BONES_ABOUT_IT_ADVANCEMENT.description(),
                "Kill a Mutant Skeleton using a crossbow while wearing a full set of Mutant Skeleton armor"
        );
        builder.add(ModAdvancementProvider.SPIDER_PIG_SPIDER_PIG_ADVANCEMENT.title(), "Spider-Pig, Spider-Pig...");
        builder.add(ModAdvancementProvider.SPIDER_PIG_SPIDER_PIG_ADVANCEMENT.description(),
                "Feed a Pig a Fermented Spider Eye and throw Chemical X at it"
        );
        builder.add(ModAdvancementProvider.YOU_DA_BOMBY_ADVANCEMENT.title(), "You da Bomby");
        builder.add(ModAdvancementProvider.YOU_DA_BOMBY_ADVANCEMENT.description(),
                "Hatch a Creeper Minion from its egg"
        );
    }
}
