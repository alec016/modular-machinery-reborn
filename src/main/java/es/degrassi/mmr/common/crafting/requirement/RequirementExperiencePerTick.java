package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.ExperienceResource;
import es.degrassi.experiencelib.api.capability.IExperienceHandler;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.experiencelib.util.ExperienceUtils;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.ExperienceComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@Getter
public class RequirementExperiencePerTick implements IRequirement<ExperienceComponent, IExperienceHandler> {
  public static final NamedMapCodec<RequirementExperiencePerTick> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(req -> req.required),
      NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode)
  ).apply(instance, (amount, type) -> new RequirementExperiencePerTick(type, amount)), "EnergyRequirement");

  private final IOType mode;
  private final PositionedRequirement position;
  public final long required;

  public RequirementExperiencePerTick(IOType actionType, long amount) {
    this.required = amount;
    this.position = new PositionedRequirement(0, 0);
    this.mode = actionType;
  }

  @Override
  public RequirementType<RequirementExperiencePerTick, ExperienceComponent, IExperienceHandler> getType() {
    return RequirementTypeRegistration.EXPERIENCE_PER_TICK.get();
  }

  @Override
  public ComponentType<IExperienceHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_EXPERIENCE.get();
  }

  @Override
  public boolean test(ExperienceComponent component, ICraftingContext context) {
    IExperienceHandler handler = component.getContainerProvider();
    var amount = 0L;
    var capacity = 0L;
    for (int i = 0; i < handler.size(); i++) {
      amount += handler.getAmountAsLong(i);
      capacity += handler.getCapacityAsLong(0, ExperienceResource.EMPTY);
    }
    return switch (mode) {
      case INPUT -> amount >= required;
      case OUTPUT -> capacity >= amount + required;
      case NONE -> true;
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<ExperienceComponent> list) {
    if (mode.isInput()) {
      list.processEachTick(this::processInput);
    } else if (mode.isOutput()) {
      list.processEachTick(this::processOutput);
    }
  }

  private CraftingResult processInput(ExperienceComponent component, ICraftingContext context) {
    long amount = (long) context.getModifiedValue(required, this);
    var handler = component.getContainerProvider();
    final long originAmount = amount;
    long canExtract = 0;
    try (var root = Transaction.open(null)) {
      for (int i = 0; i < handler.size() && amount > 0; i++) {
        long toExtract = 0L;
        try (var tx = Transaction.open(root)) {
          toExtract = handler.extractRecipe(i, amount, tx);
        }
        canExtract += toExtract;
        amount -= toExtract;
      }
      if (canExtract == originAmount) {
        root.commit();
        /*for (int i = 0; i < handler.getTanks(); i++) {
          long toExtract = component.getContainerProvider().extractExperienceRecipe(i, canExtract, false);
          canExtract += toExtract;
        }*/
        return CraftingResult.success();
      }
    }
    var experience = 0L;
    for (int i = 0; i < handler.size(); i++) {
      experience += handler.getAmountAsLong(i);
    }
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.experience.input", amount, experience
    ));
  }

  private CraftingResult processOutput(ExperienceComponent component, ICraftingContext context) {
    IExperienceHandler handler = component.getContainerProvider();
    long amount = (long) context.getModifiedValue(required, this);
    var experience = 0L;
    var capacity = 0L;
    for (int i = 0; i < handler.size(); i++) {
      experience += handler.getAmountAsLong(i);
      capacity += handler.getCapacityAsLong(0, ExperienceResource.EMPTY);
    }
    long remaining = capacity - experience;
    if (remaining >= amount) {
      try (var root  = Transaction.open(null)) {
        for (int i = 0; i < handler.size(); i++) {
          try (var tx = Transaction.open(root)) {
            amount -= handler.insertRecipe(i, amount, tx);
          }
        }
        root.commit();
      }
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable(
        "craftcheck.failure.experience.output", amount, capacity - experience
    ));
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("amount", required);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.experience.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(ExperienceComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    String literal = String.format("%s XP/t", ExperienceUtils.format(getRequired()));
    String level =  ExperienceUtils.format(ExperienceUtils.getLevelFromXp(getRequired()));
    info.addTooltip(
        Component.translatable("mmr.gui.element.experience.tooltip." + getMode().getSerializedName(),
            literal,
            Component.translatable("mmr.gui.element.experience.level", level)
        )
    );
    info.setItemIcon(Items.EXPERIENCE_BOTTLE);
  }
}
