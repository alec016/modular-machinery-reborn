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
