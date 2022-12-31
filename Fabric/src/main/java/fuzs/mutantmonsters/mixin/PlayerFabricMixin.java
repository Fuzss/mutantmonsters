package fuzs.mutantmonsters.mixin;

import fuzs.mutantmonsters.api.event.entity.PlayerTickEvents;
import fuzs.mutantmonsters.api.event.entity.item.ItemTossCallback;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerFabricMixin extends LivingEntity {

    protected PlayerFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick$head(CallbackInfo callbackInfo) {
        PlayerTickEvents.START_TICK.invoker().onStartTick((Player) (Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick$tail(CallbackInfo callbackInfo) {
        PlayerTickEvents.END_TICK.invoker().onEndTick((Player) (Object) this);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
    public void drop(ItemStack itemStack, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> callback) {
        callback.setReturnValue(ItemTossCallback.onPlayerTossEvent((Player) (Object) this, itemStack, includeThrowerName));
    }
}
