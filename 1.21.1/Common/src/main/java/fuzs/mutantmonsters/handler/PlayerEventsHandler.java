package fuzs.mutantmonsters.handler;

import fuzs.mutantmonsters.capability.SeismicWavesCapability;
import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.world.level.SeismicWave;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.entity.EndersoulFragment;
import fuzs.puzzleslib.api.entity.v1.DamageSourcesHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

public class PlayerEventsHandler {
    private static final int MAX_SEISMIC_WAVES_PER_PLAYER = 16;

    public static EventResult onItemUseTick(LivingEntity entity, ItemStack useItem, MutableInt useItemRemaining) {
        // quick charge for bows
        if (entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.value()) {
            if (useItem.getItem() instanceof BowItem && BowItem.getPowerForTime(useItem.getUseDuration() - useItemRemaining.getAsInt()) < 1.0F) {
                useItemRemaining.mapInt(i -> i - 2);
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onArrowLoose(Player player, ItemStack stack, Level level, MutableInt charge, boolean hasAmmo) {
        // multi-shot for bows
        if (!(stack.getItem() instanceof BowItem)) return EventResult.PASS;
        if (hasAmmo && player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModRegistry.MUTANT_SKELETON_SKULL_ITEM.value()) {
            float velocity = BowItem.getPowerForTime(charge.getAsInt());
            if (!level.isClientSide && velocity >= 0.1F) {
                ItemStack itemstack = player.getProjectile(stack);
                ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                float[] shotPitches = getShotPitches(level.random, velocity);
                for (int i = 0; i < 2; i++) {
                    AbstractArrow abstractarrow = arrowitem.createArrow(level, itemstack, player);
                    abstractarrow = CommonAbstractions.INSTANCE.getCustomArrowShotFromBow((BowItem) stack.getItem(), abstractarrow, itemstack);
                    abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.5F);
                    applyPowerEnchantment(abstractarrow, stack);
                    applyPunchEnchantment(abstractarrow, stack);
                    applyFlameEnchantment(abstractarrow, stack);
                    applyPiercingEnchantment(abstractarrow, stack);
                    abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                    level.addFreshEntity(abstractarrow);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, shotPitches[i + 1]);
                }
            }
        }
        return EventResult.PASS;
    }

    private static float[] getShotPitches(RandomSource random, float velocity) {
        boolean flag = random.nextBoolean();
        return new float[]{1.0F, getRandomShotPitch(flag, random, velocity), getRandomShotPitch(!flag, random, velocity)};
    }

    private static float getRandomShotPitch(boolean p_150798_, RandomSource random, float velocity) {
        float f = p_150798_ ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f * velocity;
    }

    public static void applyPowerEnchantment(AbstractArrow arrow, ItemStack stack) {
        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (powerLevel > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5 + 0.5);
        }
    }

    public static void applyPunchEnchantment(AbstractArrow arrow, ItemStack stack) {
        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
        if (punchLevel > 0) {
            arrow.setKnockback(punchLevel);
        }
    }

    public static void applyFlameEnchantment(AbstractArrow arrow, ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
            arrow.setSecondsOnFire(100);
        }
    }

    public static void applyPiercingEnchantment(AbstractArrow arrow, ItemStack stack) {
        int pierceLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, stack);
        if (pierceLevel > 0) {
            arrow.setPierceLevel((byte) pierceLevel);
        }
    }

    public static void onPlayerTick$End(Player player) {
        playShoulderEntitySound(player, player.getShoulderEntityLeft());
        playShoulderEntitySound(player, player.getShoulderEntityRight());
        if (!player.level().isClientSide) {
            SeismicWavesCapability capability = ModRegistry.SEISMIC_WAVES_CAPABILITY.get(player);
            Queue<SeismicWave> seismicWaves = capability.getSeismicWaves();
            int oldSize = seismicWaves.size();
            while (seismicWaves.size() > MAX_SEISMIC_WAVES_PER_PLAYER) {
                seismicWaves.poll();
            }
            if (!seismicWaves.isEmpty()) {
                handleSeismicWave(player, seismicWaves.poll());
            }
            if (oldSize != seismicWaves.size()) {
                capability.setChanged();
            }
        }
    }

    private static void handleSeismicWave(Player player, @NotNull SeismicWave seismicWave) {
        seismicWave.affectBlocks(player.level(), player);
        AABB box = new AABB(seismicWave.getX(), (double) seismicWave.getY() + 1.0, seismicWave.getZ(), (double) seismicWave.getX() + 1.0, (double) seismicWave.getY() + 2.0, (double) seismicWave.getZ() + 1.0);

        for (LivingEntity livingEntity : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (livingEntity != player && player.getVehicle() != livingEntity) {
                livingEntity.hurt(DamageSourcesHelper.source(player.level(), ModRegistry.PLAYER_SEISMIC_WAVE_DAMAGE_TYPE, player), (float) (6 + player.getRandom().nextInt(3)));
            }
        }
    }

    private static void playShoulderEntitySound(Player player, @Nullable CompoundTag compoundNBT) {
        if (compoundNBT != null && !compoundNBT.contains("Silent") || !compoundNBT.getBoolean("Silent")) {
            EntityType.byString(compoundNBT.getString("id")).filter(ModRegistry.CREEPER_MINION_ENTITY_TYPE.value()::equals).ifPresent((entityType) -> {
                if (player.level().random.nextInt(500) == 0) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModRegistry.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.value(), player.getSoundSource(), 1.0F, (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F + 1.5F);
                }
            });
        }
    }

    public static EventResult onItemToss(Player player, ItemEntity entityItem) {
        if (!player.level().isClientSide) {
            ItemStack stack = entityItem.getItem();
            boolean isHand = stack.getItem() == ModRegistry.ENDERSOUL_HAND_ITEM.value() && stack.isDamaged();
            if (stack.getItem() == Items.ENDER_EYE || isHand) {
                int count = 0;

                for (EndersoulFragment orb : player.level().getEntitiesOfClass(EndersoulFragment.class, player.getBoundingBox().inflate(8.0))) {
                    if (orb.getOwner() == player) {
                        ++count;
                        orb.discard();
                    }
                }

                if (count > 0) {
                    EntityUtil.sendParticlePacket(player, ModRegistry.ENDERSOUL_PARTICLE_TYPE.value(), 256);
                    int addDmg = count * 60;
                    if (isHand) {
                        int dmg = stack.getDamageValue() - addDmg;
                        stack.setDamageValue(Math.max(dmg, 0));
                    } else {
                        ItemStack newStack = new ItemStack(ModRegistry.ENDERSOUL_HAND_ITEM.value());
                        newStack.setDamageValue(ModRegistry.ENDERSOUL_HAND_ITEM.value().getMaxDamage() - addDmg);
                        entityItem.setItem(newStack);
                    }
                }
            }
        }
        return EventResult.PASS;
    }
}
