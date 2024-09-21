package fuzs.mutantmonsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.EndersoulHandModel;
import fuzs.puzzleslib.api.client.init.v1.ReloadingBuiltInItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EndersoulHandRenderer implements ReloadingBuiltInItemRenderer {
    public static final ResourceLocation ENDERSOUL_BUILT_IN_MODEL = MutantMonsters.id("item/endersoul_hand_in_hand");
    public static final ModelResourceLocation ENDERSOUL_ITEM_MODEL = new ModelResourceLocation(
            MutantMonsters.id("endersoul_hand"), "inventory");
    private static final ResourceLocation ENDERSOUL_HAND_TEXTURE = MutantMonsters.id(
            "textures/item/endersoul_hand_model.png");

    private final Minecraft minecraft = Minecraft.getInstance();
    private EndersoulHandModel enderSoulHandModel;

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int overlay) {
        poseStack.pushPose();
        float partialTick = this.minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        float ageInTicks = (float) this.minecraft.player.tickCount + partialTick;
        this.enderSoulHandModel.setAngles();
        RenderType renderType = MutantRenderTypes.energySwirl(ENDERSOUL_HAND_TEXTURE, ageInTicks * 0.008F,
                ageInTicks * 0.008F
        );
        // ignore enchanting glint, it looks bad
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
        int color = FastColor.ARGB32.colorFromFloat(1.0F, 0.9F, 0.3F, 1.0F);
        this.enderSoulHandModel.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.enderSoulHandModel = new EndersoulHandModel(
                this.minecraft.getEntityModels().bakeLayer(ClientModRegistry.ENDERSOUL_HAND_RIGHT), true);
    }
}
