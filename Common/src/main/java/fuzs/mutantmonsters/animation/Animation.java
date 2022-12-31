package fuzs.mutantmonsters.animation;

public record Animation(int duration) {
    public static final Animation NONE = new Animation(0);
}
