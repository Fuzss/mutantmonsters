package fuzs.mutantmonsters.world.level;

import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;

import java.util.List;

public class MutatedExplosionHelper {

    public static void onExplosionDetonate(ServerLevel serverLevel, ServerExplosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities) {
        if (explosion.damageCalculator instanceof MutatedExplosionDamageCalculator) {
            affectedEntities.removeIf(entity -> {
                return !isAffectedByExplosion(serverLevel, explosion, entity);
            });
        }
    }

    private static boolean isAffectedByExplosion(ServerLevel serverLevel, ServerExplosion explosion, Entity entity) {
        if (entity.ignoreExplosion(explosion)) {
            return false;
        } else if (entity instanceof MutantCreeper) {
            return false;
        } else if (explosion.getDirectSourceEntity() instanceof SkullSpirit skullSpirit) {
            if (entity == skullSpirit.getTarget()) {
                return !skullSpirit.isAttached();
            } else {
                return !(entity instanceof LivingEntity livingEntity) || ChemicalXMobEffect.IS_APPLICABLE.test(
                        livingEntity,
                        serverLevel);
            }
        } else {
            return true;
        }
    }

    public static void explode(Entity entity, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        DamageSource damageSource = Explosion.getDefaultDamageSource(entity.level(), entity);
        MutatedExplosionDamageCalculator damageCalculator = new MutatedExplosionDamageCalculator();
        entity.level()
                .explode(entity,
                        damageSource,
                        damageCalculator,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        radius,
                        fire,
                        explosionInteraction,
                        ParticleTypes.EXPLOSION,
                        ParticleTypes.EXPLOSION_EMITTER,
                        SoundEvents.GENERIC_EXPLODE);
    }

    public static class MutatedExplosionDamageCalculator extends ExplosionDamageCalculator {

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            if (explosion instanceof ServerExplosion serverExplosion && entity instanceof ServerPlayer serverPlayer
                    && serverPlayer.isBlocking()
                    && explosion.getDirectSourceEntity() instanceof MutantCreeper mutantCreeper) {
                float seenPercent;
                if (this.getKnockbackMultiplier(entity) == 0.0F) {
                    seenPercent = 0.0F;
                } else {
                    seenPercent = ServerExplosion.getSeenPercent(explosion.center(), entity);
                }
                float damageAmount = this.getEntityDamageAmount(explosion, entity, seenPercent);
                if (!entity.hurtServer(serverPlayer.level(), serverExplosion.damageSource, damageAmount)) {
                    if (mutantCreeper.isJumpAttacking()) {
                        EntityUtil.disableShield(serverPlayer, mutantCreeper.isCharged() ? 200 : 100);
                    } else {
                        ItemHelper.hurtAndBreak(serverPlayer.getUseItem(),
                                (int) (damageAmount * 2.0F),
                                serverPlayer,
                                serverPlayer.getUsedItemHand());
                    }
                    entity.hurtServer(serverPlayer.level(), serverExplosion.damageSource, damageAmount * 0.5F);
                }

                return false;
            }

            return super.shouldDamageEntity(explosion, entity);
        }
    }
}
