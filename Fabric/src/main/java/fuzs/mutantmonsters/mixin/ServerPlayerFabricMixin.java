package fuzs.mutantmonsters.mixin;

import com.mojang.authlib.GameProfile;
import fuzs.mutantmonsters.api.event.entity.item.ItemTossCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerFabricMixin extends Player {

    public ServerPlayerFabricMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Inject(method = "drop(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void drop(boolean bl, CallbackInfoReturnable<Boolean> callback, Inventory inventory, ItemStack itemStack) {
        callback.setReturnValue(ItemTossCallback.onPlayerTossEvent(this, itemStack, true) != null);
    }
}
