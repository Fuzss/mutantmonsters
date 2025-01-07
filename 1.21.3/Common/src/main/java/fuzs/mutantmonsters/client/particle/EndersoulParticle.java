package fuzs.mutantmonsters.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class EndersoulParticle extends TextureSheetParticle {

    private EndersoulParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
        this.lifetime = (int) (Math.random() * 15.0) + 10;
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.xd += xSpeedIn;
        this.yd += ySpeedIn;
        this.zd += zSpeedIn;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.4F + 2.4F);
        float color = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = color * 0.9F;
        this.gCol = color * 0.3F;
        this.bCol = color;
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float scale = 1.0F - ((float) this.age + partialTicks) / (float) this.lifetime;
        scale *= scale;
        scale = 1.0F - scale;
        return this.quadSize * scale;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.yd += 0.002;
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }

        this.xd *= 0.9;
        this.yd *= 0.9;
        this.zd *= 0.9;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        @Nullable
        public Particle createParticle(SimpleParticleType particleType, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            EndersoulParticle endersoulParticle = new EndersoulParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
            endersoulParticle.pickSprite(this.spriteSet);
            return endersoulParticle;
        }
    }
}
