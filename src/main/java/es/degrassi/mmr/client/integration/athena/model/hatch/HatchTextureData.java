package es.degrassi.mmr.client.integration.athena.model.hatch;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record HatchTextureData(
    String baseTextureName,
    @Nullable Identifier baseTexture,
    Identifier defaultBaseTexture,
    String overlayTextureName,
    @Nullable Identifier overlayTexture,
    Identifier defaultOverlayTexture,
    boolean usesReinforcedOverlay,
    Identifier defaultModel
) {
  public Identifier baseTexture() {
    return Optional.ofNullable(baseTexture).orElse(defaultBaseTexture);
  }

  public Identifier overlayTexture() {
    return Optional.ofNullable(overlayTexture).orElse(defaultOverlayTexture);
  }

  public boolean hasDefaultTextures() {
    if (baseTexture == null || overlayTexture == null || defaultBaseTexture == null || defaultOverlayTexture == null) return true;
    return defaultBaseTexture.equals(baseTexture) && defaultOverlayTexture.equals(overlayTexture);
  }

  public static HatchTextureData withDefault(String modelId) {
    return new HatchTextureData(
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        ModularMachineryReborn.rl("default/hatch_" + modelId)
    );
  }

  /**
   * 1.21.1 parity for the in-world fallback: the block's own default chain
   * ({@code default/hatches/<block>}) instead of the generic {@code default/hatch_all},
   * so disabled-CTM / custom-texture hatches (formed machines, texture overrides) still show
   * their own casing and overlay.
   */
  public static HatchTextureData withBlockDefault(Identifier blockId) {
    return new HatchTextureData(
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        ModularMachineryReborn.rl("default/hatches/" + blockId.getPath())
    );
  }

  public HatchTextureData derive(String name, Identifier texture, Identifier defaultTexture, boolean base, boolean reinforced) {
    return new HatchTextureData(
      base ? name : baseTextureName,
      base ? texture : baseTexture,
      base ? defaultTexture : defaultBaseTexture,
      !base ? name : overlayTextureName,
      !base ? texture : overlayTexture,
      !base ? defaultTexture : defaultOverlayTexture,
      reinforced,
      defaultModel
    );
  }

  public HatchTextureData derive(
      String baseTextureName,
      Identifier baseTexture,
      Identifier defaultBaseTexture,
      String overlayTextureName,
      Identifier overlayTexture,
      Identifier defaultOverlayTexture,
      boolean reinforced
  ) {
    return new HatchTextureData(
        baseTextureName,
        baseTexture,
        defaultBaseTexture,
        overlayTextureName,
        overlayTexture,
        defaultOverlayTexture,
        reinforced,
        defaultModel
    );
  }
}
