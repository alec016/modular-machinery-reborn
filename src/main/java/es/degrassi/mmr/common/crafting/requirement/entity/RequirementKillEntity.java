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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;

@Getter
public class RequirementKillEntity extends RequirementEntity {
  public static final NamedCodec<RequirementKillEntity> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.INT.fieldOf("amount").forGetter(RequirementEntity::getAmount),
      NamedCodec.INT.fieldOf("radius").forGetter(RequirementEntity::getRadius),
      RegistrarCodec.ENTITY.listOf().optionalFieldOf("entity", Collections.emptyList()).forGetter(RequirementKillEntity::getEntityTypes)
  ).apply(instance, RequirementKillEntity::new), "Kill Entity Requirement");

  private final List<EntityType<?>> entityTypes;

  public RequirementKillEntity(int amount, int radius, List<EntityType<?>> entityTypes) {
    super(Action.KILL, amount, radius);
    this.entityTypes = entityTypes;
  }

  @Override
  public RequirementType<RequirementKillEntity, EntityComponent, EntityHandler> getType() {
    return RequirementTypeRegistration.KILL_ENTITY.get();
  }

  @Override
  public boolean test(EntityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.getAmount(), this);
    int radius = (int)context.getIntegerModifiedValue(this.getRadius(), this);
    return component.getContainerProvider().getEntitiesInRadius(radius, this::predicate) >= amount;
  }

  @Override
  public void gatherRequirements(IRequirementList<EntityComponent> list) {
    list.process(this.getMode(), this::process);
  }

  private CraftingResult process(EntityComponent component, ICraftingContext context) {
    if (component.getContainerProvider().getEntitiesInRadius(getRadius(), this::predicate) >= getAmount()) {
      component.getContainerProvider().killEntities(getRadius(), this::predicate, getAmount());
      return CraftingResult.success();
    }
    return CraftingResult.error(Component.translatable("craftcheck.failure.entity.amount"));
  }

  private boolean predicate(Entity entity) {
    return this.entityTypes.contains(entity.getType());
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    super.getDefaultDisplayInfo(info, requirement);
    if (!this.entityTypes.isEmpty()) {
      info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.entity.whitelist"));
      this.entityTypes.forEach(type -> info.addTooltip(Component.literal("*").append(type.getDescription())));
    }
    info.setItemIcon(Items.MOOSHROOM_SPAWN_EGG);
  }
}
