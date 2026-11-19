package es.degrassi.mmreborn.client.screen.widget.tabs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.api.BlockIngredient;
import es.degrassi.mmreborn.client.item.MMRItemTooltipComponent;
import es.degrassi.mmreborn.client.screen.ControllerScreen;
import es.degrassi.mmreborn.client.screen.popup.ConfirmationPopup;
import es.degrassi.mmreborn.client.screen.widget.ItemOrIconButton;
import es.degrassi.mmreborn.common.data.MMRConfig;
import es.degrassi.mmreborn.common.network.client.CPlaceStructurePacket;
import es.degrassi.mmreborn.common.util.CycleTimer;
import es.degrassi.mmreborn.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class StructurePlacerWidget extends TopTabWidget {
  private final ControllerScreen parentScreen;

  private final ResourceLocation machine;
  private final BlockPos controllerPos;
  private static final CycleTimer timer = new CycleTimer(() -> 1000, true);

  public final Component component = Component.translatable("modular_machinery_reborn.gui.structure_placer_button");
  private final Component tooltipTime, time, timeWithout;

  public StructurePlacerWidget(ControllerScreen parentScreen, ResourceLocation machine, BlockPos controllerPos) {
    super(0, 0, new ItemOrIconButton(5, 5, Blocks.STRUCTURE_BLOCK.asItem(), button -> {}));
    this.parentScreen = parentScreen;
    this.machine = machine;
    this.controllerPos = controllerPos;
    int positions = parentScreen.getMenu().getEntity().getFoundMachine().getPattern().getBlocks(Direction.NORTH).size();
    int positionsWithout = parentScreen.getMenu().getEntity().getFoundMachine().getPattern().getBlocksFiltered(Direction.NORTH).size();
    this.tooltipTime = Component.translatable("mmr.gui.structure.time.tooltip", MMRConfig.get().delayOperation.get());
    this.time = Component.translatable("mmr.gui.structure.time", Utils.decimalFormat(positions / 20f), positions);
    this.timeWithout = Component.translatable("mmr.gui.structure.time", Utils.decimalFormat(positionsWithout / 20f), positionsWithout);
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    timer.onDraw();
    super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
  }

  @Override
  public void onClick(double mouseX, double mouseY, int button) {
    parentScreen.setFocused(this);
    parentScreen.getMenu().getEntity().setLastFocus(0);
    parentScreen.openPopup(new ConfirmationPopup<>(
        parentScreen,
        180,
        96,
        () -> parentScreen.openPopup(
            new ConfirmationPopup<>(
                parentScreen,
                180,
                96,
                () -> PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos, true))
            )
                .cancelCallback(() -> onClick(mouseX, mouseY, button))
                .text(
                    Component.translatable("mmr.gui.structure.place.modifier.true"),
                    time
                )
        )
    )
        .confirmText(Component.translatable("mmr.gui.structure.place.confirm.modifier"))
        .cancelText(Component.translatable("mmr.gui.structure.place.cancel.modifier"))
        .text(Component.translatable("mmr.gui.structure.place.modifier"))
        .addCloseButton()
        .cancelCallback(() -> parentScreen.openPopup(
                new ConfirmationPopup<>(
                    parentScreen,
                    180,
                    96,
                    () -> PacketDistributor.sendToServer(new CPlaceStructurePacket(machine, controllerPos, false))
                )
                    .cancelCallback(() -> onClick(mouseX, mouseY, button))
                    .text(
                        Component.translatable("mmr.gui.structure.place.modifier.false"),
                        timeWithout
                    )
            )
        ), "popup");
  }

  @Override
  public void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    super.renderTooltip(guiGraphics, x, y);
    List<Either<FormattedText, TooltipComponent>> components = Lists.newArrayList();
    gatherComponents(components);
    guiGraphics.renderComponentTooltipFromElements(
          Minecraft.getInstance().font,
          components,
          x,
          y,
          ItemStack.EMPTY
      );
  }

  public void gatherComponents(List<Either<FormattedText, TooltipComponent>> components) {
    components.add(Either.left(component));
    components.add(Either.left(tooltipTime));
    Optional.of(parentScreen.getMenu().getEntity().getFoundMachine())
        .ifPresentOrElse(machine -> {
              if (Screen.hasShiftDown() && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_N)) {
                machine.getPattern().getMinBlocksPredicate()
                    .minBlocks()
                    .forEach((key, value) -> {
                      components.add(Either.left(Component.translatable(
                          "modular_machinery_reborn.controller.required.block",
                          key.getNamesUnified().append(value.guiText())
                      ).withStyle(ChatFormatting.GRAY)));
                    });
              } else if (Screen.hasShiftDown()) {
                components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
                    Component.translatable("modular_machinery_reborn.controller.required.shift").append(" + N")
                        .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
                components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required").withStyle(ChatFormatting.GRAY)));
                Map<MutableComponent, List<ItemStack>> map = Maps.newHashMap();
                machine.getPattern()
                    .getPattern()
                    .asList()
                    .stream()
                    .flatMap(List::stream)
                    .flatMap(s -> s.chars().mapToObj(c -> (char) c))
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                      BlockIngredient ingredient = machine.getPattern().getPattern().asMap().get(entry.getKey());
                      if (ingredient == null) return null;
                      return Pair.of(ingredient.getStacks(entry.getValue().intValue()), ingredient.getNamesUnified());
                    })
                    .filter(Objects::nonNull)
                    .forEachOrdered(pair -> {
                      List<ItemStack> stacks = pair.getFirst();
                      MutableComponent component = pair.getSecond();
                      List<ItemStack> s2 = Lists.newArrayList();
                      if (map.containsKey(component)) {
                        stacks.forEach(stack -> map.forEach((c, s) -> {
                          if (c.getString().equals(component.getString())) {
                            s.forEach(s1 -> {
                              if (s1.is(stack.getItem())) {
                                s1.grow(stack.getCount());
                              }
                              s2.add(s1);
                            });
                          }
                        }));
                      } else {
                        s2.addAll(stacks);
                      }

                      map.put(component, s2);
                    });
                map.forEach((c, stacks) -> {
                  if (c != null && !stacks.isEmpty()) {
                    MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, timer);
                    component.setComponent(
                        Component.translatable(
                            "modular_machinery_reborn.controller.required.block",
                            c
                        ).withStyle(ChatFormatting.GRAY)
                    );
                    components.add(Either.right(component));
                  }
                });
              } else {
                components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
                    Component.translatable("modular_machinery_reborn.controller.required.shift").append(" + N")
                        .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
                components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
                    Component.translatable("modular_machinery_reborn.controller.required.shift")
                        .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
              }

              if (Screen.hasControlDown() && !Screen.hasShiftDown()) {
                components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.modifier").withStyle(ChatFormatting.GRAY)));
                Map<MutableComponent, List<ItemStack>> modifierMap = Maps.newHashMap();
                machine.getPattern()
                    .getPattern()
                    .getModifiers()
                    .forEach(ingredient -> {
                      MutableComponent component = Component.empty();
                      ingredient.getDescriptionLines().forEach(component::append);
                      modifierMap.put(component, ingredient.getIngredient().getStacks(1));
                    });

                if (!modifierMap.isEmpty()) {
                  modifierMap.forEach((c, stacks) -> {
                    if (c != null && !stacks.isEmpty()) {
                      MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, timer);
                      component.setComponent(
                          Component.translatable(
                              "modular_machinery_reborn.controller.required.block",
                              c
                          ).withStyle(ChatFormatting.GRAY)
                      );
                      components.add(Either.right(component));
                    }
                  });
                }
              } else {
                components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.required.block.key",
                    Component.translatable("modular_machinery_reborn.controller.required.control").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY)));
              }
            },
            () -> components.add(Either.left(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY))));
  }
}
