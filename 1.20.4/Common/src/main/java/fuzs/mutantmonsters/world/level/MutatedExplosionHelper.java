package fuzs.mutantmonsters.world.level;

import fuzs.mutantmonsters.util.EntityUtil;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import fuzs.mutantmonsters.world.entity.SkullSpirit;
import fuzs.mutantmonsters.world.entity.mutant.MutantCreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

import java.util.List;

public class MutatedExplosionHelper {

    public static void onExplosionDetonate(Level level, Explosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities) {
        if (explosion.damageCalculator instanceof MutatedExplosionDamageCalculator) {
            affectedEntities.removeIf(entity -> {
                return !isAffectedByExplosion(explosion, entity);
            });
        }
    }

    private static boolean isAffectedByExplosion(Explosion explosion, Entity entity) {
        if (entity.ignoreExplosion(explosion)) {
            return false;
        } else if (entity instanceof MutantCreeper) {
            return false;
        } else if (explosion.getDirectSourceEntity() instanceof SkullSpirit skullSpirit) {
            if (entity == skullSpirit.getTarget()) {
                return !skullSpirit.isAttached();
            } else {
                return !(entity instanceof LivingEntity livingEntity) ||
                        ChemicalXMobEffect.IS_APPLICABLE.test(livingEntity);
            }
        } else {
            return true;
        }
    }

    public static Explosion explode(Entity source, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return source.level()
                .explode(source, Explosion.getDefaultDamageSource(source.level(), source),
                        new MutatedExplosionDamageCalculator(), source.getX(), source.getY(), source.getZ(), radius,
                        fire, explosionInteraction, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
                        SoundEvents.GENERIC_EXPLODE
                );
    }

    public static class MutatedExplosionDamageCalculator extends ExplosionDamageCalculator {

        @Override
        public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
            if (entity instanceof Player player && player.isBlocking() &&
                    explosion.getDirectSourceEntity() instanceof MutantCreeper mutantCreeper) {
                float entityDamageAmount = this.getEntityDamageAmount(explosion, entity);
                if (!entity.hurt(explosion.damageSource, entityDamageAmount)) {
                    if (mutantCreeper.isJumpAttacking()) {
                        EntityUtil.disableShield(player, mutantCreeper.isCharged() ? 200 : 100);
                    } else {
                        player.getUseItem().hurtAndBreak((int) entityDamageAmount * 2, player, (player1) -> {
                            player1.broadcastBreakEvent(player.getUsedItemHand());
                        });
                    }
                    entity.hurt(explosion.damageSource, entityDamageAmount * 0.5F);
                }

                return false;
            }

            return super.shouldDamageEntity(explosion, entity);
        }
    }
}
