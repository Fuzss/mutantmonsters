package fuzs.mutantmonsters.world.item;

import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public class CreeperShardItem extends Item {

    public CreeperShardItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.5F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(), 1.0F, 1, false);
    }

    @Override
    public void hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        int damageValue = itemStack.getDamageValue();
        if (damageValue > 0) {
            itemStack.setDamageValue(damageValue - 1);
            if ((!((LivingEntity) attacker instanceof Player player) || !player.isCreative())
                    && attacker.getRandom().nextInt(4) == 0) {
                attacker.addEffect(new MobEffectInstance(MobEffects.POISON, 80 + attacker.getRandom().nextInt(40)));
            }
        }

        target.knockback(0.9F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        attacker.level()
                .playSound(null,
                        target.blockPosition(),
                        SoundEvents.GENERIC_EXPLODE.value(),
                        SoundSource.NEUTRAL,
                        0.3F,
                        0.8F + attacker.getRandom().nextFloat() * 0.4F);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        int maxDamage = itemInHand.getMaxDamage();
        int damageValue = itemInHand.getDamageValue();
        if (damageValue < maxDamage) {
            if (!level.isClientSide()) {
                float radius = 5.0F * (maxDamage - damageValue) / 16.0F;
                if (damageValue == 0) {
                    radius += 2.0F;
                }

                level.explode(player,
                        Explosion.getDefaultDamageSource(level, player),
                        new MutatedExplosionHelper.MutatedExplosionDamageCalculator(),
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        radius,
                        false,
                        player.mayBuild() ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
            }

            itemInHand.hurtAndBreak(1, player, interactionHand.asEquipmentSlot());
            player.getCooldowns().addCooldown(itemInHand, (maxDamage - damageValue) * 2);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHelper.success(itemInHand);
        } else {
            return super.use(level, player, interactionHand);
        }
    }
}
