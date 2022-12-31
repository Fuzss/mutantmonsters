package fuzs.mutantmonsters;

import fuzs.mutantmonsters.entity.CreeperMinionEntity;
import fuzs.mutantmonsters.entity.EndersoulFragmentEntity;
import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import fuzs.mutantmonsters.entity.mutant.MutantZombieEntity;
import fuzs.mutantmonsters.entity.mutant.SpiderPigEntity;
import fuzs.mutantmonsters.init.ModRegistry;
import fuzs.mutantmonsters.item.ArmorBlockItem;
import fuzs.mutantmonsters.item.HulkHammerItem;
import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.util.SeismicWave;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(
        modid = MutantMonsters.MOD_ID
)
public class EventHandler {
    public EventHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof PathfinderMob creature) {
            if (EntityUtil.isFeline(creature)) {
                creature.goalSelector.addGoal(2, new AvoidEntityGoal<>(creature, MutantCreeperEntity.class, 16.0F, 1.33, 1.33));
            }

            if (creature.getType() == EntityType.PIG) {
                creature.goalSelector.addGoal(2, new TemptGoal(creature, 1.0, Ingredient.of(Items.FERMENTED_SPIDER_EYE), false));
            }

            if (creature.getType() == EntityType.VILLAGER) {
                creature.goalSelector.addGoal(0, new AvoidEntityGoal<>(creature, MutantZombieEntity.class, 12.0F, 0.8, 0.8));
            }

            if (creature.getType() == EntityType.WANDERING_TRADER) {
                creature.goalSelector.addGoal(1, new AvoidEntityGoal<>(creature, MutantZombieEntity.class, 12.0F, 0.5, 0.5));
            }
        }

    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (event.getTarget().getType() == EntityType.PIG && !((LivingEntity)event.getTarget()).hasEffect(MobEffects.UNLUCK) && stack.getItem() == Items.FERMENTED_SPIDER_EYE) {
            if (!event.getEntity().isCreative()) {
                stack.shrink(1);
            }

            ((LivingEntity)event.getTarget()).addEffect(new MobEffectInstance(MobEffects.UNLUCK, 600, 13));
            event.setCancellationResult(InteractionResult.SUCCESS);
        }

    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorBlockItem) {
            float damage = event.getAmount();
            if (!(damage <= 0.0F)) {
                damage /= 4.0F;
                if (damage < 1.0F) {
                    damage = 1.0F;
                }

                ItemStack itemstack = event.getEntity().getItemBySlot(EquipmentSlot.HEAD);
                if (!event.getSource().isFire() || !itemstack.getItem().isFireResistant()) {
                    itemstack.hurtAndBreak((int)damage, event.getEntity(), (livingEntity) -> {
                        livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD);
                    });
                }
            }
        }

    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity trueSource = event.getSource().getEntity();
        if (SpiderPigEntity.isPigOrSpider(event.getEntity()) && trueSource instanceof SpiderPigEntity) {
            event.setCanceled(true);
        }

        if ((trueSource instanceof MutantCreeperEntity && ((MutantCreeperEntity)trueSource).isCharged() || trueSource instanceof CreeperMinionEntity && ((CreeperMinionEntity)trueSource).isCharged()) && event.getSource().isExplosion()) {
            ItemStack itemStack = EntityUtil.getSkullDrop(event.getEntity().getType());
            if (!itemStack.isEmpty()) {
                event.getDrops().add(new ItemEntity(trueSource.level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), itemStack));
            }
        }

    }

    @SubscribeEvent
    public static void onLivingUseItem(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity().getItemBySlot(EquipmentSlot.CHEST).getItem() == ModRegistry.MUTANT_SKELETON_CHESTPLATE_ITEM.get() && event.getItem().getUseAnimation() == UseAnim.BOW && event.getDuration() > 4) {
            event.setDuration(event.getDuration() - 3);
        }

    }

    @SubscribeEvent
    public static void onPlayerShootArrow(ArrowLooseEvent event) {
        if (event.getEntity().getItemBySlot(EquipmentSlot.HEAD).getItem() == ModRegistry.MUTANT_SKELETON_SKULL_ITEM.get() && event.hasAmmo()) {
            event.setCanceled(true);
            Player player = event.getEntity();
            Level world = event.getLevel();
            ItemStack bow = event.getBow();
            boolean inAir = !player.isOnGround() && !player.isInWater() && !player.isInLava();
            ItemStack ammo = player.getProjectile(bow);
            if (!ammo.isEmpty() || event.hasAmmo()) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }

                float velocity = BowItem.getPowerForTime(bow.getUseDuration() - event.getCharge());
                boolean infiniteArrows = player.getAbilities().instabuild || ammo.getItem() instanceof ArrowItem && ((ArrowItem)ammo.getItem()).isInfinite(ammo, bow, player);
                if (!world.isClientSide) {
                    ArrowItem arrowitem = (ArrowItem)((ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW));
                    AbstractArrow abstractarrowentity = arrowitem.createArrow(world, ammo, player);
                    abstractarrowentity = ((BowItem)bow.getItem()).customArrow(abstractarrowentity);
                    abstractarrowentity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
                    if (velocity == 1.0F && inAir) {
                        abstractarrowentity.setCritArrow(true);
                    }

                    int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow);
                    if (j > 0) {
                        abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double)j * 0.5 + 0.5);
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

                    world.addFreshEntity(abstractarrowentity);
                }

                world.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                if (!infiniteArrows && !player.getAbilities().instabuild) {
                    ammo.shrink(1);
                    if (ammo.isEmpty()) {
                        player.getInventory().removeItem(ammo);
                    }
                }

                player.awardStat(Stats.ITEM_USED.get(bow.getItem()));
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        playShoulderEntitySound(event.player, event.player.getShoulderEntityLeft());
        playShoulderEntitySound(event.player, event.player.getShoulderEntityRight());
        if (!event.player.level.isClientSide && !HulkHammerItem.WAVES.isEmpty() && HulkHammerItem.WAVES.containsKey(event.player.getUUID())) {
            Player player = event.player;
            List<SeismicWave> waveList = HulkHammerItem.WAVES.get(player.getUUID());

            while(waveList.size() > 16) {
                waveList.remove(0);
            }

            SeismicWave wave = (SeismicWave)waveList.remove(0);
            wave.affectBlocks(player.level, player);
            AABB box = new AABB((double)wave.getX(), (double)wave.getY() + 1.0, (double)wave.getZ(), (double)wave.getX() + 1.0, (double)wave.getY() + 2.0, (double)wave.getZ() + 1.0);

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

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        Level world = event.getEntity().level;
        Player player = event.getPlayer();
        if (!world.isClientSide) {
            ItemStack stack = event.getEntity().getItem();
            boolean isHand = stack.getItem() == ModRegistry.ENDERSOUL_HAND_ITEM.get() && stack.isDamaged();
            if (stack.getItem() == Items.ENDER_EYE || isHand) {
                int count = 0;

                for (EndersoulFragmentEntity orb : world.getEntitiesOfClass(EndersoulFragmentEntity.class, player.getBoundingBox().inflate(8.0))) {
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
                        newStack.setDamageValue(ModRegistry.ENDERSOUL_HAND_ITEM.get().getMaxDamage(stack) - addDmg);
                        event.getEntity().setItem(newStack);
                    }
                }
            }
        }

    }

//    @SubscribeEvent(
//            priority = EventPriority.HIGH
//    )
//    public static void addBiomeSpawns(BiomeLoadingEvent event) {
//        ResourceLocation name = event.getName();
//        List<? extends String> biomeWhitelist = (List)MBConfig.COMMON.biomeWhitelist.get();
//        if (!biomeWhitelist.isEmpty() && biomeWhitelist.contains(name.getNamespace())) {
//            Biome.Category category = event.getCategory();
//            List<MobSpawnInfo.Spawners> monsterSpawners = event.getSpawns().getSpawner(EntityClassification.MONSTER);
//            if (!monsterSpawners.isEmpty() && category != Category.MUSHROOM) {
//                if (category != Category.NETHER || Biomes.NETHER_WASTES.getRegistryName().equals(name) || Biomes.SOUL_SAND_VALLEY.getRegistryName().equals(name) || Biomes.WARPED_FOREST.getRegistryName().equals(name)) {
//                    addSpawn(monsterSpawners, ModRegistry.MUTANT_ENDERMAN, (Integer)MBConfig.COMMON.mutantEndermanSpawnWeight.get(), 1, 1);
//                    if ((Biomes.SOUL_SAND_VALLEY.getRegistryName().equals(name) || Biomes.WARPED_FOREST.getRegistryName().equals(name)) && (Integer)MBConfig.COMMON.mutantEndermanSpawnWeight.get() > 0) {
//                        event.getSpawns().addMobCharge(ModRegistry.MUTANT_ENDERMAN, 0.7, 0.15);
//                    }
//                }
//
//                if (category != Category.THEEND) {
//                    if (category != Category.NETHER || Biomes.SOUL_SAND_VALLEY.getRegistryName().equals(name)) {
//                        addSpawn(monsterSpawners, ModRegistry.MUTANT_SKELETON, (Integer)MBConfig.COMMON.mutantSkeletonSpawnWeight.get(), 1, 1);
//                        if (Biomes.SOUL_SAND_VALLEY.getRegistryName().equals(name) && (Integer)MBConfig.COMMON.mutantSkeletonSpawnWeight.get() > 0) {
//                            event.getSpawns().addMobCharge(ModRegistry.MUTANT_SKELETON, 0.7, 0.15);
//                        }
//                    }
//
//                    if (category != Category.NETHER) {
//                        addSpawn(monsterSpawners, ModRegistry.MUTANT_CREEPER, (Integer)MBConfig.COMMON.mutantCreeperSpawnWeight.get(), 1, 1);
//                        addSpawn(monsterSpawners, ModRegistry.MUTANT_ZOMBIE, (Integer)MBConfig.COMMON.mutantZombieSpawnWeight.get(), 1, 1);
//                    }
//                }
//            }
//
//        }
//    }
//
//    private static void addSpawn(List<MobSpawnInfo.Spawners> spawners, EntityType<? extends Mob> entityType, int weight, int min, int max) {
//        if (weight > 0) {
//            spawners.add(new MobSpawnInfo.Spawners(entityType, weight, min, max));
//        }
//
//    }

    private static void playShoulderEntitySound(Player player, @Nullable CompoundTag compoundNBT) {
        if (compoundNBT != null && !compoundNBT.contains("Silent") || !compoundNBT.getBoolean("Silent")) {
            EntityType.byString(compoundNBT.getString("id")).filter(ModRegistry.CREEPER_MINION_ENTITY_TYPE.get()::equals).ifPresent((entityType) -> {
                if (player.level.random.nextInt(500) == 0) {
                    player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), ModRegistry.ENTITY_CREEPER_MINION_AMBIENT_SOUND_EVENT.get(), player.getSoundSource(), 1.0F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 0.2F + 1.5F);
                }

            });
        }

    }
}
