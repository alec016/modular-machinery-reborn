package es.degrassi.mmr.client.screen;

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
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.api.client.machine.TooltipUse;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.integration.jei.MMRJeiPlugin;
import es.degrassi.mmr.common.machine.DynamicMachine;
import es.degrassi.mmr.common.network.client.CBreakStructurePacket;
import es.degrassi.mmr.common.network.client.CPlaceStructurePacket;
import es.degrassi.mmr.common.util.Mods;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * LDLib2 replacement for the old {@code ControllerContainer} + {@code ControllerScreen} pair.
 *
 * <p>{@code guicontroller.png} panel with the top tabs (structure placer, breaker, core grid and the
 * extra MMR tooltips tab), the 0.72-scaled status text at legacy text coords (14, 14) with 7px line
 * steps and 158/0.72 wrapping, and the full popup flows ({@code MMRPopups}).
 */
public final class ControllerUI {

  private ControllerUI() {}

  /** Status area geometry: the legacy black screen region of {@code guicontroller.png}. */
  private static final int STATUS_AREA_X = 12;
  private static final int STATUS_AREA_Y = 8;
  private static final int STATUS_AREA_WIDTH = 152;
  private static final int STATUS_AREA_HEIGHT = 68;

  public static ModularUI create(BlockUIMenuType.BlockUIHolder holder, MachineControllerEntity controller) {
    UIElement panel = MMRMachineUI.panel(MMRMachineUI.TEX_GUICONTROLLER, MMRMachineUI.PANEL_WIDTH, 166);

    // The legacy black screen area is a scroller now: the 0.72-scaled status text scrolls inside
    // it when it overflows, with the small MMR scroller hugging the area's right edge.
    ScrollerView statusScroller = new ScrollerView();
    statusScroller.lss("width", STATUS_AREA_WIDTH + 11).lss("height", STATUS_AREA_HEIGHT);
    MMRMachineUI.add(panel, statusScroller, STATUS_AREA_X, STATUS_AREA_Y);
    statusScroller.getScrollerViewStyle().mode(ScrollerMode.VERTICAL).verticalScrollDisplay(ScrollDisplay.AUTO).scrollerViewStyle(4);
    // No default viewport chrome; the MMR small_scroller sprites replace the theme ones.
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
    int[] next = {0};
    // Placer tab (structure block icon) — the place/break packets are the legacy client packets.
    MMRTabs.topTab(panel, MMRTabs.TOP_TAB_PITCH * next[0]++, -MMRTabs.TOP_TAB_HEIGHT,
        () -> new ItemStack(Items.STRUCTURE_BLOCK),
        () -> List.of(Component.translatable("modular_machinery_reborn.gui.structure_placer_button")),
        event -> openPlacerPopups(panel, controller, machine(controller), pos));
    // Breaker tab (diamond pickaxe).
    MMRTabs.topTab(panel, MMRTabs.TOP_TAB_PITCH * next[0]++, -MMRTabs.TOP_TAB_HEIGHT,
        () -> new ItemStack(Items.DIAMOND_PICKAXE),
        () -> List.of(Component.translatable("modular_machinery_reborn.gui.structure_breaker_button")),
        event -> MMRPopups.confirmation(panel, 180, 84, null,
            List.of(Component.translatable("mmr.gui.structure.break"), Component.empty(), Component.empty()),
            null, null,
            () -> ClientPacketDistributor.sendToServer(
                new CBreakStructurePacket(machine(controller), pos)),
            null, true));
    // Core grid tab (check icon).
    MMRTabs.topTab(panel, MMRTabs.TOP_TAB_PITCH * next[0]++, -MMRTabs.TOP_TAB_HEIGHT,
        Icon.CHECK,
        () -> List.of(Component.translatable("modular_machinery_reborn.gui.core_button")),
        event -> MMRPopups.coreGrid(panel, controller));
    // Extra tooltips tab when the machine carries GUI tooltip entries.
    if (addExtraTooltipsTab(panel, controller, next[0])) {
      next[0]++;
    }
    // Legacy "Show Recipes" tab (only while JEI is around and the structure resolves the machine).
    addRecipesTab(panel, controller, next[0]);
  }

  private static Identifier machine(MachineControllerEntity controller) {
    return controller.getId() == null ? DynamicMachine.DUMMY.getRegistryName() : controller.getId();
  }

  /** Optional extra-tooltips tab (legacy {@code ControllerExtraTooltipsTabWidget}), Icon.HELP. */
  private static boolean addExtraTooltipsTab(UIElement panel, MachineControllerEntity controller, int index) {
    var extra = ModularMachineryReborn.MACHINE_EXTRA_TOOLTIPS.get(controller.getId());
    if (extra == null) {
      return false;
    }
    var list = extra.get(TooltipUse.GUI);
    if (list == null || list.isEmpty()) {
      return false;
    }
    List<Component> tooltips = new ArrayList<>();
    for (var either : list) {
      if (either.left().isPresent()) {
        tooltips.add(Component.translatable(either.left().get().toString()));
      } else if (either.right().isPresent()) {
        tooltips.add(either.right().get());
      }
    }
    MMRTabs.topTab(panel, MMRTabs.TOP_TAB_PITCH * index, -MMRTabs.TOP_TAB_HEIGHT, Icon.HELP, () -> tooltips, null);
    return true;
  }

  /**
   * The legacy "Show Recipes" tab: only with JEI loaded and a resolved machine, opening the
   * machine's recipe categories in the JEI screen (legacy {@code ShowRecipesTabWidget}).
   */
  private static void addRecipesTab(UIElement panel, MachineControllerEntity controller, int index) {
    if (!Mods.isJEILoaded()) {
      return;
    }
    DynamicMachine machine = controller.getFoundMachine();
    if (machine == null || machine.getRegistryName() == null) {
      return;
    }
    MMRTabs.topTab(panel, MMRTabs.TOP_TAB_PITCH * index, -MMRTabs.TOP_TAB_HEIGHT,
        () -> new ItemStack(Items.CRAFTING_TABLE),
        () -> List.of(Component.translatable("emi.tooltip.show.recipes")),
        event -> MMRJeiPlugin.showRecipes(machine.getRegistryName()));
  }

  /**
   * Opens the legacy two-stage structure-placer chain: first the with/without modifiers question,
   * then one confirmation per choice; cancelling a confirmation re-opens the question dialog.
   */
  private static void openPlacerPopups(UIElement panel, MachineControllerEntity controller,
                                       Identifier machine, BlockPos pos) {
    AtomicReference<Runnable> openFirst = new AtomicReference<>();
    openFirst.set(() -> MMRPopups.confirmation(panel, 180, 84, null,
        List.of(Component.translatable("mmr.gui.structure.place.modifier")),
        Component.translatable("mmr.gui.structure.place.confirm.modifier"),
        Component.translatable("mmr.gui.structure.place.cancel.modifier"),
        // "with modifiers" needs its own confirmation before the packet leaves.
        () -> MMRPopups.confirmation(panel, 180, 84, null,
            List.of(Component.translatable("mmr.gui.structure.place.modifier.true")),
            null, null,
            () -> ClientPacketDistributor.sendToServer(new CPlaceStructurePacket(machine, pos, true)),
            openFirst.get(),
            true),
        // So does "without modifiers": same two-step flow for the other action.
        () -> MMRPopups.confirmation(panel, 180, 84, null,
            List.of(Component.translatable("mmr.gui.structure.place.modifier.false")),
            null, null,
            () -> ClientPacketDistributor.sendToServer(new CPlaceStructurePacket(machine, pos, false)),
            openFirst.get(),
            true),
        true));
    openFirst.get().run();
  }

  /**
   * The legacy controller status text: rendered at 0.72 scale inside the scrollable black area,
   * 14px text steps on the legacy scale and colors preserved by sequence splitting. The element is
   * the scroller's content, so its height is set from the measured text to drive the scrollbar.
   */
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
      // Text is painted in the element's screen space: every offset below is relative to it.
      int x0 = (int) getContentX();
      int y0 = (int) getContentY();
      // Text occupies the legacy 152px wide black region minus the text's own left inset.
      int wrapWidth = Mth.floor((STATUS_AREA_WIDTH - 4) / SCALE);
      float y;
      // Machine found: the localized machine name, otherwise the classic "Structure Found: None".
      DynamicMachine machine = controller.getFoundMachine();
      if (machine != null) {
        // The legacy name block: first line at text y=14, then 14px (2x7) steps.
        y = 7;
        for (var line : font.split(Component.literal(machine.getLocalizedName()), wrapWidth)) {
          y += 7;
          drawStatusLine(gui, font, line, x0 + 2, y0 + y);
          y += 7;
        }
        y -= 7;
      } else {
        y = 14;
        drawStatusLine(gui, font,
            Component.translatable("gui.controller.structure",
                Component.translatable("gui.controller.structure.none")).getVisualOrderText(),
            x0 + 2, y0 + y);
      }
      y += 10;
      if (controller.isPaused()) {
        for (var line : font.split(Component.translatable("gui.controller.status.redstone_stopped"), wrapWidth)) {
          y += 7;
          drawStatusLine(gui, font, line, x0 + 2, y0 + y);
          y += 7;
        }
        updateHeight(y);
        return;
      }
      var error = controller.getErrorMessage();
      if (!error.getString().isEmpty()) {
        var errorInfo = Component.translatable("gui.controller.error.info").append(error);
        for (var line : font.split(errorInfo, wrapWidth)) {
          y += 7;
          drawStatusLine(gui, font, line, x0 + 2, y0 + y);
          y += 7;
        }
      }
      var status = Component.translatable("gui.controller.status")
          .append(controller.getCraftingStatus().getUnlocMessage());
      for (var line : font.split(status, wrapWidth)) {
        y += 7;
        drawStatusLine(gui, font, line, x0 + 2, y0 + y);
        y += 7;
      }
      updateHeight(y);
    }

    /** {@code DrawerHelperClient.drawStringSized} for the pre-split visual sequences. */
    private static void drawStatusLine(GUIContext gui, net.minecraft.client.gui.Font font,
                                       net.minecraft.util.FormattedCharSequence line, float x, float y) {
      gui.pose.pushPose();
      gui.pose.translate(x, y);
      gui.pose.scale(SCALE, SCALE);
      LDFonts.drawText(gui, font, line, 0, 0, 0xFFFFFF, true);
      gui.pose.popPose();
    }

    /** Keeps the scroller's content height in sync with the drawn text (only on change). */
    private void updateHeight(float y) {
      int height = Mth.ceil(y) + 2;
      if (height != lastHeight) {
        lastHeight = height;
        lss("height", Math.max(10, height));
      }
    }
  }
}
