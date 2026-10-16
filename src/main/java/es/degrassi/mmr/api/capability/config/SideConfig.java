package es.degrassi.mmr.api.capability.config;

import es.degrassi.mmr.api.capability.config.SideConfig.SideMode;
import es.degrassi.mmr.common.util.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public abstract class SideConfig<M extends SideMode> implements ValueIOSerializable {

  public static final Color DEFAULT_COLOR = Color.fromColors(0.5, 0, 0, 1);

  @Getter
  final Map<RelativeSide, M> sides = new EnumMap<>(RelativeSide.class);
  @Getter
  private final ISideConfigComponent<M> component;
  @Getter
  @Setter
  private boolean enabled;
  //Color of the slot in the MachineConfigScreen
  @Getter
  private final Color color;
  @Setter
  private TriConsumer<RelativeSide, M, M> callback;

  public SideConfig(ISideConfigComponent<M> component, Map<RelativeSide, M> defaultConfig, boolean enabled, Color color) {
    this.component = component;
    this.sides.putAll(defaultConfig);
    this.enabled = enabled;
    this.color = color;
  }

  private Direction getControllerFacing() {
    return Optional.ofNullable(component).map(ISideConfigComponent::getControllerFacing).orElse(Direction.NORTH);
  }

  public M getSideMode(RelativeSide side) {
    return this.sides.get(side);
  }

  public M getDirectionMode(Direction direction) {
    return getSideMode(RelativeSide.fromDirections(this.getControllerFacing(), direction));
  }

  public void setSideMode(RelativeSide side, M mode) {
    M oldMode = this.sides.put(side, mode);
    if(this.callback != null && !getComponent().getLevel().isClientSide())
      this.callback.accept(side, oldMode, mode);
  }

  public void set(SideConfig<M> config) {
    for(RelativeSide side : RelativeSide.values())
      setSideMode(side, config.getSideMode(side));
  }

  public abstract void setNext(RelativeSide side);

  public abstract void setPrevious(RelativeSide side);

  public abstract SideConfig<M> copy();

  public interface SideMode {
    Component title();
    int color();
  }

  public interface Template<M extends SideMode> {
    Map<RelativeSide, M> sides();
    boolean enabled();
    Color color();
  }
}
