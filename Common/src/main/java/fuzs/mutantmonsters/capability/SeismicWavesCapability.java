package fuzs.mutantmonsters.capability;

import fuzs.mutantmonsters.core.SeismicWave;
import fuzs.puzzleslib.capability.data.CapabilityComponent;

import java.util.Queue;

public interface SeismicWavesCapability extends CapabilityComponent {

    Queue<SeismicWave> getSeismicWaves();
}
