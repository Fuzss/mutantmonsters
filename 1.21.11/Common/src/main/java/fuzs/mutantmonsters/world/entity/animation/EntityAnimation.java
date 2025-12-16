package fuzs.mutantmonsters.world.entity.animation;

import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.resources.Identifier;

public record EntityAnimation(Identifier identifier, int duration) {
    public static final EntityAnimation NONE = new EntityAnimation("none", 0);

    public EntityAnimation(String path, int duration) {
        this(MutantMonsters.id(path), duration);
    }
}
