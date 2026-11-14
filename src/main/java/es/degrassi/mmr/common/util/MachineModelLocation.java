package es.degrassi.mmr.common.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import net.minecraft.IdentifierException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MachineModelLocation {
  public static final MachineModelLocation DEFAULT = new MachineModelLocation(
      ModularMachineryReborn.rl("default/controller").toString(),
      null,
      null,
      ModularMachineryReborn.rl("default/controller"),
      null
  );

  /**
   * The default model of the controller for a state of the machine. They are the ones
   * the controller uses when the machine does not define a model of its own.
   */
  private static final Map<MachineStatus, MachineModelLocation> DEFAULTS = Stream.of(MachineStatus.values())
      .collect(Collectors.toMap(
          status -> status,
          status -> new MachineModelLocation(
              ControllerOverlays.model(status).toString(),
              null,
              null,
              ControllerOverlays.model(status),
              null
          )
      ));

  /**
   * The default model of the controller for a state of the machine.
   */
  public static MachineModelLocation defaultFor(MachineStatus status) {
    return DEFAULTS.getOrDefault(status, DEFAULT);
  }

  /**
   * Whether this is one of the default models of the controller (and not a model the
   * machine brought with it).
   */
  public boolean isDefault() {
    return this == DEFAULT || DEFAULTS.containsValue(this);
  }

  @HideFromJS
  public static final NamedCodec<MachineModelLocation> CODEC = NamedCodec.STRING.comapFlatMap(s -> {
    try {
      return DataResult.success(MachineModelLocation.of(s));
    } catch (IdentifierException e) {
      return DataResult.error(e::getMessage);
    }
  }, MachineModelLocation::toString, "Model location");

  @HideFromJS
  private final String loc;
  @Nullable
  @HideFromJS
  private final BlockState state;
  @Nullable Item item;
  @Nullable
  @HideFromJS
  private final Identifier id;
  @Nullable
  @HideFromJS
  private final String properties;

  public static MachineModelLocation of(String loc) {
    BlockState state = null;
    try {
      state = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, new StringReader(loc), false).blockState();
    } catch (CommandSyntaxException ignored) {
    }
    Identifier id = null;
    String properties = null;
    if (loc.contains("#")) {
      id = Identifier.parse(loc.substring(0, loc.indexOf("#")));
      properties = loc.substring(loc.indexOf("#") + 1);
    } else if (state == null)
      id = Identifier.parse(loc);

    Item item = null;
    if (id != null && BuiltInRegistries.ITEM.containsKey(id))
      item = BuiltInRegistries.ITEM.get(id).map(ref -> ref.value()).orElse(null);

    return new MachineModelLocation(loc, state, item, id, properties);
  }

  @HideFromJS
  private MachineModelLocation(String loc, @Nullable BlockState state, @Nullable Item item, @Nullable Identifier id, @Nullable String properties) {
    this.loc = loc;
    this.state = state;
    this.item = item;
    this.id = id;
    this.properties = properties;
  }

  @Nullable
  @HideFromJS
  public BlockState getState() {
    return this.state;
  }

  @Nullable
  @HideFromJS
  public Item getItem() {
    return this.item;
  }

  @Nullable
  @HideFromJS
  public Identifier getLoc() {
    return this.id;
  }

  @Nullable
  @HideFromJS
  public String getProperties() {
    return this.properties;
  }

  @Override
  @HideFromJS
  public String toString() {
    return this.loc;
  }
}
