package es.degrassi.mmr.common.machine;

import com.google.gson.JsonObject;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public abstract class MachineComponent<T> implements Comparable<MachineComponent<T>> {
  private final IOType ioType;

  protected MachineComponent(IOType ioType) {
    this.ioType = ioType;
  }

  public IOType getIOType() {
    return ioType;
  }

  public abstract ComponentType<T> getComponentType();

  @Nullable
  public abstract T getContainerProvider();

  public void onStatusChanged(MachineStatus oldStatus, MachineStatus newStatus, Component errorMessage) {
  }

  public JsonObject asJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", getComponentType().getId().toString());
    json.addProperty("mode", getIOType().getSerializedName());
    return json;
  }

  public void asTag(ValueOutput output) {
    output.putString("type", getComponentType().getId().toString());
    output.putString("mode", getIOType().getSerializedName());
  }

  @Override
  public String toString() {
    return asJson().toString();
  }

  public abstract <C extends MachineComponent<T>> C merge(C c);

  public <C extends MachineComponent<T>> boolean canMerge(C c) {
    return c.getIOType().equals(getIOType());
  }

  @Override
  public int compareTo(MachineComponent<T> o) {
    return 0;
  }
}
