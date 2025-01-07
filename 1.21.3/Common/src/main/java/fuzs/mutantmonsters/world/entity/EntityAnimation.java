package fuzs.mutantmonsters.world.entity;

public record EntityAnimation(int duration) {
    public static final EntityAnimation NONE = new EntityAnimation(0);
}
