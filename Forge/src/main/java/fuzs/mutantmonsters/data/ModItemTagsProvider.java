package fuzs.mutantmonsters.data;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends AbstractTagProvider.Items {

    public ModItemTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(Tags.Items.ARMORS_BOOTS).add(ModRegistry.MUTANT_SKELETON_BOOTS_ITEM.get());
        this.tag(Tags.Items.ARMORS_CHESTPLATES).add(ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.get());
        this.tag(Tags.Items.ARMORS_HELMETS).add(ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get());
        this.tag(Tags.Items.ARMORS_LEGGINGS).add(ModRegistry.MUTANT_SKELETON_LEGGINGS_ITEM.get());
    }
}
