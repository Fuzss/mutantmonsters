package fuzs.mutantmonsters.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import fuzs.mutantmonsters.packet.CreeperMinionTrackerPacket;
import fuzs.mutantmonsters.packet.MBPacketHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreeperMinionTrackerScreen extends Screen {
    private static final ResourceLocation TEXTURE = MutantMonsters.prefix("textures/gui/creeper_minion_tracker.png");
    private final int xSize = 176;
    private final int ySize = 166;
    private int guiX;
    private int guiY;
    private final CreeperMinionEntity creeperMinion;
    private boolean canRideOnShoulder;
    private boolean canDestroyBlocks;
    private boolean alwaysShowName;

    public CreeperMinionTrackerScreen(CreeperMinionEntity creeperMinion) {
        super(creeperMinion.getDisplayName());
        this.creeperMinion = creeperMinion;
    }

    @Override
    protected void init() {
        this.canDestroyBlocks = this.creeperMinion.canDestroyBlocks();
        this.alwaysShowName = this.creeperMinion.isCustomNameVisible();
        this.canRideOnShoulder = this.creeperMinion.canRideOnShoulder();
        this.guiX = (this.width - 176) / 2;
        this.guiY = (this.height - 166) / 2;
        int buttonWidth = 176 / 2 - 10;
        this.addRenderableWidget(new Button(this.guiX + 8, this.guiY + 166 - 78, buttonWidth * 2 + 4, 20, this.canDestroyBlocks(), (button) -> {
            this.canDestroyBlocks = !this.canDestroyBlocks;
            MBPacketHandler.INSTANCE.sendToServer(new CreeperMinionTrackerPacket(this.creeperMinion, 0, this.canDestroyBlocks));
            button.setMessage(this.canDestroyBlocks());
        }));
        this.addRenderableWidget(new Button(this.guiX + 8, this.guiY + 166 - 54, buttonWidth * 2 + 4, 20, this.alwaysShowName(), (button) -> {
            this.alwaysShowName = !this.alwaysShowName;
            MBPacketHandler.INSTANCE.sendToServer(new CreeperMinionTrackerPacket(this.creeperMinion, 1, this.alwaysShowName));
            button.setMessage(this.alwaysShowName());
        }));
        this.addRenderableWidget(new Button(this.guiX + 8, this.guiY + 166 - 30, buttonWidth * 2 + 4, 20, this.canRideOnShoulder(), (button) -> {
            this.canRideOnShoulder = !this.canRideOnShoulder;
            MBPacketHandler.INSTANCE.sendToServer(new CreeperMinionTrackerPacket(this.creeperMinion, 2, this.canRideOnShoulder));
            button.setMessage(this.canRideOnShoulder());
        }));
        if (!this.creeperMinion.isOwnedBy(this.minecraft.player)) {
            this.renderables.stream().filter(widget -> widget instanceof AbstractWidget).forEach(widget -> ((AbstractWidget) widget).active = false);
        }

    }

    @Override
    public void tick() {
        if (!this.creeperMinion.isAlive()) {
            this.minecraft.player.closeContainer();
        }

    }

    private Component alwaysShowName() {
        return onOffComponent("show_name", this.alwaysShowName);
    }

    private Component canDestroyBlocks() {
        return onOffComponent("destroys_blocks", this.canDestroyBlocks);
    }

    private Component canRideOnShoulder() {
        return onOffComponent("ride_on_shoulder", this.canRideOnShoulder);
    }

    private static Component onOffComponent(String translationKey, boolean on) {
        return Component.translatable("gui.mutantbeasts.creeper_minion_tracker." + translationKey).append(": ").append(on ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(matrixStack, this.guiX, this.guiY, 0, 0, 176, 166);
        int health = (int)(this.creeperMinion.getHealth() * 150.0F / this.creeperMinion.getMaxHealth());
        this.blit(matrixStack, this.guiX + 13, this.guiY + 16, 0, 166, health, 6);
        this.font.draw(matrixStack, this.title.getString(), (float)(this.guiX + 13), (float)(this.guiY + 5), 4210752);
        this.font.draw(matrixStack, Component.translatable("gui.mutantbeasts.creeper_minion_tracker.health"), (float)(this.guiX + 13), (float)(this.guiY + 28), 4210752);
        this.font.draw(matrixStack, Component.translatable("gui.mutantbeasts.creeper_minion_tracker.explosion"), (float)(this.guiX + 13), (float)(this.guiY + 48), 4210752);
        this.font.draw(matrixStack, Component.translatable("gui.mutantbeasts.creeper_minion_tracker.blast_radius"), (float)(this.guiX + 13), (float)(this.guiY + 68), 4210752);
        StringBuilder sb = new StringBuilder();
        sb.append(this.creeperMinion.getHealth() / 2.0F).append(" / ").append(this.creeperMinion.getMaxHealth() / 2.0F);
        drawCenteredString(matrixStack, this.font, sb.toString(), this.guiX + 176 / 2 + 38, this.guiY + 30, 16777215);
        drawCenteredString(matrixStack, this.font, this.creeperMinion.canExplodeContinuously() ? Component.translatable("gui.mutantbeasts.creeper_minion_tracker.explosion.continuous") : Component.translatable("gui.mutantbeasts.creeper_minion_tracker.explosion.one_time"), this.guiX + 176 / 2 + 38, this.guiY + 50, 16777215);
        int temp = (int)(this.creeperMinion.getExplosionRadius() * 10.0F);
        sb = (new StringBuilder()).append((float)temp / 10.0F);
        drawCenteredString(matrixStack, this.font, sb.toString(), this.guiX + 176 / 2 + 38, this.guiY + 70, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
