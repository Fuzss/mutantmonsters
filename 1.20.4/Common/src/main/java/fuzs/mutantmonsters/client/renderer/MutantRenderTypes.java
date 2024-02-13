package fuzs.mutantmonsters.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import fuzs.mutantmonsters.MutantMonsters;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class MutantRenderTypes extends RenderType {
    private static final RenderStateShard.TransparencyStateShard ALPHA_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
            MutantMonsters.id("alpha_transparency").toString(),
            () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
            },
            () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }
    );
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize((ResourceLocation resourceLocation) -> {
        RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(
                resourceLocation,
                false,
                false
        );
        return create(MutantMonsters.id("eyes").toString(),
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_EYES_SHADER)
                        .setTextureState(renderstateshard$texturestateshard)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false)
        );
    });

    private MutantRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType eyes(ResourceLocation pLocation) {
        return EYES.apply(pLocation);
    }

    public static RenderType energySwirl(ResourceLocation resourceLocation, float u, float v) {
        return create("energy_swirl",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                        .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(u, v))
                        .setTransparencyState(ALPHA_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false)
        );
    }
}
