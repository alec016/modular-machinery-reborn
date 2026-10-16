package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.Structure;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.client.entity.renderer.StructureCheckerRenderer;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.StructureComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class RequirementStructure implements IRequirement<StructureComponent, Structure> {
  public static final NamedCodec<RequirementStructure> CODEC = NamedCodec.record(instance -> instance.group(
      Structure.CODEC.fieldOf("structure").forGetter(RequirementStructure::getStructure),
      NamedCodec.enumCodec(Action.class).optionalFieldOf("action", Action.CHECK).forGetter(RequirementStructure::getAction)
  ).apply(instance, RequirementStructure::new), "Requirement Structure");

  private final Structure structure;
  private final Action action;
  public RequirementStructure(Structure structure, Action action) {
    this.structure = structure;
    this.action = action;
  }

  @Override
  public RequirementType<RequirementStructure, StructureComponent, Structure> getType() {
    return RequirementTypeRegistration.STRUCTURE.get();
  }

  @Override
  public ComponentType<Structure> getComponentType() {
    return ComponentRegistration.COMPONENT_STRUCTURE.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(StructureComponent component, ICraftingContext context) {
    return switch(this.action) {
      case CHECK, DESTROY, BREAK -> component.checkStructure(this.structure);
      default -> true;
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<StructureComponent> list) {
    if(this.action == Action.CHECK)
      list.worldCondition(this::check);
    else
      list.processDelayed(1.0D, this::process);
  }

  public CraftingResult process(StructureComponent component, ICraftingContext context) {
    switch(this.action) {
      case BREAK -> component.destroyStructure(this.structure, true);
      case DESTROY -> component.destroyStructure(this.structure, false);
      case PLACE_BREAK -> component.placeStructure(this.structure, true);
      case PLACE_DESTROY -> component.placeStructure(this.structure, false);
    }
    return CraftingResult.success();
  }

  public CraftingResult check(StructureComponent component, ICraftingContext context) {
    if(component.checkStructure(this.structure)) {
      return CraftingResult.success();
    }
    else return CraftingResult.error(Component.translatable("craftcheck.failure.structure"));
  }

  @Override
  public PositionedRequirement getPosition() {
    return new PositionedRequirement(0, 0);
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.structure");
  }

  @Override
  public boolean isComponentValid(StructureComponent m, ICraftingContext context) {
    return true;
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.info"));
    info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.click"));
    this.structure.getPattern().asList().stream().flatMap(List::stream).flatMap(s -> s.chars().mapToObj(c -> (char)c)).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).forEach((key, amount) -> {
      BlockIngredient ingredients = this.structure.getPattern().asMap().get(key);
      if(ingredients != null && amount > 0) {
        Component block = Component.translatable("modular_machinery_reborn.jei.ingredient.structure.list", amount, ingredients.getNamesUnified().withStyle(ChatFormatting.GOLD));
        info.addTooltip(Component.literal("✓").withStyle(ChatFormatting.GREEN).append(block), ((player, advancedTooltips) -> hasBlockItem(player, ingredients, amount)));
        info.addTooltip(Component.literal("  ").append(block), ((player, advancedTooltips) -> !hasBlockItem(player, ingredients, amount)));
      }
    });
    switch(this.action) {
      case BREAK -> info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.break").withStyle(ChatFormatting.DARK_RED));
      case DESTROY -> info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.destroy").withStyle(ChatFormatting.DARK_RED));
      case PLACE_BREAK, PLACE_DESTROY -> info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.structure.place").withStyle(ChatFormatting.DARK_RED));
    }
    info.setClickAction((machine, recipe, mouseButton) -> StructureCheckerRenderer.add(machine.getRegistryName(), this.structure));
    info.setItemIcon(Items.STRUCTURE_BLOCK);
  }

  public enum Action {
    CHECK,
    DESTROY,
    BREAK,
    PLACE_BREAK,
    PLACE_DESTROY
  }

  private boolean hasBlockItem(Player player, BlockIngredient block, long amount) {
    return player.getInventory().getNonEquipmentItems().stream()
        .filter(stack -> stack.getItem() instanceof BlockItem && block.test(((BlockItem)stack.getItem()).getBlock()))
        .mapToLong(ItemStack::getCount)
        .sum() >= amount;
  }

  @Override
  public JsonObject asJson() {
    var json = IRequirement.super.asJson();
    json.add("structure", this.structure.asJson());
    json.addProperty("action", action.name().toLowerCase());
    return json;
  }
}
