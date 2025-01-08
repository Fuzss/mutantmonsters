package fuzs.mutantmonsters.world.item;

import fuzs.mutantmonsters.world.level.MutatedExplosionHelper;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CreeperShardItem extends Item {

    public CreeperShardItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ).add(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.5F, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        ).build();
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.getDamageValue() == 0;
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        Player player = (Player) attacker;
        int damage = itemStack.getDamageValue();
        if (damage > 0) {
            itemStack.setDamageValue(damage - 1);
            if (!player.isCreative() && player.getRandom().nextInt(4) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 80 + player.getRandom().nextInt(40)));
            }
        }

        target.knockback(0.9F, player.getX() - target.getX(), player.getZ() - target.getZ());
        player.level().playSound(null, target.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL,
                0.3F, 0.8F + player.getRandom().nextFloat() * 0.4F
        );
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        int maxDamage = itemInHand.getMaxDamage();
        int damageValue = itemInHand.getDamageValue();
        if (damageValue < maxDamage) {
            if (!level.isClientSide) {
                float radius = 5.0F * (maxDamage - damageValue) / 16.0F;
                if (damageValue == 0) {
                    radius += 2.0F;
                }

                level.explode(player, Explosion.getDefaultDamageSource(level, player),
                        new MutatedExplosionHelper.MutatedExplosionDamageCalculator(), player.getX(), player.getY() + 1.0,
                        player.getZ(), radius, false,
                        player.mayBuild() ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE
                );
            }

            if (!player.getAbilities().instabuild) {
                itemInHand.setDamageValue(damageValue + 1);
            }

            player.getCooldowns().addCooldown(itemInHand, (maxDamage - damageValue) * 2);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHelper.success(itemInHand);
        } else {
            return super.use(level, player, interactionHand);
        }
    }
}
