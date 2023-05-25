package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.CreeperMinion;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import fuzs.mutantmonsters.world.entity.mutant.SpiderPig;
import fuzs.mutantmonsters.world.item.ArmorBlockItem;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class EntityEventsHandler {

    public static EventResult onEntityJoinServerLevel(Entity entity, ServerLevel level, @Nullable MobSpawnType spawnType) {
        if (entity instanceof PathfinderMob creature) {
            if (EntityUtil.isFeline(creature)) {
                creature.goalSelector.addGoal(2, new AvoidEntityGoal<>(creature, MutantCreeper.class, 16.0F, 1.33, 1.33));
            }

            if (creature.getType() == EntityType.PIG) {
                creature.goalSelector.addGoal(2, new TemptGoal(creature, 1.0, Ingredient.of(Items.FERMENTED_SPIDER_EYE), false));
            }

            if (creature.getType() == EntityType.VILLAGER) {
                creature.goalSelector.addGoal(0, new AvoidEntityGoal<>(creature, MutantZombie.class, 12.0F, 0.8, 0.8));
            }

            if (creature.getType() == EntityType.WANDERING_TRADER) {
                creature.goalSelector.addGoal(1, new AvoidEntityGoal<>(creature, MutantZombie.class, 12.0F, 0.5, 0.5));
            }
        }
        return EventResult.PASS;
    }

    public static EventResultHolder<InteractionResult> onEntityInteract(Player player, Level level, InteractionHand hand, Entity entity) {
        if (entity instanceof Pig pig && !pig.hasEffect(MobEffects.UNLUCK)) {
            ItemStack stackInHand = player.getItemInHand(hand);
            if (stackInHand.getItem() == Items.FERMENTED_SPIDER_EYE) {
                if (!player.isCreative()) {
                    stackInHand.shrink(1);
                }

                pig.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 600, 13));
                return EventResultHolder.interrupt(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
        return EventResultHolder.pass();
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (entity instanceof Player && entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorBlockItem) {
            float damage = amount.getAsFloat();
            if (!(damage <= 0.0F)) {
                damage /= 4.0F;
                if (damage < 1.0F) {
                    damage = 1.0F;
                }

                ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.HEAD);
                if (!source.is(DamageTypeTags.IS_FIRE) || !itemstack.getItem().isFireResistant()) {
                    itemstack.hurtAndBreak((int) damage, entity, (livingEntity) -> {
                        livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                    });
                }
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
        Entity trueSource = source.getEntity();
        if (SpiderPig.isPigOrSpider(entity) && trueSource instanceof SpiderPig) {
            return EventResult.INTERRUPT;
        }

        if ((trueSource instanceof MutantCreeper && ((MutantCreeper) trueSource).isCharged() || trueSource instanceof CreeperMinion && ((CreeperMinion) trueSource).isCharged()) && source.is(DamageTypeTags.IS_EXPLOSION)) {
            ItemStack itemStack = EntityUtil.getSkullDrop(entity.getType());
            if (!itemStack.isEmpty()) {
                drops.add(new ItemEntity(trueSource.level, entity.getX(), entity.getY(), entity.getZ(), itemStack));
            }
        }
        return EventResult.PASS;
    }
}
