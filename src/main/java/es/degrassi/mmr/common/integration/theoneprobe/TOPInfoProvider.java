package es.degrassi.mmr.common.integration.theoneprobe;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.IAutoEntity;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.common.crafting.MachineRecipe;
import es.degrassi.mmr.common.crafting.requirement.RequirementFluid;
import es.degrassi.mmr.common.crafting.requirement.RequirementFluidPerTick;
import es.degrassi.mmr.common.crafting.requirement.RequirementItem;
import es.degrassi.mmr.common.entity.EffectDispenserEntity;
import es.degrassi.mmr.common.entity.FuelTankEntity;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.entity.RedstonePortEntity;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.integration.theoneprobe.element.CustomProgress;
import es.degrassi.mmr.common.integration.theoneprobe.element.FuelElement;
import es.degrassi.mmr.common.integration.theoneprobe.element.SizedFluidIngredientRenderer;
import es.degrassi.mmr.common.integration.theoneprobe.element.SizedItemIngredientRenderer;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.util.RomanNumber;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.ILayoutStyle;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TOPInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {
  @Override
  public Void apply(ITheOneProbe probe) {
    probe.registerProvider(this);
    probe.registerElementFactory(new CustomProgress.CustomProgressFactory());
    probe.registerElementFactory(new FuelElement.FuelElementFactory());
    probe.registerElementFactory(new SizedItemIngredientRenderer.SizedItemIngredientFactory());
    probe.registerElementFactory(new SizedFluidIngredientRenderer.SizedFluidIngredientFactory());
    return null;
  }

  @Override
  public Identifier getID() {
    return ModularMachineryReborn.rl("mmr_info_provider");
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData data) {
    BlockEntity tile = level.getBlockEntity(data.getPos());
    if (tile instanceof MachineControllerEntity controller) {
      if (controller.isPaused()) {
        info.mcText(Component.translatable("gui.controller.status.paused").withStyle(ChatFormatting.DARK_RED));
      } else{
        showCraftingInfo(controller, info);
      }
    }
    if (tile instanceof ColorableMachineComponentEntity entity) {
      if (entity instanceof FuelTankEntity e) addFuelInfo(e, info);
      if (entity instanceof RedstonePortEntity e) addRedstoneInfo(e, info);
      if (entity instanceof EffectDispenserEntity e) addEffectInfo(e, info);
      if (entity instanceof IAutoEntity<?> e) addAutoInfo(e, info);
    }
  }

  private void addAutoInfo(IAutoEntity<?> entity, IProbeInfo info) {
    if (entity instanceof IAutoInputEntity) {
      boolean autoInput = entity.shouldAuto();
      Component input = Component.translatable("mmr.tooltip.auto_input", Component.translatable("mmr.gui.tooltip.enabled." + autoInput).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
      info.mcText(input);
    }

    if (entity instanceof IAutoOutputEntity) {
      boolean autoOutput = entity.shouldAuto();
      Component output = Component.translatable("mmr.tooltip.auto_output", Component.translatable("mmr.gui.tooltip.enabled." + autoOutput).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
      info.mcText(output);
    }
  }

  private void addFuelInfo(FuelTankEntity entity, IProbeInfo info) {
    long amount = entity.getFuelHandler().getFuel();
    long capacity = entity.getFuelHandler().getMaxFuel();
    var percent = amount * 1f / capacity;
    ChatFormatting currentColor = ChatFormatting.RED;
    if (percent >= 0.80f) currentColor = ChatFormatting.GREEN;
    else if (percent >= 0.30f) currentColor = ChatFormatting.GOLD;
    info.horizontal(style())
        .element(new FuelElement(percent))
        .mcText(Component.translatable("tooltip.fuel_tank.tank", amount, capacity).withStyle(currentColor));
  }

  private void addRedstoneInfo(RedstonePortEntity entity, IProbeInfo info) {
    Component text = null;
    int amount = entity.getMode().isOutput()
        ? entity.getOutputAmount()
        : entity.provideComponent().getContainerProvider();
    Component power = Component.literal("" + amount).withStyle(ChatFormatting.RED);
    if (entity.getMode().isOutput()) {
      if (amount > 0)
        text = Component.translatable("mmr.tooltip.redstone.emit", power).withStyle(ChatFormatting.GRAY);
    } else {
      text = Component.translatable("mmr.tooltip.redstone.receive", power).withStyle(ChatFormatting.GRAY);
    }
    if (text != null) {
      info.horizontal(style())
          .item(new ItemStack(Items.REDSTONE), info.defaultItemStyle().bounds(8, 8))
          .mcText(text);
    }
  }

  private void addEffectInfo(EffectDispenserEntity entity, IProbeInfo info) {
    if (entity.isApplyingEffect()) {
      int radius = entity.getSize().radius;
      boolean interdimensional = entity.getSize().interdimensional;
      entity.getEffect().ifPresent(effect -> {
        var level = RomanNumber.toRoman(effect.getAmplifier() + 1);
        var stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(PotionContents.getColorOptional(List.of(effect)).orElse(-1)), List.of(effect), Optional.empty()));
        info.horizontal(style())
            .item(stack)
            .mcText(Component.translatable(effect.getDescriptionId()).append(" ").append(level).withStyle(ChatFormatting.DARK_AQUA));
        if (interdimensional) {
          info.mcText(Component.translatable("mmr.tooltip.effect.interdimensional").withStyle(ChatFormatting.GRAY));
        } else {
          info.mcText(Component.translatable("mmr.tooltip.effect", Component.literal(radius + "").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        }
      });
    }
  }

  private void showCraftingInfo(MachineControllerEntity tile, IProbeInfo info) {
    var status = tile.getStatus();
    MutableComponent message = status.text().copy();
    switch (status) {
      case RUNNING -> message.withStyle(ChatFormatting.GREEN);
      case IDLE -> message.withStyle(ChatFormatting.GOLD);
      case MISSING_STRUCTURE, ERRORED, PAUSED -> message.withStyle(ChatFormatting.RED);
    }
    info.mcText(message);
    long runningCores = tile.getProcessor()
        .cores()
        .stream()
        .filter(MachineProcessorCore::isActive)
        .filter(MachineProcessorCore::hasActiveRecipe)
        .count();
    if (runningCores != 1) {
      Component component = Component.translatable(
          "mmr.waila.cores",
          runningCores
      );
      info.mcText(component);
      return;
    }
    var core = tile.getProcessor()
        .cores()
        .stream()
        .filter(MachineProcessorCore::hasActiveRecipe)
        .findFirst();
    if (core.isEmpty() || !core.get().hasActiveRecipe()) return;
    info.progress((int)core.get().getRecipeProgressTime(), (int)core.get().getRecipeTotalTime(), info.defaultProgressStyle().suffix("/%s", (int)core.get().getRecipeTotalTime()));
    info.mcText(Component.literal("Outputs:").withStyle(ChatFormatting.GRAY));
    var outRequirements = core
        .map(MachineProcessorCore::getCurrentRecipe)
        .map(RecipeHolder::value)
        .map(MachineRecipe::getRequirements)
        .stream()
        .flatMap(List::stream)
        .filter(r -> r.requirement().getMode().isOutput())
        .toList();
    List<IElement> items = new ArrayList<>();
    outRequirements.stream()
        .filter(r -> r.requirement() instanceof RequirementItem)
        .forEach(r -> items.add(new SizedItemIngredientRenderer(((RequirementItem)r.requirement()).getIngredient(), r.chance())));
    info.horizontal(style()).elements(items);
    List<IElement> fluids = new ArrayList<>();
    outRequirements.stream()
        .filter(r -> r.requirement() instanceof RequirementFluid || r.requirement() instanceof RequirementFluidPerTick)
        .forEach(r -> {
          switch (r.requirement()) {
            case RequirementFluid f -> fluids.add(new SizedFluidIngredientRenderer(f.getIngredient(), r.chance(), info.defaultIconStyle()));
            case RequirementFluidPerTick f -> fluids.add(new SizedFluidIngredientRenderer(f.getIngredient(), r.chance(), info.defaultIconStyle()));
            default -> {}
          }
        });
    info.horizontal(style()).elements(fluids);
  }

  private ILayoutStyle style() {
    return new LayoutStyle().spacing(4).alignment(ElementAlignment.ALIGN_CENTER).leftPadding(4);
  }
}
