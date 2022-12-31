package fuzs.mutantmonsters.client;

import fuzs.mutantmonsters.client.gui.screen.CreeperMinionTrackerScreen;
import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import net.minecraft.client.Minecraft;

public class CreeperMinionScreenHandler {

    public static void displayCreeperMinionTrackerGUI(CreeperMinionEntity creeperMinion) {
        Minecraft.getInstance().setScreen(new CreeperMinionTrackerScreen(creeperMinion));
    }
}
