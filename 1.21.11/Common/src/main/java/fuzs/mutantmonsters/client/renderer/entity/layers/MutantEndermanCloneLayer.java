package fuzs.mutantmonsters.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.mutantmonsters.MutantMonsters;
import fuzs.mutantmonsters.client.model.geom.ModModelLayers;
import fuzs.mutantmonsters.client.model.MutantEndermanModel;
import fuzs.mutantmonsters.client.renderer.entity.state.MutantEndermanRenderState;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class MutantEndermanCloneLayer extends EnderEnergySwirlLayer<MutantEndermanRenderState, MutantEndermanModel> {
    public static final ResourceLocation TEXTURE_LOCATION = MutantMonsters.id("textures/entity/endersoul.png");

    private final EndermanModel<MutantEndermanRenderState> model;

    public MutantEndermanCloneLayer(RenderLayerParent<MutantEndermanRenderState, MutantEndermanModel> renderer, EntityModelSet entityModelSet) {
        super(renderer);
        this.model = new EndermanModel<>(entityModelSet.bakeLayer(ModModelLayers.ENDERMAN_CLONE));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, MutantEndermanRenderState renderState, float yRot, float xRot) {
        if (renderState.isClone) {
            super.submit(poseStack, nodeCollector, packedLight, renderState, yRot, xRot);
        }
    }

    @Override
    protected EntityModel<? super MutantEndermanRenderState> getModel() {
        return this.model;
    }
}
