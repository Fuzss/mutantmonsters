package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.item.ArmorBlockItem;
import fuzs.mutantmonsters.world.item.MutantSkeletonArmorMaterial;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import static fuzs.mutantmonsters.init.ModRegistry.REGISTRY;

public class ModRegistryFabric {
    public static final RegistryReference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.register(Registries.ENTITY_TYPE, "mutant_arrow", () -> FabricEntityTypeBuilder.<MutantArrow>create(MobCategory.MISC, MutantArrow::new).forceTrackedVelocityUpdates(false).disableSaving().build());
    public static final RegistryReference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.register(Registries.ENTITY_TYPE, "skull_spirit", () -> FabricEntityTypeBuilder.<SkullSpirit>create(MobCategory.MISC, SkullSpirit::new).trackRangeChunks(10).trackedUpdateRate(20).forceTrackedVelocityUpdates(false).dimensions(EntityDimensions.scalable(0.1F, 0.1F)).build());
    public static final RegistryReference<Item> MUTANT_SKELETON_SKULL_ITEM = REGISTRY.registerItem("mutant_skeleton_skull", () -> new ArmorBlockItem(MutantSkeletonArmorMaterial.INSTANCE, ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.get(), ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.get(), new FabricItemSettings().rarity(Rarity.UNCOMMON).equipmentSlot(stack -> EquipmentSlot.HEAD)));

    public static void touch() {

    }
}
