package fuzs.mutantmonsters.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.EndersoulHandModel;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.renderer.ModRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import java.util.Set;

public class EndersoulHandSpecialRenderer implements NoDataSpecialModelRenderer {
    private static final ResourceLocation ENDERSOUL_HAND_TEXTURE_LOCATION = MutantMonsters.id(
            "textures/item/endersoul_hand_model.png");

    private final Minecraft minecraft = Minecraft.getInstance();
    private final EndersoulHandModel model;

    public EndersoulHandSpecialRenderer(EndersoulHandModel model) {
        this.model = model;
    }

    @Override
    public void submit(ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        float partialTick = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float ageInTicks = this.minecraft.player.tickCount + partialTick;
        RenderType renderType = ModRenderType.energySwirl(ENDERSOUL_HAND_TEXTURE_LOCATION,
                ageInTicks * 0.008F,
                ageInTicks * 0.008F);
        // ignore enchanting glint, it looks bad
        int color = ARGB.colorFromFloat(1.0F, 0.9F, 0.3F, 1.0F);
        nodeCollector.submitModel(this.model,
                Unit.INSTANCE,
                poseStack,
                renderType,
                0XF000F0,
                OverlayTexture.NO_OVERLAY,
                color,
                null,
                outlineColor,
                null);
    }

    @Override
    public void getExtents(Set<Vector3f> output) {
        PoseStack poseStack = new PoseStack();
        this.model.setupAnim(Unit.INSTANCE);
        this.model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<EndersoulHandSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new EndersoulHandSpecialRenderer.Unbaked());

        @Override
        public SpecialModelRenderer<?> bake(BakingContext context) {
            return new EndersoulHandSpecialRenderer(new EndersoulHandModel(context.entityModelSet()
                    .bakeLayer(ModModelLayers.ENDERSOUL_HAND_RIGHT), true));
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
