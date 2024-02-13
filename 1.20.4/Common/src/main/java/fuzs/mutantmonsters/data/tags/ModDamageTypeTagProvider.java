package fuzs.mutantmonsters.data.tags;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeTagProvider extends AbstractTagProvider.Simple<DamageType> {

    public ModDamageTypeTagProvider(DataProviderContext context) {
        super(Registries.DAMAGE_TYPE, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        // TODO add damage types to existing file helper
//        this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, ModRegistry.EFFECTS_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE);
//        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModRegistry.ARMOR_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE);
//        this.tag(DamageTypeTags.IS_PROJECTILE).add(ModRegistry.MUTANT_ARROW_DAMAGE_TYPE, ModRegistry.ARMOR_BYPASSING_THROWN_DAMAGE_TYPE);
//        this.tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).add(ModRegistry.MUTANT_ARROW_DAMAGE_TYPE);
    }
}
