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
import es.degrassi.mmr.common.machine.component.ItemComponent;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jspecify.annotations.Nullable;

@Getter
public class RequirementItem implements IRequirement<ItemComponent, ItemHandler> {
  public static final NamedCodec<RequirementItem> CODEC = NamedCodec.record(instance -> instance.group(
     NamedCodec.of(SizedIngredient.NESTED_CODEC).fieldOf("sizedIngredient").forGetter(req -> req.ingredient),
      //DefaultCodecs.SIZED_INGREDIENT_WITH_NBT.fieldOf("sizedIngredient").forGetter(req -> req.ingredient),
          NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition),
          // WARNING: do not use this property, this is used to strict check if any data component is present on the requirement
          NamedCodec.BOOL.optionalFieldOf("usesDataComponents", false).forGetter(RequirementItem::isUsesDataComponents)
      ).apply(instance, (item, mode, position, usesDataComponents) -> {
        RequirementItem requirement = new RequirementItem(mode, item, position);
        requirement.setUsesDataComponents(usesDataComponents || requirement.usesDataComponents);
        return requirement;
      }),
      "RequirementItem");

  public final SizedIngredient ingredient;
  private final IOType mode;
  private final PositionedRequirement position;
  @Setter
  private boolean modified = false;
  @Setter
  private boolean usesDataComponents;
  private final Component uiComponent;

  public RequirementItem(IOType ioType, SizedIngredient ingredient, PositionedRequirement position) {
    this.ingredient = ingredient;
    this.usesDataComponents = ingredient.ingredient().display().resolveForStacks(ContextMap.EMPTY).stream().anyMatch(stack -> !stack.getComponents().isEmpty());
    this.mode = ioType;
    this.position = position;
    var stackTexts = ingredient.ingredient().display().resolve(
            ContextMap.EMPTY,
            SlotDisplay.ItemStackContentsFactory.INSTANCE
        ).map(ItemStack::getHoverName)
        .toList();
    var text = Component.empty();
    for (int i = 0; i < stackTexts.size(); i++) {
      text.append(stackTexts.get(i));
      if (i != stackTexts.size() - 1) text.append(RequirementTexts.or.text());
    }
    this.uiComponent = RequirementTexts.item.text(ingredient.count(), text);
  }

  public JsonObject asJson(SizedIngredient ingredient) {
    JsonObject json = new JsonObject();
    JsonArray stacks = new JsonArray();
    for (ItemStack stack : ingredient.ingredient().display().resolveForStacks(ContextMap.EMPTY)) {
      ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).result().map(JsonElement::toString).ifPresent(stacks::add);
    }
    json.add("items", stacks);
    json.addProperty("count", ingredient.count());
    return json;
  }

  @Override
  public RequirementType<RequirementItem, ItemComponent, ItemHandler> getType() {
    return RequirementTypeRegistration.ITEM.get();
  }

  @Override
  public ComponentType<ItemHandler> getComponentType() {
    return ComponentRegistration.COMPONENT_ITEM.get();
  }

  @Override
  public boolean test(ItemComponent component, ICraftingContext context) {
    long amount = context.getIntegerModifiedValue(this.ingredient.count(), this);
    var items = ingredient.ingredient().display().resolveForStacks(SlotDisplayContext.fromLevel(context.getMachineTile().getLevel()));
    if (getMode() == IOType.INPUT) {
      return items.stream().mapToLong(component::getItemAmount).sum() >= amount;
    } else if(getMode() == IOType.OUTPUT) {
      if (items.size() == 1)
        return component.getSpaceForItem(items.getFirst()) >= amount;
      else throw new IllegalStateException("Can't use output empty item");
    } else {
      return true;
    }
  }

  @Override
  public void gatherRequirements(IRequirementList<ItemComponent> list) {
    if (this.mode == IOType.INPUT)
      list.processOnStart(this::processInput);
    else
      list.processOnEnd(this::processOutput);
  }

  private CraftingResult processInput(ItemComponent component, ICraftingContext context) {
    long amount = context.getIntegerModifiedValue(this.ingredient.count(), this);
    long maxExtract = component.getIngredientAmount(this.ingredient.ingredient());
    if (maxExtract >= amount) {
      component.removeFromInputs(this.ingredient.ingredient(), amount);
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable("craftcheck.failure.item.input", amount, ingredient.ingredient().toString()));
  }

  private CraftingResult processOutput(ItemComponent component, ICraftingContext context) {
    long amount = context.getIntegerModifiedValue(this.ingredient.count(), this);
    var items = ingredient.ingredient().display().resolveForStacks(SlotDisplayContext.fromLevel(context.getMachineTile().getLevel()));
    if (items.size() == 1) {
      ItemStack item = items.getFirst();
      long canInsert = component.getSpaceForItem(item);
      if (canInsert >= amount) {
        component.addToOutputs(item.copy(), amount);
        return CraftingResult.success();
      }
      return CraftingResult.error(Component.translatable("craftcheck.failure.item.output.space"));
    } else throw new IllegalStateException("Can't use output item requirement with item tag");
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.add("ingredient", asJson(ingredient));
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable(String.format("component.missing.item.%s", ioType.name().toLowerCase()));
  }

  @Override
  public boolean isComponentValid(ItemComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public @Nullable Component toControllerUI() {
    return this.uiComponent;
  }
}
