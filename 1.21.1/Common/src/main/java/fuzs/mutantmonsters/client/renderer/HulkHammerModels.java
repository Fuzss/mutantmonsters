package fuzs.mutantmonsters.client.renderer;

import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class HulkHammerModels {
    public static final ResourceLocation HULK_HAMMER_IN_HAND_MODEL = MutantMonsters.id("item/hulk_hammer_in_hand");
    public static final ModelResourceLocation HULK_HAMMER_ITEM_MODEL = new ModelResourceLocation(
            MutantMonsters.id("hulk_hammer"), "inventory");
}
