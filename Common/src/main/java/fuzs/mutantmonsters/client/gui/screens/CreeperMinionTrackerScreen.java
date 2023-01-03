package fuzs.mutantmonsters.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.client.gui.screens.CommonScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreeperMinionTrackerScreen extends Screen {
    private static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/gui/creeper_minion_tracker.png");
    private final int imageWidth = 176;
    private final int imageHeight = 166;

    private int leftPos;
    private int topPos;
    private EditBox name;
    private final CreeperMinion creeperMinion;
    private boolean canRideOnShoulder;
    private boolean canDestroyBlocks;
    private boolean alwaysShowName;
    private int titleLabelX;
    private int titleLabelY;

    public CreeperMinionTrackerScreen(CreeperMinion creeperMinion) {
        super(creeperMinion.getType().getDescription());
        this.creeperMinion = creeperMinion;
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
        this.addRenderableWidget(new Button(this.leftPos + 8, this.topPos + this.imageHeight - 78, buttonWidth * 2 + 4, 20, this.canDestroyBlocks(), (button) -> {
            this.canDestroyBlocks = !this.canDestroyBlocks;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 0, this.canDestroyBlocks));
            button.setMessage(this.canDestroyBlocks());
        }));
        this.addRenderableWidget(new Button(this.leftPos + 8, this.topPos + this.imageHeight - 54, buttonWidth * 2 + 4, 20, this.alwaysShowName(), (button) -> {
            this.alwaysShowName = !this.alwaysShowName;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 1, this.alwaysShowName));
            button.setMessage(this.alwaysShowName());
        }));
        this.addRenderableWidget(new Button(this.leftPos + 8, this.topPos + this.imageHeight - 30, buttonWidth * 2 + 4, 20, this.canRideOnShoulder(), (button) -> {
            this.canRideOnShoulder = !this.canRideOnShoulder;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 2, this.canRideOnShoulder));
            button.setMessage(this.canRideOnShoulder());
        }));
        if (!this.creeperMinion.isOwnedBy(this.minecraft.player)) {
            CommonScreens.INSTANCE.getRenderableButtons(this).stream().filter(widget -> widget instanceof AbstractWidget).forEach(widget -> ((AbstractWidget) widget).active = false);
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

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
        return Component.translatable("gui.mutantmonsters.creeper_minion_tracker." + translationKey).append(": ").append(on ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int health = (int)(this.creeperMinion.getHealth() * 150.0F / this.creeperMinion.getMaxHealth());
        this.blit(matrixStack, this.leftPos + 13, this.topPos + 16, 0, this.imageHeight, health, 6);
        this.font.draw(matrixStack, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 4210752);
        this.name.render(matrixStack, mouseX, mouseY, partialTicks);
        this.font.draw(matrixStack, Component.translatable("gui.mutantmonsters.creeper_minion_tracker.health"), (float)(this.leftPos + 13), (float)(this.topPos + 28), 4210752);
        this.font.draw(matrixStack, Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion"), (float)(this.leftPos + 13), (float)(this.topPos + 48), 4210752);
        this.font.draw(matrixStack, Component.translatable("gui.mutantmonsters.creeper_minion_tracker.blast_radius"), (float)(this.leftPos + 13), (float)(this.topPos + 68), 4210752);
        StringBuilder sb = new StringBuilder();
        sb.append(this.creeperMinion.getHealth() / 2.0F).append(" / ").append(this.creeperMinion.getMaxHealth() / 2.0F);
        drawCenteredString(matrixStack, this.font, sb.toString(), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 30, 16777215);
        drawCenteredString(matrixStack, this.font, this.creeperMinion.canExplodeContinuously() ? Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion.continuous") : Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion.one_time"), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 50, 16777215);
        int temp = (int)(this.creeperMinion.getExplosionRadius() * 10.0F);
        sb = (new StringBuilder()).append((float)temp / 10.0F);
        drawCenteredString(matrixStack, this.font, sb.toString(), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 70, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
