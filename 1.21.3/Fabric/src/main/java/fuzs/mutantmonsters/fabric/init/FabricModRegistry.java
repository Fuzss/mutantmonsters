package fuzs.mutantmonsters.fabric.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class FabricModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(MutantMonsters.MOD_ID);
    public static final Holder.Reference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRIES.registerEntityType(
            "mutant_arrow",
            () -> EntityType.Builder.<MutantArrow>of(MutantArrow::new, MobCategory.MISC)
                    .alwaysUpdateVelocity(false)
                    .noSave());
    public static final Holder.Reference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRIES.registerEntityType(
            "skull_spirit",
            () -> EntityType.Builder.<SkullSpirit>of(SkullSpirit::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .alwaysUpdateVelocity(false)
                    .sized(0.1F, 0.1F));

    public static void bootstrap() {
        // NO-OP
    }
}
