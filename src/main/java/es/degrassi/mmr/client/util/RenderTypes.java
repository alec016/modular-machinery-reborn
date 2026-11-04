package es.degrassi.mmr.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

public class RenderTypes {

    public static final RenderType PHANTOM = RenderType.create("phantom",
        RenderSetup.builder(RenderPipelines.TRANSLUCENT_BLOCK)
            .useLightmap()
            .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS,
                () -> RenderSystem.getSamplerCache()
                    .getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true))
            .sortOnUpload()
            .createRenderSetup());

    public static final RenderType NOPE = RenderType.create("nope",
        RenderSetup.builder(RenderPipelines.TRANSLUCENT_BLOCK)
            .useLightmap()
            .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS,
                () -> RenderSystem.getSamplerCache()
                    .getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true))
            .sortOnUpload()
            .createRenderSetup());

    public static final RenderType THICK_LINES = net.minecraft.client.renderer.rendertype.RenderTypes.LINES;
}
