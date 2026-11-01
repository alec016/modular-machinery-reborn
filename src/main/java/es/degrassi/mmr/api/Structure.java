package es.degrassi.mmr.api;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.common.crafting.modifier.ModifierReplacement;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.util.Comparators;
import es.degrassi.mmr.common.util.TaskDelayer;
import es.degrassi.mmr.data.tags.MMRTags;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class Structure {
  public static final NamedCodec<Structure> CODEC = NamedCodec.record(structure -> structure.group(
      DefaultCodecs.CHARACTER.optionalFieldOf("machine_key", 'm').forGetter(Structure::getMachineKey),
      NamedCodec.STRING.listOf().listOf().fieldOf("pattern").forGetter(s -> s.pattern.asList()),
      NamedCodec.unboundedMap(DefaultCodecs.CHARACTER, BlockIngredient.CODEC, "Map<Character, Block>").fieldOf("keys").forGetter(s -> s.pattern.asMap()),
      ModifierReplacement.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(s -> s.pattern.getModifiers()),
      MinBlocksPredicate.CODEC.optionalFieldOf("min_blocks", MinBlocksPredicate.EMPTY).forGetter(Structure::minBlocks)
  ).apply(structure, Structure::makeStructure), "Structure with modifiers");

  public static final Structure EMPTY = new Structure('m', Map.of(), List.of(List.of("m")), Map.of(), List.of(), MinBlocksPredicate.EMPTY);
  private static final RandomSource random = RandomSource.create(42L);

  private static Structure makeStructure(Character machineKey, List<List<String>> pattern, Map<Character, BlockIngredient> keys, List<ModifierReplacement> modifiers,
                                         MinBlocksPredicate minBlocks) {
    Structure.Builder builder = Structure.Builder.start(machineKey);
    for (List<String> levels : pattern)
      builder.aisle(levels.toArray(new String[0]));
    for (Map.Entry<Character, BlockIngredient> key : keys.entrySet())
      builder.where(key.getKey(), key.getValue());
    return builder.build(pattern, keys, modifiers, minBlocks);
  }

  public static void place(DynamicMachine machine, BlockPos controllerPos, Level level, boolean isCreative, ServerPlayer player, boolean withModifiers) {
    Structure structure = machine.getPattern();
    BlockState blockState = level.getBlockState(controllerPos);
    Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
    Map<BlockPos, BlockIngredient> blocks = withModifiers ? structure.getBlocks(facing) : structure.getBlocksFiltered(facing);
    Map<BlockPos, BlockIngredient> ordered = blocks
        .entrySet()
        .stream()
        .sorted(Comparators.STRUCTURE_COMPARATOR)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (a, b) -> a,
            LinkedHashMap::new
        ));
    AtomicInteger ticks = new AtomicInteger(0);
    for (BlockPos pos : ordered.keySet()) {
      TaskDelayer.enqueue(ticks.getAndAdd(MMRConfig.get().delayOperation.get()), () -> {
        BlockPos.MutableBlockPos worldPos = new BlockPos.MutableBlockPos();
        BlockIngredient ingredient = blocks.get(pos);
        if (
            ingredient.equals(BlockIngredient.AIR) ||
                ingredient.equals(BlockIngredient.ANY)
        ) {
          return;
        } else if (ingredient.getAll().stream().anyMatch(state ->
            state.equals(PartialBlockState.AIR) ||
                state.equals(PartialBlockState.ANY) ||
                state.getBlockState().isAir()
        )) {
          ingredient = new BlockIngredient(ingredient.getId(), ingredient.insertedTags, ingredient.insertedStates.stream().filter(state ->
              !state.equals(PartialBlockState.AIR) &&
                  !state.equals(PartialBlockState.ANY) &&
                  !state.getBlockState().isAir()
          ).toList());
        }
        if (ingredient.getAll().isEmpty()) return;
        worldPos.set(pos.getX() + controllerPos.getX(), pos.getY() + controllerPos.getY(), pos.getZ() + controllerPos.getZ());
        BlockInWorld info = new BlockInWorld(level, worldPos, false);
        BlockInWorld finalInfo = info;
        if (!info.getState().isAir() && !ingredient.test(finalInfo)) {
          if (isCreative) level.destroyBlock(worldPos, false);
          else {
            if (MMRConfig.get().shouldReplace.get()) {
              if (finalInfo.getState().is(MMRTags.Blocks.REPLACEABLE)) {
                level.destroyBlock(worldPos, true);
                if (MMRConfig.get().sendReplaceMessage.get()) {
                  player.sendSystemMessage(Component.translatable(
                      "mmr.place.replace",
                      finalInfo.getState().getBlock().getName(),
                      "X:" + worldPos.getX() + " Y:" + worldPos.getY() + " Z:" + worldPos.getZ()
                  ));
                }
                info = new BlockInWorld(level, worldPos, false);
              }
            } else {
              if (MMRConfig.get().sendErrorMessage.get()) {
                player.sendSystemMessage(
                    Component.translatable(
                        "mmr.place.non_air",
                        finalInfo.getState().getBlock().getName(),
                        "X:" + worldPos.getX() + " Y:" + worldPos.getY() + " Z:" + worldPos.getZ()
                    )
                );
              }
            }
          }
        }
        if (!isCreative) {
          boolean placed = false;
          if (!info.getState().isAir()) {
            if (!MMRConfig.get().shouldReplace.get()) return;
            if (!level.getBlockState(worldPos).is(MMRTags.Blocks.REPLACEABLE)) return;
          }
          for (PartialBlockState state : ingredient.getAll()) {
            if (state.equals(PartialBlockState.AIR) || state.equals(PartialBlockState.ANY)) return;
            ItemStack blockToRemove2 = new ItemStack(state.getBlockState().getBlock());
            if (state.isFluid()) blockToRemove2 = new ItemStack(state.getBlockState().getFluidState().getType().getBucket());
            if (!ingredient.isNot() && player.getInventory().contains(blockToRemove2)) {
              int slot = player.getInventory().findSlotMatchingItem(blockToRemove2);
              player.getInventory().removeItem(slot, 1);
              if (blockToRemove2.getCraftingRemainder() != null) player.getInventory().placeItemBackInInventory(blockToRemove2.getCraftingRemainder().create());
              player.containerMenu.broadcastChanges();
              player.inventoryMenu.slotsChanged(player.getInventory());
              setBlock(level, worldPos, state);
              placed = true;
              break;
            } else if (ingredient.isNot()) {
              for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (state.isFluid()) {
                  if (!stack.is(blockToRemove2.getItem()) && stack.getItem() instanceof BucketItem bi) {
                    player.getInventory().removeItem(i, 1);
                    if (stack.getCraftingRemainder() != null) player.getInventory().placeItemBackInInventory(stack.getCraftingRemainder().create());
                    player.containerMenu.broadcastChanges();
                    player.inventoryMenu.slotsChanged(player.getInventory());
                    setBlock(level, worldPos, new PartialBlockState(bi.getContent().defaultFluidState().createLegacyBlock().getBlock()));
                    placed = true;
                    break;
                  }
                } else {
                  if (!stack.is(blockToRemove2.getItem()) && stack.getItem() instanceof BlockItem bi) {
                    player.getInventory().removeItem(i, 1);
                    if (stack.getCraftingRemainder() != null) player.getInventory().placeItemBackInInventory(stack.getCraftingRemainder().create());
                    player.containerMenu.broadcastChanges();
                    player.inventoryMenu.slotsChanged(player.getInventory());
                    setBlock(level, worldPos, new PartialBlockState(bi.getBlock()));
                    placed = true;
                    break;
                  }
                }
              }
              break;
            }
          }
          if (!placed && MMRConfig.get().sendErrorMessage.get())
            player.sendSystemMessage(
                Component.translatable(
                    "mmr.place.no_item",
                    ingredient.getString(),
                    "X:" + worldPos.getX() + " Y:" + worldPos.getY() + " Z:" + worldPos.getZ(),
                    ingredient.getString()
                )
            );
          return;
        }
        if (worldPos.equals(controllerPos)) return;
        if (ingredient.isNot()) {
          BlockIngredient finalIngredient = ingredient;
          var filtered = BuiltInRegistries.BLOCK.stream().filter(b -> finalIngredient.getAll().stream().noneMatch(s -> s.getBlockState().is(b))).toList();
          setBlock(level, worldPos, new PartialBlockState(filtered.get(random.nextInt(0, filtered.size()))));
        } else {
          setBlock(level, worldPos, ingredient.getAll().get(random.nextInt(0, ingredient.getAll().size())));
        }
      });
    }
  }

  public static void breakStructure(DynamicMachine machine, BlockPos controllerPos, Level level, ServerPlayer player) {
    boolean isCreative = player.isCreative();
    Direction direction = level.getBlockState(controllerPos).getValue(BlockStateProperties.HORIZONTAL_FACING);
    Map<BlockPos, BlockIngredient> blocks = machine.getPattern().getBlocks(direction);
    BlockPos.MutableBlockPos worldPos = new BlockPos.MutableBlockPos();
    AtomicInteger delay = new AtomicInteger(0);
    List<BlockPos> ordered = blocks
        .entrySet()
        .stream()
        .sorted(Comparators.STRUCTURE_COMPARATOR.reversed())
        .map(Map.Entry::getKey)
        .toList();
    for (BlockPos pos : ordered) {
      BlockIngredient ingredient = blocks.get(pos);
      worldPos.set(pos.getX() + controllerPos.getX(), pos.getY() + controllerPos.getY(), pos.getZ() + controllerPos.getZ());
      BlockInWorld info = new BlockInWorld(level, worldPos, false);
      if (info.getState().isAir()) continue;
      if (info.getEntity() instanceof MachineControllerEntity) continue;
      if (ingredient.test(info)) {
        TaskDelayer.enqueue(delay.getAndAdd(MMRConfig.get().delayOperation.get()), () -> {
          var wp = new BlockPos.MutableBlockPos();
          wp.set(pos.getX() + controllerPos.getX(), pos.getY() + controllerPos.getY(), pos.getZ() + controllerPos.getZ());
          if (!info.getState().getFluidState().isEmpty() && info.getState().getFluidState().isSource()) {
            var removed = false;
            for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
              if (!player.getInventory().getItem(i).is(Items.BUCKET)) continue;
              ItemStack toAdd = new ItemStack(info.getState().getFluidState().getType().getBucket());
              player.getInventory().removeItem(i, 1);
              spawnItem(level, toAdd, direction, wp.immutable().getCenter());
              removed = true;
              break;
            }
            if (removed) {
              level.setBlockAndUpdate(wp.immutable(), Blocks.AIR.defaultBlockState());
              level.playSound(null, wp.immutable(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS);
            }
          } else  {
            level.destroyBlock(wp.immutable(), !isCreative);
          }
        });
      }
    }
  }

  private static void spawnItem(Level level, ItemStack stack, Direction facing, Position position) {
    double d0 = position.x();
    double d1 = position.y();
    double d2 = position.z();
    if (facing.getAxis() == Direction.Axis.Y) {
      d1 -= 0.125F;
    } else {
      d1 -= 0.15625F;
    }

    ItemEntity itementity = new ItemEntity(level, d0, d1, d2, stack);
    level.addFreshEntity(itementity);
  }

  private static void setBlock(Level world, BlockPos pos, PartialBlockState state) {
    world.setBlockAndUpdate(pos, state.getBlockState());
    SoundType soundType = state.getBlockState().getSoundType(world, pos, null);
    if (state.isFluid()) world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS);
    else world.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS);
    BlockEntity tile = world.getBlockEntity(pos);
    if (tile != null && state.getNbt() != null && !state.getNbt().isEmpty()) {
      CompoundTag nbt = state.getNbt().copy();
      nbt.putInt("x", pos.getX());
      nbt.putInt("y", pos.getY());
      nbt.putInt("z", pos.getZ());
      var input = TagValueInput.create(new ProblemReporter.ScopedCollector(tile.problemPath(), LogUtils.getLogger()), world.registryAccess(), nbt);
      tile.loadWithComponents(input);
    }
  }

  private final Pattern pattern;
  private final Character machineKey;
  private final MinBlocksPredicate minBlocksPredicate;

  public Structure(Character machineKey, Map<BlockPos, BlockIngredient> blocks, List<List<String>> pattern, Map<Character, BlockIngredient> keys,
                   List<ModifierReplacement> modifiers, MinBlocksPredicate minBlocks) {
    this.pattern = new Pattern(blocks, pattern, keys, modifiers);
    this.machineKey = machineKey;
    this.minBlocksPredicate = minBlocks;
  }

  public Structure(Character machineKey, Map<BlockPos, BlockIngredient> blocks, List<List<String>> pattern,
                   Map<Character, BlockIngredient> keys, MinBlocksPredicate minBlocks) {
    this.pattern = new Pattern(blocks, pattern, keys);
    this.machineKey = machineKey;
    this.minBlocksPredicate = minBlocks;
  }

  public MinBlocksPredicate minBlocks() {
    return minBlocksPredicate;
  }

  public Map<BlockPos, BlockIngredient> getBlocks(Direction direction) {
    return pattern.get(direction);
  }

  public Map<BlockPos, BlockIngredient> getBlocksFiltered(Direction direction) {
    return pattern.getFiltered(direction);
  }

  public boolean match(LevelReader world, BlockPos machinePos, Direction machineFacing) {
    minBlocksPredicate.reset();
    return pattern.match(world, machinePos, machineFacing, minBlocksPredicate);
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.add("pattern", pattern.asJson());
    json.add("minBlocks", minBlocksPredicate.asJson());
    return json;
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  public static class Builder {

    private static final Joiner COMMA_JOIN = Joiner.on(",");
    private final List<String[]> depth = Lists.newArrayList();
    private final Map<Character, BlockIngredient> symbolMap = Maps.newHashMap();
    private int aisleHeight;
    private int rowWidth;
    private final char machineKey;

    private Builder(char machineKey) {
      this(machineKey, BlockIngredient.MACHINE, BlockIngredient.NOT_MACHINE);
    }

    private Builder(char machineKey, BlockIngredient machine, BlockIngredient notMachine) {
      this.machineKey = machineKey;
      this.symbolMap.put(' ', BlockIngredient.ANY);
      this.symbolMap.put(machineKey, machine);
      this.symbolMap.put('_', notMachine);
    }

    /**
     * Adds a single aisle to this pattern, going in the y-axis. (so multiple calls to this will increase the y-size by
     * 1)
     */
    public Builder aisle(String... aisle) {
      if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
        if (this.depth.isEmpty()) {
          this.aisleHeight = aisle.length;
          this.rowWidth = aisle[0].length();
        }

        if (aisle.length != this.aisleHeight) {
          throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")");
        } else {
          for (String s : aisle) {
            if (s.length() != this.rowWidth) {
              throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + s.length() + ")");
            }

            for (char c0 : s.toCharArray()) {
              if (!this.symbolMap.containsKey(c0)) {
                this.symbolMap.put(c0, null);
              }
            }
          }

          this.depth.add(aisle);
          return this;
        }
      } else {
        throw new IllegalArgumentException("Empty pattern for aisle");
      }
    }

    public static Builder start(char machineKey) {
      return machineKey == 'm' ? new Builder(machineKey) : new Builder(machineKey, BlockIngredient.STRUCTURE_CHECKER,
          BlockIngredient.NOT_STRUCTURE_CHECKER);
    }

    public Builder where(char symbol, BlockIngredient blockMatcher) {
      this.symbolMap.put(symbol, blockMatcher);
      return this;
    }

    public Structure build(List<List<String>> pattern, Map<Character, BlockIngredient> keys,
                           List<ModifierReplacement> modifiers,
                           MinBlocksPredicate minBlocks) {
      this.checkMissingPredicates();
      BlockPos machinePos = this.getMachinePos();
      Map<BlockPos, BlockIngredient> blocks = Maps.newHashMap();
      for (int i = 0; i < this.depth.size(); ++i) {
        for (int j = 0; j < this.aisleHeight; ++j) {
          for (int k = 0; k < this.rowWidth; ++k) {
            blocks.put(new BlockPos(k - machinePos.getX(), i - machinePos.getY(), j - machinePos.getZ()), this.symbolMap.get((this.depth.get(i))[j].charAt(k)));
          }
        }
      }
      return new Structure(machineKey, blocks, pattern, keys, modifiers, minBlocks);
    }

    private BlockPos getMachinePos() {
      BlockPos machinePos = null;
      for (int i = 0; i < this.depth.size(); ++i) {
        for (int j = 0; j < this.aisleHeight; ++j) {
          for (int k = 0; k < this.rowWidth; ++k) {
            if ((this.depth.get(i))[j].charAt(k) == machineKey)
              if (machinePos == null)
                machinePos = new BlockPos(k, i, j);
              else
                throw new IllegalStateException(
                    String.format("The structure pattern need exactly one '%s' character to defined the machine position, several found !", machineKey)
                );
          }
        }
      }
      if (machinePos != null)
        return machinePos;
      throw new IllegalStateException("You need to define the machine position in the structure with character 'm'");
    }

    private void checkMissingPredicates() {
      List<Character> list = Lists.newArrayList();

      for (Map.Entry<Character, BlockIngredient> entry : this.symbolMap.entrySet()) {
        if (entry.getValue() == null) {
          list.add(entry.getKey());
        }
      }

      if (!list.isEmpty()) {
        throw new IllegalStateException("Blocks for character(s) " + COMMA_JOIN.join(list) + " are missing");
      }
    }

    public JsonObject asJson() {
      JsonObject json = new JsonObject();
      json.addProperty("aisleHeight", aisleHeight);
      json.addProperty("rowWidth", rowWidth);
      JsonArray depth = new JsonArray();
      this.depth.forEach(array -> {
        JsonArray newOne = new JsonArray();
        Arrays.asList(array).forEach(newOne::add);
        depth.add(newOne);
      });
      json.add("depth", depth);
      JsonObject symbols = new JsonObject();
      this.symbolMap.forEach((key, value) -> symbols.add(String.valueOf(key), value.asJson()));
      json.add("symbolMap", symbols);
      return json;
    }

    @Override
    public String toString() {
      return asJson().toString();
    }
  }
}
