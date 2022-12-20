package fuzs.mutantmonsters.client.animationapi;

public class Animation {
    public static final Animation NONE = new Animation(0);
    private final int duration;

    public Animation(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return this.duration;
    }
}
