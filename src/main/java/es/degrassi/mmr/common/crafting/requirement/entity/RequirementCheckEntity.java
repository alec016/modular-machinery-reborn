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
import java.util.Locale;

@Getter
public class RequirementCheckEntity extends RequirementEntity {
  public static final NamedCodec<RequirementCheckEntity> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.INT.fieldOf("amount").forGetter(RequirementEntity::getAmount),
      NamedCodec.INT.fieldOf("radius").forGetter(RequirementEntity::getRadius),
      CheckAction.CODEC.fieldOf("check_type").forGetter(RequirementCheckEntity::getCheckActionMode),
      RegistrarCodec.ENTITY.listOf().optionalFieldOf("filter", Collections.emptyList()).forGetter(RequirementCheckEntity::getEntityTypes),
      NamedCodec.BOOL.optionalFieldOf("whitelist", false).forGetter(RequirementCheckEntity::isWhitelist)
  ).apply(instance, RequirementCheckEntity::new), "Check Entity Requirement");

  private final List<EntityType<?>> entityTypes;
  private final boolean whitelist;
  private final CheckAction checkActionMode;

  public RequirementCheckEntity(int amount, int radius, CheckAction mode, List<EntityType<?>> entityTypes, boolean whitelist) {
    super(mode.toAction(), amount, radius);
    this.entityTypes = entityTypes;
    this.checkActionMode = mode;
    this.whitelist = whitelist;
  }

  private boolean predicate(Entity entity) {
    return this.entityTypes.contains(entity.getType()) == this.whitelist;
  }

  @Override
  public RequirementType<RequirementCheckEntity, EntityComponent, EntityHandler> getType() {
    return RequirementTypeRegistration.CHECK_ENTITY.get();
  }

  @Override
  public boolean test(EntityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.getAmount(), this);
    int radius = (int)context.getIntegerModifiedValue(this.getRadius(), this);
    return switch (getCheckActionMode()) {
      case HEALTH -> component.getContainerProvider().getEntitiesInRadiusHealth(radius, this::predicate) >= amount;
      case AMOUNT -> component.getContainerProvider().getEntitiesInRadius(radius, this::predicate) >= amount;
    };
  }

  @Override
  public void gatherRequirements(IRequirementList<EntityComponent> list) {
    list.processEachTick(this::check);
  }

  private CraftingResult check(EntityComponent component, ICraftingContext context) {
    int amount = (int)context.getIntegerModifiedValue(this.getAmount(), this);
    int radius = (int)context.getIntegerModifiedValue(this.getRadius(), this);
    if(this.getAction() == Action.CHECK_AMOUNT)
      return component.getContainerProvider().getEntitiesInRadius(radius, this::predicate) >= amount ? CraftingResult.success() :
        CraftingResult.error(Component.translatable("craftcheck.failure.entity.amount"));
    else if(this.getAction() == Action.CHECK_HEALTH)
      return component.getContainerProvider().getEntitiesInRadiusHealth(radius, this::predicate) >= amount ? CraftingResult.success() :
        CraftingResult.error(Component.translatable("craftcheck.failure.entity.health", amount));
    else
      return CraftingResult.pass();
  }

  @Override
  public void getDefaultDisplayInfo(IDisplayInfo info, RecipeRequirement<?, ?, ?> requirement) {
    super.getDefaultDisplayInfo(info, requirement);
    if (!this.entityTypes.isEmpty()) {
      info.addTooltip(Component.translatable("modular_machinery_reborn.jei.ingredient.entity." + (whitelist ? "whitelist" : "blacklist")));
      this.entityTypes.forEach(type -> info.addTooltip(Component.literal("*").append(type.getDescription())));
    }
    info.setItemIcon(Items.TROPICAL_FISH_SPAWN_EGG);
  }

  public enum CheckAction {
    HEALTH,
    AMOUNT;
    public static final NamedCodec<CheckAction> CODEC = NamedCodec.enumCodec(CheckAction.class);

    public static CheckAction value(String value) {
      return valueOf(value.toUpperCase(Locale.ENGLISH));
    }

    public Action toAction() {
      return switch (this) {
        case AMOUNT -> Action.CHECK_AMOUNT;
        case HEALTH -> Action.CHECK_HEALTH;
      };
    }
  }
}
