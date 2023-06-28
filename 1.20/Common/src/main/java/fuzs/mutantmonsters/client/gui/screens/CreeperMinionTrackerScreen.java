package fuzs.mutantmonsters.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreeperMinionTrackerScreen extends Screen {
    private static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/gui/creeper_minion_tracker.png");
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private final CreeperMinion creeperMinion;
    private int leftPos;
    private int topPos;
    private EditBox name;
    private boolean canRideOnShoulder;
    private boolean canDestroyBlocks;
    private boolean alwaysShowName;
    private int titleLabelX;
    private int titleLabelY;

    public CreeperMinionTrackerScreen(CreeperMinion creeperMinion) {
        super(creeperMinion.getType().getDescription());
        this.creeperMinion = creeperMinion;
    }

    private static Component onOffComponent(String translationKey, boolean on) {
        return Component.translatable("gui.mutantmonsters.creeper_minion_tracker." + translationKey).append(": ").append(on ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    @Override
    protected void init() {
        this.canDestroyBlocks = this.creeperMinion.canDestroyBlocks();
        this.alwaysShowName = this.creeperMinion.isCustomNameVisible();
        this.canRideOnShoulder = this.creeperMinion.canRideOnShoulder();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, this.leftPos + 5, this.topPos - 24, this.imageHeight, 20, Component.empty());
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setMaxLength(50);
        this.name.setResponder(this::onNameChanged);
        this.name.setValue(this.creeperMinion.getName().getString());
        this.addWidget(this.name);
        int buttonWidth = this.imageWidth / 2 - 10;
        this.addRenderableWidget(Button.builder(this.canDestroyBlocks(), (button) -> {
            this.canDestroyBlocks = !this.canDestroyBlocks;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 0, this.canDestroyBlocks));
            button.setMessage(this.canDestroyBlocks());
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 78, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(this.alwaysShowName(), (button) -> {
            this.alwaysShowName = !this.alwaysShowName;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 1, this.alwaysShowName));
            button.setMessage(this.alwaysShowName());
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 54, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(this.canRideOnShoulder(), (button) -> {
            this.canRideOnShoulder = !this.canRideOnShoulder;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 2, this.canRideOnShoulder));
            button.setMessage(this.canRideOnShoulder());
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 30, buttonWidth * 2 + 4, 20).build());
        if (!this.creeperMinion.isOwnedBy(this.minecraft.player)) {
            this.renderables.stream().filter(AbstractWidget.class::isInstance).map(AbstractWidget.class::cast).forEach(widget -> widget.active = false);
        }
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return this.name.keyPressed(keyCode, scanCode, modifiers) || this.name.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        if (!this.creeperMinion.isAlive()) {
            this.minecraft.player.closeContainer();
        }
        this.name.tick();
    }

    private void onNameChanged(String input) {
        input = input.trim();
        if (!input.equals(this.creeperMinion.getName().getString())) {
            this.creeperMinion.setCustomName(Component.literal(input));
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionNameMessage(this.creeperMinion, input));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String string = this.name.getValue();
        this.init(minecraft, width, height);
        this.name.setValue(string);
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int health = (int) (this.creeperMinion.getHealth() * 150.0F / this.creeperMinion.getMaxHealth());
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 13, this.topPos + 16, 0, this.imageHeight, health, 6);
        guiGraphics.drawString(this.font, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 4210752, false);
        this.name.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawString(this.font, Component.translatable("gui.mutantmonsters.creeper_minion_tracker.health"), (this.leftPos + 13), (this.topPos + 28), 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion"), (this.leftPos + 13), (this.topPos + 48), 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("gui.mutantmonsters.creeper_minion_tracker.blast_radius"), (this.leftPos + 13), (this.topPos + 68), 4210752, false);
        StringBuilder builder = new StringBuilder();
        builder.append(this.creeperMinion.getHealth() / 2.0F).append(" / ").append(this.creeperMinion.getMaxHealth() / 2.0F);
        guiGraphics.drawCenteredString(this.font, builder.toString(), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 30, 16777215);
        guiGraphics.drawCenteredString(this.font, this.creeperMinion.canExplodeContinuously() ? Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion.continuous") : Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion.one_time"), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 50, 16777215);
        int temp = (int) (this.creeperMinion.getExplosionRadius() * 10.0F);
        builder = new StringBuilder().append(temp / 10.0F);
        guiGraphics.drawCenteredString(this.font, builder.toString(), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 70, 16777215);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
