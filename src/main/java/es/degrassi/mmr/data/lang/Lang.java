package es.degrassi.mmr.data.lang;

import com.google.gson.JsonElement;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

@Getter
@ParametersAreNonnullByDefault
abstract class Lang {
  private final Map<String, String> data = new TreeMap<>();
  private final Map<String, JsonElement> jsonData = new TreeMap<>();

  public static @Nullable Lang fromLocale(String locale) {
    return switch(locale) {
      case "en_us" -> new EnUsLang();
      case "es_es" -> new EsEsLang();
      case "pt_br" -> new PtBrLang();
      case "ru_ru" -> new RuRuLang();
      case "zh_cn" -> new ZhCnLang();
      case "uk_ua" -> new UkUaLang();
      default -> null;
    };
  }

  public final void init() {
    addTags();
    addItemGroups();
    addCommands();
    addStructureCreator();
    addGuiTitles();
    addGuiController();
    addCraftcheck();
    addComponents();
    addTooltips();
    addItems();
    addBlocks();
    addIngredients();
    addControllerTexts();
    addJade();
    addKeys();
    addRecipeModifiers();
    addJsonProps();
  }

  protected void addGuiController(){}
  protected void addTags(){}
  protected void addItemGroups() {}
  protected void addCommands(){}
  protected void addKeys(){}
  protected void addJade(){}
  protected void addControllerTexts(){}
  protected void addRecipeModifiers(){}
  protected void addIngredients(){}
  protected void addItems(){}
  protected void addBlocks(){}
  protected void addTooltips(){}
  protected void addComponents(){}
  protected void addCraftcheck(){}
  protected void addGuiTitles(){}
  protected void addStructureCreator(){}
  protected void addJsonProps(){}

  protected final void add(String key, String value) {
    if (this.jsonData.containsKey(key) || this.data.put(key, value) != null) {
      throw new IllegalStateException("Duplicate translation key " + key);
    }
  }

  protected final void add(String key, JsonElement value) {
    if (this.data.containsKey(key) || this.jsonData.put(key, value) != null) {
      throw new IllegalStateException("Duplicate translation key " + key);
    }
  }

  public final String modId() {
    return ModularMachineryReborn.MODID;
  }

  @Contract(pure = true)
  protected final @NotNull String gui(String suffix) {
    return "gui." + suffix;
  }

  @Contract(pure = true)
  protected final @NotNull String mmr(String suffix) {
    return "mmr." + suffix;
  }

  @Contract(pure = true)
  protected final @NotNull String mm(String suffix) {
    return modId() + "." + suffix;
  }

  @Contract(pure = true)
  protected final @NotNull String craftCheck(String suffix) {
    return "craftcheck.failure." + suffix;
  }

  @Contract(pure = true)
  protected final @NotNull String missingComponent(String type) {
    return "component.missing." + type;
  }

  @Contract(pure = true)
  protected final @NotNull String tooltip(String message) {
    return "tooltip." + message;
  }

  @Contract(pure = true)
  protected final @NotNull String jei(String suffix) {
    return "jei." + suffix;
  }

  @Contract(pure = true)
  protected final @NotNull String ingredient(String suffix) {
    return "ingredient." + suffix;
  }

  protected final @NotNull String jeiIngredient(String suffix) {
    return mm(jei(ingredient(suffix)));
  }

  @Contract(pure = true)
  protected final @NotNull String recipeModifier(String type) {
    return mmr("recipe.modifier." + type);
  }

  protected final void addBlock(Supplier<? extends Block> key, String name) {
    this.add(key.get(), name);
  }

  protected final void add(Block key, String name) {
    this.add(key.getDescriptionId(), name);
  }

  protected final void addItem(Supplier<? extends Item> key, String name) {
    this.add(key.get(), name);
  }

  protected final void add(Item key, String name) {
    this.add(key.getDescriptionId(), name);
  }

  protected final void addItemStack(Supplier<ItemStack> key, String name) {
    this.add(key.get(), name);
  }

  protected final void add(ItemStack key, String name) {
    this.add(key.getDescriptionId(), name);
  }

  protected final void addEffect(Supplier<? extends MobEffect> key, String name) {
    this.add(key.get(), name);
  }

  protected final void add(MobEffect key, String name) {
    this.add(key.getDescriptionId(), name);
  }

  protected final void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
    this.add(key.get(), name);
  }

  protected final void add(EntityType<?> key, String name) {
    this.add(key.getDescriptionId(), name);
  }

  protected final void addTag(Supplier<? extends TagKey<?>> key, String name) {
    this.add(key.get(), name);
  }

  protected final void add(TagKey<?> tagKey, String name) {
    this.add(Tags.getTagTranslationKey(tagKey), name);
  }

  protected final void addDimension(ResourceKey<Level> dimension, String value) {
    this.add(dimension.location().toLanguageKey("dimension"), value);
  }
}
