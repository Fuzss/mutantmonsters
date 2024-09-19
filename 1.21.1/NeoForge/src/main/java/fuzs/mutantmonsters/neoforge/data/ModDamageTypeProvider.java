package fuzs.mutantmonsters.neoforge.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.neoforge.api.data.v2.AbstractBuiltInDataProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeProvider extends AbstractBuiltInDataProvider.DamageTypes {

    public ModDamageTypeProvider(ForgeDataProviderContext context) {
        super(context);
    }

    @Override
    protected void addBootstrap(BootstapContext<DamageType> bootstapContext) {
        this.add(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("player", 0.1F));
        this.add(ModRegistry.MUTANT_SKELETON_SHATTER_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        this.add(ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE, new DamageType("thrown", 0.1F));
    }
}
