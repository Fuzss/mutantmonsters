package fuzs.mutantmonsters.client.gui.screens;

import com.mojang.blaze3d.platform.InputConstants;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CreeperMinionTrackerScreen extends Screen {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id(
            "textures/gui/creeper_minion_tracker.png");
    public static final Component HEALTH_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.health");
    public static final Component EXPLOSION_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.explosion");
    public static final Component BLAST_RADIUS_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.blast_radius");
    public static final Component CONTINUOUS_EXPLOSION_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.explosion.continuous");
    public static final Component ONE_TIME_EXPLOSION_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.explosion.one_time");
    public static final Component SHOW_NAME_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.show_name");
    public static final Component DESTROY_BLOCKS_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.destroys_blocks");
    public static final Component RIDE_ON_SHOULDER_COMPONENT = Component.translatable(
            "gui." + MutantMonsters.MOD_ID + ".creeper_minion_tracker.ride_on_shoulder");
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY,
                4210752, false
        );
        this.name.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, HEALTH_COMPONENT, (this.leftPos + 13), (this.topPos + 31), 4210752, false);
        guiGraphics.drawString(this.font, EXPLOSION_COMPONENT, (this.leftPos + 13), (this.topPos + 51), 4210752, false);
        guiGraphics.drawString(this.font, BLAST_RADIUS_COMPONENT, (this.leftPos + 13), (this.topPos + 71), 4210752,
                false
        );
        guiGraphics.drawCenteredString(this.font,
                String.format("%s / %s", DECIMAL_FORMAT.format(this.creeperMinion.getHealth()),
                        DECIMAL_FORMAT.format(this.creeperMinion.getMaxHealth())
                ), this.leftPos + this.imageWidth / 2 + 38, this.topPos + 31, 16777215
        );
        guiGraphics.drawCenteredString(this.font,
                this.creeperMinion.canExplodeContinuously() ? CONTINUOUS_EXPLOSION_COMPONENT :
                        ONE_TIME_EXPLOSION_COMPONENT, this.leftPos + this.imageWidth / 2 + 38, this.topPos + 51,
                16777215
        );
        guiGraphics.drawCenteredString(this.font, DECIMAL_FORMAT.format(this.creeperMinion.getExplosionRadius()),
                this.leftPos + this.imageWidth / 2 + 38, this.topPos + 71, 16777215
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else {
            return this.name.keyPressed(keyCode, scanCode, modifiers) || this.name.canConsumeInput() ||
                    super.keyPressed(keyCode, scanCode, modifiers);
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
                (button) -> {
                    this.canDestroyBlocks = !this.canDestroyBlocks;
                    MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 0,
                            this.canDestroyBlocks
                    ).toServerboundMessage());
                    button.setMessage(onOffComponent(DESTROY_BLOCKS_COMPONENT, this.canDestroyBlocks));
                }
        ).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 75, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(onOffComponent(SHOW_NAME_COMPONENT, this.alwaysShowName), (button) -> {
            this.alwaysShowName = !this.alwaysShowName;
            MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 1,
                    this.alwaysShowName
            ).toServerboundMessage());
            button.setMessage(onOffComponent(SHOW_NAME_COMPONENT, this.alwaysShowName));
        }).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 51, buttonWidth * 2 + 4, 20).build());
        this.addRenderableWidget(Button.builder(onOffComponent(RIDE_ON_SHOULDER_COMPONENT, this.canRideOnShoulder),
                (button) -> {
                    this.canRideOnShoulder = !this.canRideOnShoulder;
                    MutantMonsters.NETWORK.sendToServer(new C2SCreeperMinionTrackerMessage(this.creeperMinion, 2,
                            this.canRideOnShoulder
                    ).toServerboundMessage());
                    button.setMessage(onOffComponent(RIDE_ON_SHOULDER_COMPONENT, this.canRideOnShoulder));
                }
        ).bounds(this.leftPos + 8, this.topPos + this.imageHeight - 27, buttonWidth * 2 + 4, 20).build());
        if (!this.creeperMinion.isOwnedBy(this.minecraft.player)) {
            this.renderables.stream().filter(AbstractWidget.class::isInstance).map(AbstractWidget.class::cast).forEach(
                    widget -> widget.active = false);
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
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 15, this.topPos + 16, 0, 166, 146, 5);
        float healthProgress = Mth.clamp(this.creeperMinion.getHealth() / this.creeperMinion.getMaxHealth(), 0.0F,
                1.0F
        );
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 15, this.topPos + 16, 0, 171, (int) (healthProgress * 146.0F),
                5
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String string = this.name.getValue();
        this.init(minecraft, width, height);
        this.name.setValue(string);
    }

    private void onNameChanged(String input) {
        input = input.trim();
        if (!input.equals(this.creeperMinion.getName().getString())) {
            this.creeperMinion.setCustomName(Component.literal(input));
            MutantMonsters.NETWORK.sendToServer(
                    new C2SCreeperMinionNameMessage(this.creeperMinion, input).toServerboundMessage());
        }
    }

    private static Component onOffComponent(Component component, boolean on) {
        return Component.empty().append(component).append(": ").append(
                on ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }
}
