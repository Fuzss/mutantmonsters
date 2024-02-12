package fuzs.mutantmonsters.neoforge.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.data.v2.AbstractBuiltInDataProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeProvider extends AbstractBuiltInDataProvider.DamageTypes {

    public ModDamageTypeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    protected void addBootstrap(BootstapContext<DamageType> bootstapContext) {
        this.add(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("player", 0.1F));
        this.add(ModRegistry.ARMOR_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.EFFECTS_BYPASSING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.MUTANT_ARROW_DAMAGE_TYPE, new DamageType("arrow", 0.1F));
        this.add(ModRegistry.ARMOR_BYPASSING_THROWN_DAMAGE_TYPE, new DamageType("thrown", 0.1F));
    }
}
