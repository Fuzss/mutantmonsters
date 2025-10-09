package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.init.ModEntityTypes;
import fuzs.mutantmonsters.init.ModItems;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.init.ModSoundEvents;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.item.v2.EnchantingHelper;
import fuzs.puzzleslib.api.util.v1.DamageHelper;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerEventsHandler {

    public static EventResult onItemUseTick(LivingEntity entity, ItemStack useItem, MutableInt useItemRemaining) {
        // quick charge for bows
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.MUTANT_SKELETON_CHESTPLATE_ITEM)) {
            if (useItem.getItem() instanceof BowItem
                    && BowItem.getPowerForTime(useItem.getUseDuration(entity) - useItemRemaining.getAsInt()) < 1.0F) {
                useItemRemaining.mapInt(i -> i - 2);
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onArrowLoose(Player player, ItemStack weapon, Level level, MutableInt charge, boolean hasAmmo) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MUTANT_SKELETON_SKULL_ITEM)
                && weapon.getItem() instanceof BowItem) {
            Holder<Enchantment> enchantment = EnchantingHelper.lookup(level, Enchantments.MULTISHOT);
            if (weapon.getEnchantments().getLevel(enchantment) == 0) {
                // multi-shot for bows
                ItemStack itemStack = weapon.copy();
                itemStack.enchant(enchantment, 1);
                itemStack.releaseUsing(level, player, player.getUseItemRemainingTicks());
                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }

    public static void onEndPlayerTick(Player player) {
        playShoulderEntitySound(player, player.getShoulderEntityLeft());
        playShoulderEntitySound(player, player.getShoulderEntityRight());
        if (player.level() instanceof ServerLevel serverLevel) {
            SeismicWave seismicWave = SeismicWave.poll(player);
            if (seismicWave != null) {
                handleSeismicWave(serverLevel, player, seismicWave);
            }
        }
    }

    private static void handleSeismicWave(ServerLevel serverLevel, Player player, @NotNull SeismicWave seismicWave) {
        seismicWave.affectBlocks(serverLevel, player);
        AABB box = new AABB(seismicWave.getX(),
                (double) seismicWave.getY() + 1.0,
                seismicWave.getZ(),
                (double) seismicWave.getX() + 1.0,
                (double) seismicWave.getY() + 2.0,
                (double) seismicWave.getZ() + 1.0);

        for (LivingEntity livingEntity : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
            if (livingEntity != player && player.getVehicle() != livingEntity) {
                DamageSource damageSource = DamageHelper.damageSource(serverLevel,
                        ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE,
                        player);
                livingEntity.hurtServer(serverLevel, damageSource, 6.0F + player.getRandom().nextInt(3));
            }
        }
    }

    private static void playShoulderEntitySound(Player player, @Nullable CompoundTag compoundTag) {
        if (compoundTag != null && !compoundTag.isEmpty() && !compoundTag.getBooleanOr("Silent", false)) {
            EntityType<?> entityType = compoundTag.read("id", EntityType.CODEC).orElse(null);
            if (entityType == ModEntityTypes.CREEPER_MINION_ENTITY_TYPE.value()) {
                if (player.level().random.nextInt(500) == 0) {
                    player.level()
                            .playSound(null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    ModSoundEvents.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.value(),
                                    player.getSoundSource(),
                                    1.0F,
                                    (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F
                                            + 1.5F);
                }
            }
        }
    }

    public static EventResult onItemToss(ServerPlayer serverPlayer, ItemStack itemStack) {
        boolean isHoldingEndersoulHand = itemStack.is(ModItems.ENDERSOUL_HAND_ITEM) && itemStack.isDamaged();
        if (itemStack.is(Items.ENDER_EYE) || isHoldingEndersoulHand) {
            int endersoulFragments = 0;

            for (EndersoulFragment endersoulFragment : serverPlayer.level()
                    .getEntitiesOfClass(EndersoulFragment.class, serverPlayer.getBoundingBox().inflate(8.0))) {
                if (endersoulFragment.ownedBy(serverPlayer)) {
                    ++endersoulFragments;
                    endersoulFragment.discard();
                }
            }

            if (endersoulFragments > 0) {
                EntityUtil.sendParticlePacket(serverPlayer, ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), 256);
                int additionalDamageValue = endersoulFragments * 60;
                if (isHoldingEndersoulHand) {
                    int damageValue = itemStack.getDamageValue() - additionalDamageValue;
                    itemStack.setDamageValue(Math.max(damageValue, 0));
                } else {
                    ItemStack newItemStack = new ItemStack(ModItems.ENDERSOUL_HAND_ITEM);
                    newItemStack.setDamageValue(newItemStack.getMaxDamage() - additionalDamageValue);
                    ItemEntity itemEntity = serverPlayer.createItemStackToDrop(newItemStack, false, true);
                    if (itemEntity != null) {
                        serverPlayer.level().addFreshEntity(itemEntity);
                        serverPlayer.awardStat(Stats.ITEM_DROPPED.get(newItemStack.getItem()), itemStack.getCount());
                        serverPlayer.awardStat(Stats.DROP);
                        return EventResult.INTERRUPT;
                    }
                }
            }
        }

        return EventResult.PASS;
    }
}
