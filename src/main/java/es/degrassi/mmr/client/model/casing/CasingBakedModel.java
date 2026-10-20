package es.degrassi.mmr.client.model.casing;

import com.google.common.base.Strings;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.model.data.ModelProperty;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class CasingBakedModel implements BlockStateModel {

  public static final ModelProperty<CasingState> CASING_STATE = new ModelProperty<>();

  private final Material.Baked[] frameTextures;
  private final Material.Baked[] centerTextures;
  static final Material[] TEXTURES_FRAME = generateTexturesFrame();

  static final Material TEXTURE = new Material(ModularMachineryReborn.rl("block/casing_plain_a"));

  private static Material[] generateTexturesFrame() {
    return IntStream.range(1, 16)
        .mapToObj(Integer::toBinaryString)
        .map(s -> Strings.padStart(s, 4, '0'))
        .map(c -> ModularMachineryReborn.rl("block/casing_plain" + c))
        .map(Material::new)
        .toArray(Material[]::new);
  }

  public CasingBakedModel(Function<Material, Material.Baked> bakedTextureGetter) {
    this.centerTextures = new Material.Baked[] {
        bakedTextureGetter.apply(TEXTURE)
    };
    this.frameTextures = new Material.Baked[16];
    for (int i = 0; i < TEXTURES_FRAME.length; i++) {
      this.frameTextures[1 + i] = bakedTextureGetter.apply(TEXTURES_FRAME[i]);
    }
  }

  @Override
  public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
  }

  @Override
  public Material.Baked particleMaterial() {
    return this.frameTextures[this.frameTextures.length - 1];
  }

  @Override
  public int materialFlags() {
    return 0;
  }
}
