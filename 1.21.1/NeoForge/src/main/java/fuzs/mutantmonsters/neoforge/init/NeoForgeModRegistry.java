package fuzs.mutantmonsters.neoforge.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.neoforge.world.item.HulkHammerNeoForgeItem;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.projectile.MutantArrow;
import fuzs.mutantmonsters.world.item.HulkHammerItem;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class NeoForgeModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(MutantMonsters.MOD_ID);
    public static final Holder.Reference<EntityType<MutantArrow>> MUTANT_ARROW_ENTITY_TYPE = REGISTRIES.registerEntityType(
            "mutant_arrow", () -> EntityType.Builder.<MutantArrow>of(MutantArrow::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(false)
                    .noSave());
    public static final Holder.Reference<EntityType<SkullSpirit>> SKULL_SPIRIT_ENTITY_TYPE = REGISTRIES.registerEntityType(
            "skull_spirit", () -> EntityType.Builder.<SkullSpirit>of(SkullSpirit::new, MobCategory.MISC)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .setShouldReceiveVelocityUpdates(false)
                    .sized(0.1F, 0.1F));
    public static final Holder.Reference<Item> HULK_HAMMER_ITEM = REGISTRIES.registerItem("hulk_hammer",
            () -> new HulkHammerNeoForgeItem(new Item.Properties().durability(64)
                    .rarity(Rarity.UNCOMMON)
                    .attributes(HulkHammerItem.createAttributes())
                    .component(DataComponents.TOOL, HulkHammerItem.createToolProperties()))
    );

    public static void touch() {
        // NO-OP
    }
}
