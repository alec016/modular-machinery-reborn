package es.degrassi.mmr.client.util;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public class RenderTypes {

    public static final RenderType PHANTOM = RenderType.create("phantom",
        RenderSetup.builder(RenderPipelines.TRANSLUCENT_BLOCK)
            .withTexture("Sampler0", Identifier.withDefaultNamespace("atlases/block"))
            .useLightmap()
            .sortOnUpload()
            .createRenderSetup());

    public static final RenderType NOPE = RenderType.create("nope",
        RenderSetup.builder(RenderPipelines.TRANSLUCENT_BLOCK)
            .withTexture("Sampler0", Identifier.withDefaultNamespace("atlases/block"))
            .useLightmap()
            .sortOnUpload()
            .createRenderSetup());

    public static final RenderType THICK_LINES = RenderType.create("thick_lines",
        RenderSetup.builder(RenderPipelines.WIREFRAME)
            .createRenderSetup());
}
