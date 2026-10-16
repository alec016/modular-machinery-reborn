package es.degrassi.mmr.api.capability.config;

import com.google.common.collect.Maps;
import es.degrassi.experiencelib.api.codec.EnumMapCodec;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.common.util.Color;
import net.minecraft.core.Direction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class IOSideConfig extends SideConfig<IOSideMode>{
  private final Map<Direction, Boolean> autoIOFaces = new EnumMap<>(Direction.class);

  public IOSideConfig(ISideConfigComponent<IOSideMode> component, Map<RelativeSide, IOSideMode> defaultConfig, boolean enabled, Color color) {
    super(component, defaultConfig, enabled, color);
    Arrays.stream(Direction.values()).forEach(side -> this.autoIOFaces.put(side, false));
    this.refreshAutoIO();
  }

  @Override
  public void setNext(RelativeSide side) {
    this.setSideMode(side, this.getSideMode(side) == IOSideMode.ENABLED ? IOSideMode.DISABLED : IOSideMode.ENABLED);
    refreshAutoIO();
  }

  @Override
  public void setPrevious(RelativeSide side) {
    this.setSideMode(side, this.getSideMode(side) == IOSideMode.ENABLED ? IOSideMode.DISABLED : IOSideMode.ENABLED);
    refreshAutoIO();
  }

  @Override
  public IOSideConfig copy() {
    var config = new IOSideConfig(this.getComponent(), this.sides, this.isEnabled(), this.getColor());
    config.autoIOFaces.putAll(autoIOFaces);
    return config;
  }

  @Override
  public void serialize(ValueOutput output) {
    for (var side : RelativeSide.values()) {
      output.store(side.name(), IOSideMode.CODEC.codec(), sides.get(side));
    }
    for (var side : Direction.values()) {
      output.putBoolean(side.getSerializedName(), autoIOFaces.get(side));
    }
  }

  @Override
  public void deserialize(ValueInput nbt) {
    for(RelativeSide side : RelativeSide.values())
      this.sides.put(side, nbt.read(side.name(), IOSideMode.CODEC.codec()).orElse(IOSideMode.ENABLED));
    for (Direction side : Direction.values())
      this.autoIOFaces.put(side, nbt.getBooleanOr(side.getSerializedName(), true));
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this)
      return true;
    else if(obj instanceof IOSideConfig config) {
      for(RelativeSide side : RelativeSide.values()) {
        if(config.getSideMode(side) != this.getSideMode(side))
          return false;
      }
      return true;
    }
    return false;
  }

  private void refreshAutoIO() {
    if(!this.isEnabled())
      this.autoIOFaces.replaceAll((side, io) -> false);
    else
      this.autoIOFaces.replaceAll((side, io) -> this.getDirectionMode(side) != IOSideMode.DISABLED);
  }

  public boolean canAutoIO(Direction side) {
    return this.autoIOFaces.get(side);
  }

  public record Template(Map<RelativeSide, IOSideMode> sides, boolean enabled, Color color) implements SideConfig.Template<IOSideMode> {

    public static final NamedCodec<Template> CODEC = NamedCodec.record(templateInstance ->
        templateInstance.group(
            EnumMapCodec.of(RelativeSide.class, IOSideMode.CODEC, IOSideMode.ENABLED).forGetter(template -> template.sides),
            NamedCodec.BOOL.optionalFieldOf("enabled", true).forGetter(Template::enabled),
            Color.CODEC.optionalFieldOf("color", DEFAULT_COLOR).forGetter(Template::color)
        ).apply(templateInstance, Template::new), "Toggle Side Config Template");

    public static final Template DEFAULT_ALL_ENABLED = makeDefault(IOSideMode.ENABLED, true);
    public static final Template DEFAULT_ALL_DISABLED = makeDefault(IOSideMode.DISABLED, true);

    private static Template makeDefault(IOSideMode defaultMode, boolean enabled) {
      EnumMap<RelativeSide, IOSideMode> map = Maps.newEnumMap(RelativeSide.class);
      for(RelativeSide side : RelativeSide.values())
        map.put(side, defaultMode);
      return new Template(map, enabled, DEFAULT_COLOR);
    }

    public <T extends ISideConfigComponent<IOSideMode>> IOSideConfig build(T component) {
      return new IOSideConfig(component, this.sides, this.enabled, this.color);
    }
  }
}
