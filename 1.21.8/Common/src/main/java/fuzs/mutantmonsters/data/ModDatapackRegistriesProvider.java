package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractDatapackRegistriesProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public class ModDatapackRegistriesProvider extends AbstractDatapackRegistriesProvider {

    public ModDatapackRegistriesProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBootstrap(RegistryBoostrapConsumer consumer) {
        consumer.add(Registries.DAMAGE_TYPE, ModDatapackRegistriesProvider::bootstrapDamageTypes);
    }

    static void bootstrapDamageTypes(BootstrapContext<DamageType> context) {
        context.register(ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("player", 0.1F));
        context.register(ModRegistry.MUTANT_SKELETON_SHATTER_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ModRegistry.MUTANT_ZOMBIE_SEISMIC_WAVE_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ModRegistry.PIERCING_MOB_ATTACK_DAMAGE_TYPE, new DamageType("mob", 0.1F));
        context.register(ModRegistry.ENDERSOUL_FRAGMENT_EXPLOSION_DAMAGE_TYPE, new DamageType("thrown", 0.1F));
    }
}
