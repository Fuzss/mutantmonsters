package fuzs.mutantmonsters.proxy;

import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;

public interface Proxy {
    Proxy INSTANCE = ModLoaderEnvironment.INSTANCE.isClient() ? new ClientProxy() : new ServerProxy();

    void displayCreeperMinionTrackerGUI(CreeperMinion creeperMinion);
}
