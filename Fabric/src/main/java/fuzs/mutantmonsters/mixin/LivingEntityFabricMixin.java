package fuzs.mutantmonsters.mixin;

import com.google.common.collect.Lists;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.api.event.entity.living.LivingDropsCallback;
import fuzs.mutantmonsters.api.event.entity.living.LivingEntityUseItemEvents;
import fuzs.mutantmonsters.api.event.entity.living.LivingHurtCallback;
import fuzs.mutantmonsters.api.event.entity.living.CapturedDropsEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LivingEntity.class)
abstract class LivingEntityFabricMixin extends Entity {
    @Shadow
    protected ItemStack useItem;
    @Shadow
    protected int useItemRemaining;
    @Shadow
    protected int lastHurtByPlayerTime;

    public LivingEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updateUsingItem(Lnet/minecraft/world/item/ItemStack;)V"))
    private void updatingUsingItem$invokeUpdateUsingItem(CallbackInfo callback) {
        LivingEntityUseItemEvents.TICK.invoker().onUseItemTick((LivingEntity) (Object) this, this.useItem, this.useItemRemaining).ifPresent(newDuration -> {
            this.useItemRemaining = newDuration;
        });
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), cancellable = true)
    protected void actuallyHurt$invokeGetDamageAfterArmorAbsorb(DamageSource damageSource, float damageAmount, CallbackInfo callback) {
        LivingHurtCallback.EVENT.invoker().onLivingHurt((LivingEntity) (Object) this, damageSource, damageAmount).ifPresent(unit -> callback.cancel());
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"))
    protected void dropAllDeathLoot$inject$head$2(DamageSource pDamageSource, CallbackInfo callback) {
        ((CapturedDropsEntity) this).mutantmonsters$setCapturedDrops(Lists.newArrayList());
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    protected void dropAllDeathLoot(DamageSource pDamageSource, CallbackInfo callback) {
        Collection<ItemEntity> drops = ((CapturedDropsEntity) this).mutantmonsters$setCapturedDrops(null);
        if (drops != null) {
            // this is bad as other mixins might modify looting level which is lost here
            // unfortunately though capturing locals does not seem to work here, even though TAIL is specified the LVT does not contain entries for killer and lootingLevel
            Entity killer = pDamageSource.getEntity();
            int lootingLevel;
            if (killer instanceof Player) {
                lootingLevel = EnchantmentHelper.getMobLooting((LivingEntity)killer);
            } else {
                lootingLevel = 0;
            }
            if (LivingDropsCallback.EVENT.invoker().onLivingDrops((LivingEntity) (Object) this, pDamageSource, drops, lootingLevel, this.lastHurtByPlayerTime > 0).isEmpty()) {
                drops.forEach(item -> this.level.addFreshEntity(item));
            }
        } else {
            MutantMonsters.LOGGER.warn("Unable to invoke LivingDropsCallback for entity {}: Drops is null", this.getName().getString());
        }
    }
}
