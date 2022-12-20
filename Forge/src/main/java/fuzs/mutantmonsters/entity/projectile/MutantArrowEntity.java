package fuzs.mutantmonsters.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

import java.util.ArrayList;
import java.util.List;

public class MutantArrowEntity extends Entity {
    private static final EntityDataAccessor<Integer> TARGET_X;
    private static final EntityDataAccessor<Integer> TARGET_Y;
    private static final EntityDataAccessor<Integer> TARGET_Z;
    private static final EntityDataAccessor<Float> SPEED;
    private static final EntityDataAccessor<Integer> CLONES;
    private int damage;
    private final List<Entity> pointedEntities;
    private MobEffectInstance effectInstance;
    private LivingEntity shooter;

    public MutantArrowEntity(EntityType<? extends MutantArrowEntity> type, Level world) {
        super(type, world);
        this.damage = 10 + this.random.nextInt(3);
        this.pointedEntities = new ArrayList<>();
        this.noPhysics = true;
    }

    public MutantArrowEntity(Level world, LivingEntity shooter, LivingEntity target) {
        this(MBEntityType.MUTANT_ARROW, world);
        this.shooter = shooter;
        if (!world.isClientSide) {
            this.setTargetX(target.getX());
            this.setTargetY(target.getY());
            this.setTargetZ(target.getZ());
        }

        double yPos = shooter.getEyeY();
        if (shooter instanceof MutantSkeletonEntity) {
            yPos = shooter.getY() + 1.28;
        }

        this.setPos(shooter.getX(), yPos, shooter.getZ());
        double x = this.getTargetX() - this.getX();
        double y = this.getTargetY() - this.getY();
        double z = this.getTargetZ() - this.getZ();
        double d = Math.sqrt(x * x + z * z);
        this.setYRot(180.0F + (float)Math.toDegrees(Math.atan2(x, z)));
        this.setXRot((float)Math.toDegrees(Math.atan2(y, d)));
    }

    public MutantArrowEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(MBEntityType.MUTANT_ARROW, world);
    }

    protected void defineSynchedData() {
        this.entityData.define(TARGET_X, 0);
        this.entityData.define(TARGET_Y, 0);
        this.entityData.define(TARGET_Z, 0);
        this.entityData.define(SPEED, 12.0F);
        this.entityData.define(CLONES, 10);
    }

    public double getTargetX() {
        return (double)(Integer)this.entityData.get(TARGET_X) / 10000.0;
    }

    public void setTargetX(double targetX) {
        this.entityData.set(TARGET_X, (int)(targetX * 10000.0));
    }

    public double getTargetY() {
        return (double)(Integer)this.entityData.get(TARGET_Y) / 10000.0;
    }

    public void setTargetY(double targetY) {
        this.entityData.set(TARGET_Y, (int)(targetY * 10000.0));
    }

    public double getTargetZ() {
        return (double)(Integer)this.entityData.get(TARGET_Z) / 10000.0;
    }

    public void setTargetZ(double targetZ) {
        this.entityData.set(TARGET_Z, (int)(targetZ * 10000.0));
    }

    public float getSpeed() {
        return (Float)this.entityData.get(SPEED) / 10.0F;
    }

    public void setSpeed(float speed) {
        this.entityData.set(SPEED, speed * 10.0F);
    }

    public int getClones() {
        return (Integer)this.entityData.get(CLONES);
    }

    public void setClones(int clones) {
        this.entityData.set(CLONES, clones);
    }

    public void randomize(float scale) {
        this.setTargetX(this.getTargetX() + (double)((this.random.nextFloat() - 0.5F) * scale * 2.0F));
        this.setTargetY(this.getTargetY() + (double)((this.random.nextFloat() - 0.5F) * scale * 2.0F));
        this.setTargetZ(this.getTargetZ() + (double)((this.random.nextFloat() - 0.5F) * scale * 2.0F));
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setPotionEffect(MobEffectInstance effectInstance) {
        this.effectInstance = effectInstance;
    }

    public void tick() {
        super.tick();
        double x = this.getTargetX() - this.getX();
        double y = this.getTargetY() - this.getY();
        double z = this.getTargetZ() - this.getZ();
        double d = Math.sqrt(x * x + z * z);
        this.setYRot(180.0F + (float)Math.toDegrees(Math.atan2(x, z)));
        if (this.getYRot() > 360.0F) {
            this.setYRot(this.getYRot() - 360.0F);
        }

        this.setXRot((float)Math.toDegrees(Math.atan2(y, d)));
        if (!this.level.isClientSide) {
            if (this.tickCount == 2) {
                this.hitEntities(0);
            }

            if (this.tickCount == 3) {
                this.hitEntities(32);
            }

            if (this.tickCount == 4) {
                this.handleEntities();
            }
        }

        if (this.tickCount > 10) {
            this.discard();
        }

    }

    protected void hitEntities(int offset) {
        double targetX = this.getTargetX();
        double targetY = this.getTargetY();
        double targetZ = this.getTargetZ();
        double d3 = this.getX() - targetX;
        double d4 = this.getY() - targetY;
        double d5 = this.getZ() - targetZ;
        double dist = (double) Mth.sqrt((float) (d3 * d3 + d4 * d4 + d5 * d5));
        double dx = (targetX - this.getX()) / dist;
        double dy = (targetY - this.getY()) / dist;
        double dz = (targetZ - this.getZ()) / dist;

        for(int i = offset; i < offset + 200; ++i) {
            double x = this.getX() + dx * (double)i * 0.5;
            double y = this.getY() + dy * (double)i * 0.5;
            double z = this.getZ() + dz * (double)i * 0.5;
            AABB box = (new AABB(x, y, z, x, y, z)).inflate(0.3);
            this.pointedEntities.addAll(this.level.getEntities(this.shooter, box, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isPickable)));
        }

    }

    protected void handleEntities() {

        for (Entity entity : this.pointedEntities) {
            DamageSource damageSource = (new IndirectEntityDamageSource("arrow", this, this.shooter) {
                public Vec3 getSourcePosition() {
                    return null;
                }
            }).setProjectile();
            if (entity instanceof EnderDragonPart) {
                damageSource.setExplosion();
            }

            if (entity.hurt(damageSource, (float) this.damage)) {
                if (!this.isSilent()) {
                    this.level.playSound( null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_HIT, this.getSoundSource(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                }

                if (this.effectInstance != null && entity instanceof LivingEntity) {
                    ((LivingEntity) entity).addEffect(this.effectInstance);
                }
            }
        }

        this.pointedEntities.clear();
    }

    public void addAdditionalSaveData(CompoundTag compound) {
    }

    public void readAdditionalSaveData(CompoundTag compound) {
    }

    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static {
        TARGET_X = SynchedEntityData.defineId(MutantArrowEntity.class, EntityDataSerializers.INT);
        TARGET_Y = SynchedEntityData.defineId(MutantArrowEntity.class, EntityDataSerializers.INT);
        TARGET_Z = SynchedEntityData.defineId(MutantArrowEntity.class, EntityDataSerializers.INT);
        SPEED = SynchedEntityData.defineId(MutantArrowEntity.class, EntityDataSerializers.FLOAT);
        CLONES = SynchedEntityData.defineId(MutantArrowEntity.class, EntityDataSerializers.INT);
    }
}
