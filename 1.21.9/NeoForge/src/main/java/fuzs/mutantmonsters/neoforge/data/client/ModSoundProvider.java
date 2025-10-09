package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.client.data.v2.AbstractSoundProvider;
import net.minecraft.sounds.SoundEvents;

public class ModSoundProvider extends AbstractSoundProvider {

    public ModSoundProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addSounds() {
        this.add(ModSoundEvents.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.value(),
                sound(SoundEvents.CREEPER_HURT).volume(0.6));
        this.add(ModSoundEvents.ENTITY_CREEPER_MINION_DEATH_SOUND_EVENT.value(), SoundEvents.CREEPER_DEATH);
        this.add(ModSoundEvents.ENTITY_CREEPER_MINION_HURT_SOUND_EVENT.value(), SoundEvents.CREEPER_HURT);
        this.add(ModSoundEvents.ENTITY_CREEPER_MINION_PRIMED_SOUND_EVENT.value(), SoundEvents.CREEPER_PRIMED);
        this.add(ModSoundEvents.ENTITY_CREEPER_MINION_EGG_HATCH_SOUND_EVENT.value(),
                MutantMonsters.id("entity/creeper_minion_egg_hatch"));
        this.add(ModSoundEvents.ENTITY_ENDERSOUL_CLONE_DEATH_SOUND_EVENT.value(), "mob/illusion_illager/mirror_move1");
        this.add(ModSoundEvents.ENTITY_ENDERSOUL_CLONE_TELEPORT_SOUND_EVENT.value(), SoundEvents.ENDERMAN_TELEPORT);
        this.add(ModSoundEvents.ENTITY_ENDERSOUL_FRAGMENT_EXPLODE_SOUND_EVENT.value(), SoundEvents.SHULKER_BULLET_HIT);
        this.add(ModSoundEvents.ENTITY_MUTANT_CREEPER_AMBIENT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_creeper/ambient1"),
                MutantMonsters.id("entity/mutant_creeper/ambient2"),
                MutantMonsters.id("entity/mutant_creeper/ambient3"),
                MutantMonsters.id("entity/mutant_creeper/ambient4"));
        this.add(ModSoundEvents.ENTITY_MUTANT_CREEPER_CHARGE_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_creeper/charge"));
        this.add(ModSoundEvents.ENTITY_MUTANT_CREEPER_DEATH_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_creeper/death"));
        this.add(ModSoundEvents.ENTITY_MUTANT_CREEPER_HURT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_creeper/hurt1"),
                MutantMonsters.id("entity/mutant_creeper/hurt2"));
        this.add(ModSoundEvents.ENTITY_MUTANT_CREEPER_PRIMED_SOUND_EVENT.value(),
                sound(SoundEvents.CREEPER_PRIMED).pitch(0.25));
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_AMBIENT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_enderman/ambient1"),
                MutantMonsters.id("entity/mutant_enderman/ambient2"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_DEATH_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_enderman/death"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_HURT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_enderman/hurt1"),
                MutantMonsters.id("entity/mutant_enderman/hurt2"),
                MutantMonsters.id("entity/mutant_enderman/hurt3"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_MORPH_SOUND_EVENT.value(), "mob/illusion_illager/mirror_move2");
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_SCREAM_SOUND_EVENT.value(),
                sound(MutantMonsters.id("entity/mutant_enderman/scream")).attenuationDistance(32));
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_STARE_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_enderman/stare"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ENDERMAN_TELEPORT_SOUND_EVENT.value(), SoundEvents.ENDERMAN_TELEPORT);
        this.add(ModSoundEvents.ENTITY_MUTANT_SKELETON_AMBIENT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_skeleton/ambient1"),
                MutantMonsters.id("entity/mutant_skeleton/ambient2"),
                MutantMonsters.id("entity/mutant_skeleton/ambient3"));
        this.add(ModSoundEvents.ENTITY_MUTANT_SKELETON_DEATH_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_skeleton/death"));
        this.add(ModSoundEvents.ENTITY_MUTANT_SKELETON_HURT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_skeleton/hurt1"),
                MutantMonsters.id("entity/mutant_skeleton/hurt2"),
                MutantMonsters.id("entity/mutant_skeleton/hurt3"),
                MutantMonsters.id("entity/mutant_skeleton/hurt4"));
        this.add(ModSoundEvents.ENTITY_MUTANT_SKELETON_STEP_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_skeleton/step1"),
                MutantMonsters.id("entity/mutant_skeleton/step2"),
                MutantMonsters.id("entity/mutant_skeleton/step3"),
                MutantMonsters.id("entity/mutant_skeleton/step4"));
        this.add(ModSoundEvents.ENTITY_MUTANT_SNOW_GOLEM_DEATH_SOUND_EVENT.value(),
                sound(SoundEvents.SNOW_GOLEM_DEATH).pitch(0.75));
        this.add(ModSoundEvents.ENTITY_MUTANT_SNOW_GOLEM_HURT_SOUND_EVENT.value(),
                sound(SoundEvents.SNOW_GOLEM_HURT).pitch(0.75));
        this.add(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_AMBIENT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_zombie/ambient1"),
                MutantMonsters.id("entity/mutant_zombie/ambient2"),
                MutantMonsters.id("entity/mutant_zombie/ambient3"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_zombie/attack"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_DEATH_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_zombie/hurt1"),
                MutantMonsters.id("entity/mutant_zombie/hurt2"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_GRUNT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_zombie/grunt"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_HURT_SOUND_EVENT.value(),
                MutantMonsters.id("entity/mutant_zombie/hurt1"),
                MutantMonsters.id("entity/mutant_zombie/hurt2"));
        this.add(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_ROAR_SOUND_EVENT.value(),
                sound(MutantMonsters.id("entity/mutant_zombie/roar1")).attenuationDistance(32).weight(2),
                sound(MutantMonsters.id("entity/mutant_zombie/roar2")).attenuationDistance(32).weight(2),
                sound(MutantMonsters.id("entity/mutant_zombie/roarslow1")).attenuationDistance(32),
                sound(MutantMonsters.id("entity/mutant_zombie/roarslow2")).attenuationDistance(32));
        this.add(ModSoundEvents.ENTITY_SPIDER_PIG_AMBIENT_SOUND_EVENT.value(), SoundEvents.PIG_AMBIENT);
        this.add(ModSoundEvents.ENTITY_SPIDER_PIG_DEATH_SOUND_EVENT.value(), SoundEvents.PIG_DEATH);
        this.add(ModSoundEvents.ENTITY_SPIDER_PIG_HURT_SOUND_EVENT.value(), SoundEvents.PIG_HURT);
    }
}
