package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.client.data.v2.AbstractEquipmentProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;

public class ModEquipmentProvider extends AbstractEquipmentProvider {

    public ModEquipmentProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addEquipment() {
        ResourceLocation resourceLocation = ModItems.MUTANT_SKELETON_ARMOR_MATERIAL.assetId().location();
        this.unconditional(resourceLocation, EquipmentClientInfo.builder().addHumanoidLayers(resourceLocation).build());
    }
}
