package fuzs.mutantmonsters.client.gui.screens;

import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.network.client.ServerboundCreeperMinionNameMessage;
import fuzs.mutantmonsters.network.client.ServerboundCreeperMinionTrackerMessage;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CreeperMinionTrackerScreen extends Screen {
    public static final Identifier TEXTURE_LOCATION = MutantMonsters.id("textures/gui/creeper_minion_tracker.png");
    public static final Component HEALTH_COMPONENT = createComponent("health");
    public static final Component EXPLOSION_COMPONENT = createComponent("explosion");
    public static final Component BLAST_RADIUS_COMPONENT = createComponent("blast_radius");
    public static final Component CONTINUOUS_EXPLOSION_COMPONENT = createComponent("explosion.continuous");
    public static final Component ONE_TIME_EXPLOSION_COMPONENT = createComponent("explosion.one_time");
    public static final Component SHOW_NAME_COMPONENT = createComponent("show_name");
    public static final Component DESTROY_BLOCKS_COMPONENT = createComponent("destroys_blocks");
    public static final Component RIDE_ON_SHOULDER_COMPONENT = createComponent("ride_on_shoulder");
    private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("0.0"),
            (DecimalFormat decimalFormat) -> {
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

    static Component createComponent(String translationKey) {
        return Component.translatable("gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker." + translationKey);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font,
                this.title,
                this.leftPos + this.titleLabelX,
                this.topPos + this.titleLabelY,
                0xFF404040,
                false);
        this.name.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, HEALTH_COMPONENT, (this.leftPos + 13), (this.topPos + 31), 0xFF404040, false);
        guiGraphics.drawString(this.font,
                EXPLOSION_COMPONENT,
                (this.leftPos + 13),
                (this.topPos + 51),
                0xFF404040,
                false);
        guiGraphics.drawString(this.font,
                BLAST_RADIUS_COMPONENT,
                (this.leftPos + 13),
                (this.topPos + 71),
                0xFF404040,
                false);
        guiGraphics.drawCenteredString(this.font,
                String.format("%s / %s",
                        DECIMAL_FORMAT.format(this.creeperMinion.getHealth()),
                        DECIMAL_FORMAT.format(this.creeperMinion.getMaxHealth())),
                this.leftPos + this.imageWidth / 2 + 38,
                this.topPos + 31,
                -1);
        guiGraphics.drawCenteredString(this.font,
                this.creeperMinion.canExplodeContinuously() ? CONTINUOUS_EXPLOSION_COMPONENT :
                        ONE_TIME_EXPLOSION_COMPONENT,
                this.leftPos + this.imageWidth / 2 + 38,
                this.topPos + 51,
                -1);
        guiGraphics.drawCenteredString(this.font,
                DECIMAL_FORMAT.format(this.creeperMinion.getExplosionRadius()),
                this.leftPos + this.imageWidth / 2 + 38,
                this.topPos + 71,
                -1);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (keyEvent.isEscape() && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else {
            return this.name.keyPressed(keyEvent) || this.name.canConsumeInput() || super.keyPressed(keyEvent);
        }
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
        this.addRenderableWidget(Button.builder(onOffComponent(DESTROY_BLOCKS_COMPONENT, this.canDestroyBlocks),
                (Button button) -> {
                    this.canDestroyBlocks = !this.canDestroyBlocks;
                    MessageSender.broadcast(new ServerboundCreeperMinionTrackerMessage(this.creeperMinion.getId(),
                            ServerboundCreeperMinionTrackerMessage.DataType.CAN_DESTROY_BLOCKS,
                            this.canDestroyBlocks));
                    button.setMessage(onOffComponent(DESTROY_BLOCKS_COMPONENT, this.canDestroyBlocks));
                }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 75, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(onOffComponent(SHOW_NAME_COMPONENT, this.alwaysShowName),
                (Button button) -> {
                    this.alwaysShowName = !this.alwaysShowName;
                    MessageSender.broadcast(new ServerboundCreeperMinionTrackerMessage(this.creeperMinion.getId(),
                            ServerboundCreeperMinionTrackerMessage.DataType.IS_CUSTOM_NAME_VISIBLE,
                            this.alwaysShowName));
                    button.setMessage(onOffComponent(SHOW_NAME_COMPONENT, this.alwaysShowName));
                }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 51, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(onOffComponent(RIDE_ON_SHOULDER_COMPONENT, this.canRideOnShoulder),
                (Button button) -> {
                    this.canRideOnShoulder = !this.canRideOnShoulder;
                    MessageSender.broadcast(new ServerboundCreeperMinionTrackerMessage(this.creeperMinion.getId(),
                            ServerboundCreeperMinionTrackerMessage.DataType.CAN_RIDE_ON_SHOULDER,
                            this.canRideOnShoulder));
                    button.setMessage(onOffComponent(RIDE_ON_SHOULDER_COMPONENT, this.canRideOnShoulder));
                }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 27, buttonWidth * 2 + 4, 20).build());
        if (!this.creeperMinion.isOwnedBy(this.minecraft.player)) {
            this.renderables.stream()
                    .filter(AbstractWidget.class::isInstance)
                    .map(AbstractWidget.class::cast)
                    .forEach((AbstractWidget widget) -> widget.active = false);
        }
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
    }

    @Override
    public void tick() {
        if (!this.creeperMinion.isAlive()) {
            this.minecraft.player.closeContainer();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                TEXTURE_LOCATION,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                256,
                256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                TEXTURE_LOCATION,
                this.leftPos + 15,
                this.topPos + 16,
                0,
                166,
                146,
                5,
                256,
                256);
        float healthProgress = Mth.clamp(this.creeperMinion.getHealth() / this.creeperMinion.getMaxHealth(),
                0.0F,
                1.0F);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
                TEXTURE_LOCATION,
                this.leftPos + 15,
                this.topPos + 16,
                0,
                171,
                (int) (healthProgress * 146.0F),
                5,
                256,
                256);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void resize(int width, int height) {
        String string = this.name.getValue();
        super.resize(width, height);
        this.name.setValue(string);
    }

    private void onNameChanged(String input) {
        input = input.trim();
        if (!input.equals(this.creeperMinion.getName().getString())) {
            this.creeperMinion.setCustomName(Component.literal(input));
            MessageSender.broadcast(new ServerboundCreeperMinionNameMessage(this.creeperMinion.getId(), input));
        }
    }

    private static Component onOffComponent(Component component, boolean on) {
        return Component.empty()
                .append(component)
                .append(": ")
                .append(on ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
