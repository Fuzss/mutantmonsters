package fuzs.mutantmonsters.util;

import fuzs.mutantmonsters.core.CommonAbstractions;
import fuzs.mutantmonsters.entity.SkullSpirit;
import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import fuzs.mutantmonsters.world.effect.ChemicalXMobEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MutatedExplosion extends Explosion {
    private final Level world;
    private final float size;

    private MutatedExplosion(Level worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Explosion.BlockInteraction modeIn) {
        super(worldIn, exploderIn, null, null, xIn, yIn, zIn, sizeIn, causesFireIn, modeIn);
        this.world = worldIn;
        this.size = sizeIn;
    }

    @Override
    public void explode() {
        if (!(this.size <= 0.0F)) {
            Set<BlockPos> set = new HashSet<>();
            Entity exploder = CommonAbstractions.INSTANCE.getExplosionExploder(this);
            Vec3 position = CommonAbstractions.INSTANCE.getExplosionPosition(this);
            ExplosionDamageCalculator explosionContext = exploder == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(exploder);

            int k;
            int l;
            double x;
            double y;
            double z;
            for(int j = 0; j < 16; ++j) {
                for(k = 0; k < 16; ++k) {
                    for(l = 0; l < 16; ++l) {
                        if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                            double d0 = (float)j / 15.0F * 2.0F - 1.0F;
                            double d1 = (float)k / 15.0F * 2.0F - 1.0F;
                            double d2 = (float)l / 15.0F * 2.0F - 1.0F;
                            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                            d0 /= d3;
                            d1 /= d3;
                            d2 /= d3;
                            float intensity = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                            x = position.x;
                            y = position.y;
                            z = position.z;

                            for(float attenuation = 0.3F; intensity > 0.0F; intensity -= 0.22500001F) {
                                BlockPos blockpos = new BlockPos(x, y, z);
                                BlockState blockstate = this.world.getBlockState(blockpos);
                                Optional<Float> optional = (explosionContext).getBlockExplosionResistance(this, this.world, blockpos, blockstate, Fluids.EMPTY.defaultFluidState());
                                if (optional.isPresent()) {
                                    intensity -= (optional.get() + attenuation) * attenuation;
                                }

                                if (intensity > 0.0F && (explosionContext).shouldBlockExplode(this, this.world, blockpos, blockstate, intensity)) {
                                    set.add(blockpos);
                                }

                                x += d0 * (double)attenuation;
                                y += d1 * (double)attenuation;
                                z += d2 * (double)attenuation;
                            }
                        }
                    }
                }
            }

            this.getToBlow().addAll(set);
            float diameter = this.size * 2.0F;
            k = Mth.floor(position.x - (double)diameter - 1.0);
            l = Mth.floor(position.x + (double)diameter + 1.0);
            int minY = Mth.floor(position.y - (double)diameter - 1.0);
            int maxY = Mth.floor(position.y + (double)diameter + 1.0);
            int minZ = Mth.floor(position.z - (double)diameter - 1.0);
            int maxZ = Mth.floor(position.z + (double)diameter + 1.0);
            List<Entity> list = this.world.getEntities(exploder, new AABB(k, minY, minZ, l, maxY, maxZ), (entityx) -> {
                if (entityx.ignoreExplosion()) {
                    return false;
                } else if (exploder instanceof SkullSpirit skullSpirit) {
                    if (entityx == skullSpirit.getTarget()) {
                        return !skullSpirit.isAttached();
                    } else {
                        return !(entityx instanceof LivingEntity) || ChemicalXMobEffect.IS_APPLICABLE.test((LivingEntity)entityx);
                    }
                } else {
                    return true;
                }
            });
            CommonAbstractions.INSTANCE.onExplosionDetonate(this.world, this, list, (double)diameter);
            Iterator<Entity> var11 = list.iterator();

            while(true) {
                Player playerentity;
                double impact;
                do {
                    do {
                        Entity entity;
                        do {
                            double distance;
                            double d0;
                            do {
                                do {
                                    if (!var11.hasNext()) {
                                        return;
                                    }

                                    entity = var11.next();
                                    distance = (Mth.sqrt((float) entity.distanceToSqr(position)) / diameter);
                                } while(!(distance <= 1.0));

                                x = entity.getX() - position.x;
                                y = entity.getEyeY() - position.y;
                                z = entity.getZ() - position.z;
                                d0 = Mth.sqrt((float) (x * x + y * y + z * z));
                            } while(d0 == 0.0);

                            x /= d0;
                            y /= d0;
                            z /= d0;
                            impact = (1.0 - distance) * (double)getSeenPercent(position, entity);
                            float damage = (float)((int)((impact * impact + impact) / 2.0 * 6.0 * (double)diameter + 1.0));
                            if (!entity.hurt(this.getDamageSource(), damage) && exploder instanceof MutantCreeperEntity mutantCreeper && entity instanceof Player player && ((Player)entity).isBlocking()) {
                                if (mutantCreeper.isJumpAttacking()) {
                                    EntityUtil.disableShield(player, mutantCreeper.isCharged() ? 200 : 100);
                                    entity.hurt(this.getDamageSource(), damage * 0.5F);
                                } else {
                                    player.getUseItem().hurtAndBreak((int)damage * 2, player, (e) -> {
                                        e.broadcastBreakEvent(player.getUsedItemHand());
                                    });
                                    entity.hurt(this.getDamageSource(), damage * 0.5F);
                                }
                            }

                            double exposure = impact;
                            if (entity instanceof LivingEntity) {
                                exposure = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, impact);
                            }

                            if (!(entity instanceof MutantCreeperEntity)) {
                                entity.setDeltaMovement(entity.getDeltaMovement().add(x * exposure, y * exposure, z * exposure));
                            }
                        } while(!(entity instanceof Player));

                        playerentity = (Player)entity;
                    } while(playerentity.isSpectator());
                } while(playerentity.isCreative() && playerentity.getAbilities().flying);

                this.getHitPlayers().put(playerentity, new Vec3(x * impact, y * impact, z * impact));
            }
        }
    }

    public static MutatedExplosion create(@Nonnull Entity exploderIn, float sizeIn, boolean causesFireIn, Explosion.BlockInteraction mode) {
        return create(exploderIn.level, exploderIn, exploderIn.getX(), exploderIn.getY(), exploderIn.getZ(), sizeIn, causesFireIn, mode);
    }

    public static MutatedExplosion create(Level worldIn, @Nullable Entity exploderIn, double xIn, double yIn, double zIn, float sizeIn, boolean causesFireIn, Explosion.BlockInteraction mode) {
        if (exploderIn instanceof Mob && !CommonAbstractions.INSTANCE.getMobGriefingEvent(worldIn, exploderIn)) {
            mode = BlockInteraction.NONE;
        }

        MutatedExplosion explosion = new MutatedExplosion(worldIn, exploderIn, xIn, yIn, zIn, sizeIn, causesFireIn, mode);
        if (CommonAbstractions.INSTANCE.onExplosionStart(worldIn, explosion)) {
            return explosion;
        } else {
            if (worldIn instanceof ServerLevel) {
                explosion.explode();
                explosion.finalizeExplosion(false);
                if (mode == BlockInteraction.NONE) {
                    explosion.clearToBlow();
                }

                for (ServerPlayer serverplayerentity : ((ServerLevel) worldIn).players()) {
                    if (serverplayerentity.distanceToSqr(xIn, yIn, zIn) < 4096.0) {
                        serverplayerentity.connection.send(new ClientboundExplodePacket(xIn, yIn, zIn, sizeIn, explosion.getToBlow(), explosion.getHitPlayers().get(serverplayerentity)));
                    }
                }
            }

            return explosion;
        }
    }
}
