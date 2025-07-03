package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public class ModDamageTypeTagProvider extends AbstractTagProvider<DamageType> {

    public ModDamageTypeTagProvider(DataProviderContext context) {
        super(Registries.DAMAGE_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(ModRegistry.MUTANT_SKELETON_SHATTER_DAMAGE_TYPE,
                        ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE,
                        ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE,
                        ModRegistry.MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE,
                        ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE);
        this.tag(DamageTypeTags.IS_EXPLOSION).add(ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE);
        this.tag(ModTags.MUTANT_ENDERMAN_DODGE_DAMAGE_TYPE_TAG)
                .add(DamageTypes.FALL)
                .addTag(DamageTypeTags.IS_PROJECTILE, DamageTypeTags.IS_EXPLOSION);
    }
}
