package fuzs.mutantmonsters.proxy;

import fuzs.mutantmonsters.client.gui.screens.CreeperMinionTrackerScreen;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ClientProxy extends ServerProxy {

    @Override
    public void displayCreeperMinionTrackerGUI(CreeperMinion creeperMinion) {
        Minecraft.getInstance().setScreen(new CreeperMinionTrackerScreen(creeperMinion));
    }

    @Override
    public void showDismountMessage() {
        Minecraft minecraft = Minecraft.getInstance();
        Component component = Component.translatable("mount.onboard", minecraft.options.keyShift.getTranslatedKeyMessage());
        minecraft.gui.setOverlayMessage(component, false);
        minecraft.getNarrator().sayNow(component);
    }
}
