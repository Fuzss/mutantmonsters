package fuzs.mutantmonsters.world.entity;

import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.resources.ResourceLocation;

public record EntityAnimation(ResourceLocation resourceLocation, int duration) {
    public static final EntityAnimation NONE = new EntityAnimation("none", 0);

    public EntityAnimation(String path, int duration) {
        this(MutantMonsters.id(path), duration);
    }
}
