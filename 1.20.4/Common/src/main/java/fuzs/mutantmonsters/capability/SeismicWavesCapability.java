package fuzs.mutantmonsters.capability;

import fuzs.mutantmonsters.core.SeismicWave;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;

import java.util.Queue;

public interface SeismicWavesCapability extends CapabilityComponent {

    Queue<SeismicWave> seismicWaves();
}
