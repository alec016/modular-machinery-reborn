package es.degrassi.mmr.common.machine.component;

import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.transfer.ExperienceStacksResourceHandler;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.MachineComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

public class ExperienceComponent extends MachineComponent<IExperienceHandler> {
  private final IExperienceHandler handler;

  public ExperienceComponent(IExperienceHandler handler, IOType ioType) {
    super(ioType);
    this.handler = handler;
  }

  @Override
  public ComponentType<IExperienceHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_EXPERIENCE.get();
  }

  @Override
  public @NotNull IExperienceHandler getContainerProvider() {
    return handler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C extends MachineComponent<IExperienceHandler>> C merge(C c) {
    ExperienceComponent comp = (ExperienceComponent) c;
    return (C) new ExperienceComponent(new ExperienceStacksResourceHandler(2, handler.getCapacityAsLong(0, ExperienceResource.EMPTY) + comp.handler.getCapacityAsLong(0, ExperienceResource.EMPTY), null) {
      @Override
      public void serialize(ValueOutput output) {
        output.putChild("first", handler);
        output.putChild("second", comp.handler);
      }

      @Override
      public void deserialize(ValueInput input) {
        input.readChild("first", handler);
        input.readChild("second", comp.handler);
      }

      @Override
      public long insertRecipe(int tank, long amount, TransactionContext context) {
        try (var sub = Transaction.open(context)) {
          var first = handler.insert(0, amount, sub);
          var second = 0L;
          amount -= first;
          if (amount > 0) {
            second = comp.handler.insert(0, amount, sub);
          }
          sub.commit();
          return first + second;
        }
      }

      @Override
      public long insert(int tank, long amount, TransactionContext context) {
        try (var sub = Transaction.open(context)) {
          var first = handler.insertRecipe(0, amount, sub);
          var second = 0L;
          amount -= first;
          if (amount > 0) {
            second = comp.handler.insertRecipe(0, amount, sub);
          }
          sub.commit();
          return first + second;
        }
      }

      @Override
      public long extract(int tank, long amount, TransactionContext context) {
        try (var sub = Transaction.open(context)) {
          var first = handler.extract(0, amount, sub);
          var second = 0L;
          amount -= first;
          if (amount > 0) {
            second = comp.handler.extract(0, amount, sub);
          }
          sub.commit();
          return first + second;
        }
      }

      @Override
      public long extractRecipe(int tank, long amount, TransactionContext context) {
        try (var sub = Transaction.open(context)) {
          var first = handler.extractRecipe(0, amount, sub);
          var second = 0L;
          amount -= first;
          if (amount > 0) {
            second = comp.handler.extractRecipe(0, amount, sub);
          }
          sub.commit();
          return first + second;
        }
      }
    },
    getIOType()
    );
  }
}
