package fuzs.mutantmonsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.EndersoulHandModel;
import fuzs.puzzleslib.api.client.init.v1.DynamicBuiltinItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

public class EndersoulHandRenderer implements DynamicBuiltinItemRenderer {
    public static final ModelResourceLocation ENDERSOUL_BUILT_IN_MODEL = new ModelResourceLocation(MutantMonsters.id("endersoul_hand_in_hand"), "inventory");
    public static final ModelResourceLocation ENDERSOUL_ITEM_MODEL = new ModelResourceLocation(MutantMonsters.id("endersoul_hand"), "inventory");
    private static final ResourceLocation ENDERSOUL_HAND_TEXTURE = MutantMonsters.id("textures/item/endersoul_hand_model.png");

    private final Minecraft minecraft = Minecraft.getInstance();
    private EndersoulHandModel enderSoulHandModel;

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        float ageInTicks = (float) this.minecraft.player.tickCount + this.minecraft.getFrameTime();
        this.enderSoulHandModel.setAngles();
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(vertexConsumers, MutantRenderTypes.energySwirl(ENDERSOUL_HAND_TEXTURE, ageInTicks * 0.008F, ageInTicks * 0.008F), true, stack.hasFoil());
        this.enderSoulHandModel.renderToBuffer(matrices, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 0.9F, 0.3F, 1.0F, 1.0F);
        matrices.popPose();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.enderSoulHandModel = new EndersoulHandModel(this.minecraft.getEntityModels().bakeLayer(ClientModRegistry.ENDERSOUL_HAND_RIGHT), true);
    }
}
