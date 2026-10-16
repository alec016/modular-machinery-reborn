package es.degrassi.mmr.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.stream.Stream;

public class TagUtil {
  private TagUtil() {}

  public static Stream<Item> getItems(TagKey<Item> tag) {
    return BuiltInRegistries.ITEM.get(tag).map(named -> named.stream().map(Holder::value)).orElse(Stream.empty());
  }

  public static boolean contains(TagKey<Item> tag, Item value) {
    return BuiltInRegistries.ITEM.get(tag).map(named -> named.contains(Holder.direct(value))).orElse(false);
  }

  public static Stream<Block> getBlocks(TagKey<Block> tag) {
    return BuiltInRegistries.BLOCK.get(tag).map(named -> named.stream().map(Holder::value)).orElse(Stream.empty());
  }

  public static boolean contains(TagKey<Block> tag, Block value) {
    return BuiltInRegistries.BLOCK.get(tag).map(named -> named.contains(Holder.direct(value))).orElse(false);
  }

  public static Stream<Fluid> getFluids(TagKey<Fluid> tag) {
    return BuiltInRegistries.FLUID.get(tag).map(named -> named.stream().map(Holder::value)).orElse(Stream.empty());
  }

  public static boolean contains(TagKey<Fluid> tag, Fluid value) {
    return BuiltInRegistries.FLUID.get(tag).map(named -> named.contains(Holder.direct(value))).orElse(false);
  }
}
