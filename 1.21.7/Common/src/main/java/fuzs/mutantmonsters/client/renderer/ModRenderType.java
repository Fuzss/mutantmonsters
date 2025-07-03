package fuzs.mutantmonsters.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

public abstract class ModRenderType extends RenderType {
    public static final BlendFunction ALPHA_BLEND_FUNCTION = new BlendFunction(SourceFactor.SRC_ALPHA,
            DestFactor.ONE_MINUS_SRC_ALPHA);
    /**
     * @see RenderPipelines#ENERGY_SWIRL
     */
    public static final RenderPipeline ENERGY_SWIRL_RENDER_PIPELINE = (RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_FOG_SNIPPET)
            .withLocation(MutantMonsters.id("pipeline/energy_swirl"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withSampler("Sampler0")
            .withUniform("TextureMat", UniformType.MATRIX4X4)
            .withBlend(ALPHA_BLEND_FUNCTION)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
            .build());

    private ModRenderType(String string, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, i, bl, bl2, runnable, runnable2);
    }

    /**
     * @see RenderType#energySwirl(ResourceLocation, float, float)
     */
    public static RenderType energySwirl(ResourceLocation resourceLocation, float u, float v) {
        return create(MutantMonsters.id("energy_swirl").toString(),
                1536,
                false,
                true,
                ENERGY_SWIRL_RENDER_PIPELINE,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation,
                                TriState.FALSE,
                                false))
                        .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
    }
}
