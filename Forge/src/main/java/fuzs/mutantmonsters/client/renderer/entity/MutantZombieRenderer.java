package fuzs.mutantmonsters.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ClientModRegistry;
import fuzs.mutantmonsters.client.renderer.entity.model.MutantZombieModel;
import fuzs.mutantmonsters.entity.mutant.MutantZombieEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MutantZombieRenderer extends AlternateMobRenderer<MutantZombieEntity, MutantZombieModel> {
    private static final ResourceLocation TEXTURE = MutantMonsters.getEntityTexture("mutant_zombie");

    public MutantZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantZombieModel(context.bakeLayer(ClientModRegistry.MUTANT_ZOMBIE)), 1.0F);
    }

    @Override
    protected void scale(MutantZombieEntity livingEntity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.3F, 1.3F, 1.3F);
    }

    @Override
    protected void setupRotations(MutantZombieEntity livingEntity, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (livingEntity.deathTime > 0) {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
            int pitch = Math.min(20, livingEntity.deathTime);
            boolean reviving = false;
            if (livingEntity.deathTime > 100) {
                pitch = 140 - livingEntity.deathTime;
                reviving = true;
            }

            if (pitch > 0) {
                float f = ((float)pitch + partialTicks - 1.0F) / 20.0F * 1.6F;
                if (reviving) {
                    f = ((float)pitch - partialTicks) / 40.0F * 1.6F;
                }

                f = Mth.sqrt(f);
                if (f > 1.0F) {
                    f = 1.0F;
                }

                matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(f * this.getFlipDegrees(livingEntity)));
            }
        } else {
            super.setupRotations(livingEntity, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        }
    }

    @Override
    protected float getFlipDegrees(MutantZombieEntity livingEntity) {
        return 80.0F;
    }

    @Override
    protected RenderType getRenderType(MutantZombieEntity livingEntity, boolean isVisible, boolean visibleToSpectator, boolean glowing) {
        return super.getRenderType(livingEntity, isVisible, visibleToSpectator | livingEntity.vanishTime > 0, glowing);
    }

    @Override
    protected float getAlpha(MutantZombieEntity mob, float partialTicks) {
        return mob.vanishTime > 0 ? 1.0F - ((float)mob.vanishTime + partialTicks) / 100.0F * 0.6F : 1.0F;
    }

    @Override
    protected boolean showsHurtColor(MutantZombieEntity mob) {
        return super.showsHurtColor(mob) || mob.deathTime > 0 && mob.getLives() <= 0;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantZombieEntity entity) {
        return TEXTURE;
    }
}
