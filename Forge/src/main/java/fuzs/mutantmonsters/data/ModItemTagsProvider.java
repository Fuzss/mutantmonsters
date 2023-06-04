package fuzs.mutantmonsters.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends AbstractTagProvider.Items {

    public ModItemTagsProvider(DataGenerator packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
    }

    @Override
    protected void addTags() {
//        this.tag(Tags.Items.ARMORS_BOOTS).add(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.get());
//        this.tag(Tags.Items.ARMORS_CHESTPLATES).add(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.get());
//        this.tag(Tags.Items.ARMORS_HELMETS).add(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get());
//        this.tag(Tags.Items.ARMORS_LEGGINGS).add(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.get());
    }
}
