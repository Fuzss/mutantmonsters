package fuzs.mutantmonsters.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;

public final class ModRenderTypes {
    public static final BlendFunction ALPHA_BLEND_FUNCTION = new BlendFunction(SourceFactor.SRC_ALPHA,
            DestFactor.ONE_MINUS_SRC_ALPHA);
    /**
     * @see RenderPipelines#ENERGY_SWIRL
     */
    public static final RenderPipeline ENERGY_SWIRL_RENDER_PIPELINE = (RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withLocation(MutantMonsters.id("pipeline/energy_swirl"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withSampler("Sampler0")
            .withBlend(ALPHA_BLEND_FUNCTION)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
            .build());

    private ModRenderTypes() {
        // NO-OP
    }

    /**
     * @see net.minecraft.client.renderer.rendertype.RenderTypes#energySwirl(Identifier, float, float)
     */
    public static RenderType energySwirl(Identifier identifier, float u, float v) {
        return RenderType.create(MutantMonsters.id("energy_swirl").toString(),
                RenderSetup.builder(ENERGY_SWIRL_RENDER_PIPELINE)
                        .withTexture("Sampler0", identifier)
                        .setTextureTransform(new TextureTransform.OffsetTextureTransform(u, v))
                        .useOverlay()
                        .sortOnUpload()
                        .createRenderSetup());
    }
}
