package fuzs.mutantmonsters.client.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.client.model.geom.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;

public class ClientModRegistry {
    private static final ModelLayerRegistry REGISTRY = ClientFactories.INSTANCE.modelLayerRegistration(MutantMonsters.MOD_ID);
    public static final ModelLayerLocation ENDER_SOUL_HAND_LEFT = REGISTRY.register("ender_soul_hand", "left");
    public static final ModelLayerLocation ENDER_SOUL_HAND_RIGHT = REGISTRY.register("ender_soul_hand", "right");
}
