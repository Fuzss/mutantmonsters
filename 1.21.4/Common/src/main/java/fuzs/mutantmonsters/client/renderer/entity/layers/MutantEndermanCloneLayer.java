package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.init.ModelLayerLocations;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class MutantEndermanCloneLayer extends EnderEnergySwirlLayer<MutantEndermanRenderState, MutantEndermanModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul.png");

    private final EndermanModel<MutantEndermanRenderState> model;

    public MutantEndermanCloneLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.model = new EndermanModel<>(entityModelSet.bakeLayer(ModelLayerLocations.ENDERMAN_CLONE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (renderState.isClone) {
            super.render(poseStack, bufferSource, packedLight, renderState, yRot, xRot);
        }
    }

    @Override
    protected EntityModel<? super MutantEndermanRenderState> getModel() {
        return this.model;
    }
}