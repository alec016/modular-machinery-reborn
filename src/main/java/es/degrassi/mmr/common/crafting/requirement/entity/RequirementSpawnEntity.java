package es.degrassi.mmr.common.crafting.requirement.entity;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.api.capability.EntityHandler;
import es.degrassi.mmr.api.codec.RegistrarCodec;
import es.degrassi.mmr.api.crafting.CraftingResult;
import es.degrassi.mmr.api.crafting.ICraftingContext;
import es.degrassi.mmr.api.crafting.requirement.IDisplayInfo;
import es.degrassi.mmr.api.crafting.requirement.IRequirementList;
import es.degrassi.mmr.api.crafting.requirement.RecipeRequirement;
import es.degrassi.mmr.common.crafting.requirement.RequirementType;
import es.degrassi.mmr.common.machine.component.EntityComponent;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

@Getter
public class RequirementSpawnEntity extends RequirementEntity {
  public static final NamedCodec<RequirementSpawnEntity> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.INT.fieldOf("amount").forGetter(RequirementEntity::getAmount),
      NamedCodec.INT.fieldOf("radius").forGetter(RequirementEntity::getRadius),
      RegistrarCodec.ENTITY.fieldOf("entity").forGetter(RequirementSpawnEntity::getEntityType)
  ).apply(instance, RequirementSpawnEntity::new), "Spawn Entity Requirement");

  private final EntityType<?> entityType;

  public RequirementSpawnEntity(int amount, int radius, EntityType<?> entityType) {
    super(Action.SPAWN, amount, radius);
    this.entityType = entityType;
  }

  @Override
  public RequirementType<RequirementSpawnEntity, EntityComponent, EntityHandler> getType() {
    return RequirementTypeRegistration.SPAWN_ENTITY.get();
  }

  @Override
  public boolean test(EntityComponent component, ICraftingContext context) {
    return true;
  }

  @Override
  public void gatherRequirements(IRequirementList<EntityComponent> list) {
    list.process(this.getMode(), this::process);
  }

  private CraftingResult process(EntityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.getAmount(), this);
    int radius = (int)context.getIntegerModifiedValue(this.getRadius(), this);
    if (component.getContainerProvider().spawnEntities(radius, amount, entityType))
      return CraftingResult.success();
    return CraftingResult.error(Component.translatable("craftcheck.failure.entity.spawn", getAmount(), entityType.getDescription(), getRadius()));
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    super.getDefaultDisplayInfo(info, requirement);
    info.setItemIcon(Items.SLIME_SPAWN_EGG);
  }
}
