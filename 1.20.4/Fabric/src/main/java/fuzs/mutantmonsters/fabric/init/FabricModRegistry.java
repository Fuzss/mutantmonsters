package fuzs.mutantmonsters.fabric.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class FabricModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(MutantMonsters.MOD_ID);
    public static final Holder.Reference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.register(Registries.ENTITY_TYPE, "mutant_arrow", () -> FabricEntityTypeBuilder.<MutantArrow>create(MobCategory.MISC, MutantArrow::new).forceTrackedVelocityUpdates(false).disableSaving().build());
    public static final Holder.Reference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.register(Registries.ENTITY_TYPE, "skull_spirit", () -> FabricEntityTypeBuilder.<SkullSpirit>create(MobCategory.MISC, SkullSpirit::new).trackRangeChunks(10).trackedUpdateRate(20).forceTrackedVelocityUpdates(false).dimensions(EntityDimensions.scalable(0.1F, 0.1F)).build());

    public static void touch() {

    }
}
