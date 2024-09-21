package fuzs.mutantmonsters.mixin;

import fuzs.mutantmonsters.init.ModRegistry;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements DataComponentHolder {

    @Inject(method = "canBeHurtBy", at = @At("HEAD"), cancellable = true)
    public void canBeHurtBy(DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        if (this.has(ModRegistry.EXPLOSION_RESISTANT_DATA_COMPONENT_TYPE.value()) && damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            callback.setReturnValue(false);
        }
    }
}
