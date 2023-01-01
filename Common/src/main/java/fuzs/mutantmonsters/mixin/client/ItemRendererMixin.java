package fuzs.mutantmonsters.mixin.client;

import fuzs.mutantmonsters.client.renderer.EndersoulHandRenderer;
import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {
    @Final
    @Shadow
    private ItemModelShaper itemModelShaper;

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    public BakedModel mutantmonsters$render(BakedModel bakedModel, ItemStack itemStack, ItemTransforms.TransformType transformType) {
        if (!itemStack.isEmpty()) {
            boolean flag = transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND || transformType == ItemTransforms.TransformType.FIXED;
            if (flag) {
                return mutantmonsters$getModel(bakedModel, itemStack, this.itemModelShaper, EndersoulHandRenderer.ENDERSOUL_ITEM_MODEL);
            }
        }
        return bakedModel;
    }

    @ModifyVariable(method = "getModel", at = @At("STORE"), ordinal = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;")))
    public BakedModel mutantmonsters$getModel(BakedModel bakedModel, ItemStack itemStack) {
        return mutantmonsters$getModel(bakedModel, itemStack, this.itemModelShaper, EndersoulHandRenderer.ENDERSOUL_BUILT_IN_MODEL);
    }

    @Unique
    private static BakedModel mutantmonsters$getModel(BakedModel bakedModel, ItemStack itemStack, ItemModelShaper itemModelShaper, ModelResourceLocation model) {
        if (itemStack.is(ModRegistry.ENDERSOUL_HAND_ITEM.get())) {
            return itemModelShaper.getModelManager().getModel(model);
        }
        return bakedModel;
    }
}
