package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @see net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer
 */
public class SimpleEquipmentLayer<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>> extends RenderLayer<S, RM> {
    private final Map<ResourceKey<EquipmentAsset>, List<EquipmentClientInfo.Layer>> equipmentAssets;
    private final StringRepresentable layerType;
    private final Function<S, ItemStack> itemGetter;
    private final EM adultModel;
    private final EM babyModel;

    public SimpleEquipmentLayer(RenderLayerParent<S, RM> renderLayerParent, Map<ResourceKey<EquipmentAsset>, List<EquipmentClientInfo.Layer>> equipmentAssets, StringRepresentable layerType, Function<S, ItemStack> function, EM adultModel, EM babyModel) {
        super(renderLayerParent);
        this.equipmentAssets = equipmentAssets;
        this.layerType = layerType;
        this.itemGetter = function;
        this.adultModel = adultModel;
        this.babyModel = babyModel;
    }

    public SimpleEquipmentLayer(RenderLayerParent<S, RM> renderLayerParent, ResourceKey<EquipmentAsset> assetId, EquipmentClientInfo.Layer layer, StringRepresentable layerType, Function<S, ItemStack> function, EM entityModel, EM entityModel2) {
        this(renderLayerParent,
                Collections.singletonMap(assetId, Collections.singletonList(layer)),
                layerType,
                function,
                entityModel,
                entityModel2);
    }

    public SimpleEquipmentLayer(RenderLayerParent<S, RM> renderLayerParent, Map<ResourceKey<EquipmentAsset>, List<EquipmentClientInfo.Layer>> equipmentAssets, StringRepresentable layerType, Function<S, ItemStack> function, EM entityModel) {
        this(renderLayerParent, equipmentAssets, layerType, function, entityModel, entityModel);
    }

    public SimpleEquipmentLayer(RenderLayerParent<S, RM> renderLayerParent, ResourceKey<EquipmentAsset> assetId, EquipmentClientInfo.Layer layer, StringRepresentable layerType, Function<S, ItemStack> function, EM entityModel) {
        this(renderLayerParent,
                Collections.singletonMap(assetId, Collections.singletonList(layer)),
                layerType,
                function,
                entityModel);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, S livingEntityRenderState, float yRot, float xRot) {
        ItemStack itemStack = this.itemGetter.apply(livingEntityRenderState);
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && !equippable.assetId().isEmpty()) {
            EM entityModel = livingEntityRenderState.isBaby ? this.babyModel : this.adultModel;
            entityModel.setupAnim(livingEntityRenderState);
            this.renderLayers(equippable.assetId().get(),
                    entityModel,
                    itemStack,
                    poseStack,
                    multiBufferSource,
                    packedLight);
        }
    }

    /**
     * @see EquipmentLayerRenderer#renderLayers(EquipmentClientInfo.LayerType, ResourceKey, Model, ItemStack,
     *         PoseStack, MultiBufferSource, int)
     */
    public void renderLayers(ResourceKey<EquipmentAsset> equipmentAsset, Model armorModel, ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.renderLayers(equipmentAsset, armorModel, item, poseStack, bufferSource, packedLight, null);
    }

    /**
     * @see EquipmentLayerRenderer#renderLayers(EquipmentClientInfo.LayerType, ResourceKey, Model, ItemStack,
     *         PoseStack, MultiBufferSource, int, ResourceLocation)
     */
    public void renderLayers(ResourceKey<EquipmentAsset> equipmentAsset, Model armorModel, ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, @Nullable ResourceLocation playerTexture) {
        List<EquipmentClientInfo.Layer> list = this.equipmentAssets.get(equipmentAsset);
        if (!list.isEmpty()) {
            int i = DyedItemColor.getOrDefault(item, 0);
            boolean bl = item.hasFoil();

            for (EquipmentClientInfo.Layer layer : list) {
                int j = EquipmentLayerRenderer.getColorForLayer(layer, i);
                if (j != 0) {
                    ResourceLocation resourceLocation =
                            layer.usePlayerTexture() && playerTexture != null ? playerTexture :
                                    this.getTextureLocation(layer, this.layerType);
                    VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(bufferSource,
                            RenderType.armorCutoutNoCull(resourceLocation),
                            bl);
                    armorModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, j);
                    bl = false;
                }
            }
        }
    }

    /**
     * @see EquipmentClientInfo.Layer#getTextureLocation(EquipmentClientInfo.LayerType)
     */
    public ResourceLocation getTextureLocation(EquipmentClientInfo.Layer layer, StringRepresentable type) {
        return layer.textureId()
                .withPath((String string) -> "textures/entity/equipment/" + type.getSerializedName() + "/" + string +
                        ".png");
    }
}
