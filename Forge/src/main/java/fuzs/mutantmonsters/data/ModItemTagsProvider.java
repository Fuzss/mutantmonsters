package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator, new BlockTagsProvider(dataGenerator, modId, fileHelper), modId, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(Tags.Items.ARMORS_BOOTS).add(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.get());
        this.tag(Tags.Items.ARMORS_CHESTPLATES).add(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.get());
        this.tag(Tags.Items.ARMORS_HELMETS).add(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get());
        this.tag(Tags.Items.ARMORS_LEGGINGS).add(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.get());
    }
}
