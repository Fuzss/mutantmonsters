package fuzs.mutantmonsters.world.level;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GenericExplosionHelper {

    public static Explosion explode(ExplosionFactory factory, Level level, @Nullable Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory, level, source, Explosion.getDefaultDamageSource(level, source), null, x, y, z, radius,
                false, explosionInteraction, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    public static Explosion explode(ExplosionFactory factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, SoundEvent explosionSound) {
        Explosion explosion = explode(factory, level, source, damageSource, damageCalculator, x, y, z, radius, fire,
                explosionInteraction, level.isClientSide, smallExplosionParticles, largeExplosionParticles,
                explosionSound
        );

        if (!level.isClientSide) {

            if (!explosion.interactsWithBlocks()) {
                explosion.clearToBlow();
            }

            for (ServerPlayer serverplayer : ((ServerLevel) level).players()) {
                if (serverplayer.distanceToSqr(x, y, z) < 4096.0) {
                    serverplayer.connection.send(new ClientboundExplodePacket(x, y, z, radius, explosion.getToBlow(),
                            explosion.getHitPlayers().get(serverplayer), explosion.getBlockInteraction(),
                            explosion.getSmallExplosionParticles(), explosion.getLargeExplosionParticles(),
                            explosion.getExplosionSound()
                    ));
                }
            }
        }

        return explosion;
    }

    private static Explosion explode(ExplosionFactory factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, boolean spawnParticles, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, SoundEvent explosionSound) {
        Explosion explosion = factory.create(level, source, damageSource, damageCalculator, x, y, z, radius, fire,
                getBlockInteraction(level, source, explosionInteraction), smallExplosionParticles,
                largeExplosionParticles, explosionSound
        );
        if (fuzs.mutantmonsters.core.CommonAbstractions.INSTANCE.onExplosionStart(level, explosion)) {
            return explosion;
        } else {
            explosion.explode();
            explosion.finalizeExplosion(spawnParticles);
            return explosion;
        }
    }

    private static Explosion.BlockInteraction getBlockInteraction(Level level, @Nullable Entity source, Level.ExplosionInteraction explosionInteraction) {
        return switch (explosionInteraction) {
            case NONE -> Explosion.BlockInteraction.KEEP;
            case BLOCK -> getDestroyType(level.getGameRules(), GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
            case MOB -> CommonAbstractions.INSTANCE.getMobGriefingRule(level, source) ?
                    getDestroyType(level.getGameRules(), GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) :
                    Explosion.BlockInteraction.KEEP;
            case TNT -> getDestroyType(level.getGameRules(), GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
            case BLOW -> Explosion.BlockInteraction.TRIGGER_BLOCK;
        };
    }

    private static Explosion.BlockInteraction getDestroyType(GameRules gameRules, GameRules.Key<GameRules.BooleanValue> gameRule) {
        return gameRules.getBoolean(gameRule) ?
                Explosion.BlockInteraction.DESTROY_WITH_DECAY :
                Explosion.BlockInteraction.DESTROY;
    }

    public static Explosion explode(ExplosionFactory factory, Level level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory, level, source, Explosion.getDefaultDamageSource(level, source), null, x, y, z, radius,
                fire, explosionInteraction, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    public static Explosion explode(ExplosionFactory factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, Vec3 pos, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory, level, source, damageSource, damageCalculator, pos.x(), pos.y(), pos.z(), radius, fire,
                explosionInteraction, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    public static Explosion explode(ExplosionFactory factory, Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction) {
        return explode(factory, level, source, damageSource, damageCalculator, x, y, z, radius, fire,
                explosionInteraction, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
        );
    }

    /**
     * A simple reference for an {@link Explosion} constructor.
     */
    @FunctionalInterface
    public interface ExplosionFactory {

        Explosion create(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, SoundEvent explosionSound);
    }
}
