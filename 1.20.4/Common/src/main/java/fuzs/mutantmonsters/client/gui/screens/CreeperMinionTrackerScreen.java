package fuzs.mutantmonsters.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.C2SCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CreeperMinionTrackerScreen extends Screen {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/gui/creeper_minion_tracker.png");
    public static final MutableComponent HEALTH_COMPONENT = Component.translatable("gui.mutantmonsters.creeper_minion_tracker.health");
    public static final MutableComponent EXPLOSION_COMPONENT = Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion");
    public static final MutableComponent BLAST_RADIUS_COMPONENT = Component.translatable("gui.mutantmonsters.creeper_minion_tracker.blast_radius");
    private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("#.0"), decimalFormat -> {
        decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });

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
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 75, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(this.alwaysShowName(), (button) -> {
            this.alwaysShowName = !this.alwaysShowName;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 1, this.alwaysShowName));
            button.setMessage(this.alwaysShowName());
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 51, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(this.canRideOnShoulder(), (button) -> {
            this.canRideOnShoulder = !this.canRideOnShoulder;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 2, this.canRideOnShoulder));
            button.setMessage(this.canRideOnShoulder());
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 27, buttonWidth * 2 + 4, 20).build());
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 4210752, false);
        this.name.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, HEALTH_COMPONENT, (this.leftPos + 13), (this.topPos + 31), 4210752, false);
        guiGraphics.drawString(this.font, EXPLOSION_COMPONENT, (this.leftPos + 13), (this.topPos + 51), 4210752, false);
        guiGraphics.drawString(this.font, BLAST_RADIUS_COMPONENT, (this.leftPos + 13), (this.topPos + 71), 4210752, false);
        guiGraphics.drawCenteredString(this.font, String.format("%s / %s", DECIMAL_FORMAT.format(this.creeperMinion.getHealth()), DECIMAL_FORMAT.format(this.creeperMinion.getMaxHealth())), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 31, 16777215);
        guiGraphics.drawCenteredString(this.font, this.creeperMinion.canExplodeContinuously() ? Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion.continuous") : Component.translatable("gui.mutantmonsters.creeper_minion_tracker.explosion.one_time"), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 51, 16777215);
        guiGraphics.drawCenteredString(this.font, DECIMAL_FORMAT.format(this.creeperMinion.getExplosionRadius()), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 71, 16777215);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 15, this.topPos + 16, 0, 166, 146, 5);
        float healthProgress = Mth.clamp(this.creeperMinion.getHealth() / this.creeperMinion.getMaxHealth(), 0.0F, 1.0F);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 15, this.topPos + 16, 0, 171, (int) (healthProgress * 146.0F), 5);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
