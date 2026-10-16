package es.degrassi.mmr.api;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockIngredient implements IIngredient<PartialBlockState, BlockInWorld> {
  public static final BlockIngredient AIR = new BlockIngredient("air", false, PartialBlockState.AIR);
  public static final BlockIngredient ANY = new BlockIngredient("any", false, PartialBlockState.ANY);
  public static final BlockIngredient MACHINE = new BlockIngredient("machine", false, PartialBlockState.MACHINE);
  public static final BlockIngredient NOT_MACHINE = new BlockIngredient("not_machine", true, PartialBlockState.MACHINE);
  public static final BlockIngredient STRUCTURE_CHECKER = new BlockIngredient("structure_creator", false, PartialBlockState.STRUCTURE_CHECKER);
  public static final BlockIngredient NOT_STRUCTURE_CHECKER = new BlockIngredient("not_structure_creator", true, PartialBlockState.STRUCTURE_CHECKER);

  public static final NamedCodec<BlockIngredient> STRING_CODEC = NamedCodec.STRING.comapFlatMap(s -> {
    try {
      StringReader reader = new StringReader(s);
      reader.skipWhitespace();
      boolean not = false;

      if (!reader.getRemaining().contains("[") && reader.getRemaining().contains("]")) {
        reader = new StringReader(reader.getRemaining().replaceAll("]", ""));
      }

      if (reader.peek() == '!') {
        not = true;
        reader.skip();
      }
      if (reader.peek() == '[') {
        reader.skip();
        s = reader.readStringUntil(']');
      } else {
        s = reader.getRemaining();
      }

      String[] arr = s.split(", ");
      return DataResult.success(
          Arrays.stream(arr)
              .map(string -> {
                try {
                  return BlockIngredient.of(string);
                } catch (CommandSyntaxException e) {
                  throw new IllegalArgumentException(e);
                }
              })
              .reduce(new BlockIngredient("", not, Collections.emptyList(), Collections.emptyList()), BlockIngredient::merge)
      );
    } catch(IllegalArgumentException | CommandSyntaxException e) {
      return DataResult.error(e::getMessage);
    }
  }, BlockIngredient::getString, "BlockIngredient from string");

  public static final NamedCodec<BlockIngredient> ING_CODEC = NamedCodec.either(
      PartialBlockState.CODEC,
      STRING_CODEC,
      "Block Ingredient"
  )
  .listOf()
  .flatComapMap(
      list -> {
        List<BlockIngredient> ings = Lists.newArrayList();
        list.forEach(either -> ings.add(either.map(BlockIngredient::new, Function.identity())));
        AtomicReference<BlockIngredient> ing = new AtomicReference<>(null);
        ings.iterator().forEachRemaining(i -> {
          if (ing.get() == null) {
            ing.set(i);
            return;
          }
          ing.set(ing.get().merge(i));
        });
        return ing.get();
      },
      ing -> {
        List<Either<PartialBlockState, BlockIngredient>> list = Lists.newArrayList();
        list.add(Either.right(ing));
        return DataResult.success(list);
      },
      "Block Ingredient"
  );

  public static final NamedCodec<BlockIngredient> MAP_CODEC = NamedCodec.record(blockIngredientInstance ->
          blockIngredientInstance.group(
              NamedCodec.BOOL.optionalFieldOf("not", false).forGetter(ingredient -> ingredient.not),
              ING_CODEC.fieldOf("ingredient").forGetter(Function.identity())
          ).apply(
              blockIngredientInstance,
              (not, ingredient) -> new BlockIngredient(ingredient.id, not, ingredient.insertedTags, ingredient.insertedStates)
          ),
      "Block ingredient"
  );

  public static final NamedCodec<BlockIngredient> CODEC =
      NamedCodec.either(MAP_CODEC, STRING_CODEC).xmap(either -> either.map(Function.identity(), Function.identity()),
          Either::left, "Block Ingredient");

  private final Supplier<List<PartialBlockState>> partialBlockStates;
  @Getter
  @Setter
  private List<TagKey<Block>> tags = Lists.newArrayList();
  @Getter
  private final boolean not;
  final List<TagKey<Block>> insertedTags;
  final List<PartialBlockState> insertedStates;

  @Getter
  private final String id;

  public BlockIngredient(String id, List<TagKey<Block>> tags, List<PartialBlockState> states) {
    this(id, false, tags, states);
  }

  public BlockIngredient(String id, boolean not, List<TagKey<Block>> tags, List<PartialBlockState> states) {
    List<PartialBlockState> statesCopy = Lists.newArrayList(states);
    this.insertedStates = states;
    this.insertedTags = tags;
    this.tags.addAll(tags);
    this.not = not;
    tags.forEach(tag ->
        statesCopy.addAll(TagUtil.getBlocks(tag)
            .map(PartialBlockState::new)
            .toList())
    );
    this.partialBlockStates = Suppliers.memoize(() -> ImmutableList.copyOf(statesCopy));
    this.id = id;
  }

  public BlockIngredient(String id, boolean not, PartialBlockState partialBlockState) {
    this(id, not, Collections.emptyList(), Collections.singletonList(partialBlockState));
  }

  public BlockIngredient(String id, PartialBlockState partialBlockState) {
    this(id, false, partialBlockState);
  }

  public BlockIngredient(PartialBlockState partialBlockState) {
    this("", partialBlockState);
  }

  public static BlockIngredient create(Object o) throws IllegalArgumentException, CommandSyntaxException {
    if (o instanceof List<?> sa) {
      return sa.stream()
          .filter(s -> s instanceof CharSequence)
          .map(s -> (CharSequence) s)
          .map(s -> {
            try {
              return BlockIngredient.of(s);
            } catch (CommandSyntaxException e) {
              throw new IllegalArgumentException(e);
            }
          })
          .reduce(
              new BlockIngredient("", false, Collections.emptyList(), Collections.emptyList()),
              BlockIngredient::merge
          );
    } else if (!(o instanceof CharSequence s)) throw new IllegalArgumentException("Block ingredient must be a string or string[]");
    else return BlockIngredient.of(s);
  }

  public BlockIngredient copy() {
    return new BlockIngredient(
        id,
        not,
        insertedTags.stream()
            .map(TagKey::location)
            .map(tag -> TagKey.create(BuiltInRegistries.BLOCK.key(), tag))
            .toList(),
        insertedStates
            .stream()
            .map(PartialBlockState::copy)
            .toList()
    );
  }

  @Override
  public List<PartialBlockState> getAll() {
    return this.partialBlockStates.get();
  }

  @Override
  public boolean test(BlockInWorld block) {
    boolean isTag = !this.insertedTags.isEmpty();
    boolean partial = false;
    if (isTag) {
      if (this.not) {
        partial = this.insertedTags.stream().noneMatch(tag -> block.getState().is(tag));
      } else {
        partial = this.insertedTags.stream().anyMatch(tag -> block.getState().is(tag));
      }
    }
    if (this.not) {
      return partial || this.insertedStates.stream().noneMatch(state -> state.test(block));
    } else {
      return partial || this.insertedStates.stream().anyMatch(state -> state.test(block));
    }
  }

  public boolean test(Block block) {
    boolean isTag = !this.insertedTags.isEmpty();
    boolean partial = false;
    if (isTag) {
      if (not) {
        partial = this.insertedTags.stream().noneMatch(tag -> TagUtil.contains(tag, block));
      } else {
        partial = this.insertedTags.stream().anyMatch(tag -> TagUtil.contains(tag, block));
      }
    }
    if (this.not) {
      return partial || this.insertedStates.stream().noneMatch(state -> state.getBlockState().getBlock() == block);
    } else {
      return partial || this.insertedStates.stream().anyMatch(state -> state.getBlockState().getBlock() == block);
    }
  }

  public List<ItemStack> getStacks(int amount) {
    List<ItemStack> stacks = getTagStacks(amount);
    stacks.addAll(getNonTagStacks(amount));
    return stacks
        .stream()
        .collect(Collectors.groupingBy(ItemStack::getItem, Collectors.summingInt(ItemStack::getCount)))
        .entrySet()
        .stream()
        .map(entry -> new ItemStack(entry.getKey(), entry.getValue()))
        .toList();
  }

  public List<ItemStack> getNonTagStacks(int amount) {
    return insertedStates
        .stream()
        .map(PartialBlockState::getBlockState)
        .map(BlockState::getBlock)
        .map(b -> b instanceof LiquidBlock lb ? lb.fluid.getBucket() : b.asItem())
        .map(Item::getDefaultInstance)
        .map(stack -> stack.copyWithCount(amount))
        .toList();
  }

  public List<ItemStack> getTagStacks(int amount) {
    return Lists.newArrayList(
        insertedTags
            .stream()
            .flatMap(TagUtil::getBlocks)
            .map(b -> b instanceof LiquidBlock lb ? lb.fluid.getBucket() : b.asItem())
            .map(Item::getDefaultInstance)
            .map(stack -> stack.copyWithCount(amount))
            .iterator()
    );
  }

  public List<Component> getNames() {
    List<Component> ingredients = Lists.newArrayList();
    ingredients.addAll(this.insertedTags.stream().map(TagKey::location).map(Identifier::toString).map(s -> "#" + s).map(Component::literal).toList());

    ingredients.addAll(
        insertedStates.stream()
            .map(PartialBlockState::getName)
            .toList()
    );

    return ingredients;
  }

  public MutableComponent getNamesUnified() {
    MutableComponent name = Component.empty();
    Component current;
    Iterator<Component> iterator = getNames().iterator();
    if (not) {
      name.append(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.not"));
    }
    while (iterator.hasNext()) {
      current = iterator.next();
      name.append(current);
      if (iterator.hasNext()) {
        name.append(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.or"));
      }
    }
    return name;
  }

  public String getString() {
    Set<String> ings = Sets.newHashSet();
    ings.addAll(this.insertedTags.stream().map(TagKey::location).map(Identifier::toString).map(s -> "#" + s).toList());

    ings.addAll(insertedStates.stream().map(PartialBlockState::toString).toList());

    if (ings.size() == 1) {
      return (this.not ? "!" : "") + ings.stream().toList().getFirst();
    }

    return (this.not ? "!" : "") + ings;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  public BlockIngredient copyWithRotation(Rotation rotation) {
    return new BlockIngredient(id, not, insertedTags, insertedStates.stream().map(state -> state.copyWithRotation(rotation)).toList());
  }

  public BlockIngredient merge(BlockIngredient other) {
    if (other == null) return AIR.merge(this);
    Set<PartialBlockState> ingredients = Sets.newHashSet();
    ingredients.addAll(insertedStates);
    ingredients.addAll(other.insertedStates);
    Set<TagKey<Block>> tags = Sets.newHashSet();
    tags.addAll(this.insertedTags);
    tags.addAll(other.insertedTags);
    var i = id;
    if (!i.isEmpty() && !other.id.isEmpty()) i += ",";
    i += other.id;
    return new BlockIngredient(i, other.not || not, tags.stream().toList(), ingredients.stream().toList());
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("not", not);
    json.addProperty("tags", insertedTags.toString());
    JsonArray array = new JsonArray();
    insertedStates.forEach(state -> array.add(state.toString()));
    json.add("states", array);
    return json;
  }

  public CompoundTag asTag() {
    CompoundTag tag = new CompoundTag();
    ListTag tagList = new ListTag();
    tag.putBoolean("not", not);
    insertedTags.forEach(t -> tagList.add(StringTag.valueOf(t.toString())));
    tag.put("tags", tagList);
    ListTag states = new ListTag();
    insertedStates.forEach(state -> states.add(StringTag.valueOf(state.toString())));
    tag.put("states", states);
    return tag;
  }

  public static BlockIngredient of(CharSequence s) throws CommandSyntaxException {
    StringReader reader = new StringReader(s.toString());

    reader.skipWhitespace();

    if (!reader.getRemaining().contains("[") && reader.getRemaining().contains("]")) {
      reader = new StringReader(reader.getRemaining().replaceAll("]", ""));
    }

    boolean not = false;

    if (reader.peek() == '!') {
      not = true;
      reader.skip();
    }

    if (reader.peek() == '#') {
      reader.skip();
      TagKey<Block> tag = TagKey.create(Registries.BLOCK, Identifier.tryParse(reader.getRemaining()));
      return new BlockIngredient("#" + tag.location(), not, Collections.singletonList(tag), Collections.emptyList());
    }

    PartialBlockState state = PartialBlockState.of(reader.getRemaining());
    return new BlockIngredient(state.toString(), not, Collections.emptyList(), Collections.singletonList(state));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof BlockIngredient that)) return false;
    return this.not == that.not && Objects.equals(insertedStates, that.insertedStates) && Objects.equals(insertedTags, that.insertedTags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(not, insertedStates, insertedTags);
  }
}
