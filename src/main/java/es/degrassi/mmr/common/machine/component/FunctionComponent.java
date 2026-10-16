package es.degrassi.mmr.common.machine.component;

import es.degrassi.mmr.api.controller.ControllerAccessible;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class FunctionComponent extends MachineComponent<Void> implements ControllerAccessible {
  protected BlockPos controllerPos;

  public FunctionComponent(@Nullable BlockPos controllerPos) {
    super(IOType.NONE);
    this.controllerPos = controllerPos;
  }

  @Override
  public ComponentType<Void> getComponentType() {
    return ComponentRegistration.COMPONENT_FUNCTION.get();
  }

  @Override
  public @Nullable Void getContainerProvider() {
    return new Void(null);
  }

  @Override
  public <C extends MachineComponent<Void>> boolean canMerge(C c) {
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<Void>> C merge(C c) {
    return (C) this;
  }

  @Override
  public @Nullable Level getLevel() {
    return getController() == null ? null : getController().getLevel();
  }
}
