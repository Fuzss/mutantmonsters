package fuzs.mutantmonsters.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.mutantmonsters.world.entity.CreeperMinionEgg;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
abstract class EntityMixin {

    @ModifyExpressionValue(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
    public boolean startRiding(boolean canSerialize) {
        // allow creeper minion egg to be carried by players, it will be dropped upon reentering the level though
        return canSerialize || CreeperMinionEgg.class.isInstance(this);
    }
}
