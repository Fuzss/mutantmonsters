package fuzs.mutantmonsters.init;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.tileentity.MBSkullTileEntity;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModRegistry {
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(MutantMonsters.MOD_ID);
    // TODO add blocks
    public static final RegistryReference<BlockEntityType<MBSkullTileEntity>> SKULL_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityTypeBuilder("skull", () -> ModBlockEntityTypeBuilder.of(MBSkullTileEntity::new));
    public static final RegistryReference<SimpleParticleType> ENDERSOUL_PARTICLE_TYPE = REGISTRY.register(Registry.PARTICLE_TYPE_REGISTRY, "endersoul", () -> new SimpleParticleType(false));
    public static final RegistryReference<SimpleParticleType> SKULL_SPIRIT_PARTICLE_TYPE = REGISTRY.register(Registry.PARTICLE_TYPE_REGISTRY, "skull_spirit", () -> new SimpleParticleType(true));

    public static final TagKey<Block> MUTANT_ENDERMAN_HOLABLE = BlockTags.create(MutantMonsters.prefix("mutant_enderman_holdable"));
    public static final TagKey<Block> ENDERSOUL_HAND_HOLDABLE = BlockTags.create(MutantMonsters.prefix("endersoul_hand_holdable"));

    public static void touch() {

    }
}
