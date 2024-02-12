package fuzs.mutantmonsters.neoforge.data.tags;

import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG)
                .add(Blocks.BARRIER,
                        Blocks.BEDROCK,
                        Blocks.END_PORTAL,
                        Blocks.END_PORTAL_FRAME,
                        Blocks.END_GATEWAY,
                        Blocks.COMMAND_BLOCK,
                        Blocks.REPEATING_COMMAND_BLOCK,
                        Blocks.CHAIN_COMMAND_BLOCK,
                        Blocks.STRUCTURE_BLOCK,
                        Blocks.JIGSAW,
                        Blocks.MOVING_PISTON,
                        Blocks.OBSIDIAN,
                        Blocks.CRYING_OBSIDIAN,
                        Blocks.RESPAWN_ANCHOR,
                        Blocks.REINFORCED_DEEPSLATE
                );
        this.tag(ModRegistry.MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG)
                .add(Blocks.BARRIER,
                        Blocks.BEDROCK,
                        Blocks.END_PORTAL,
                        Blocks.END_PORTAL_FRAME,
                        Blocks.END_GATEWAY,
                        Blocks.COMMAND_BLOCK,
                        Blocks.REPEATING_COMMAND_BLOCK,
                        Blocks.CHAIN_COMMAND_BLOCK,
                        Blocks.STRUCTURE_BLOCK,
                        Blocks.JIGSAW,
                        Blocks.MOVING_PISTON,
                        Blocks.OBSIDIAN,
                        Blocks.CRYING_OBSIDIAN,
                        Blocks.RESPAWN_ANCHOR,
                        Blocks.REINFORCED_DEEPSLATE
                );
    }
}
