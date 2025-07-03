package fuzs.mutantmonsters.data.client;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.puzzleslib.api.client.data.v2.AbstractEquipmentProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.function.BiConsumer;

public class ModEquipmentProvider extends AbstractEquipmentProvider {

    public ModEquipmentProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addEquipmentAssets(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssetConsumer) {
        equipmentAssetConsumer.accept(ModItems.MUTANT_SKELETON_ARMOR_MATERIAL.assetId(),
                onlyHumanoid(ModItems.MUTANT_SKELETON_ARMOR_MATERIAL.assetId().location()));
    }
}
