package fuzs.mutantmonsters.util;

import fuzs.mutantmonsters.entity.SkullSpirit;
import fuzs.mutantmonsters.entity.mutant.MutantCreeperEntity;
import fuzs.mutantmonsters.entity.projectile.ChemicalXEntity;
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
import net.minecraftforge.event.ForgeEventFactory;

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
            ExplosionDamageCalculator explosionContext = this.getExploder() == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(this.getExploder());

            int k;
            int l;
            double x;
            double y;
            double z;
            for(int j = 0; j < 16; ++j) {
                for(k = 0; k < 16; ++k) {
                    for(l = 0; l < 16; ++l) {
                        if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                            double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                            double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                            double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                            d0 /= d3;
                            d1 /= d3;
                            d2 /= d3;
                            float intensity = this.size * (0.7F + this.world.random.nextFloat() * 0.6F);
                            x = this.getPosition().x;
                            y = this.getPosition().y;
                            z = this.getPosition().z;

                            for(float attenuation = 0.3F; intensity > 0.0F; intensity -= 0.22500001F) {
                                BlockPos blockpos = new BlockPos(x, y, z);
                                BlockState blockstate = this.world.getBlockState(blockpos);
                                Optional<Float> optional = (explosionContext).getBlockExplosionResistance(this, this.world, blockpos, blockstate, Fluids.EMPTY.defaultFluidState());
                                if (optional.isPresent()) {
                                    intensity -= ((Float)optional.get() + attenuation) * attenuation;
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
            k = Mth.floor(this.getPosition().x - (double)diameter - 1.0);
            l = Mth.floor(this.getPosition().x + (double)diameter + 1.0);
            int minY = Mth.floor(this.getPosition().y - (double)diameter - 1.0);
            int maxY = Mth.floor(this.getPosition().y + (double)diameter + 1.0);
            int minZ = Mth.floor(this.getPosition().z - (double)diameter - 1.0);
            int maxZ = Mth.floor(this.getPosition().z + (double)diameter + 1.0);
            List<Entity> list = this.world.getEntities(this.getExploder(), new AABB((double)k, (double)minY, (double)minZ, (double)l, (double)maxY, (double)maxZ), (entityx) -> {
                if (entityx.ignoreExplosion()) {
                    return false;
                } else if (this.getExploder() instanceof SkullSpirit skullSpirit) {
                    if (entityx == skullSpirit.getTarget()) {
                        return !skullSpirit.isAttached();
                    } else {
                        return !(entityx instanceof LivingEntity) || ChemicalXEntity.IS_APPLICABLE.test((LivingEntity)entityx);
                    }
                } else {
                    return true;
                }
            });
            ForgeEventFactory.onExplosionDetonate(this.world, this, list, (double)diameter);
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

                                    entity = (Entity)var11.next();
                                    distance = (Mth.sqrt((float) entity.distanceToSqr(this.getPosition())) / diameter);
                                } while(!(distance <= 1.0));

                                x = entity.getX() - this.getPosition().x;
                                y = entity.getEyeY() - this.getPosition().y;
                                z = entity.getZ() - this.getPosition().z;
                                d0 = (double)Mth.sqrt((float) (x * x + y * y + z * z));
                            } while(d0 == 0.0);

                            x /= d0;
                            y /= d0;
                            z /= d0;
                            impact = (1.0 - distance) * (double)getSeenPercent(this.getPosition(), entity);
                            float damage = (float)((int)((impact * impact + impact) / 2.0 * 6.0 * (double)diameter + 1.0));
                            if (!entity.hurt(this.getDamageSource(), damage) && this.getExploder() instanceof MutantCreeperEntity mutantCreeper && entity instanceof Player player && ((Player)entity).isBlocking()) {
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
        if (exploderIn instanceof Mob && !ForgeEventFactory.getMobGriefingEvent(worldIn, exploderIn)) {
            mode = BlockInteraction.NONE;
        }

        MutatedExplosion explosion = new MutatedExplosion(worldIn, exploderIn, xIn, yIn, zIn, sizeIn, causesFireIn, mode);
        if (ForgeEventFactory.onExplosionStart(worldIn, explosion)) {
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
