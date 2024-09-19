package fuzs.mutantmonsters.capability;

import com.google.common.collect.Queues;
import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import net.minecraft.world.entity.player.Player;

import java.util.Queue;

public class SeismicWavesCapability extends CapabilityComponent<Player> {
    private final Queue<SeismicWave> seismicWaves;

    public SeismicWavesCapability(Queue<SeismicWave> seismicWaves) {
        this.seismicWaves = seismicWaves;
    }

    public SeismicWavesCapability() {
        this(Queues.newArrayDeque());
    }

    public Queue<SeismicWave> getSeismicWaves() {
        return this.seismicWaves;
    }
}
