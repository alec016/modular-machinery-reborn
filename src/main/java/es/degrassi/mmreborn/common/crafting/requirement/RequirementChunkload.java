package es.degrassi.mmreborn.common.crafting.requirement;

import com.google.gson.JsonObject;
import es.degrassi.mmreborn.api.codec.NamedCodec;
import es.degrassi.mmreborn.api.crafting.CraftingResult;
import es.degrassi.mmreborn.api.crafting.ICraftingContext;
import es.degrassi.mmreborn.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirement;
import es.degrassi.mmreborn.api.crafting.requirement.IRequirementList;
import es.degrassi.mmreborn.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmreborn.common.crafting.ComponentType;
import es.degrassi.mmreborn.common.machine.IOType;
import es.degrassi.mmreborn.common.machine.component.ChunkloadComponent;
import es.degrassi.mmreborn.common.registration.ComponentRegistration;
import es.degrassi.mmreborn.common.registration.ItemRegistration;
import es.degrassi.mmreborn.common.registration.RequirementTypeRegistration;
import es.degrassi.mmreborn.common.util.Chunkloader;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class RequirementChunkload implements IRequirement<ChunkloadComponent, Chunkloader> {
  public static final NamedCodec<RequirementChunkload> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(1, 32).optionalFieldOf("radius", 1).forGetter(RequirementChunkload::radius)
  ).apply(instance, RequirementChunkload::new), "ChunkloadComponent Requirement");

  @Getter
  private final IOType actionType;
  @Getter
  private final PositionedRequirement position;
  private final Integer radius;

  public RequirementChunkload(Integer radius) {
    this.radius = radius;
    this.actionType = IOType.OUTPUT;
    this.position = new PositionedRequirement(0, 0);
  }

  public Integer radius() {
    return radius;
  }

  @Override
  public RequirementType<RequirementChunkload, ChunkloadComponent, Chunkloader> getType() {
    return RequirementTypeRegistration.CHUNKLOAD.get();
  }

  @Override
  public ComponentType<Chunkloader> getComponentType() {
    return ComponentRegistration.COMPONENT_CHUNKLOAD.get();
  }

  @Override
  public IOType getMode() {
    return getActionType();
  }

  @Override
  public boolean test(ChunkloadComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<ChunkloadComponent> list) {
    list.processEachTick(((component, context) -> {
      component.getContainerProvider().setActiveWithTempo((ServerLevel) context.getMachineTile().getLevel(), this.radius, 2);
      return CraftingResult.success();
    }));
  }

  @Override
  public JsonObject asJson() {
    JsonObject json = IRequirement.super.asJson();
    json.addProperty("radius", radius);
    return json;
  }

  @Override
  public Component getMissingComponentErrorMessage(IOType ioType) {
    return Component.translatable("component.missing.chunkload");
  }

  @Override
  public boolean isComponentValid(ChunkloadComponent m, ICraftingContext context) {
    return getMode().equals(m.getIOType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    info.addTooltip(Component.translatable(
        "modular_machinery_reborn.jei.ingredient.chunkload",
        radius()
    ));
    info.setItemIcon(ItemRegistration.CHUNKLOADER.asItem());
  }
}
