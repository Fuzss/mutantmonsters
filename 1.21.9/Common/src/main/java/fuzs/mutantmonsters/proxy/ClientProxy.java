package fuzs.mutantmonsters.proxy;

import fuzs.mutantmonsters.client.gui.screens.CreeperMinionTrackerScreen;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import net.minecraft.client.Minecraft;

public class ClientProxy extends ServerProxy {

    @Override
    public void displayCreeperMinionTrackerGUI(CreeperMinion creeperMinion) {
        Minecraft.getInstance().setScreen(new CreeperMinionTrackerScreen(creeperMinion));
    }
}
