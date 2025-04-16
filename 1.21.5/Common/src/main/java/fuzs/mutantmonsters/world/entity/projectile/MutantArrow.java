package fuzs.mutantmonsters.world.entity.projectile;

import fuzs.mutantmonsters.init.ModEntityTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MutantArrow extends Arrow {
    private static final EntityDataAccessor<Integer> DATA_CLONES = SynchedEntityData.defineId(MutantArrow.class, EntityDataSerializers.INT);

    public MutantArrow(EntityType<? extends MutantArrow> type, Level level) {
        super(type, level);
    }

    public MutantArrow(Level level, LivingEntity shooter) {
        this(ModEntityTypes.MUTANT_ARROW_ENTITY_TYPE.value(), level);
        this.setPos(shooter.getX(), shooter.getEyeY() - (double) 0.1F, shooter.getZ());
        this.setNoGravity(true);
        this.setOwner(shooter);
//        this.setBaseDamage(3.0 + this.random.nextInt(3));
        this.setCritArrow(true);
    }

    public void shoot(LivingEntity target, float velocity, float randomization) {
        Vec3 attackLocation = this.getAttackLocation(target, randomization);
        Vec3 attackVector = attackLocation.subtract(this.position());
        double horizontalDistance = attackVector.horizontalDistance();
        double x = attackVector.x;
        double y = attackVector.y;
        double z = attackVector.z;
        this.shoot(x, y, z, velocity, 0.0F);
        this.setYRot(180.0F + (float) Math.toDegrees(Math.atan2(x, z)));
        while (this.getYRot() > 360.0F) {
            this.setYRot(this.getYRot() - 360.0F);
        }
        this.setXRot((float) Math.toDegrees(Math.atan2(y, horizontalDistance)));
    }

    private Vec3 getAttackLocation(LivingEntity target, float randomization) {
        double x = target.getX() + (this.random.nextFloat() - 0.5F) * randomization * 2.0F;
        double y = target.getY(0.8) + (this.random.nextFloat() - 0.5F) * randomization * 2.0F;
        double z = target.getZ() + (this.random.nextFloat() - 0.5F) * randomization * 2.0F;
        return new Vec3(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CLONES, 1);
    }

    public int getClones() {
        return this.entityData.get(DATA_CLONES);
    }

    public void setClones(int clones) {
        this.entityData.set(DATA_CLONES, clones);
    }

    @Override
    public void tick() {
        float xRot = this.getXRot();
        float xRotO = this.xRotO;
        float yRot = this.getYRot();
        float yRotO = this.yRotO;
        super.tick();
        this.setXRot(xRot);
        this.xRotO = xRotO;
        this.setYRot(yRot);
        this.yRotO = yRotO;
        if (!this.isRemoved() && this.tickCount > 200) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (result.getType() != HitResult.Type.MISS) {
            this.discard();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    protected void tickDespawn() {
        this.discard();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, startVec, endVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity, 1.0F);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        if (!this.isSilent()) {
            this.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.CROSSBOW_HIT, this.getSoundSource(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        }
    }
}
