package fuzs.mutantmonsters.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SkullSpiritParticle extends SingleQuadParticle {

    protected SkullSpiritParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite sprite) {
        super(clientLevel, x, y, z, 0.0, 0.0, 0.0, sprite);
        this.xd *= 0.1;
        this.yd *= 0.1;
        this.zd *= 0.1;
        this.xd += xSpeed;
        this.yd += ySpeed;
        this.zd += zSpeed;
        float color = 1.0F - this.random.nextFloat() * 0.2F;
        this.setColor(color, color, color);
        float scale = 0.4F + this.random.nextFloat() * 0.6F;
        this.quadSize *= scale;
        this.lifetime = (int) (8.0F / (this.random.nextFloat() * 0.8F + 0.2F) * scale);
        this.hasPhysics = false;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float scale = (this.age + partialTick) / this.lifetime * 32.0F;
        return this.quadSize * Mth.clamp(scale, 0.0F, 1.0F);
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
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource randomSource) {
            return new SkullSpiritParticle(clientLevel,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    this.sprites.get(randomSource));
        }
    }
}
