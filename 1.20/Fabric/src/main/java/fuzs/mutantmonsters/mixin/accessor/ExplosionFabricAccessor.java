package fuzs.mutantmonsters.mixin.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionFabricAccessor {

    @Accessor("x")
    double mutantmonsters$getX();

    @Accessor("y")
    double mutantmonsters$getY();

    @Accessor("z")
    double mutantmonsters$getZ();

    @Accessor("source")
    @Nullable
    Entity mutantmonsters$getExploder();
}
