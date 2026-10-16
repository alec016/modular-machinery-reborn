package es.degrassi.mmr.common.crafting.modifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.machine.MachineComponent;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;
import java.util.function.Supplier;

public class RecipeModifierTargetEvent {
  public static void init() {
    Blacklist blacklistEvent = new Blacklist();
    ModLoader.postEventWrapContainerInModOrder(blacklistEvent);
    Blacklist.BLACKLIST = blacklistEvent.getBlacklist();
  }
  public static class Blacklist extends Event implements IModBusEvent {
    public static List<RequirementType<?, ?, ?>> BLACKLIST;
    private final List<RequirementType<?, ?, ?>> blacklist = Lists.newArrayList();

    public <
        R extends IRequirement<C, T>,
        C extends MachineComponent<T>,
        T
        > void register(RequirementType<R, C, T> requirement) {
      blacklist.add(requirement);
    }

    public <
        R extends IRequirement<C, T>,
        C extends MachineComponent<T>,
        T
        > void register(Supplier<RequirementType<R, C, T>> requirement) {
      blacklist.add(requirement.get());
    }

    public List<RequirementType<?, ?, ?>> getBlacklist() {
      return ImmutableList.copyOf(blacklist);
    }
  }
}
