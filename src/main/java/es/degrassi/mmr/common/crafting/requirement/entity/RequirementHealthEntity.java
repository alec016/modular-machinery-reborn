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
import es.degrassi.mmr.common.machine.IOType;
import es.degrassi.mmr.common.machine.component.EntityComponent;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

@Getter
public class RequirementHealthEntity extends RequirementEntity {
  public static final NamedCodec<RequirementHealthEntity> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.INT.fieldOf("amount").forGetter(RequirementEntity::getAmount),
      NamedCodec.INT.fieldOf("radius").forGetter(RequirementEntity::getRadius),
      Mode.CODEC.fieldOf("mode").forGetter(RequirementHealthEntity::getHealthMode),
      RegistrarCodec.ENTITY.listOf().optionalFieldOf("entity", Collections.emptyList()).forGetter(RequirementHealthEntity::getEntityTypes)
  ).apply(instance, RequirementHealthEntity::new), "Add or Remove Health Entity Requirement");

  private final List<EntityType<?>> entityTypes;
  private final Mode healthMode;

  public RequirementHealthEntity(int amount, int radius, Mode healthMode, List<EntityType<?>> entityTypes) {
    super(healthMode.toIOType(), healthMode.isInput() ? Action.CONSUME_HEALTH : Action.ADD_HEALTH, amount, radius);
    this.entityTypes = entityTypes;
    this.healthMode = healthMode;
  }

  @Override
  public RequirementType<RequirementHealthEntity, EntityComponent, EntityHandler> getType() {
    return RequirementTypeRegistration.HEATH_ENTITY.get();
  }

  @Override
  public boolean test(EntityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.getAmount(), this);
    int radius = (int)context.getIntegerModifiedValue(this.getRadius(), this);
    return switch (healthMode) {
      case INPUT -> component.getContainerProvider().canHurtEntitiesInRadius(radius, this.getFilter(), amount);
      case OUTPUT -> component.getContainerProvider().canHealEntitiesInRadius(radius, this.getFilter(), amount);
    };
  }

  private Predicate<Entity> getFilter() {
    return entity -> entityTypes.isEmpty() || entityTypes.contains(entity.getType());
  }

  @Override
  public void gatherRequirements(IRequirementList<EntityComponent> list) {
    switch (healthMode) {
      case INPUT -> list.processOnStart(this::process);
      case OUTPUT -> list.processOnEnd(this::process);
    }
  }

  public CraftingResult process(EntityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.getAmount(), this);
    int radius = (int)context.getIntegerModifiedValue(this.getRadius(), this);
    switch (healthMode) {
      case INPUT -> component.getContainerProvider().removeEntitiesHealth(radius, this.getFilter(), amount);
      case OUTPUT -> component.getContainerProvider().addEntitiesHealth(radius, this.getFilter(), amount);
    };
    return CraftingResult.success();
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    super.getDefaultDisplayInfo(info, requirement);
    if (!this.entityTypes.isEmpty()) {
      info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.entity.whitelist"));
      this.entityTypes.forEach(type -> info.addTooltip(Component.literal("*").append(type.getDescription())));
    }
    switch (healthMode) {
      case INPUT -> info.setItemIcon(Items.MOOSHROOM_SPAWN_EGG);
      case OUTPUT -> info.setItemIcon(Items.SLIME_SPAWN_EGG);
    }
  }

  public enum Mode {
    INPUT,
    OUTPUT;
    public static final NamedCodec<Mode> CODEC = NamedCodec.enumCodec(Mode.class);

    public static Mode value(String mode) {
      return valueOf(mode.toUpperCase(Locale.ENGLISH));
    }

    public IOType toIOType() {
      return switch(this) {
        case INPUT -> IOType.INPUT;
        case OUTPUT -> IOType.OUTPUT;
      };
    }

    private boolean isInput() {
      return this == INPUT;
    }
  }
}
