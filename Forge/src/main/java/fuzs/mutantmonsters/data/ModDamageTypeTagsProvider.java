package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends AbstractTagProvider.DamageTypes {

    public ModDamageTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_EFFECTS).add(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, ModRegistry.EFFECTS_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE);
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ModRegistry.ARMOR_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE);
        this.tag(DamageTypeTags.IS_PROJECTILE).add(ModRegistry.MUTANT_ARROW_DAMAGE_TYPE, ModRegistry.ARMOR_BYPASSING_THROWN_DAMAGE_TYPE);
        this.tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).add(ModRegistry.MUTANT_ARROW_DAMAGE_TYPE);
    }
}
