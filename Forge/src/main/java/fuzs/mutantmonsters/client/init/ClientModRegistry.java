package fuzs.mutantmonsters.client.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.client.core.ClientFactories;
import fuzs.puzzleslib.client.model.geom.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ClientModRegistry {
    private static final ModelLayerRegistry REGISTRY = ClientFactories.INSTANCE.modelLayerRegistration(MutantMonsters.MOD_ID);
    public static final ModelLayerLocation ENDER_SOUL_HAND_LEFT = REGISTRY.register("ender_soul_hand", "left");
    public static final ModelLayerLocation ENDER_SOUL_HAND_RIGHT = REGISTRY.register("ender_soul_hand", "right");
    public static final ModelLayerLocation MUTANT_SKELETON_SKULL = REGISTRY.register("mutant_skeleton_skull");

    public static LayerDefinition createSkullModelLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -0.2F, 3.5F, 0.09817477F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }
}
