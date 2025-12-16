package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @see net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer
 */
public class ModEquipmentLayer<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>> extends RenderLayer<S, RM> {
    private final Map<ResourceKey<EquipmentAsset>, List<EquipmentClientInfo.Layer>> equipmentAssets;
    private final String layerType;
    private final Function<S, ItemStack> itemGetter;
    private final EM adultModel;
    private final EM babyModel;
    private final int order;

    public ModEquipmentLayer(RenderLayerParent<S, RM> renderLayerParent, ResourceKey<EquipmentAsset> assetId, Identifier layer, String layerType, Function<S, ItemStack> itemGetter, EM adultModel, EM babyModel, int order) {
        super(renderLayerParent);
        this.equipmentAssets = Collections.singletonMap(assetId,
                Collections.singletonList(new EquipmentClientInfo.Layer(layer)));
        this.layerType = layerType;
        this.itemGetter = itemGetter;
        this.adultModel = adultModel;
        this.babyModel = babyModel;
        this.order = order;
    }

    public ModEquipmentLayer(RenderLayerParent<S, RM> renderLayerParent, ResourceKey<EquipmentAsset> assetId, Identifier layer, String layerType, Function<S, ItemStack> itemGetter, EM adultModel, EM babyModel) {
        this(renderLayerParent, assetId, layer, layerType, itemGetter, adultModel, babyModel, 0);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S renderState, float yRot, float xRot) {
        ItemStack itemStack = this.itemGetter.apply(renderState);
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable != null && !equippable.assetId().isEmpty()) {
            EM entityModel = renderState.isBaby ? this.babyModel : this.adultModel;
            this.renderLayers(this.layerType,
                    equippable.assetId().get(),
                    entityModel,
                    renderState,
                    itemStack,
                    poseStack,
                    nodeCollector,
                    packedLight,
                    null,
                    renderState.outlineColor,
                    this.order);
        }
    }

    /**
     * @see EquipmentLayerRenderer#renderLayers(EquipmentClientInfo.LayerType, ResourceKey, Model, Object, ItemStack,
     *         PoseStack, SubmitNodeCollector, int, int)
     */
    public void renderLayers(String layerType, ResourceKey<EquipmentAsset> equipmentAsset, Model<? super S> armorModel, S renderState, ItemStack item, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int outlineColor) {
        this.renderLayers(layerType,
                equipmentAsset,
                armorModel,
                renderState,
                item,
                poseStack,
                nodeCollector,
                packedLight,
                null,
                outlineColor,
                1);
    }

    /**
     * @see EquipmentLayerRenderer#renderLayers(EquipmentClientInfo.LayerType, ResourceKey, Model, Object, ItemStack,
     *         PoseStack, SubmitNodeCollector, int, Identifier, int, int)
     */
    public void renderLayers(String layerType, ResourceKey<EquipmentAsset> equipmentAsset, Model<? super S> armorModel, S renderState, ItemStack item, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, @Nullable Identifier texture, int outlineColor, int order) {
        List<EquipmentClientInfo.Layer> list = this.equipmentAssets.get(equipmentAsset);
        if (!list.isEmpty()) {
            int dyeColor = DyedItemColor.getOrDefault(item, 0);
            boolean hasFoil = item.hasFoil();
            for (EquipmentClientInfo.Layer layer : list) {
                int tintColor = EquipmentLayerRenderer.getColorForLayer(layer, dyeColor);
                if (tintColor != 0) {
                    Identifier identifier = this.layerTextureLookup(layer, layerType);
                    nodeCollector.order(order++)
                            .submitModel(armorModel,
                                    renderState,
                                    poseStack,
                                    RenderTypes.armorCutoutNoCull(identifier),
                                    packedLight,
                                    OverlayTexture.NO_OVERLAY,
                                    tintColor,
                                    null,
                                    outlineColor,
                                    null);
                    if (hasFoil) {
                        nodeCollector.order(order++)
                                .submitModel(armorModel,
                                        renderState,
                                        poseStack,
                                        RenderTypes.armorEntityGlint(),
                                        packedLight,
                                        OverlayTexture.NO_OVERLAY,
                                        tintColor,
                                        null,
                                        outlineColor,
                                        null);
                    }

                    hasFoil = false;
                }
            }
        }
    }

    /**
     * @see EquipmentLayerRenderer#layerTextureLookup
     * @see EquipmentClientInfo.Layer#getTextureLocation(EquipmentClientInfo.LayerType)
     */
    public Identifier layerTextureLookup(EquipmentClientInfo.Layer layer, String type) {
        return layer.textureId()
                .withPath((String string) -> "textures/entity/equipment/" + type + "/" + string + ".png");
    }
}
