package es.degrassi.mmr.common.crafting.modifier;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public interface IRecipeModifier<
    R extends IRequirement<C, T>,
    C extends MachineComponent<T>,
    T
    > {

  boolean shouldApply(RequirementType<R, C, T> type, IOType mode);

  float apply(float original);

  Component getTooltip();

  Component getDefaultTooltip();

  enum OPERATION {
    ADDITION,
    MULTIPLICATION;

    public static final NamedCodec<OPERATION> CODEC = NamedCodec.enumCodec(OPERATION.class);

    public static OPERATION value(String value) {
      return valueOf(value.toUpperCase(Locale.ROOT));
    }
  }
}
