package es.degrassi.mmr.common.integration.jade;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.integration.jade.elements.SizedFluidIngredientElement;
import es.degrassi.mmr.common.integration.jade.elements.SizedItemIngredientElement;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.Element;
import snownee.jade.api.view.ProgressView;
import snownee.jade.impl.ui.ProgressElement;
import snownee.jade.impl.ui.SimpleProgressStyle;

import java.util.List;

public class DynamicMachineComponentProvider implements IBlockComponentProvider {

  public static final DynamicMachineComponentProvider INSTANCE = new DynamicMachineComponentProvider();
  public static final Identifier ID = ModularMachineryReborn.rl("machine_component_provider");

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    if (accessor.getBlockEntity() instanceof MachineControllerEntity) {
      accessor.getServerData().getCompound(ModularMachineryReborn.MODID + ".controller").ifPresent(tag -> {
        if (tag.isEmpty()) return;
        if (tag.contains("paused")) {
          tooltip.add(Component.translatable("gui.controller.status.paused").withStyle(ChatFormatting.DARK_RED));
          return;
        }
        if (tag.contains("status")) {
          MachineStatus status = tag.read(
              "status",
              MachineStatus.CODEC.codec(),
              NbtOps.INSTANCE
          ).orElse(MachineStatus.MISSING_STRUCTURE);

          MutableComponent message = status.text().copy();
          switch (status) {
            case RUNNING -> message.withStyle(ChatFormatting.GREEN);
            case IDLE -> message.withStyle(ChatFormatting.GOLD);
            case MISSING_STRUCTURE, ERRORED, PAUSED -> message.withStyle(ChatFormatting.RED);
          }
          tooltip.add(message);
        }
        if (tag.contains("runningCores")) {
          tag.getLong("runningCores").ifPresent(runningCores -> {
            if (runningCores == 1) {
              var progress = tag.getCompound("progress").orElseGet(CompoundTag::new);
              var outputs = tag.getCompound("outputs").orElseGet(CompoundTag::new);
              var items = outputs.getList("item");
              var fluids = outputs.getList("fluid");
              var current = progress.getFloatOr("current", 0f);
              var total = progress.getFloatOr("total", 1f);
              var progressStyle = new SimpleProgressStyle();
              progressStyle.color = ChatFormatting.WHITE.getColor();
              var progressView = new ProgressView(
                  ProgressView.Part.of(current / total),
                  Component.translatable("%s/%s", (int)current, (int)total),
                  progressStyle,
                  BoxStyle.nestedBox()
              );
              tooltip.add(new ProgressElement(progressView));
              tooltip.add(Component.literal("Outputs:").withStyle(ChatFormatting.GRAY));
              List<Element> itemElements = Lists.newArrayList();
              items.orElseGet(ListTag::new)
                  .stream()
                  .map(t -> (CompoundTag) t)
                  .forEach(t -> {
                    SizedIngredient.NESTED_CODEC.decode(NbtOps.INSTANCE, t.getCompound("ingredient").orElseGet(CompoundTag::new))
                        .resultOrPartial().map(Pair::getFirst)
                        .ifPresent(ing -> {
                      var chance = t.getFloatOr("chance", 100);
                      itemElements.add(SizedItemIngredientElement.of(ing, chance));
                    });
                  });
              tooltip.add(itemElements);
              List<Element> fluidElements = Lists.newArrayList();
              fluids.orElseGet(ListTag::new).stream()
                  .map(t -> (CompoundTag) t)
                  .forEach(t -> {
                    SizedFluidIngredient.CODEC.decode(NbtOps.INSTANCE, t.getCompound("ingredient").orElseGet(CompoundTag::new))
                        .resultOrPartial().map(Pair::getFirst)
                        .ifPresent(ing -> {
                      var chance = t.getFloatOr("chance", 100);
                      fluidElements.add(new SizedFluidIngredientElement(ing, chance));
                    });
                  });
              tooltip.add(fluidElements);
            } else {
              Component component = Component.translatable(
                  "mmr.waila.cores",
                  runningCores
              );
              tooltip.add(component);
            }
          });
        }
      });
    }
  }

  @Override
  public Identifier getUid() {
    return ID;
  }
}
