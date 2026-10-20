package es.degrassi.mmr.data.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.client.item.MMRItemTintSource;
import es.degrassi.mmr.common.block.BlockCasing;
import es.degrassi.mmr.common.block.BlockController;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.data.blockstate.builder.StateCasingBuilder;
import es.degrassi.mmr.data.blockstate.builder.StateControllerBuilder;
import es.degrassi.mmr.data.blockstate.builder.StateHatchBuilder;
import lombok.Getter;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class BaseMMRBlockStateProvider implements DataProvider {
  protected static final Identifier empty = ModularMachineryReborn.rl("block/casing_empty");
  protected static final Identifier plainCenter = ModularMachineryReborn.rl("block/casing_plain_corners");
  protected static final Identifier plainHorizontal = ModularMachineryReborn.rl("block/casing_plain_horizontal");
  protected static final Identifier plainVertical = ModularMachineryReborn.rl("block/casing_plain_vertical");
  protected static final Identifier plainParticle = ModularMachineryReborn.rl("block/casing_plain");
  protected static final Identifier reinforcedCenter = ModularMachineryReborn.rl("block/casing_reinforced_corners");
  protected static final Identifier reinforcedHorizontal = ModularMachineryReborn.rl("block/casing_reinforced_horizontal");
  protected static final Identifier reinforcedVertical = ModularMachineryReborn.rl("block/casing_reinforced_vertical");
  protected static final Identifier reinforcedParticle = ModularMachineryReborn.rl("block/casing_reinforced");

  protected final PackOutput output;
  protected final String modId;
  protected final Map<Block, Supplier<JsonElement>> registeredBlocks = new LinkedHashMap<>();
  protected final Map<Identifier, JsonObject> generatedModels = new LinkedHashMap<>();
  protected ItemModelGenerators itemModels;
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  protected Identifier key(Block block) {
    return BuiltInRegistries.BLOCK.getKey(block);
  }

  protected Identifier key(Item item) {
    return BuiltInRegistries.ITEM.getKey(item);
  }

  protected BaseMMRBlockStateProvider(PackOutput output, String modId) {
    this.output = output;
    this.modId = modId;
  }

  protected Stream<? extends Holder<Block>> getKnownBlocks() {
    return BuiltInRegistries.BLOCK.listElements().filter((holder) -> holder.getKey().identifier().getNamespace().equals(this.modId));
  }

  protected Stream<? extends Holder<Item>> getKnownItems() {
    return BuiltInRegistries.ITEM.listElements().filter((holder) -> holder.getKey().identifier().getNamespace().equals(this.modId));
  }

  protected abstract void registerStatesAndModels();

  protected Identifier modLoc(String path) {
    return Identifier.fromNamespaceAndPath(modId, path);
  }

  protected Identifier mcLoc(String path) {
    return Identifier.fromNamespaceAndPath("minecraft", path);
  }

  protected void addCasing(BlockCasing block, boolean isReinforced, @Nullable Identifier overlay, Identifier ovAll) {
    var builder = new StateCasingBuilder()
        .center(plainCenter)
        .horizontal(plainHorizontal)
        .empty(empty)
        .vertical(plainVertical)
        .particle(plainParticle);
    if (isReinforced) {
      builder = builder.connectToReinforced()
          .ovCenter(reinforcedCenter)
          .ovHorizontal(reinforcedHorizontal)
          .ovVertical(reinforcedVertical)
          .ovParticle(reinforcedParticle);
    } else {
      builder = builder.connectToPlain();
    }
    if (overlay != null) {
      builder = builder.overlay(overlay);
    }
    builder.variantModel(this.key(block).withPrefix("block/"));
    this.registeredBlocks.put(block, builder);
    item(block, false, false, ovAll);
  }
  protected void addController(BlockController block, boolean isReinforced, Identifier overlay) {
    var builder = new StateControllerBuilder()
        .center(plainCenter)
        .horizontal(plainHorizontal)
        .empty(empty)
        .vertical(plainVertical)
        .particle(plainParticle)
        .overlay(overlay);
    var defaultControllerBuilder = modelBuilder(this.key(block).withPrefix("default/"))
        .parent(modLoc("block/blockmodel_overlay_orientable_all"))
        .texture("bg_all", plainParticle)
        .texture("ov_front", overlay)
        .texture("ov_side", modLoc("block/overlay_transparent"))
        .texture("ov_top", modLoc("block/overlay_transparent"));
    if (isReinforced) {
      builder = builder.connectToReinforced()
          .ovCenter(reinforcedCenter)
          .ovHorizontal(reinforcedHorizontal)
          .ovVertical(reinforcedVertical)
          .ovParticle(reinforcedParticle);
      defaultControllerBuilder = defaultControllerBuilder
          .texture("ov_r_all", reinforcedParticle);
    } else {
      builder = builder.connectToPlain();
    }
    builder.variantModel(this.key(block).withPrefix("default/"));
    this.registeredBlocks.put(block, builder);
    item(block, true, true, null);
    generatedModels.put(this.key(block).withPrefix("default/"), defaultControllerBuilder.build());
  }
  protected void addHatch(BlockMachineComponent block, boolean isReinforced, Identifier overlay, boolean orientable) {
    var builder = new StateHatchBuilder()
        .center(plainCenter)
        .horizontal(plainHorizontal)
        .empty(empty)
        .vertical(plainVertical)
        .particle(plainParticle)
        .overlay(overlay);
    var defaultHatchBuilder = modelBuilder(this.key(block).withPrefix("default/hatches/"))
        .parent(orientable
            ? modLoc("block/blockmodel_overlay_orientable_all")
            : modLoc("block/blockmodel_overlay_all")
        ).texture("bg_all", plainParticle)
        .texture(orientable ? "ov_front" : "ov_all", overlay);
    if (orientable) {
      defaultHatchBuilder = defaultHatchBuilder
          .texture("ov_side", modLoc("block/overlay_transparent"))
          .texture("ov_top", modLoc("block/overlay_transparent"));
    }
    if (isReinforced) {
      builder = builder.connectToReinforced()
          .ovCenter(reinforcedCenter)
          .ovHorizontal(reinforcedHorizontal)
          .ovVertical(reinforcedVertical)
          .ovParticle(reinforcedParticle);
      defaultHatchBuilder = defaultHatchBuilder
          .texture("ov_r_all", reinforcedParticle);
    } else {
      builder = builder.connectToPlain();
    }
    builder.variantModel(this.key(block).withPrefix("default/hatches/"));
    this.registeredBlocks.put(block, builder);
    item(block, orientable, true, null);
    defaultHatch(block, defaultHatchBuilder.build());
  }
  protected Identifier loader(Block block) {
    return switch (block) {
      case BlockController blockController -> ModularMachineryReborn.rl("controller");
      case BlockMachineComponent blockMachineComponent -> ModularMachineryReborn.rl("hatch");
      default -> null;
    };
  }
  protected void item(Block block, boolean orientable, boolean loader, @Nullable Identifier ovAll) {
    JsonObject model;
    if (loader) {
      // Athena loaders only paint in-world block models: an item model json carrying just the
      // loader bakes zero geometry through the 1.21.4+ item pipeline and the item renders like
      // air. Items point at this block's own default model instead — the same model the legacy
      // DefaultHatchBakedModel + DEFAULT_MODEL component flow displayed for these very items.
      model = new JsonObject();
      model.addProperty("parent", defaultModelPath(block).toString());
    } else {
      assert ovAll != null;
      MMRModelBuilder builder = modelBuilder(this.key(block))
          .parent(orientable
              ? modLoc("block/blockmodel_overlay_orientable_all")
              : modLoc("block/blockmodel_overlay_all")
          ).texture("bg_all", plainParticle)
          .texture(orientable ? "ov_front" : "ov_all", ovAll);
      if (orientable) {
        builder = builder
            .texture("ov_side", modLoc("block/overlay_transparent"))
            .texture("ov_top", modLoc("block/overlay_transparent"));
      }
      model = builder.build();
    }
    simpleBlockWithItem(block, model, true);
  }
  /**
   * The default fallback model this athena block writes ({@code models/default/hatches/<name>} for
   * hatches, {@code models/default/controller} for the controller) — the same json athena-block
   * items parent to for their display model.
   */
  protected Identifier defaultModelPath(Block block) {
    return switch (block) {
      case BlockController blockController -> this.key(blockController).withPrefix("default/");
      case BlockMachineComponent blockMachineComponent ->
          this.key(blockMachineComponent).withPrefix("default/hatches/");
      default -> throw new IllegalArgumentException("Block has no athena default model: " + block);
    };
  }

  protected void simpleBlockWithItem(Block block, JsonObject model, boolean tinted) {
    this.simpleBlock(block, model);
    this.simpleBlockItem(block, model, tinted);
  }
  protected void simpleBlock(Block block, JsonObject model) {
    generatedModels.put(this.key(block).withPrefix("block/"), model);
  }
  public void defaultHatch(Block block, JsonObject model) {
    generatedModels.put(this.key(block).withPrefix("default/hatches/"), model);
  }
  public void defaultModel(Identifier path, JsonObject model) {
    generatedModels.put(path.withPrefix("default/"), model);
  }
  public void simpleBlockItem(Block block, JsonObject model, boolean tinted) {
    var id = key(block).withPrefix("item/");
    generatedModels.put(id, model);
    if (tinted) {
      // Legacy parity: the overlay cube's base faces carry tintindex 4, and per-stack color layers
      // are looked up by that index, so the definition needs entries for indices 0..4.
      itemModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.tintedModel(
          id,
          new Constant(-1),
          new MMRItemTintSource()
      ));
    } else {
      itemModels.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(id));
    }
  }
  public void block(Identifier path, JsonObject model) {
    generatedModels.put(path.withPath(old -> old.startsWith("block/") ? old : "block/%s".formatted(old)), model);
  }
  public void basicItem(Item item, Identifier texture) {
    this.basicItem(Objects.requireNonNull(key(item)), Objects.requireNonNull(texture), false);
  }
  public void basicItem(Item item) {
    basicItem(item, key(item).withPrefix("item/"));
  }
  public void basicTintedItem(Item item) {
    basicItem(key(item), key(item).withPrefix("item/"), true);
  }
  public void basicItem(Identifier item, Identifier texture, boolean tinted) {
    JsonObject model = new JsonObject();
    model.addProperty("parent", "minecraft:item/generated");
    JsonObject textures = new JsonObject();
    textures.addProperty("layer0", texture.toString());
    model.add("textures", textures);
    var id = item.withPath(old -> old.startsWith("item/") ? old : "item/%s".formatted(old));
    generatedModels.put(id, model);
    if (tinted) {
      itemModels.itemModelOutput.accept(BuiltInRegistries.ITEM.getValue(item), ItemModelUtils.tintedModel(
          id,
          new Constant(-1),
          new MMRItemTintSource()
      ));
    } else {
      itemModels.itemModelOutput.accept(BuiltInRegistries.ITEM.getValue(item), ItemModelUtils.plainModel(id));
    }
  }

  protected MMRModelBuilder modelBuilder(Identifier path) {
    return new MMRModelBuilder(path);
  }

  protected MMRModelBuilder modelBuilder(String path) {
    return new MMRModelBuilder(Identifier.fromNamespaceAndPath(modId, path));
  }

  private static Identifier getId(Block block) {
    var id = BuiltInRegistries.BLOCK.getKey(block);
    if (id == BuiltInRegistries.BLOCK.getDefaultKey()) {
      throw new IllegalStateException("Block " + block + " is not registered");
    }
    return id;
  }

  @Override
  public @NotNull CompletableFuture<?> run(CachedOutput cache) {
    ItemInfoCollector itemCollector = new ItemInfoCollector(this::getKnownItems);
    SimpleModelCollector simpleModels = new SimpleModelCollector();
    itemModels = new ItemModelGenerators(itemCollector, simpleModels);
    registerStatesAndModels();

    itemCollector.finalizeAndValidate();

    var blockStatesPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
    var modelsPath = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    var itemInfoPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");

    Map<Block, JsonElement> blockstates = new HashMap<>();
    registeredBlocks.forEach((block, sup) -> blockstates.put(block, sup.get()));
    Map<Identifier, JsonElement> models = new HashMap<>(generatedModels);

    CompletableFuture<?> blockstateFuture = DataProvider.saveAll(
        cache,
        ExtraCodecs.JSON,
        block -> blockStatesPath.json(getId(block)),
        blockstates
    );

    CompletableFuture<?> modelsFuture = DataProvider.saveAll(
        cache,
        ExtraCodecs.JSON,
        modelsPath::json,
        models
    );

    return CompletableFuture.allOf(blockstateFuture, simpleModels.save(cache, modelsPath), itemCollector.save(cache, itemInfoPathProvider), modelsFuture);
  }

  @Override
  public @NotNull String getName() {
    return "MMR Block State Provider - " + modId;
  }

  /**
   * Legacy parity of the {@code blockmodel_overlay} root model: three coplanar full-block boxes in
   * draw order — the base texture layer first (every face carrying tintindex 4, the dynamic
   * machine-color index used by both the item and block tint pipelines), the reinforced overlay
   * second, and the normal overlay last so it stays drawn on top of the reinforcement. Cutout
   * discards transparent pixels, so models whose {@code ov_r} resolves to
   * {@code overlay_transparent} render identically without any conditional.
   */
  protected static JsonArray bgOverlayCubeElements() {
    JsonArray elements = new JsonArray();
    elements.add(cubeLayer("bg", 1));
    elements.add(cubeLayer("ov_r", -1));
    elements.add(cubeLayer("ov", -1));
    return elements;
  }

  private static JsonObject cubeLayer(String prefix, int tintIndex) {
    JsonObject element = new JsonObject();
    element.add("from", coords(0.0));
    element.add("to", coords(16.0));
    JsonObject faces = new JsonObject();
    faces.add("down", face("#" + prefix + "_down", "down", tintIndex));
    faces.add("up", face("#" + prefix + "_up", "up", tintIndex));
    faces.add("north", face("#" + prefix + "_north", "north", tintIndex));
    faces.add("south", face("#" + prefix + "_south", "south", tintIndex));
    faces.add("west", face("#" + prefix + "_west", "west", tintIndex));
    faces.add("east", face("#" + prefix + "_east", "east", tintIndex));
    element.add("faces", faces);
    return element;
  }

  private static JsonObject face(String texture, String cullface, int tintIndex) {
    JsonObject face = new JsonObject();
    face.addProperty("texture", texture);
    face.addProperty("cullface", cullface);
    if (tintIndex != -1) {
      face.addProperty("tintindex", tintIndex);
    }
    return face;
  }

  private static JsonArray coords(double value) {
    JsonArray array = new JsonArray();
    array.add(value);
    array.add(value);
    array.add(value);
    return array;
  }

  public static class MMRModelBuilder {
    @Getter
    private final Identifier modelId;
    private final JsonObject json = new JsonObject();
    private final JsonObject textures = new JsonObject();

    public MMRModelBuilder(Identifier modelId) {
      this.modelId = modelId;
    }

    public MMRModelBuilder parent(Identifier parent) {
      json.addProperty("parent", parent.toString());
      return this;
    }

    public MMRModelBuilder parent(String parent) {
      json.addProperty("parent", parent);
      return this;
    }

    public MMRModelBuilder texture(String key, Identifier value) {
      textures.addProperty(key, value.toString());
      return this;
    }

    public MMRModelBuilder texture(String key, String value) {
      textures.addProperty(key, value);
      return this;
    }

    public MMRModelBuilder renderType(String renderType) {
      json.addProperty("render_type", renderType);
      return this;
    }

    public MMRModelBuilder customLoader(Identifier loaderId) {
      json.addProperty("loader", loaderId.toString());
      return this;
    }

    public MMRModelBuilder element(JsonObject element) {
      if (!json.has("elements")) {
        json.add("elements", new JsonArray());
      }
      json.getAsJsonArray("elements").add(element);
      return this;
    }

    public MMRModelBuilder elements(JsonArray elements) {
      if (!json.has("elements")) {
        json.add("elements", new JsonArray());
      }
      for (var element : elements) {
        json.getAsJsonArray("elements").add(element);
      }
      return this;
    }

    public JsonObject build() {
      JsonObject result = new JsonObject();
      if (json.has("parent")) result.addProperty("parent", json.get("parent").getAsString());
      if (json.has("loader")) result.addProperty("loader", json.get("loader").getAsString());
      if (json.has("render_type")) result.addProperty("render_type", json.get("render_type").getAsString());
      if (json.has("elements")) result.add("elements", json.get("elements"));
      if (!textures.isEmpty()) {
        result.add("textures", textures);
      }
      return result;
    }
  }


  private static class ItemInfoCollector implements ItemModelOutput {
    private final Map<Item, ClientItem> itemInfos;
    private final Map<Item, Item> copies;
    private final Supplier<Stream<? extends Holder<Item>>> knownItems;
    private final Map<Identifier, ClientItem> idItemInfos;

    public ItemInfoCollector(Supplier<Stream<? extends Holder<Item>>> knownItems) {
      this.itemInfos = new HashMap<>();
      this.copies = new HashMap<>();
      this.idItemInfos = new HashMap<>();
      this.knownItems = knownItems;
    }

    /** @deprecated */
    @Deprecated
    public ItemInfoCollector() {
      DefaultedRegistry<Item> var10001 = BuiltInRegistries.ITEM;
      Objects.requireNonNull(var10001);
      this(var10001::listElements);
    }

    public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
      this.register(item, new ClientItem(model, properties));
    }

    public void register(Item item, ClientItem itemInfo) {
      ClientItem prev = this.itemInfos.put(item, itemInfo);
      if (prev != null) {
        throw new IllegalStateException("Duplicate item model definition for " + item);
      }
    }

    public void register(Identifier identifier, ClientItem clientItem) {
      ClientItem existing = this.idItemInfos.putIfAbsent(identifier, clientItem);
      if (existing != null) {
        throw new IllegalStateException("Duplicate item model definition for " + identifier);
      }
    }

    public void copy(Item donor, Item acceptor) {
      this.copies.put(acceptor, donor);
    }

    public void finalizeAndValidate() {
      this.knownItems.get().map(Holder::value).forEach((item) -> {
        if (!this.copies.containsKey(item) && item instanceof BlockItem blockItem) {
          if (!this.itemInfos.containsKey(blockItem)) {
            Identifier targetModel = ModelLocationUtils.getModelLocation(blockItem.getBlock());
            this.accept(blockItem, ItemModelUtils.plainModel(targetModel));
          }
        }

      });
      this.copies.forEach((acceptor, donor) -> {
        ClientItem donorInfo = this.itemInfos.get(donor);
        if (donorInfo == null) {
          String var10002 = String.valueOf(donor);
          throw new IllegalStateException("Missing donor: " + var10002 + " -> " + acceptor);
        } else {
          this.register(acceptor, donorInfo);
        }
      });
      List<Identifier> missingDefinitions = this.knownItems.get().filter((e) -> !this.itemInfos.containsKey(e.value())).map((e) -> e.unwrapKey().orElseThrow().identifier()).toList();
      if (!missingDefinitions.isEmpty()) {
        throw new IllegalStateException("Missing item model definitions for: " + missingDefinitions);
      }
    }

    public CompletableFuture<?> save(CachedOutput cache, PackOutput.PathProvider pathProvider) {
      CompletableFuture<?>[] var10000 = new CompletableFuture[]{DataProvider.saveAll(cache, ClientItem.CODEC,
          (item) -> pathProvider.json(item.builtInRegistryHolder().key().identifier()), this.itemInfos), null};
      var var10004 = ClientItem.CODEC;
      Objects.requireNonNull(pathProvider);
      var10000[1] = DataProvider.saveAll(cache, var10004, pathProvider::json, this.idItemInfos);
      return CompletableFuture.allOf(var10000);
    }
  }

  private static class SimpleModelCollector implements BiConsumer<Identifier, ModelInstance> {
    private final Map<Identifier, ModelInstance> models = new HashMap<>();

    private SimpleModelCollector() {
    }

    public void accept(Identifier id, ModelInstance contents) {
      Supplier<JsonElement> prev = this.models.put(id, contents);
      if (prev != null) {
        throw new IllegalStateException("Duplicate model definition for " + id);
      }
    }

    public CompletableFuture<?> save(CachedOutput cache, PackOutput.PathProvider pathProvider) {
      Objects.requireNonNull(pathProvider);
      return DataProvider.saveAll(cache, Supplier::get, pathProvider::json, this.models);
    }
  }
}
