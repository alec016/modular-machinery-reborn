package es.degrassi.mmr.client.integration.athena.utils;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.function.Function;

/**
 * Reimplementation of Athena 4.0.6's {@code earth.terrarium.athena.impl.client.models.ctm.ConnectedTextureMap}.
 *
 * <p>Athena 4.7.3 removed the index based API of that class (its {@code ConnectedTextureMap} is now a
 * collection of {@code CtmProvider}s that emit quads directly and exposes no {@code put}, {@code getTexture}
 * or {@code getTextures} methods), so MMR keeps its own map with the exact semantics of the old one,
 * reconstructed from the 4.0.6 bytecode:
 *
 * <ul>
 *   <li>A direction may hold a full index map, or a single material, which behaves like a map holding only
 *       index {@code 0} (the first CTM texture slot).</li>
 *   <li>{@link #getTexture(Direction, int)} returns a <b>global</b> sprite index: it is only valid after
 *       {@link #getTextures(Function)} has been called, which rebuilds the index remapping. Unknown indices
 *       fall back to {@code 0}, which is what the models use to detect a missing overlay texture.</li>
 * </ul>
 *
 * <p>The returned texture map is keyed by those global sprite indices, so {@code quad.sprite()} values
 * produced by {@link #getTexture(Direction, int)} can be looked up directly in it.
 */
public class MMRMaterialMap {

  private final EnumMap<Direction, Int2ObjectMap<Material>> maps = new EnumMap<>(Direction.class);
  private final EnumMap<Direction, Material> simpleMaterials = new EnumMap<>(Direction.class);
  private final EnumMap<Direction, Int2IntMap> textureMap = new EnumMap<>(Direction.class);

  public void put(Direction direction, Int2ObjectMap<Material> materials) {
    this.simpleMaterials.remove(direction);
    this.maps.put(direction, materials);
  }

  public void put(Direction direction, Material material) {
    this.maps.remove(direction);
    this.simpleMaterials.put(direction, material);
  }

  private Int2ObjectMap<Material> mapOf(Direction direction) {
    Int2ObjectMap<Material> map = this.maps.get(direction);
    if (map != null) {
      return map;
    }
    Material material = this.simpleMaterials.get(direction);
    return material == null ? null : Int2ObjectMaps.singleton(0, material);
  }

  public int getTexture(Direction direction, int index) {
    Int2IntMap remap = this.textureMap.get(direction);
    return remap == null ? 0 : remap.getOrDefault(index, 0);
  }

  public <T> Int2ObjectMap<T> getTextures(Function<Material, T> function) {
    Object2ObjectArrayMap<Material, T> baked = new Object2ObjectArrayMap<>();

    for (Direction direction : Direction.values()) {
      Int2ObjectMap<Material> map = this.mapOf(direction);
      if (map == null) continue;
      for (Material material : map.values()) {
        baked.putIfAbsent(material, function.apply(material));
      }
    }

    Int2ObjectArrayMap<T> result = new Int2ObjectArrayMap<>();
    int index = 0;

    for (Direction direction : Direction.values()) {
      Int2ObjectMap<Material> map = this.mapOf(direction);
      if (map == null) {
        this.textureMap.put(direction, new Int2IntArrayMap());
        continue;
      }

      Int2IntMap remap = new Int2IntArrayMap();
      for (Int2ObjectMap.Entry<Material> entry : map.int2ObjectEntrySet()) {
        int spriteIndex = index++;
        result.put(spriteIndex, baked.get(entry.getValue()));
        remap.put(entry.getIntKey(), spriteIndex);
      }
      this.textureMap.put(direction, remap);
    }

    return result;
  }
}
