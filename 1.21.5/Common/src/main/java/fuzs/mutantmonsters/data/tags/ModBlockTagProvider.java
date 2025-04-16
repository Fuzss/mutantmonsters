package fuzs.mutantmonsters.data.tags;

import fuzs.mutantmonsters.init.ModTags;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class ModBlockTagProvider extends AbstractTagProvider<Block> {

    public ModBlockTagProvider(DataProviderContext context) {
        super(Registries.BLOCK, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG).addTag(BlockTags.WITHER_IMMUNE);
        this.tag(ModTags.MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG).addTag(BlockTags.WITHER_IMMUNE);
    }
}
