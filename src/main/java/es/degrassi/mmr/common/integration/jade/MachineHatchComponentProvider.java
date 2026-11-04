package es.degrassi.mmr.common.integration.jade;

import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.IAutoInputEntity;
import es.degrassi.mmr.api.entity.IAutoOutputEntity;
import es.degrassi.mmr.client.screen.widget.gauges.GaugesTexts;
import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;
import es.degrassi.mmr.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmr.common.integration.WailaTexts;
import es.degrassi.mmr.common.integration.jade.elements.FuelElement;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.ItemStackElement;
import snownee.jade.impl.ui.SpacerElement;
import snownee.jade.impl.ui.TextElementImpl;

import java.util.List;
import java.util.Optional;

public class MachineHatchComponentProvider implements IBlockComponentProvider {
  public static final MachineHatchComponentProvider INSTANCE = new MachineHatchComponentProvider();
  public static final Identifier ID = ModularMachineryReborn.rl("hatch_component_provider");

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    if (accessor.getBlockEntity() instanceof ColorableMachineComponentEntity entity) {
      CompoundTag tag = accessor.getServerData().getCompound(ModularMachineryReborn.MODID + ".hatch").orElseGet(CompoundTag::new);
      if (tag.isEmpty()) return;

      CompoundTag fuelTag = tag.getCompound("fuel").orElseGet(CompoundTag::new);
      if (!fuelTag.isEmpty()) addFuelInfo(tooltip, fuelTag);

      CompoundTag effectTag = tag.getCompound("effect").orElseGet(CompoundTag::new);
      if (!effectTag.isEmpty()) addEffectInfo(tooltip, effectTag);

      CompoundTag redstoneTag = tag.getCompound("redstone").orElseGet(CompoundTag::new);
      if (!redstoneTag.isEmpty()) addRedstoneInfo(tooltip, redstoneTag);

      CompoundTag autoTag = tag.getCompound("auto").orElseGet(CompoundTag::new);
      if (!autoTag.isEmpty()) {
        boolean autoInput = autoTag.getBooleanOr("input", false);
        boolean autoOutput = autoTag.getBooleanOr("output", false);
        Component input = WailaTexts.tooltipAutoInput.text(
            (autoInput ? TabTexts.tooltipEnabled : TabTexts.tooltipDisabled).text().withStyle(ChatFormatting.AQUA)
        ).withStyle(ChatFormatting.GRAY);
        Component output = WailaTexts.tooltipAutoOutput.text(
            (autoOutput ? TabTexts.tooltipEnabled : TabTexts.tooltipDisabled).text().withStyle(ChatFormatting.AQUA)
        ).withStyle(ChatFormatting.GRAY);
        if (entity instanceof IAutoInputEntity)
          tooltip.add(input);
        if (entity instanceof IAutoOutputEntity)
          tooltip.add(output);
      }
    }
  }

  private void addRedstoneInfo(ITooltip tooltip, CompoundTag redstoneTag) {
    Component text = null;
    int amount = redstoneTag.getIntOr("power", 0);
    Component power = Component.literal(amount + "").withStyle(ChatFormatting.RED);
    if (redstoneTag.getBooleanOr("emit", false)) {
      if (amount != 0)
        text = Component.translatable("mmr.tooltip.redstone.emit", power);
    } else {
      text = Component.translatable("mmr.tooltip.redstone.receive", power);
    }
    if (text != null) {
      tooltip.add(ItemStackElement.of(new ItemStack(Items.REDSTONE), 0.5f));
      tooltip.append(new SpacerElement(4, 0));
      tooltip.append(new TextElementImpl(text));
    }
  }

  private void addEffectInfo(ITooltip tooltip, CompoundTag effectTag) {
    var effect = MobEffectInstance.CODEC.decode(NbtOps.INSTANCE, effectTag.getCompound("instance").orElseGet(CompoundTag::new))
        .result()
        .map(Pair::getFirst)
        .orElse(null);
    if (effect != null) {
      var stack = new ItemStack(Items.POTION);
      stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(),
          Optional.of(PotionContents.getColorOptional(List.of(effect)).orElse(-1)),
          List.of(effect),
          Optional.empty()));

      tooltip.add(ItemStackElement.of(stack, 0.5f));
      tooltip.append(new SpacerElement(4, 0));
      tooltip.append(new TextElementImpl(Component.translatable(effect.getDescriptionId()).append(" ").append(effectTag.getStringOr("level", "")).withStyle(ChatFormatting.DARK_AQUA)));
      if (effectTag.getBooleanOr("giving", false))
        if (effectTag.getBooleanOr("interdimensional", false))
          tooltip.add(Component.translatable("mmr.tooltip.effect.interdimensional"));
        else
          tooltip.add(Component.translatable("mmr.tooltip.effect", Component.literal(effectTag.getInt("radius") + "").withStyle(ChatFormatting.AQUA)));
    }
  }

  private void addFuelInfo(ITooltip tooltip, CompoundTag fuelTag) {
    long amount = fuelTag.getLongOr("amount", 0);
    long capacity = fuelTag.getLongOr ("capacity", 1);
    var percent = amount * 1f / capacity;
    ChatFormatting currentColor = ChatFormatting.RED;
    if (percent >= 0.80f) currentColor = ChatFormatting.GREEN;
    else if (percent >= 0.30f) currentColor = ChatFormatting.GOLD;
    tooltip.add(new FuelElement(percent));
    tooltip.append(new SpacerElement(4, 0));
    tooltip.append(new TextElementImpl(GaugesTexts.fuelTankTicks.text(amount, capacity).withStyle(currentColor)));
  }

  @Override
  public Identifier getUid() {
    return ID;
  }
}
