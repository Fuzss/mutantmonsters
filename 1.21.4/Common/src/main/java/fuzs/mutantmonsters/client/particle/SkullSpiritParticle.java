package fuzs.mutantmonsters.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class SkullSpiritParticle extends TextureSheetParticle {

    private SkullSpiritParticle(ClientLevel clientLevel, double x, double y, double z, double xx, double yy, double zz) {
        super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.xd += xx;
        this.yd += yy;
        this.zd += zz;
        float color = 1.0F - (float) (Math.random() * 0.2);
        this.setColor(color, color, color);
        float scale = 0.4F + this.random.nextFloat() * 0.6F;
        this.quadSize *= scale;
        this.lifetime = (int) (8.0 / (Math.random() * 0.8 + 0.2));
        this.lifetime = (int) ((float) this.lifetime * scale);
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float partialTicks) {
        float timeScale = ((float) this.age + partialTicks) / (float) this.lifetime * 32.0F;
        timeScale = Mth.clamp(timeScale, 0.0F, 1.0F);
        return this.quadSize * timeScale;
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

        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
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
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SkullSpiritParticle skullSpiritParticle = new SkullSpiritParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed);
            skullSpiritParticle.pickSprite(this.spriteSet);
            return skullSpiritParticle;
        }
    }
}
