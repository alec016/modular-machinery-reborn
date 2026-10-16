package es.degrassi.mmr.common.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.rhino.util.HideFromJS;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.IdentifierException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MachineModelLocation {
  public static final MachineModelLocation DEFAULT = new MachineModelLocation(
      ModularMachineryReborn.rl("default/controller").toString(),
      null,
      null,
      ModularMachineryReborn.rl("default/controller"),
      null
  );

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
