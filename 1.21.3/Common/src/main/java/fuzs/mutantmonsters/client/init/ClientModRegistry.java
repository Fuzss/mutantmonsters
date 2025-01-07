package fuzs.mutantmonsters.client.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ClientModRegistry {
    static final ModelLayerFactory REGISTRY = ModelLayerFactory.from(MutantMonsters.MOD_ID);
    public static final ModelLayerLocation MUTANT_SKELETON_SKULL = REGISTRY.register("mutant_skeleton_skull");
    public static final ModelLayerLocation CREEPER_MINION_EGG = REGISTRY.register("creeper_minion_egg");
    public static final ModelLayerLocation CREEPER_MINION_EGG_ARMOR = REGISTRY.register("creeper_minion_egg", "armor");
    public static final ModelLayerLocation CREEPER_MINION = REGISTRY.register("creeper_minion");
    public static final ModelLayerLocation CREEPER_MINION_ARMOR = REGISTRY.register("creeper_minion", "armor");
    public static final ModelLayerLocation CREEPER_MINION_SHOULDER = REGISTRY.register("creeper_minion_shoulder");
    public static final ModelLayerLocation CREEPER_MINION_SHOULDER_ARMOR = REGISTRY.register("creeper_minion_shoulder", "armor");
    public static final ModelLayerLocation ENDERSOUL_CLONE = REGISTRY.register("endersoul_clone");
    public static final ModelLayerLocation ENDERSOUL_FRAGMENT = REGISTRY.register("endersoul_fragment");
    public static final ModelLayerLocation ENDERSOUL_HAND_LEFT = REGISTRY.register("endersoul_hand", "left");
    public static final ModelLayerLocation ENDERSOUL_HAND_RIGHT = REGISTRY.register("endersoul_hand", "right");
    public static final ModelLayerLocation MUTANT_ARROW = REGISTRY.register("mutant_arrow");
    public static final ModelLayerLocation MUTANT_CREEPER = REGISTRY.register("mutant_creeper");
    public static final ModelLayerLocation MUTANT_CREEPER_ARMOR = REGISTRY.register("mutant_creeper", "armor");
    public static final ModelLayerLocation MUTANT_CROSSBOW = REGISTRY.register("mutant_crossbow");
    public static final ModelLayerLocation MUTANT_ENDERMAN = REGISTRY.register("mutant_enderman");
    public static final ModelLayerLocation ENDERMAN_CLONE = REGISTRY.register("enderman_clone");
    public static final ModelLayerLocation MUTANT_SKELETON = REGISTRY.register("mutant_skeleton");
    public static final ModelLayerLocation MUTANT_SKELETON_PART = REGISTRY.register("mutant_skeleton_part");
    public static final ModelLayerLocation MUTANT_SKELETON_PART_SPINE = REGISTRY.register("mutant_skeleton_part", "spine");
    public static final ModelLayerLocation MUTANT_SNOW_GOLEM = REGISTRY.register("mutant_snow_golem");
    public static final ModelLayerLocation MUTANT_SNOW_GOLEM_HEAD = REGISTRY.register("mutant_snow_golem_head");
    public static final ModelLayerLocation MUTANT_ZOMBIE = REGISTRY.register("mutant_zombie");
    public static final ModelLayerLocation SPIDER_PIG = REGISTRY.register("spider_pig");

    public static LayerDefinition createSkullModelLayer() {
        MeshDefinition mesh = SkullModel.createHeadModel();
        PartDefinition root = mesh.getRoot();
        root.getChild("head").addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, -0.2F, 3.5F, 0.09817477F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }
}
