package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import es.degrassi.mmreborn.api.codec.DefaultCodecs;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.integration.almostunified.AlmostUnifiedAdapter;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ItemComponent;
import es.degrassi.mmreborn.common.manager.handler.ItemHandler;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.Mods;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.Arrays;

@Getter
public class RequirementItem implements IRequirement<ItemComponent, ItemHandler> {
  public static final NamedCodec<RequirementItem> CODEC = NamedCodec.record(instance -> instance.group(
          DefaultCodecs.SIZED_INGREDIENT_WITH_NBT.fieldOf("sizedIngredient").forGetter(req -> req.ingredient),
          NamedCodec.enumCodec(IOType.class).fieldOf("mode").forGetter(IRequirement::getMode),
          PositionedRequirement.POSITION_CODEC.optionalFieldOf("position", new PositionedRequirement(0, 0)).forGetter(IRequirement::getPosition),
          // WARING: do not use this property, this is used to adapt the almost unified to show in JEI/EMI only the
          // modified recipes with the icon, until they add a proper way to do it
          NamedCodec.BOOL.optionalFieldOf("modifiedByAU", false).forGetter(RequirementItem::isModified),
          // WARING: do not use this property, this is used to strict check if any data component is present on the requirement
          NamedCodec.BOOL.optionalFieldOf("usesDataComponents", false).forGetter(RequirementItem::isUsesDataComponents)
      ).apply(instance, (item, mode, position, modified, usesDataComponents) -> {
        RequirementItem requirement = new RequirementItem(mode, item, position);
        requirement.setModified(modified || requirement.modified);
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

  public RequirementItem(IOType ioType, SizedIngredient ingredient, PositionedRequirement position) {
    if (Mods.isAULoaded() && !ingredient.ingredient().isCustom()) {
      ingredient = new SizedIngredient(
          Ingredient.fromValues(
              Arrays.stream(ingredient.ingredient().getValues())
                  .map(v -> {
                    if (v instanceof Ingredient.ItemValue(ItemStack item)) {
                      if (((Ingredient.ItemValue) v).item().getComponents().isEmpty()) {
                        Item i = AlmostUnifiedAdapter.getPreferredItemForItem(item.getItemHolder());
                        if (i != null) {
                          this.modified = true;
                          return new Ingredient.ItemValue(i.getDefaultInstance());
                        }
                      }
                    } else if (v instanceof Ingredient.TagValue(TagKey<Item> tag)) {
                      Item i = AlmostUnifiedAdapter.getPreferredItemForTag(tag);
                      if (i != null) {
                        this.modified = true;
                        return new Ingredient.ItemValue(i.getDefaultInstance());
                      }
                    }
                    return v;
                  })
          ),
          ingredient.count()
      );
    }
    this.ingredient = ingredient;
    this.usesDataComponents = Arrays.stream(ingredient.ingredient().getItems()).anyMatch(stack -> !stack.getComponents().isEmpty());
    this.mode = ioType;
    this.position = position;
  }

  public JsonObject asJson(SizedIngredient ingredient) {
    JsonObject json = new JsonObject();
    JsonArray stacks = new JsonArray();
    for (ItemStack stack : ingredient.getItems()) {
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
    int amount = (int) context.getIntegerModifiedValue(this.ingredient.count(), this);
    if (getMode() == IOType.INPUT) {
      return Arrays.stream(this.ingredient.getItems()).mapToInt(component::getItemAmount).sum() >= amount;
    } else if(getMode() == IOType.OUTPUT) {
      if (this.ingredient.getItems().length == 1)
        return component.getSpaceForItem(this.ingredient.getItems()[0]) >= amount;
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
    int amount = (int) context.getIntegerModifiedValue(this.ingredient.count(), this);
    int maxExtract = component.getIngredientAmount(this.ingredient.ingredient());
    if (maxExtract >= amount) {
      component.removeFromInputs(this.ingredient.ingredient(), amount);
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable("craftcheck.failure.item.input", amount, ingredient.ingredient().toString()));
  }

  private CraftingResult processOutput(ItemComponent component, ICraftingContext context) {
    int amount = (int) context.getIntegerModifiedValue(this.ingredient.count(), this);
    if (this.ingredient.getItems().length == 1) {
      ItemStack item = this.ingredient.getItems()[0];
      int canInsert = component.getSpaceForItem(item);
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
}
