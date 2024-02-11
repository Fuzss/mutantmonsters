package fuzs.mutantmonsters.capability;

import com.google.common.collect.Queues;
import fuzs.mutantmonsters.core.SeismicWave;

import java.util.Queue;

public record SeismicWavesCapabilityImpl(Queue<SeismicWave> seismicWaves) implements SeismicWavesCapability {

    public SeismicWavesCapabilityImpl() {
        this(Queues.newArrayDeque());
    }
}
