package fuzs.mutantmonsters.mixin;

import fuzs.mutantmonsters.api.event.entity.living.CapturedDropsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

@Mixin(Entity.class)
abstract class EntityFabricMixin implements CapturedDropsEntity {
    @Unique
    @Nullable
    private Collection<ItemEntity> custom$capturedDrops;

    @Override
    public Collection<ItemEntity> mutantmonsters$setCapturedDrops(Collection<ItemEntity> collection) {
        Collection<ItemEntity> drops = this.custom$capturedDrops;
        this.custom$capturedDrops = collection;
        return drops;
    }

    @Override
    public Collection<ItemEntity> mutantmonsters$getCapturedDrops() {
        return this.custom$capturedDrops;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void spawnAtLocation$inject$invoke(ItemStack stack, float offsetY, CallbackInfoReturnable<ItemEntity> callback, ItemEntity itementity) {
        if (this.custom$capturedDrops != null) {
            this.custom$capturedDrops.add(itementity);
            callback.setReturnValue(itementity);
        }
    }
}
