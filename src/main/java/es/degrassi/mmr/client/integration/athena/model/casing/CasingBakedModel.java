package es.degrassi.mmr.client.integration.athena.model.casing;

import es.degrassi.mmr.client.integration.athena.model.MMRBakedModel;
import es.degrassi.mmr.client.integration.athena.model.MMRAthenaBlockModel;
import es.degrassi.mmr.client.integration.athena.utils.MMRMaterialMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.client.resources.model.sprite.Material;

import java.util.function.Function;

/**
 * Port of the 1.21.1 {@code es.degrassi.mmreborn.client.integration.athena.model.casing.CasingBakedModel}.
 * All shared CTM/overlay behaviour lives in {@link MMRBakedModel}; the casing injects the overlay
 * unconditionally per direction, so no extra state is needed here.
 */
public class CasingBakedModel implements MMRBakedModel {

  private final CasingBlockModel model;
  @Getter
  private final Int2ObjectMap<Material.Baked> textures;

  public CasingBakedModel(CasingBlockModel model, Function<Material, Material.Baked> function) {
    this.model = model;
    this.textures = this.model.getTextures(function);
  }

  @Override
  public MMRMaterialMap getMaterialMap() {
    return this.model.materials;
  }

  @Override
  public MMRAthenaBlockModel getModel() {
    return this.model;
  }
}
