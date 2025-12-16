package fuzs.mutantmonsters.mixin;

import com.mojang.authlib.GameProfile;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "setShoulderEntityLeft", at = @At("TAIL"))
    protected void setShoulderEntityLeft(CompoundTag tag, CallbackInfo callback) {
        ModRegistry.LEFT_SHOULDER_CREEPER_MINION_ATTACHMENT_TYPE.set(this, EntityUtil.extractCreeperMinion(tag));
    }

    @Inject(method = "setShoulderEntityRight", at = @At("TAIL"))
    protected void setShoulderEntityRight(CompoundTag tag, CallbackInfo callback) {
        ModRegistry.RIGHT_SHOULDER_CREEPER_MINION_ATTACHMENT_TYPE.set(this, EntityUtil.extractCreeperMinion(tag));
    }
}
