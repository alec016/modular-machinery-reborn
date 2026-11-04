package es.degrassi.mmr.client.integration.jei;

import es.degrassi.mmr.api.integration.jei.RegisterJeiComponentEvent;
import es.degrassi.mmr.api.integration.jei.RegisterJeiEmptyRequirementEvent;
import es.degrassi.mmr.api.integration.jei.RegisterJeiFilterDragDropEvent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiDurabilityComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiDurabilityPerTickComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiEmptyComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiEnergyComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiEnergyPerTickComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiExperienceComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiExperiencePerTickComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiFluidComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiFluidPerTickComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiFuelComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiItemComponent;
import es.degrassi.mmr.common.crafting.requirement.jei.JeiLootTableComponent;
import es.degrassi.mmr.common.integration.jei.JeiComponentRegistry;
import es.degrassi.mmr.common.integration.jei.JeiEmptyRequirementRegistry;
import es.degrassi.mmr.common.integration.jei.JeiFilterDragDropRegistry;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.registration.EmptyRequirementTypeRegistration;
import es.degrassi.mmr.common.registration.RequirementTypeRegistration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

public class MMRJeiClientIntegration {
  public MMRJeiClientIntegration(IEventBus bus) {
    bus.register(this);
    JeiComponentRegistry.init();
    JeiEmptyRequirementRegistry.init();
    JeiFilterDragDropRegistry.init();
  }

  /**
   * This is mantained commented to clarify users how to do it. This two types are builtin directly (no event dispatch)
   */
  @SubscribeEvent
  public void registerFilterDragDrop(final RegisterJeiFilterDragDropEvent event) {
    /*
    event.register(
        VanillaTypes.ITEM_STACK,
        (fs, stack) -> fs.setFromClient(stack.getIngredient())
    );
    event.register(
        NeoForgeTypes.FLUID_STACK,
        (fs, stack) -> fs.setGenericFromClient(pos -> new CSetFilterSlotFluidPacket(stack.getIngredient().copyWithAmount(1), pos))
    );
    */
  }

  @SubscribeEvent
  public void registerJeiEmptyRequirement(final RegisterJeiEmptyRequirementEvent event) {
    event.register(
        EmptyRequirementTypeRegistration.ITEM.get(),
        (component, category, builder, recipe, focuses) ->
            builder
                .addSlot(component.role(), component.getPosition().x(), component.getPosition().y())
                .setStandardSlotBackground()
    );
    event.register(
        EmptyRequirementTypeRegistration.FLUID.get(),
        (component, category, builder, recipe, focuses) ->
            builder
                .addSlot(component.role(), component.getPosition().x(), component.getPosition().y())
                .setOverlay(
                    MMRJeiPlugin.jeiHelpers.getGuiHelper().createDrawable(
                        component.texture(),
                        component.getUOffset(),
                        component.getVOffset(),
                        component.getWidth() + 2,
                        component.getHeight() + 2),
                    -1,
                    -1
                )
    );
    event.register(
        EmptyRequirementTypeRegistration.ENERGY.get(),
        (component, category, builder, recipe, focuses) ->
            builder
                .addSlot(component.role(), component.getPosition().x(), component.getPosition().y())
                .setCustomRenderer(CustomIngredientTypes.VOID, component)
                .addIngredients(CustomIngredientTypes.VOID, component.ingredients())
    );
  }

  @SubscribeEvent
  public void registerJeiComponents(final RegisterJeiComponentEvent event) {
    event.register(RequirementTypeRegistration.ENERGY.get(), JeiEnergyComponent::new);
    event.register(RequirementTypeRegistration.ENERGY_PER_TICK.get(), JeiEnergyPerTickComponent::new);
    event.register(RequirementTypeRegistration.FLUID.get(), JeiFluidComponent::new);
    event.register(RequirementTypeRegistration.FLUID_PER_TICK.get(), JeiFluidPerTickComponent::new);
    event.register(RequirementTypeRegistration.ITEM.get(), JeiItemComponent::new);
    event.register(RequirementTypeRegistration.DURABILITY.get(), JeiDurabilityComponent::new);
    event.register(RequirementTypeRegistration.DURABILITY_PER_TICK.get(), JeiDurabilityPerTickComponent::new);
    event.register(RequirementTypeRegistration.LOOT_TABLE.get(), JeiLootTableComponent::new);
    event.register(RequirementTypeRegistration.FUEL.get(), JeiFuelComponent::new);
    event.register(RequirementTypeRegistration.EMPTY.get(), JeiEmptyComponent::new);

    // Register requirements that will be invisible, just to make ae2 compatible
    event.register(RequirementTypeRegistration.EXPERIENCE.get(), JeiExperienceComponent::new);
    event.register(RequirementTypeRegistration.EXPERIENCE_PER_TICK.get(), JeiExperiencePerTickComponent::new);
  }
}
