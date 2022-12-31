package fuzs.mutantmonsters.mixin;

import fuzs.mutantmonsters.api.event.entity.player.ArrowLooseCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BowItem.class)
abstract class BowItemFabricMixin extends ProjectileWeaponItem {

    public BowItemFabricMixin(Properties p_43009_) {
        super(p_43009_);
    }

    @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;getPowerForTime(I)F"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void releaseUsing$inject$invoke$getPowerForTime(ItemStack bow, Level level, LivingEntity livingEntity, int useDuration, CallbackInfo callback, Player player, boolean hasInfiniteAmmo, ItemStack arrows, int charge) {
        ArrowLooseCallback.EVENT.invoker().onArrowLoose(player, bow, level, charge, !arrows.isEmpty() || hasInfiniteAmmo).ifPresent(unit -> callback.cancel());
    }
}
