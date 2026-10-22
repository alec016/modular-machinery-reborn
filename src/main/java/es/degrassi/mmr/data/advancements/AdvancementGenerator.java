package es.degrassi.mmr.data.advancements;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.registration.BlockRegistration;
import es.degrassi.mmr.common.registration.ItemRegistration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

import java.util.function.Consumer;

@NullMarked
public class AdvancementGenerator implements AdvancementSubProvider {
  @Override
  public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
    var root = Advancement.Builder.advancement()
        .display(
            ItemRegistration.MODULARIUM,
            AdvancementTexts.Root.text(),
            AdvancementTexts.RootDesc.text(),
            null /* background */,
            AdvancementType.TASK,
            false /* showToast */,
            false /* announceChat */,
            false /* hidden */
        )
        .addCriterion("modularium", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistration.MODULARIUM))
        .save(consumer, idString("main/root"));

    var controller = Advancement.Builder.advancement()
        .display(
            BlockRegistration.CONTROLLER,
            AdvancementTexts.Controller.text(),
            AdvancementTexts.ControllerDesc.text(),
            null /* background */,
            AdvancementType.TASK,
            true /* showToast */,
            true /* announceChat */,
            false /* hidden */
        )
        .parent(root)
        .addCriterion("controller", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.CONTROLLER))
        .save(consumer, idString("main/controller"));

    var casings = Advancement.Builder.advancement()
        .display(
            BlockRegistration.CASING_PLAIN,
            AdvancementTexts.Casings.text(),
            AdvancementTexts.CasingsDesc.text(),
            null /* background */,
            AdvancementType.GOAL,
            true /* showToast */,
            true /* announceChat */,
            false /* hidden */
        )
        .parent(root)
        .addCriterion("casings", InventoryChangeTrigger.TriggerInstance.hasItems(
            BlockRegistration.CASING_CIRCUITRY,
            BlockRegistration.CASING_FIREBOX,
            BlockRegistration.CASING_GEARBOX,
            BlockRegistration.CASING_PLAIN,
            BlockRegistration.CASING_REINFORCED,
            BlockRegistration.CASING_VENT
        ))
        .rewards(AdvancementRewards.Builder.experience(10))
        .save(consumer, idString("main/casings"));

    var itemBus = Advancement.Builder.advancement()
        .display(
            BlockRegistration.ITEM_INPUT_BUS_LUDICROUS,
            AdvancementTexts.ItemBus.text(),
            AdvancementTexts.ItemBusDesc.text(),
            null /* background */,
            AdvancementType.TASK,
            true /* showToast */,
            true /* announceChat */,
            false /* hidden */
        )
        .parent(controller)
        // input buses
        .addCriterion("input_tiny", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_TINY))
        .addCriterion("input_small", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_SMALL))
        .addCriterion("input_normal", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_NORMAL))
        .addCriterion("input_reinforced", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_REINFORCED))
        .addCriterion("input_big", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_BIG))
        .addCriterion("input_huge", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_HUGE))
        .addCriterion("input_ludicrous", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_INPUT_BUS_LUDICROUS))
        // output buses
        .addCriterion("output_tiny", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_TINY))
        .addCriterion("output_small", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_SMALL))
        .addCriterion("output_normal", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_NORMAL))
        .addCriterion("output_reinforced", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_REINFORCED))
        .addCriterion("output_big", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_BIG))
        .addCriterion("output_huge", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_HUGE))
        .addCriterion("output_ludicrous", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.ITEM_OUTPUT_BUS_LUDICROUS))
        .requirements(AdvancementRequirements.Strategy.OR)
        .save(consumer, idString("main/item_bus"));

    var fluidHatch = Advancement.Builder.advancement()
        .display(
            BlockRegistration.FLUID_INPUT_HATCH_VACUUM,
            AdvancementTexts.FluidHatch.text(),
            AdvancementTexts.FluidHatchDesc.text(),
            null /* background */,
            AdvancementType.TASK,
            true /* showToast */,
            true /* announceChat */,
            false /* hidden */
        )
        .parent(controller)
        // input buses
        .addCriterion("input_tiny", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_TINY))
        .addCriterion("input_small", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_SMALL))
        .addCriterion("input_normal", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_NORMAL))
        .addCriterion("input_reinforced", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_REINFORCED))
        .addCriterion("input_big", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_BIG))
        .addCriterion("input_huge", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_HUGE))
        .addCriterion("input_ludicrous", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_LUDICROUS))
        .addCriterion("input_vacuum", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_INPUT_HATCH_VACUUM))
        // output buses
        .addCriterion("output_tiny", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_TINY))
        .addCriterion("output_small", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_SMALL))
        .addCriterion("output_normal", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_NORMAL))
        .addCriterion("output_reinforced", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_REINFORCED))
        .addCriterion("output_big", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_BIG))
        .addCriterion("output_huge", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_HUGE))
        .addCriterion("output_ludicrous", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_LUDICROUS))
        .addCriterion("output_vacuum", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistration.FLUID_OUTPUT_HATCH_VACUUM))
        .requirements(AdvancementRequirements.Strategy.OR)
        .save(consumer, idString("main/fluid_hatch"));

    // TODO: Add criterion for crafting each multiblock independently
  }

  private String idString(String path) {
    return id(path).toString();
  }

  private Identifier id(String path) {
    return ModularMachineryReborn.rl(path);
  }
}
