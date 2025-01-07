package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.model.MutantZombieModel;
import fuzs.mutantmonsters.world.entity.mutant.MutantZombie;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MutantZombieRenderer extends AlternateMobRenderer<MutantZombie, MutantZombieModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/mutant_zombie.png");

    public MutantZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantZombieModel(context.bakeLayer(ClientModRegistry.MUTANT_ZOMBIE)), 1.0F);
    }

    @Override
    public Vec3 getRenderOffset(MutantZombie mutantZombie, float partialTick) {
        return new Vec3(0.0D, -0.0975D, 0.0D);
    }

    @Override
    public ResourceLocation getTextureLocation(MutantZombie mutantZombie) {
        return TEXTURE_LOCATION;
    }

    @Override
    protected RenderType getRenderType(MutantZombie mutantZombie, boolean bodyVisible, boolean translucent, boolean glowing) {
        return super.getRenderType(mutantZombie, bodyVisible, translucent | mutantZombie.vanishTime > 0, glowing);
    }

    @Override
    protected void setupRotations(MutantZombie mutantZombie, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        if (mutantZombie.deathTime > 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
            int pitch = Math.min(20, mutantZombie.deathTime);
            boolean reviving = false;
            if (mutantZombie.deathTime > 100) {
                pitch = 140 - mutantZombie.deathTime;
                reviving = true;
            }

            if (pitch > 0) {
                float rotationAmount = ((float) pitch + partialTick - 1.0F) / 20.0F * 1.6F;
                if (reviving) {
                    rotationAmount = ((float) pitch - partialTick) / 40.0F * 1.6F;
                }

                rotationAmount = Mth.sqrt(rotationAmount);
                if (rotationAmount > 1.0F) {
                    rotationAmount = 1.0F;
                }

                poseStack.mulPose(Axis.XN.rotationDegrees(rotationAmount * this.getFlipDegrees(mutantZombie)));
            }
        } else {
            super.setupRotations(mutantZombie, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        }
    }

    @Override
    protected float getFlipDegrees(MutantZombie mutantZombie) {
        return 80.0F;
    }

    @Override
    protected void scale(MutantZombie mutantZombie, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }

    @Override
    protected float getAlpha(MutantZombie mutantZombie, float partialTick) {
        return mutantZombie.vanishTime > 0 ?
                1.0F - ((float) mutantZombie.vanishTime + partialTick) / 100.0F * 0.6F :
                1.0F;
    }

    @Override
    protected boolean showsHurtColor(MutantZombie mob) {
        return super.showsHurtColor(mob) || mob.deathTime > 0 && mob.getLives() <= 0;
    }
}
