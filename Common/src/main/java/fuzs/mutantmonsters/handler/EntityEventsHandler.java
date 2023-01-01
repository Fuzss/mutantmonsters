package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import fuzs.mutantmonsters.world.item.ArmorBlockItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class EntityEventsHandler {

    public static void onEntityJoinServerLevel(Entity entity, ServerLevel level) {
        if (entity instanceof PathfinderMob creature) {
            if (EntityUtil.isFeline(creature)) {
                CommonAbstractions.INSTANCE.getGoalSelector(creature).addGoal(2, new AvoidEntityGoal<>(creature, MutantCreeper.class, 16.0F, 1.33, 1.33));
            }

            if (creature.getType() == EntityType.PIG) {
                CommonAbstractions.INSTANCE.getGoalSelector(creature).addGoal(2, new TemptGoal(creature, 1.0, Ingredient.of(Items.FERMENTED_SPIDER_EYE), false));
            }

            if (creature.getType() == EntityType.VILLAGER) {
                CommonAbstractions.INSTANCE.getGoalSelector(creature).addGoal(0, new AvoidEntityGoal<>(creature, MutantZombie.class, 12.0F, 0.8, 0.8));
            }

            if (creature.getType() == EntityType.WANDERING_TRADER) {
                CommonAbstractions.INSTANCE.getGoalSelector(creature).addGoal(1, new AvoidEntityGoal<>(creature, MutantZombie.class, 12.0F, 0.5, 0.5));
            }
        }
    }

    public static InteractionResult onEntityInteract(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (entity instanceof Pig pig && !pig.hasEffect(MobEffects.UNLUCK)) {
            ItemStack stackInHand = player.getItemInHand(hand);
            if (stackInHand.getItem() == Items.FERMENTED_SPIDER_EYE) {
                if (!player.isCreative()) {
                    stackInHand.shrink(1);
                }

                pig.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 600, 13));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static Optional<Unit> onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof Player && entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorBlockItem) {
            float damage = amount;
            if (!(damage <= 0.0F)) {
                damage /= 4.0F;
                if (damage < 1.0F) {
                    damage = 1.0F;
                }

                ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.HEAD);
                if (!source.isFire() || !itemstack.getItem().isFireResistant()) {
                    itemstack.hurtAndBreak((int) damage, entity, (livingEntity) -> {
                        livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                    });
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Unit> onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
        Entity trueSource = source.getEntity();
        if (SpiderPig.isPigOrSpider(entity) && trueSource instanceof SpiderPig) {
            return Optional.of(Unit.INSTANCE);
        }

        if ((trueSource instanceof MutantCreeper && ((MutantCreeper) trueSource).isCharged() || trueSource instanceof CreeperMinion && ((CreeperMinion) trueSource).isCharged()) && source.isExplosion()) {
            ItemStack itemStack = EntityUtil.getSkullDrop(entity.getType());
            if (!itemStack.isEmpty()) {
                drops.add(new ItemEntity(trueSource.level, entity.getX(), entity.getY(), entity.getZ(), itemStack));
            }
        }
        return Optional.empty();
    }
}
