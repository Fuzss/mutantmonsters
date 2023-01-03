package fuzs.mutantmonsters.capability;

import com.google.common.collect.Queues;
import fuzs.mutantmonsters.core.SeismicWave;
import net.minecraft.nbt.CompoundTag;

import java.util.Queue;

public class SeismicWavesCapabilityImpl implements SeismicWavesCapability {
    private final Queue<SeismicWave> seismicWaves = Queues.newArrayDeque();

    @Override
    public Queue<SeismicWave> getSeismicWaves() {
        return this.seismicWaves;
    }

    @Override
    public void write(CompoundTag tag) {

    }

    @Override
    public void read(CompoundTag tag) {

    }
}
