package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.core.SeismicWave;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import fuzs.mutantmonsters.world.item.HulkHammerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class PlayerEventsHandler {

    public static OptionalInt onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
        if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.get()) {
            if (item.getItem() instanceof BowItem && (item.getUseDuration() - duration) < 20) {
                return OptionalInt.of(duration - 3);
            }
        }
        return OptionalInt.empty();
    }

    public static Optional<Unit> onArrowLoose(Player player, ItemStack bow, Level level, int charge, boolean hasAmmo) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get() && hasAmmo) {
            boolean inAir = !player.isOnGround() && !player.isInWater() && !player.isInLava();
            ItemStack ammo = player.getProjectile(bow);
            if (!ammo.isEmpty() || hasAmmo) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }

                float velocity = BowItem.getPowerForTime(bow.getUseDuration() - charge);
                boolean infiniteArrows = player.getAbilities().instabuild || ammo.getItem() instanceof ArrowItem && CommonAbstractions.INSTANCE.isArrowInfinite((ArrowItem) ammo.getItem(), ammo, bow, player);
                if (!level.isClientSide) {
                    ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                    AbstractArrow abstractarrowentity = arrowitem.createArrow(level, ammo, player);
                    abstractarrowentity = CommonAbstractions.INSTANCE.getCustomArrowShotFromBow((BowItem) bow.getItem(), abstractarrowentity);
                    abstractarrowentity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
                    if (velocity == 1.0F && inAir) {
                        abstractarrowentity.setCritArrow(true);
                    }

                    int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow);
                    if (j > 0) {
                        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double) j * 0.5 + 0.5);
                    }

                    int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow);
                    if (k > 0) {
                        abstractarrowentity.setKnockback(k);
                    }

                    if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bow) > 0) {
                        abstractarrowentity.setSecondsOnFire(100);
                    }

                    abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() * (inAir ? 2.0 : 0.5));
                    bow.hurtAndBreak(1, player, (p_220009_1_) -> {
                        p_220009_1_.broadcastBreakEvent(player.getUsedItemHand());
                    });
                    if (infiniteArrows || player.getAbilities().instabuild && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                        abstractarrowentity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    }

                    level.addFreshEntity(abstractarrowentity);
                }

                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                if (!infiniteArrows && !player.getAbilities().instabuild) {
                    ammo.shrink(1);
                    if (ammo.isEmpty()) {
                        player.getInventory().removeItem(ammo);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(bow.getItem()));
            }
            return Optional.of(Unit.INSTANCE);
        }
        return Optional.empty();
    }

    public static void onPlayerTick$End(Player player) {
        playShoulderEntitySound(player, player.getShoulderEntityLeft());
        playShoulderEntitySound(player, player.getShoulderEntityRight());
        if (!player.level.isClientSide && !HulkHammerItem.WAVES.isEmpty() && HulkHammerItem.WAVES.containsKey(player.getUUID())) {
            List<SeismicWave> waveList = HulkHammerItem.WAVES.get(player.getUUID());

            while (waveList.size() > 16) {
                waveList.remove(0);
            }

            SeismicWave wave = waveList.remove(0);
            wave.affectBlocks(player.level, player);
            AABB box = new AABB(wave.getX(), (double) wave.getY() + 1.0, wave.getZ(), (double) wave.getX() + 1.0, (double) wave.getY() + 2.0, (double) wave.getZ() + 1.0);

            for (LivingEntity livingEntity : player.level.getEntitiesOfClass(LivingEntity.class, box)) {
                if (livingEntity != player && player.getVehicle() != livingEntity) {
                    livingEntity.hurt(DamageSource.playerAttack(player).bypassMagic(), (float) (6 + player.getRandom().nextInt(3)));
                }
            }

            if (waveList.isEmpty()) {
                HulkHammerItem.WAVES.remove(player.getUUID());
            }
        }
    }

    public static Optional<Unit> onItemToss(ItemEntity entityItem, Player player) {
        Level world = player.level;
        if (!world.isClientSide) {
            ItemStack stack = entityItem.getItem();
            boolean isHand = stack.getItem() == ModRegistry.ENDERSOUL_HAND_ITEM.get() && stack.isDamaged();
            if (stack.getItem() == Items.ENDER_EYE || isHand) {
                int count = 0;

                for (EndersoulFragment orb : world.getEntitiesOfClass(EndersoulFragment.class, player.getBoundingBox().inflate(8.0))) {
                    if (orb.getOwner() == player) {
                        ++count;
                        orb.discard();
                    }
                }

                if (count > 0) {
                    EntityUtil.sendParticlePacket(player, ModRegistry.ENDERSOUL_PARTICLE_TYPE.get(), 256);
                    int addDmg = count * 60;
                    if (isHand) {
                        int dmg = stack.getDamageValue() - addDmg;
                        stack.setDamageValue(Math.max(dmg, 0));
                    } else {
                        ItemStack newStack = new ItemStack(ModRegistry.ENDERSOUL_HAND_ITEM.get());
                        newStack.setDamageValue(ModRegistry.ENDERSOUL_HAND_ITEM.get().getMaxDamage() - addDmg);
                        entityItem.setItem(newStack);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static void playShoulderEntitySound(Player player, @Nullable CompoundTag compoundNBT) {
        if (compoundNBT != null && !compoundNBT.contains("Silent") || !compoundNBT.getBoolean("Silent")) {
            EntityType.byString(compoundNBT.getString("id")).filter(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get()::equals).ifPresent((entityType) -> {
                if (player.level.random.nextInt(500) == 0) {
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModRegistry.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.get(), player.getSoundSource(), 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F + 1.5F);
                }
            });
        }
    }
}
