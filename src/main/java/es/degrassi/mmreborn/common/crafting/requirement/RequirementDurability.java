package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.DurabilityComponent;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

import java.util.Arrays;
import java.util.function.Function;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class RequirementDurability implements IRequirement<DurabilityComponent, ItemHandler> {
  public static final NamedCodec<RequirementDurability> CODEC = NamedCodec.record(instance -> instance.group(
          NamedCodec.of(CraftingHelper.makeIngredientCodec(true)).fieldOf("ingredient").aliases("item").forGetter(req -> req.ingredient),
          NamedCodec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(RequirementDurability::getAmount),
          IOType.CODEC.fieldOf("mode").forGetter(IRequirement::getMode),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition)
      ).apply(instance, (item, amount, mode, position) -> new RequirementDurability(mode, item, amount, position)),
      "RequirementDurability");

  public final Ingredient ingredient;
  private final IOType mode;
  private final PositionedRequirement position;
  private final int amount;

  public RequirementDurability(IOType ioType, Ingredient ingredient, int amount, PositionedRequirement position) {
    this.amount = amount;
    this.ingredient = ingredient;
    this.mode = ioType;
    this.position = position;
  }

  @Override
  public RequirementType<RequirementDurability, DurabilityComponent, ItemHandler> getType() {
    return RequirementTypeRegistration.DURABILITY.get();
  }

  @Override
  public ComponentType<ItemHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_DURABILITY.get();
  }

  @Override
  public boolean test(DurabilityComponent component, ICraftingContext context) {
    int a = (int)context.getIntegerModifiedValue(this.amount, this);
    if(getMode().isInput())
      return processWithLog(amount -> Arrays.stream(this.ingredient.getItems()).mapToInt(item -> component.getContainerProvider().getDurabilityAmount(item)).sum() >= amount, a);
    else
      return processWithLog(amount -> Arrays.stream(this.ingredient.getItems()).mapToInt(item -> component.getContainerProvider().getSpaceForDurability(item)).sum() >= amount, a);
  }

  private boolean processWithLog(Function<Integer, Boolean> function, int amount) {
    return function.apply(amount);
  }

  @Override
  public void gatherRequirements(IRequirementList<DurabilityComponent> list) {
    if (this.mode.isInput())
      list.processOnStart(this::processInput);
    else
      list.processOnEnd(this::processOutput);
  }

  private CraftingResult processInput(DurabilityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.amount, this);
    int maxRemove = Arrays.stream(this.ingredient.getItems()).mapToInt(item -> component.getContainerProvider().getDurabilityAmount(item)).sum();
    if(maxRemove >= amount) {
      int toDamage = amount;
      for (ItemStack item : this.ingredient.getItems()) {
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
    int amount = (int)context.getIntegerModifiedValue(this.amount, this);
    int maxRepair = Arrays.stream(this.ingredient.getItems()).mapToInt(item -> component.getContainerProvider().getSpaceForDurability(item)).sum();
    if(maxRepair >= amount) {
      int toRepair = amount;
      for (ItemStack item : this.ingredient.getItems()) {
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
    for (ItemStack stack : ingredient.getItems()) {
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
}
