package fuzs.mutantmonsters.client.model.geom;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.api.client.init.v1.ModelLayerFactory;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.object.skull.SkullModel;

public class ModModelLayers {
    static final ModelLayerFactory REGISTRY = ModelLayerFactory.from(MutantMonsters.MOD_ID);
    public static final ModelLayerLocation MUTANT_SKELETON_SKULL = REGISTRY.registerModelLayer("mutant_skeleton_skull");
    public static final ModelLayerLocation CREEPER_MINION_EGG = REGISTRY.registerModelLayer("creeper_minion_egg");
    public static final ModelLayerLocation CREEPER_MINION_EGG_ARMOR = REGISTRY.registerModelLayer("creeper_minion_egg",
            "armor");
    public static final ModelLayerLocation CREEPER_MINION = REGISTRY.registerModelLayer("creeper_minion");
    public static final ModelLayerLocation CREEPER_MINION_ARMOR = REGISTRY.registerModelLayer("creeper_minion",
            "armor");
    public static final ModelLayerLocation CREEPER_MINION_SHOULDER = REGISTRY.registerModelLayer(
            "creeper_minion_shoulder");
    public static final ModelLayerLocation CREEPER_MINION_SHOULDER_ARMOR = REGISTRY.registerModelLayer(
            "creeper_minion_shoulder",
            "armor");
    public static final ModelLayerLocation ENDERSOUL_CLONE = REGISTRY.registerModelLayer("endersoul_clone");
    public static final ModelLayerLocation ENDERSOUL_FRAGMENT = REGISTRY.registerModelLayer("endersoul_fragment");
    public static final ModelLayerLocation ENDERSOUL_HAND_LEFT = REGISTRY.registerModelLayer("endersoul_hand", "left");
    public static final ModelLayerLocation ENDERSOUL_HAND_RIGHT = REGISTRY.registerModelLayer("endersoul_hand",
            "right");
    public static final ModelLayerLocation MUTANT_ARROW = REGISTRY.registerModelLayer("mutant_arrow");
    public static final ModelLayerLocation MUTANT_CREEPER = REGISTRY.registerModelLayer("mutant_creeper");
    public static final ModelLayerLocation MUTANT_CREEPER_ARMOR = REGISTRY.registerModelLayer("mutant_creeper",
            "armor");
    public static final ModelLayerLocation MUTANT_CROSSBOW = REGISTRY.registerModelLayer("mutant_crossbow");
    public static final ModelLayerLocation MUTANT_ENDERMAN = REGISTRY.registerModelLayer("mutant_enderman");
    public static final ModelLayerLocation ENDERMAN_CLONE = REGISTRY.registerModelLayer("enderman_clone");
    public static final ModelLayerLocation MUTANT_SKELETON = REGISTRY.registerModelLayer("mutant_skeleton");
    public static final ModelLayerLocation MUTANT_SKELETON_PART = REGISTRY.registerModelLayer("mutant_skeleton_part");
    public static final ModelLayerLocation MUTANT_SKELETON_PART_SPINE = REGISTRY.registerModelLayer(
            "mutant_skeleton_part",
            "spine");
    public static final ModelLayerLocation MUTANT_SNOW_GOLEM = REGISTRY.registerModelLayer("mutant_snow_golem");
    public static final ModelLayerLocation MUTANT_SNOW_GOLEM_HEAD = REGISTRY.registerModelLayer("mutant_snow_golem",
            "head");
    public static final ModelLayerLocation MUTANT_ZOMBIE = REGISTRY.registerModelLayer("mutant_zombie");
    public static final ModelLayerLocation SPIDER_PIG = REGISTRY.registerModelLayer("spider_pig");
    public static final ModelLayerLocation SPIDER_PIG_BABY = REGISTRY.registerModelLayer("spider_pig_baby");
    public static final ModelLayerLocation SPIDER_PIG_SADDLE = REGISTRY.registerModelLayer("spider_pig", "saddle");
    public static final ModelLayerLocation SPIDER_PIG_BABY_SADDLE = REGISTRY.registerModelLayer("spider_pig_baby",
            "saddle");

    public static LayerDefinition createSkullModelLayer() {
        MeshDefinition mesh = SkullModel.createHeadModel();
        PartDefinition root = mesh.getRoot();
        root.getChild("head")
                .addOrReplaceChild("jaw",
                        CubeListBuilder.create()
                                .texOffs(72, 0)
                                .addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.3F)),
                        PartPose.offsetAndRotation(0.0F, -0.2F, 3.5F, 0.09817477F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }
}
