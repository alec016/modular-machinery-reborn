package es.degrassi.mmr.common.crafting.requirement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.codec.DefaultCodecs;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmr.api.crafting.requirement.IRequirement;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.ComponentType;
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.DimensionComponent;
import es.degrassi.mmr.common.registration.ComponentRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.List;

public class RequirementDimension implements IRequirement<DimensionComponent, Identifier> {
  public static final NamedCodec<RequirementDimension> CODEC = NamedCodec.record(instance -> instance.group(
      DefaultCodecs.IDENTIFIER.listOf().fieldOf("filter").forGetter(RequirementDimension::filter),
      NamedCodec.BOOL.optionalFieldOf("blacklist", false).forGetter(RequirementDimension::blacklist)
  ).apply(instance, RequirementDimension::new), "Dimension Requirement");

  @Getter
  private final PositionedRequirement position;
  private final List<Identifier> filter;
  private final boolean blacklist;

  public RequirementDimension(List<Identifier> filter, boolean blacklist) {
    this.filter = filter;
    this.blacklist = blacklist;
    this.position = new PositionedRequirement(0, 0);
  }

  public List<Identifier> filter() {
    return filter;
  }

  public boolean blacklist() {
    return blacklist;
  }

  @Override
  public RequirementType<RequirementDimension, DimensionComponent, Identifier> getType() {
    return RequirementTypeRegistration.DIMENSION.get();
  }

  @Override
  public ComponentType<Identifier> getComponentType() {
    return ComponentRegistration.COMPONENT_DIMENSION.get();
  }

  @Override
  public IOType getMode() {
    return IOType.INPUT;
  }

  @Override
  public boolean test(DimensionComponent component, ICraftingContext context) {
    return filter.contains(component.getContainerProvider()) != blacklist;
  }

  @Override
  public void gatherRequirements(IRequirementList<DimensionComponent> list) {
    // If the {@link #test(DimensionComponent, ICraftingContext)} success then this requirement is OK to proceed
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("blacklist", blacklist);
    JsonArray array = new JsonArray();
    filter.stream().map(Identifier::toString).forEach(array::add);
    json.add("filter", array);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.dimension");
  }

  @Override
  public boolean isComponentValid(DimensionComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    StringBuilder dimensions = new StringBuilder();
    filter.forEach(dimension -> dimensions.append(dimension.toString()).append(","));
    int index = dimensions.lastIndexOf(",");
    if (index >= dimensions.length() - 1)
      dimensions.deleteCharAt(index);
    info.addTooltip(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.dimension." + blacklist(),
        dimensions.toString()
    ));
    info.setSpriteIcon(Identifier.withDefaultNamespace("textures/atlas/blocks.png"), Identifier.withDefaultNamespace("block/nether_portal"));
  }
}
