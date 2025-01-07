package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractRegistriesDatapackGenerator;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeRegistryProvider extends AbstractRegistriesDatapackGenerator<DamageType> {

    public ModDamageTypeRegistryProvider(DataProviderContext context) {
        super(Registries.DAMAGE_TYPE, context);
    }

    @Override
    public void addBootstrap(BootstrapContext<DamageType> context) {
        context.register(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("player", 0.1F));
        context.register(ModRegistry.MUTANT_SKELETON_SHATTER_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ModRegistry.MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE, new DamageType("thrown", 0.1F));
    }
}
