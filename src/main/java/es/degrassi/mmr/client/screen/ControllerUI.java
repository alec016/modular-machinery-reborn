package es.degrassi.mmr.client.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.lowdragmc.lowdraglib2.client.font.LDFonts;
import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollDisplay;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollerMode;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.utils.KeyState;
import com.mojang.blaze3d.platform.InputConstants;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.BlockIngredient;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.api.crafting.requirement.RequirementDisplayInfo;
import es.degrassi.mmr.client.MMRClientEvents;
import es.degrassi.mmr.client.item.MMRItemTooltipComponent;
import es.degrassi.mmr.client.screen.popup.ConfirmationPopup;
import es.degrassi.mmr.client.screen.popup.CoreGridPopup;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.tabs.TopTab;
import es.degrassi.mmr.common.data.MMRClientConfig;
import es.degrassi.mmr.common.data.MMRConfig;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.integration.kubejs.KubeJSIntegration;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.network.client.CBreakStructurePacket;
import es.degrassi.mmr.common.network.client.CPlaceStructurePacket;
import es.degrassi.mmr.common.registration.RecipeRegistration;
import es.degrassi.mmr.common.util.CycleTimer;
import es.degrassi.mmr.common.util.Mods;
import es.degrassi.mmr.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ControllerUI {

  private ControllerUI() {}

  private static final int STATUS_AREA_X = 12;
  private static final int STATUS_AREA_Y = 8;
  private static final int STATUS_AREA_WIDTH = 152;
  private static final int STATUS_AREA_HEIGHT = 68;

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, MachineControllerEntity controller) {
    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUICONTROLLER, MMRMachineUI.PANEL_WIDTH, 166);

    ScrollerView statusScroller = new ScrollerView();
    statusScroller.lss("width", STATUS_AREA_WIDTH + 11).lss("height", STATUS_AREA_HEIGHT);
    MMRMachineUI.add(panel, statusScroller, STATUS_AREA_X, STATUS_AREA_Y);
    statusScroller.getScrollerViewStyle().mode(ScrollerMode.VERTICAL).verticalScrollDisplay(ScrollDisplay.AUTO).scrollerViewStyle(4);
    statusScroller.viewPort.layout(layout -> layout.paddingAll(0));
    statusScroller.viewPort.style(style -> style.background(IGuiTexture.EMPTY));
    statusScroller.verticalScroller.lss("margin-left", 4).lss("width", 7);
    statusScroller.verticalScroller.scrollContainer.style(style ->
        style.background(MMRMachineUI.spriteNineSlice(MMRMachineUI.TEX_SMALL_SCROLLER_DISABLED, 3, 4, 3, 4)));
    statusScroller.verticalScroller.scrollBar.buttonStyle(style -> {
      var thumb = MMRMachineUI.spriteNineSlice(MMRMachineUI.TEX_SMALL_SCROLLER, 3, 4, 3, 4);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    statusScroller.viewContainer.addChild(MMRMachineUI.at(new StatusText(controller), 0, 0));

    buildTopTabs(panel, controller);
    MMRMachineUI.playerInventory(panel, 8, 84);
    return MMRMachineUI.menu(panel, holder.player);
  }

  private static void buildTopTabs(UIElement panel, MachineControllerEntity controller) {
    BlockPos pos = controller.getBlockPos();
    int next = 0;
    var blocks = controller.getFoundMachine().getPattern().getBlocks(Direction.NORTH).size();
    int delay = MMRConfig.get().delayOperation.get();
    new TopTab(panel, next++, -TopTab.TOP_TAB_HEIGHT,
        () -> new ItemStack(Items.STRUCTURE_BLOCK),
        () -> placerTooltips(controller),
        event -> openPlacerPopups(panel, controller, machine(controller), pos));
    new TopTab(panel, TopTab.TOP_TAB_PITCH * next++, -TopTab.TOP_TAB_HEIGHT,
        () -> new ItemStack(Items.DIAMOND_PICKAXE),
        () -> List.of(Component.translatable("modular_machinery_reborn.gui.structure_breaker_button")),
        event -> new ConfirmationPopup(panel, 180, 84, null,
            List.of(
                Component.translatable("mmr.gui.structure.break"),
                UITexts.breakPlaceTime.text(
                    Utils.format(blocks * delay),
                    Utils.decimalFormat(blocks * delay / 20f)
                )
            ),
            null, null,
            () -> ClientPacketDistributor.sendToServer(
                new CBreakStructurePacket(machine(controller), pos)),
            null, true));
    if (controller.getProcessor().getMaxActiveCores() > 1) {
      new TopTab(panel, TopTab.TOP_TAB_PITCH * next++, -TopTab.TOP_TAB_HEIGHT,
          Icon.CHECK,
          () -> List.of(Component.translatable("modular_machinery_reborn.gui.core_button")),
          event -> new CoreGridPopup(panel, controller));
    }
    if (addExtraTooltipsTab(panel, controller, next)) {
      next++;
    }
    addRecipesTab(panel, controller, next);
  }

  private static Identifier machine(MachineControllerEntity controller) {
    return controller.getId() == null ? DynamicMachine.DUMMY.getRegistryName() : controller.getId();
  }

  private static boolean addExtraTooltipsTab(UIElement panel, MachineControllerEntity controller, int index) {
    var extra = ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(controller.getId());
    if (extra == null) {
      return false;
    }
    var list = extra.get(TooltipUse.GUI);
    if (list == null || list.isEmpty()) {
      return false;
    }
    List<Object> tooltips = new ArrayList<>();
    long[] lastResolve = {-1};
    java.util.function.Supplier<List<Object>> supplier = () -> {
      long now = System.currentTimeMillis();
      if (lastResolve[0] < 0 || now - lastResolve[0] >= MMRConfig.get().dynamicTooltipTicks.get() * 50L) {
        lastResolve[0] = now;
        tooltips.clear();
        for (var either : list) {
          if (either.left().isPresent()) {
            tooltips.add(KubeJSIntegration.sendDynamicTooltipEvent(either.left().get()));
          } else if (either.right().isPresent()) {
            tooltips.add(either.right().get());
          }
        }
      }
      return tooltips;
    };
    new TopTab(panel, TopTab.TOP_TAB_PITCH * index, -TopTab.TOP_TAB_HEIGHT, Icon.HELP, supplier, null);
    return true;
  }

  private static void addRecipesTab(UIElement panel, MachineControllerEntity controller, int index) {
    if (!Mods.isJEILoaded()) {
      return;
    }
    DynamicMachine machine = controller.getFoundMachine();
    if (machine == null) {
      return;
    }
    new TopTab(panel, TopTab.TOP_TAB_PITCH * index, -TopTab.TOP_TAB_HEIGHT,
        () -> new ItemStack(Items.CRAFTING_TABLE),
        () -> List.of(Component.translatable("emi.tooltip.show.recipes")),
        event -> MMRJeiPlugin.showRecipes(machine.getRegistryName()));
  }

  private static final CycleTimer ITEM_CYCLE = new CycleTimer(() -> 1000, true);

  private static List<Object> placerTooltips(MachineControllerEntity controller) {
    ITEM_CYCLE.onDraw();
    List<Object> components = new ArrayList<>();
    components.add(Component.translatable("modular_machinery_reborn.gui.structure_placer_button"));
    DynamicMachine machine = controller.getFoundMachine();
    if (machine == null) {
      components.add(Component.translatable("modular_machinery_reborn.controller.no_machine").withStyle(ChatFormatting.GRAY));
      return components;
    }
    if (KeyState.isShiftDown() && InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_N)) {
      machine.getPattern().getMinBlocksPredicate()
          .minBlocks()
          .forEach((key, value) -> components.add(Component.translatable(
              "modular_machinery_reborn.controller.required.block",
              key.getNamesUnified().append(value.guiText())
          ).withStyle(ChatFormatting.GRAY)));
    } else if (KeyState.isShiftDown()) {
      components.add(Component.translatable("modular_machinery_reborn.controller.required.block.key",
          Component.translatable("modular_machinery_reborn.controller.required.shift").append(" + N")
              .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
      components.add(Component.translatable("modular_machinery_reborn.controller.required").withStyle(ChatFormatting.GRAY));
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
            List<ItemStack> stacks = pair.getLeft();
            MutableComponent component = pair.getRight();
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
        if (!stacks.isEmpty()) {
          MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, ITEM_CYCLE);
          component.setComponent(Component.translatable(
              "modular_machinery_reborn.controller.required.block",
              c
          ).withStyle(ChatFormatting.GRAY));
          components.add(component);
        }
      });
    } else {
      components.add(Component.translatable("modular_machinery_reborn.controller.required.block.key",
          Component.translatable("modular_machinery_reborn.controller.required.shift").append(" + N")
              .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
      components.add(Component.translatable("modular_machinery_reborn.controller.required.block.key",
          Component.translatable("modular_machinery_reborn.controller.required.shift")
              .withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
    }
    if (KeyState.isCtrlDown() && !KeyState.isShiftDown()) {
      components.add(Component.translatable("modular_machinery_reborn.controller.modifier").withStyle(ChatFormatting.GRAY));
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
          if (!stacks.isEmpty()) {
            MMRItemTooltipComponent component = new MMRItemTooltipComponent(stacks, ITEM_CYCLE);
            component.setComponent(Component.translatable(
                "modular_machinery_reborn.controller.required.block",
                c
            ).withStyle(ChatFormatting.GRAY));
            components.add(component);
          }
        });
      }
    } else {
      components.add(Component.translatable("modular_machinery_reborn.controller.required.block.key",
          Component.translatable("modular_machinery_reborn.controller.required.control").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
    }
    return components;
  }

  private static void openPlacerPopups(UIElement panel, MachineControllerEntity controller,
                                       Identifier machine, BlockPos pos) {
    var pattern = ModularMachineryReborn.MACHINES.get(machine).getPattern();
    var with = pattern.getBlocks(Direction.NORTH).size();
    var without = pattern.getBlocksFiltered(Direction.NORTH).size();
    int delay = MMRConfig.get().delayOperation.get();
    AtomicReference<Runnable> openFirst = new AtomicReference<>(() -> {});
    openFirst.set(() -> new ConfirmationPopup(panel, 180, 84, null,
        List.of(Component.translatable("mmr.gui.structure.place.modifier")),
        Component.translatable("mmr.gui.structure.place.confirm.modifier"),
        Component.translatable("mmr.gui.structure.place.cancel.modifier"),
        // "with modifiers" needs its own confirmation before the packet leaves.
        () -> new ConfirmationPopup(panel, 180, 84, null,
            List.of(
                Component.translatable("mmr.gui.structure.place.modifier.true"),
                UITexts.breakPlaceTime.text(
                    Utils.format(with * delay),
                    Utils.decimalFormat(with * delay / 20f)
                )
            ),
            null, null,
            () -> ClientPacketDistributor.sendToServer(new CPlaceStructurePacket(machine, pos, true)),
            openFirst.get(),
            true),
        // So does "without modifiers": same two-step flow for the other action.
        () -> new ConfirmationPopup(panel, 180, 84, null,
            List.of(
                Component.translatable("mmr.gui.structure.place.modifier.false"),
                UITexts.breakPlaceTime.text(
                    Utils.format(without * delay),
                    Utils.decimalFormat(without * delay / 20f)
                )
            ),
            null, null,
            () -> ClientPacketDistributor.sendToServer(new CPlaceStructurePacket(machine, pos, false)),
            openFirst.get(),
            true),
        true));
    openFirst.get().run();
  }

  private static class StatusText extends UIElement {
    private static final float SCALE = 0.72f;
    private final MachineControllerEntity controller;
    private int lastHeight = -1;

    StatusText(MachineControllerEntity controller) {
      this.controller = controller;
      lss("width", STATUS_AREA_WIDTH).lss("height", 10);
    }

    @Override
    protected void drawBackgroundAdditional(IGUIContext context) {
      if (!(context instanceof GUIContext gui)) {
        return;
      }
      var font = Minecraft.getInstance().font;
      int x0 = (int) getContentX();
      int y0 = (int) getContentY();
      int wrapWidth = Mth.floor((STATUS_AREA_WIDTH - 4) / SCALE);
      AtomicDouble y = new AtomicDouble(0);
      DynamicMachine machine = controller.getFoundMachine();
      if (machine != null) {
        for (var line : font.split(Component.literal(machine.getLocalizedName()), wrapWidth)) {
          y.addAndGet(5);
          drawStatusLine(gui, font, line, x0, y0 + (float) y.get());
          y.addAndGet(7);
        }
        y.addAndGet(-5);
      } else {
        y.set(5);
        drawStatusLine(gui, font,
            Component.translatable("gui.controller.structure",
                Component.translatable("gui.controller.structure.none")).getVisualOrderText(),
            x0, y0 + (float) y.get());
      }
      y.addAndGet(2);
      if (controller.isPaused()) {
        for (var line : font.split(Component.translatable("gui.controller.status.redstone_stopped"), wrapWidth)) {
          y.addAndGet(5);
          drawStatusLine(gui, font, line, x0, y0 + (float) y.get());
          y.addAndGet(7);
        }
        updateHeight((float) y.get());
        return;
      }
      var error = controller.getErrorMessage();
      if (!error.getString().isEmpty()) {
        var errorInfo = Component.translatable("gui.controller.error.info").append(error);
        for (var line : font.split(errorInfo, wrapWidth)) {
          y.addAndGet(5);
          drawStatusLine(gui, font, line, x0, y0 + (float) y.get());
          y.addAndGet(7);
        }
      }
      var status = Component.translatable("gui.controller.status").append(controller.getStatus().text());
      for (var line : font.split(status, wrapWidth)) {
        y.addAndGet(5);
        drawStatusLine(gui, font, line, x0, y0 + (float) y.get());
        y.addAndGet(7);
      }
      var runningCores = controller.getProcessor()
          .cores()
          .stream()
          .filter(core -> core.isActive() && core.isHasActiveRecipe())
          .toList();
      if (runningCores.size() == 1) {
        runningCores.stream()
            .findFirst()
            .ifPresent(core -> MMRClientEvents.getClientRecipes()
                .byType(RecipeRegistration.RECIPE_TYPE.get())
                .stream()
                .filter(holder -> holder.id().equals(ResourceKey.create(Registries.RECIPE, core.getFutureRecipeID())))
                .findFirst()
                .ifPresent(holder -> {
                  y.addAndGet(5);
                  drawStatusLine(gui, font, UITexts.recipeProgress.text(
                    core.getRecipeProgressTime(),
                    core.getRecipeTotalTime(),
                    Utils.decimalFormatWithPercentage(core.getRecipeProgressTime() * 100 / core.getRecipeTotalTime())
                  ).getVisualOrderText(), x0, y0 + (float) y.getAndAdd(2));
                  if (MMRClientConfig.get().showRecipeId.getAsBoolean()) {
                    for (var line : font.split(UITexts.recipeId.text(holder.id().identifier().toString()), wrapWidth)) {
                      y.getAndAdd(5);
                      drawStatusLine(gui, font, line, x0, y0 + (float) y.getAndAdd(2));
                    }
                  }
                  y.getAndAdd(5);
                  var outputs = holder.value()
                      .getRequirements()
                      .stream()
                      .filter(req -> {
                        if (!req.requirement().getMode().isOutput() && !req.requirement().getMode().isNone()) return false;
                        if (!MMRClientConfig.get().get(Objects.requireNonNull(req.getType().getId()))) return false;
                        var info = new RequirementDisplayInfo();
                        req.getDisplayInfo(info);
                        var template = req.info;
                        if (template != null) {
                          if (!template.getTooltips().isEmpty()) info.getTooltips().clear();
                          template.build(info);
                        }
                        return req.hasControllerText() || !info.getTooltips().isEmpty();
                      })
                      .toList();
                  if (outputs.isEmpty()) return;
                  drawStatusLine(gui, font, UITexts.recipeProductions.text().getVisualOrderText(), x0, y0 + (float) y.getAndAdd(2));
                  outputs.forEach(req -> {
                    var info = new RequirementDisplayInfo();
                    req.getDisplayInfo(info);
                    var template = req.info;
                    if (template != null) {
                      if(!template.getTooltips().isEmpty())
                        info.getTooltips().clear();
                      template.build(info);
                    }
                    if (req.hasControllerText()) {
                      for (var line : font.split(UITexts.recipeListItem.text(req.toUI()), wrapWidth)) {
                        y.getAndAdd(5);
                        drawStatusLine(gui, font, line, x0, y0 + (float) y.getAndAdd(2));
                      }
                      return;
                    }
                    if (!info.getTooltips().isEmpty()) {
                      boolean first = true;
                      for (var x : info.getTooltips()) {
                        if (!x.getSecond().shouldDisplay(gui.mc.player, gui.mc.options.advancedItemTooltips)) continue;
                        var text = first
                            ? UITexts.recipeListItem.text(x.getFirst())
                            : UITexts.recipeListItemExtra.text(x.getFirst());
                        first = false;
                        var lines = font.split(text, wrapWidth);
                        for (var line : lines) {
                          y.addAndGet(5);
                          drawStatusLine(gui, font, line, x0, y0 + (float) y.addAndGet(2));
                        }
                      }
                    }
                  });
                })
            );
      }
      updateHeight((float) y.addAndGet(5));
    }

    private static void drawStatusLine(GUIContext gui, Font font,
                                       FormattedCharSequence line, float x, float y) {
      gui.pose.pushPose();
      gui.pose.translate(x, y);
      gui.pose.scale(SCALE, SCALE);
      LDFonts.drawText(gui, font, line, 0, 0, ARGB.opaque(0xFFFFFF), true);
      gui.pose.popPose();
    }

    private void updateHeight(float y) {
      int height = Mth.ceil(y) + 2;
      if (height != lastHeight) {
        lastHeight = height;
        lss("height", Math.max(10, height));
      }
    }
  }
}
