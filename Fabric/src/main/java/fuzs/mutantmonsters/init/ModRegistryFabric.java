package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.entity.SkullSpirit;
import fuzs.mutantmonsters.entity.projectile.MutantArrowEntity;
import fuzs.mutantmonsters.world.item.ArmorBlockItem;
import fuzs.mutantmonsters.world.item.MutantSkeletonArmorMaterial;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModRegistryFabric {
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(MutantMonsters.MOD_ID);
    public static final RegistryReference<EntityType<MutantArrowEntity>> MUTANT_ARROW_ENTITY_TYPE = REGISTRY.registerEntityType("mutant_arrow", () -> FabricEntityTypeBuilder.<MutantArrowEntity>create(MobCategory.MISC, MutantArrowEntity::new).forceTrackedVelocityUpdates(false).disableSaving().build());
    public static final RegistryReference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRY.registerEntityType("skull_spirit", () -> FabricEntityTypeBuilder.<SkullSpirit>create(MobCategory.MISC, SkullSpirit::new).trackRangeChunks(10).trackedUpdateRate(20).forceTrackedVelocityUpdates(false).dimensions(EntityDimensions.scalable(0.1F, 0.1F)).build());
    public static final RegistryReference<Item> MUTANT_SKELETON_SKULL_ITEM = REGISTRY.registerItem("mutant_skeleton_skull", () -> new ArmorBlockItem(MutantSkeletonArmorMaterial.INSTANCE, ModRegistry.MUTANT_SKELETON_SKULL_BLOCK.get(), ModRegistry.MUTANT_SKELETON_WALL_SKULL_BLOCK.get(), new FabricItemSettings().group(ModRegistry.CREATIVE_MODE_TAB).rarity(Rarity.UNCOMMON).equipmentSlot(stack -> EquipmentSlot.HEAD)));

    public static void touch() {

    }
}
