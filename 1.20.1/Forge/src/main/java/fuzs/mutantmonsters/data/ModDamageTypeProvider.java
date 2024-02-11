package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractDamageTypeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModDamageTypeProvider extends AbstractDamageTypeProvider {

    public ModDamageTypeProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
    }

    @Override
    protected void addDamageSources() {
        this.add(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("player", 0.1F));
        this.add(ModRegistry.ARMOR_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.EFFECTS_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.MUTANT_ARROW_DAMAGE_TYPE, new DamageType("arrow", 0.1F));
        this.add(ModRegistry.ARMOR_BYPASSING_THROWN_DAMAGE_TYPE, new DamageType("thrown", 0.1F));
    }
}
