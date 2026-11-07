package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.DurabilityComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@Getter
public class RequirementDurabilityPerTick implements IRequirement<DurabilityComponent, ItemHandler> {
  public static final NamedCodec<RequirementDurabilityPerTick> CODEC = NamedCodec.record(instance -> instance.group(
          NamedCodec.of(Ingredient.CODEC).fieldOf("ingredient").aliases("item").forGetter(req -> req.ingredient),
          NamedCodec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(RequirementDurabilityPerTick::getAmount),
          IOType.CODEC.fieldOf("mode").forGetter(IRequirement::getMode),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(instance, (item, amount, mode, position) -> new RequirementDurabilityPerTick(mode, item, amount, position)),
      "RequirementDurability");

  public final Ingredient ingredient;
  private final IOType mode;
  private final PositionedRequirement position;
  private final int amount;
  private final Component uiComponent;

  public RequirementDurabilityPerTick(IOType ioType, Ingredient ingredient, int amount, PositionedRequirement position) {
    this.amount = amount;
    this.ingredient = ingredient;
    this.mode = ioType;
    this.position = position;
    var stackTexts = ingredient.display().resolve(
            ContextMap.EMPTY,
            SlotDisplay.ItemStackContentsFactory.INSTANCE
        ).map(ItemStack::getHoverName)
        .toList();
    var text = Component.empty();
    for (int i = 0; i < stackTexts.size(); i++) {
      text.append(stackTexts.get(i));
      if (i != stackTexts.size() - 1) text.append(RequirementTexts.or.text());
    }
    this.uiComponent = RequirementTexts.durabilityTick.text(amount, text);
  }

  @Override
  public RequirementType<RequirementDurabilityPerTick, DurabilityComponent, ItemHandler> getType() {
    return RequirementTypeRegistration.DURABILITY_PER_TICK.get();
  }

  @Override
  public ComponentType<ItemHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_DURABILITY.get();
  }

  @Override
  public boolean test(DurabilityComponent component, ICraftingContext context) {
    int a = (int)context.getPerTickIntegerModifiedValue(this.amount, this);
    var items = this.ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(context.getMachineTile().getLevel()));
    if(getMode().isInput())
      return processWithLog(amount -> items.stream().mapToInt(item -> component.getContainerProvider().getDurabilityAmount(item)).sum() >= amount, a);
    else
      return processWithLog(amount -> items.stream().mapToInt(item -> component.getContainerProvider().getSpaceForDurability(item)).sum() >= amount, a);
  }

  private boolean processWithLog(Function<Integer, Boolean> function, int amount) {
    return function.apply(amount);
  }

  @Override
  public void gatherRequirements(IRequirementList<DurabilityComponent> list) {
    if (this.mode.isInput())
      list.processEachTick(this::processInput);
    else
      list.processEachTick(this::processOutput);
  }

  private CraftingResult processInput(DurabilityComponent component, ICraftingContext context) {
    int amount = (int)context.getPerTickIntegerModifiedValue(this.amount, this);
    var items = this.ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(context.getMachineTile().getLevel()));
    int maxRemove = items.stream().mapToInt(item -> component.getContainerProvider().getDurabilityAmount(item)).sum();
    if(maxRemove >= amount) {
      int toDamage = amount;
      for (ItemStack item : items) {
        int canDamage = component.getContainerProvider().getDurabilityAmount(item);
        if(canDamage > 0) {
          canDamage = Math.min(canDamage, toDamage);
          component.getContainerProvider().removeDurability(item, canDamage);
          toDamage -= canDamage;
          if(toDamage == 0)
            return CraftingResult.success();
        }
      }
    }
    return CraftingResult.error(Component.translatable("craftcheck.failure.durability.input", amount, maxRemove));
  }

  private CraftingResult processOutput(DurabilityComponent component, ICraftingContext context) {
    int amount = (int)context.getPerTickIntegerModifiedValue(this.amount, this);
    var items = this.ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(context.getMachineTile().getLevel()));
    int maxRepair = items.stream().mapToInt(item -> component.getContainerProvider().getSpaceForDurability(item)).sum();
    if(maxRepair >= amount) {
      int toRepair = amount;
      for (ItemStack item : items) {
        int canRepair = component.getContainerProvider().getSpaceForDurability(item);
        if(canRepair > 0) {
          canRepair = Math.min(canRepair, toRepair);
          component.getContainerProvider().repairItem(item, canRepair);
          toRepair -= canRepair;
          if(toRepair == 0)
            return CraftingResult.success();
        }
      }
    }
    return CraftingResult.error(Component.translatable("craftcheck.failure.durability.output", amount, maxRepair));
  }

  public JsonObject asJson(Ingredient ingredient) {
    JsonObject json = new JsonObject();
    JsonArray stacks = new JsonArray();
    for (ItemStack stack : ingredient.display().resolveForStacks(ContextMap.EMPTY)) {
      ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).result().map(JsonElement::toString).ifPresent(stacks::add);
    }
    json.add("items", stacks);
    return json;
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.add("ingredient", asJson(ingredient));
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.durability.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(DurabilityComponent m, ICraftingContext context) {
    return true;
  }

  @Override
  public @Nullable Component toControllerUI() {
    return uiComponent;
  }
}
