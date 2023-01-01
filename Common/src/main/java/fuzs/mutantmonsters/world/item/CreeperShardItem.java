package fuzs.mutantmonsters.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import fuzs.mutantmonsters.world.level.MutatedExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CreeperShardItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public CreeperShardItem(Item.Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 2.0, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.5, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
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
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Player player = (Player)  attacker;
        int damage = stack.getDamageValue();
        if (damage > 0) {
            stack.setDamageValue(damage - 1);
            if (!player.isCreative() && player.getRandom().nextInt(4) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 80 + player.getRandom().nextInt(40)));
            }
        }

        target.knockback(0.9F, player.getX() - target.getX(), player.getZ() - target.getZ());
        player.level.playSound(null, target.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.3F, 0.8F + player.getRandom().nextFloat() * 0.4F);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        int maxDmg = stack.getMaxDamage();
        int dmg = stack.getDamageValue();
        if (!worldIn.isClientSide) {
            float damage = 5.0F * (float)(maxDmg - dmg) / 32.0F;
            if (dmg == 0) {
                damage += 2.0F;
            }

            MutatedExplosion.create(worldIn, playerIn, playerIn.getX(), playerIn.getY() + 1.0, playerIn.getZ(), damage, false, playerIn.mayBuild() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE);
        }

        if (!playerIn.getAbilities().instabuild) {
            stack.setDamageValue(maxDmg);
        }

        playerIn.getCooldowns().addCooldown(this, (maxDmg - dmg) * 2);
        playerIn.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canBeHurtBy(DamageSource damageSource) {
        return !damageSource.isExplosion();
    }
}
