package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class ModTags {
    static final TagFactory TAGS = TagFactory.make(MutantMonsters.MOD_ID);
    public static final TagKey<Biome> WITHOUT_MUTANT_ZOMBIE_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_zombie_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_SKELETON_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_skeleton_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_ENDERMAN_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_enderman_spawns");
    public static final TagKey<Biome> WITHOUT_MUTANT_CREEPER_SPAWNS_BIOME_TAG = TAGS.registerBiomeTag(
            "without_mutant_creeper_spawns");
    public static final TagKey<EntityType<?>> SPIDER_PIG_TARGETS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag(
            "spider_pig_targets");
    public static final TagKey<EntityType<?>> MUTANTS_ENTITY_TYPE_TAG = TAGS.registerEntityTypeTag("mutants");
    public static final TagKey<Item> REPAIRS_SKELETON_ARMOR_ITEM_TAG = TAGS.registerItemTag("repairs_skeleton_armor");
    public static final TagKey<Block> ENDERSOUL_HAND_HOLDABLE_IMMUNE_BLOCK_TAG = TAGS.registerBlockTag(
            "endersoul_hand_holdable_immune");
    public static final TagKey<Block> MUTANT_ENDERMAN_HOLDABLE_IMMUNE_BLOCK_TAG = TAGS.registerBlockTag(
            "mutant_enderman_holdable_immune");

    public static void bootstrap() {
        // NO-OP
    }
}
