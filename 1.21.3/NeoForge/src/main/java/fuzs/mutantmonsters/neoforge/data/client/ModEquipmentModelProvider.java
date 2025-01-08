package fuzs.mutantmonsters.neoforge.data.client;

import fuzs.mutantmonsters.init.ModItems;
import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractEquipmentModelProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.EquipmentModel;

public class ModEquipmentModelProvider extends AbstractEquipmentModelProvider {

    public ModEquipmentModelProvider(NeoForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addEquipmentModels() {
        ResourceLocation resourceLocation = ModItems.MUTANT_SKELETON_ARMOR_MATERIAL.modelId();
        this.unconditional(resourceLocation, EquipmentModel.builder().addHumanoidLayers(resourceLocation).build());
    }
}
