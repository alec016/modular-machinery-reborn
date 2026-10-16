package es.degrassi.mmr.data;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Locale;

public class MMRTags {
  private MMRTags() {}
  private static TagKey<Block> blockTag(String name, boolean isNeoForge) {
    return BlockTags.create(isNeoForge ? Identifier.fromNamespaceAndPath("c", name) : ModularMachineryReborn.rl(name));
  }

  private static TagKey<Item> itemTag(String name, boolean isNeoForge) {
    return ItemTags.create(isNeoForge ? Identifier.fromNamespaceAndPath("c", name) : ModularMachineryReborn.rl(name));
  }

  public static List<Pair<TagKey<?>, String>> getAllTags() {
    return Tag.tags.stream().filter(pair -> pair.getFirst().location().getNamespace().equals(ModularMachineryReborn.MODID)).toList();
  }

  private static class Tag<T> {
    private static final List<Pair<TagKey<?>, String>> tags = Lists.newArrayList();
    private final TagKey<T> tag;
    protected Tag(TagKey<T> tag, String engTranslation) {
      this.tag = tag;
      tags.add(Pair.of(tag, engTranslation));
    }

    public TagKey<T> get() {
      return tag;
    }
  }

  public static class Blocks extends Tag<Block> {
    public static final TagKey<Block> ENERGY = new Blocks(false, "energyhatch", "Energy Hatches").get();
    public static final TagKey<Block> ENERGY_INPUT = new Blocks(false, "energyinputhatch", "Energy Input Hatches").get();
    public static final TagKey<Block> ENERGY_OUTPUT = new Blocks(false, "energyoutputhatch", "Energy Output Hatches").get();

    public static final TagKey<Block> ITEM = new Blocks(false, "itembus", "Item Buses").get();
    public static final TagKey<Block> INPUT_BUS = new Blocks(false, "inputbus", "Item Input Buses").get();
    public static final TagKey<Block> OUTPUT_BUS = new Blocks(false, "outputbus", "Item Output Buses").get();

    public static final TagKey<Block> DURABILITY = new Blocks(false, "durability_hatch", "Durability Hatches").get();

    public static final TagKey<Block> FLUID = new Blocks(false, "fluidhatch", "Fluid Hatches").get();
    public static final TagKey<Block> FLUID_INPUT = new Blocks(false, "fluidinputhatch", "Fluid Input Hatches").get();
    public static final TagKey<Block> FLUID_OUTPUT = new Blocks(false, "fluidoutputhatch", "Fluid Output Hatches").get();

    public static final TagKey<Block> EXPERIENCE = new Blocks(false, "experiencehatch", "Experience Hatches").get();
    public static final TagKey<Block> EXPERIENCE_INPUT = new Blocks(false, "experienceinputhatch", "Experience Input Hatches").get();
    public static final TagKey<Block> EXPERIENCE_OUTPUT = new Blocks(false, "experienceoutputhatch", "Experience Output Hatches").get();

    public static final TagKey<Block> PARALLEL = new Blocks(false, "parallelhatch", "Parallel Hatches").get();
    public static final TagKey<Block> FUEL_TANK = new Blocks(false, "fueltank", "Fuel Tanks").get();
    public static final TagKey<Block> EFFECT_DISPENSER = new Blocks(false, "effect_dispenser", "Effect Dispensers").get();
    public static final TagKey<Block> ENTITY = new Blocks(false, "entityhatch", "Entity Hatches").get();

    public static final TagKey<Block> CASINGS = new Blocks(false, "casing", "Casings").get();
    public static final TagKey<Block> ALL_CASINGS = new Blocks(false, "all_casing", "All Casings").get();

    public static final TagKey<Block> REPLACEABLE = new Blocks(false, "replaceable", "Replaceable").get();

    public static final TagKey<Block> PLAIN_CONNECTABLE = new Blocks(false, "plain_connectable", "Plain Connectable").get();
    public static final TagKey<Block> PLAIN_HATCHES = new Blocks(false, "plain_hatches", "Plain Hatches").get();
    public static final TagKey<Block> REINFORCED_CONNECTABLE = new Blocks(false, "reinforced_connectable", "Reinforced Connectable").get();
    public static final TagKey<Block> REINFORCED_HATCHES = new Blocks(false, "reinforced_hatches", "Reinforced Hatches").get();
    public static final TagKey<Block> HATCHES = new Blocks(false, "hatches").get();

    private Blocks(boolean isNeoForge, String name) {
      this(isNeoForge, name, capitalize(name));
    }

    private Blocks(boolean isNeoForge, String name, String enTranslation) {
      super(blockTag(name, isNeoForge), enTranslation);
    }
  }

  public static class Items extends Tag<Item> {
    public static final TagKey<Item> WRENCH = Tags.Items.TOOLS_WRENCH;

    public static final TagKey<Item> ENERGY = new Items(false, "energyhatch", "Energy Hatches").get();
    public static final TagKey<Item> ENERGY_INPUT = new Items(false, "energyinputhatch", "Energy Input Hatches").get();
    public static final TagKey<Item> ENERGY_OUTPUT = new Items(false, "energyoutputhatch", "Energy Output Hatches").get();

    public static final TagKey<Item> ITEM = new Items(false, "itembus", "Item Buses").get();
    public static final TagKey<Item> INPUT_BUS = new Items(false, "inputbus", "Item Input Buses").get();
    public static final TagKey<Item> OUTPUT_BUS = new Items(false, "outputbus", "Item Output Buses").get();

    public static final TagKey<Item> DURABILITY = new Items(false, "durability_hatch", "Durability Hatches").get();

    public static final TagKey<Item> FLUID = new Items(false, "fluidhatch", "Fluid Hatches").get();
    public static final TagKey<Item> FLUID_INPUT = new Items(false, "fluidinputhatch", "Fluid Input Hatches").get();
    public static final TagKey<Item> FLUID_OUTPUT = new Items(false, "fluidoutputhatch", "Fluid Output Hatches").get();

    public static final TagKey<Item> EXPERIENCE = new Items(false, "experiencehatch", "Experience Hatches").get();
    public static final TagKey<Item> EXPERIENCE_INPUT = new Items(false, "experienceinputhatch", "Experience Input Hatches").get();
    public static final TagKey<Item> EXPERIENCE_OUTPUT = new Items(false, "experienceoutputhatch", "Experience Output Hatches").get();

    public static final TagKey<Item> PARALLEL = new Items(false, "parallelhatch", "Parallel Hatches").get();
    public static final TagKey<Item> FUEL_TANK = new Items(false, "fueltank", "Fuel Tanks").get();
    public static final TagKey<Item> EFFECT_DISPENSER = new Items(false, "effect_dispenser", "Effect Dispensers").get();
    public static final TagKey<Item> ENTITY = new Items(false, "entityhatch", "Entity Hatches").get();

    public static final TagKey<Item> CASINGS = new Items(false, "casing", "Casings").get();
    public static final TagKey<Item> ALL_CASINGS = new Items(false, "all_casing", "All Casings").get();

    public static final TagKey<Item> HATCHES = new Items(false, "hatches").get();

    private Items(boolean isNeoForge, String name) {
      this(isNeoForge, name, capitalize(name));
    }
    private Items(boolean isNeoForge, String name, String enTranslation) {
      super(itemTag(name, isNeoForge), enTranslation);
    }
  }

  private static String capitalize(String toCapitalize) {
    if (toCapitalize.trim().isEmpty()) return toCapitalize;
    String[] splitted = toCapitalize.split("_");
    StringBuilder builder = new StringBuilder();
    for (var part : splitted) {
      if (part.trim().isEmpty()) continue;
      if (part.trim().length() == 1) {
        builder.append(part.trim().toUpperCase(Locale.ENGLISH))
            .append(" ");
        continue;
      }
      String first = (part.trim().charAt(0) + "").toUpperCase(Locale.ENGLISH);
      builder.append(first)
          .append(part.substring(1))
          .append(" ");
    }
    return builder.toString().trim();
  }
}
