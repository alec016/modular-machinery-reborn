package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.texture.Icons;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.TextWrap;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Dialog;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvent;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.util.WindowDragHelper;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.MMRWidgets.IconButton;
import es.degrassi.mmr.common.crafting.helper.CraftingStatus;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.manager.crafting.MachineProcessorCore;
import es.degrassi.mmr.common.manager.crafting.MachineStatus;
import es.degrassi.mmr.common.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * LDLib2 replacements of the legacy popups ({@code ConfirmationPopup}, {@code CoreGridPopupScreen},
 * {@code CorePopupScreen}). Each is a {@link Dialog} whose overlay is fully styled with the legacy
 * {@code background.png} nine-slice popup texture and the classic button sprites; popups stay open
 * on outside clicks (legacy {@code PopupScreen} semantics) and close on ESC.
 */
public final class MMRPopups {

  /** Legacy default confirm/cancel texts of {@code ConfirmationPopup}. */
  public static final Component CONFIRM = Component.translatable("mmr.gui.popup.confirm").withStyle(ChatFormatting.GREEN);
  public static final Component CANCEL = Component.translatable("mmr.gui.popup.cancel").withStyle(ChatFormatting.RED);

  private MMRPopups() {}

  // ------------------------------------------------------------------
  // Base popup window
  // ------------------------------------------------------------------

  /**
   * Builds a popup window: a {@code width x height} content area over the legacy nine-slice
   * {@code background.png}, no auto-close on outside clicks, ESC closes (topmost popup behaviour).
   *
   * <p>The dialog is <em>not</em> shown here: {@link Dialog#show} hides every empty container at
   * show time, so every popup must add its children first and then call {@code show(parent)}
   * (see {@link #confirmation}, {@link #coreGrid} and {@link #coreDetail}). Showing an empty
   * content container is exactly the "fully transparent popup" bug.
   */
  public static Dialog build(UIElement parent, int width, int height) {
    Dialog dialog = new Dialog();
    dialog.setAutoClose(false);
    dialog.setClickOutsideClose(false);
    dialog.overlay.layout(layout -> {
      layout.width(width);
      layout.height(height);
    });
    dialog.contentContainer.layout(layout -> {
      layout.widthPercent(100);
      layout.heightPercent(100);
      layout.gapAll(0);
      layout.paddingAll(0);
    });
    // The real nine-slice: the legacy background.png blitted with its 4px borders and corner
    // pillars preserved. A plain sprite stretch (or repeat) looked wrong at any other size.
    dialog.contentContainer.style(style ->
        style.background(MMRMachineUI.nineSliceTexture(MMRMachineUI.TEX_BACKGROUND)));
    // ESC closes this popup, mirroring the old popup stack behaviour.
    dialog.addEventListener(UIEvents.KEY_DOWN, event -> {
      if (event.keyCode == GLFW.GLFW_KEY_ESCAPE) {
        dialog.close();
      }
    });
    makeDraggable(dialog, width);
    return dialog;
  }

  // ------------------------------------------------------------------
  // Popup dragging: the classic "title bar" grab, minus the X / back corner
  // ------------------------------------------------------------------

  /** Height of the draggable top strip of every popup. */
  private static final int DRAG_STRIP = 22;

  /**
   * Turns the popup into a movable window: pressing on its top strip starts a drag that moves the
   * whole overlay. Presses anywhere else (buttons, grid cells, ...) never trigger it, and the
   * top-right rectangle stays reserved for the "X" close button and the core detail back button.
   */
  private static void makeDraggable(Dialog dialog, int width) {
    UIElement overlay = dialog.overlay;
    overlay.addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (!insideDragStrip(overlay, event, width)) {
        return;
      }
      // Freeze the (flex-centred) position as an explicit offset so the window is picked up
      // exactly where it visually is while the drag updates re-anchor left/top on every step.
      // The string stylesheet keeps this taffy-free: "position: absolute" wins over the theme.
      float startX = overlay.getLayoutX();
      float startY = overlay.getLayoutY();
      overlay.lss("position", "absolute").lss("left", startX).lss("top", startY);
      overlay.startDrag(new WindowDragHelper.DragMove(startX, startY), Icons.MOVE)
          .setDragTexture(-6, -6, 12, 12);
      event.stopPropagation();
    });
    overlay.addEventListener(UIEvents.DRAG_SOURCE_UPDATE, event -> {
      if (event.dragHandler.draggingObject instanceof WindowDragHelper.DragMove move) {
        var delta = overlay.getLocalMouseNormal(event.x - event.dragStartX, event.y - event.dragStartY);
        overlay.getLayout().left(move.startX() + delta.x).top(move.startY() + delta.y);
      }
    });
  }

  /** True when the press lands on the top strip and off the top-right X / back reserve. */
  private static boolean insideDragStrip(UIElement overlay, UIEvent event, int width) {
    Vector2f local = overlay.worldToLocalLayoutOffset(new Vector2f(event.x, event.y));
    if (local.y < 0 || local.y > DRAG_STRIP) {
      return false;
    }
    // The 27px top-right corner hosts the "X" (and the core detail back button).
    return !(local.x >= width - 27 && local.x <= width && local.y <= DRAG_STRIP);
  }

  /** Adds the traditional "X" close button at the top right of a popup. */
  public static void addCloseButton(Dialog dialog, int width) {
    Button close = MMRWidgets.spriteButton(dialog.contentContainer, Component.literal("X"),
        width - 25, 5, 20, 20, event -> dialog.close());
    close.style(style -> style.tooltips(Component.translatable("mmr.gui.button.close")));
  }

  /** A popup text label: text word-wraps as a function of the given width. */
  private static Label popupLabel(Dialog dialog, Component text, int x, int y, int width) {
    Label label = new Label();
    label.setText(text);
    label.layout(layout -> {
      layout.width(width);
      layout.gapAll(0);
      layout.paddingAll(0);
    });
    label.textStyle(style -> {
      style.textWrap(TextWrap.WRAP); // wrap when the text reaches the end of the popup width
      style.adaptiveHeight(true);    // the label follows the wrapped line count
      style.adaptiveWidth(false);
    });
    return MMRMachineUI.add(dialog.contentContainer, label, x, y);
  }

  // ------------------------------------------------------------------
  // ConfirmationPopup parity
  // ------------------------------------------------------------------

  /**
   * A legacy {@code ConfirmationPopup}: optional "X" button, optional title, centered text lines and
   * the green confirm / red cancel 75x20 sprite buttons side by side on the bottom row.
   *
   * @return the dialog, so callers can reference it further (legacy nested-popup flows)
   */
  public static Dialog confirmation(UIElement parent, int width, int height,
                                    @Nullable Component title, List<Component> text,
                                    @Nullable Component confirmText, @Nullable Component cancelText,
                                    @Nullable Runnable onConfirm, @Nullable Runnable onCancel,
                                    boolean closeButton) {
    Dialog dialog = build(parent, width, height);
    int y = 5;
    if (closeButton) {
      addCloseButton(dialog, width);
      y = 25;
    }
    if (title != null) {
      popupLabel(dialog, title, 0, y, width);
      y += 20; // 10 text + the legacy row paddings
    }
    if (!text.isEmpty()) {
      // Legacy cell paddings; the wrapped label grows below while the buttons stay bottom-anchored.
      popupLabel(dialog, joined(text), 5, y, width - 10);
    }
    MMRWidgets.spriteButton(dialog.contentContainer,
        confirmText == null ? CONFIRM : confirmText, 5, height - 25, 75, 20,
        event -> {
          if (onConfirm != null) {
            onConfirm.run();
          }
          dialog.close();
        });
    MMRWidgets.spriteButton(dialog.contentContainer,
        cancelText == null ? CANCEL : cancelText, 5 + 75 + 10, height - 25, 75, 20,
        event -> {
          if (onCancel != null) {
            onCancel.run();
          }
          dialog.close();
        });
    // All children are in place now — showing the dialog re-enables its containers.
    dialog.show(parent);
    return dialog;
  }

  private static Component joined(List<Component> text) {
    MutableComponent joined = Component.empty();
    for (Component line : text) {
      joined.append("\n").append(line);
    }
    return joined;
  }

  // ------------------------------------------------------------------
  // Core grid popup (legacy 220x180 CoreGridPopupScreen)
  // ------------------------------------------------------------------

  /** Opens the core overview grid ({@code CoreGridPopupScreen}: 50 cores per page + pager). */
  public static void coreGrid(UIElement parent, MachineControllerEntity entity) {
    Dialog dialog = build(parent, 220, 180);
    int[] page = {1};
    rebuildCoreGrid(dialog, parent, entity, page);
    // The first populate must happen before show(), otherwise the dialog stays transparent.
    dialog.show(parent);
  }

  /** Full rebuild per page change (like the legacy {@code rebuildWidgets()}). */
  private static void rebuildCoreGrid(Dialog dialog, UIElement uiRoot,
                                      MachineControllerEntity entity, int[] page) {
    dialog.contentContainer.clearAllChildren();
    addCloseButton(dialog, 220);
    MachineStatus status = entity.getStatus();
    if (status.isPaused() || status.isMissingStructure()) {
      popupLabel(dialog, Component.translatable(status.isPaused()
              ? "mmr.gui.paused" : "mmr.gui.missing_structure"),
          0, 60, 220).setId("core_status");
    } else {
      int pages = pagesNumber(entity);
      int current = Math.max(1, Math.min(page[0], pages));
      List<MachineProcessorCore> cores = entity.getPages().get(current);
      if (cores != null) {
        for (int i = 0; i < cores.size(); i++) {
          MachineProcessorCore core = cores.get(i);
          int coreIndex = core.getCore();
          IconButton coreButton = MMRWidgets.iconButton(dialog.contentContainer,
              core.isActive() ? Icon.CORE_ACTIVE : Icon.CORE_INACTIVE,
              21 + (i % 10) * 18, 30 + (i / 10) * 20,
              event -> {
                dialog.close();
                coreDetail(uiRoot, entity, current, coreIndex);
              }, null);
          coreButton.style(style ->
              style.tooltips(Component.translatable("mmr.gui.core.button", core.getCore())));
          coreButton.setId("core_cell");
        }
      }
    }
    // Pager row, anchored at the bottom like the legacy layout.
    MMRWidgets.spriteButton(dialog.contentContainer, Component.literal("<"), 36, 150, 20, 20,
        event -> {
          if (page[0] > 1) {
            page[0]--;
            rebuildCoreGrid(dialog, uiRoot, entity, page);
          }
        }).style(style -> style.tooltips(Component.translatable("mmr.gui.button.page.prev")));
    MMRWidgets.spriteCaption(dialog.contentContainer,
        Component.literal(Math.min(page[0], pagesNumber(entity)) + "/" + pagesNumber(entity)),
        88, 155, 44, 10);
    MMRWidgets.spriteButton(dialog.contentContainer, Component.literal(">"), 164, 150, 20, 20,
        event -> {
          if (page[0] < pagesNumber(entity)) {
            page[0]++;
            rebuildCoreGrid(dialog, uiRoot, entity, page);
          }
        }).style(style -> style.tooltips(Component.translatable("mmr.gui.button.page.next")));
  }

  private static int pagesNumber(MachineControllerEntity entity) {
    if (entity.getPages() == null) {
      return 1;
    }
    int size = entity.getPages().size();
    return Math.max(1, size);
  }

  // ------------------------------------------------------------------
  // Core detail popup (legacy 180x96 CorePopupScreen)
  // ------------------------------------------------------------------

  /** Opens the legacy {@code CorePopupScreen}: back button, core status lines, progress label. */
  public static void coreDetail(UIElement uiRoot, MachineControllerEntity entity, int page, int coreIndex) {
    Dialog dialog = build(uiRoot, 180, 96);
    MachineProcessorCore core = entity.getProcessor().cores().get(coreIndex - 1);
    IconButton back = MMRWidgets.iconButton(dialog.contentContainer, Icon.BACK, 159, 5,
        event -> {
          dialog.close();
          coreGrid(uiRoot, entity);
        }, null);
    back.style(style -> style.tooltips(Component.translatable("mmr.gui.button.back")));
    popupLabel(dialog, Component.translatable("mmr.core.active." + core.isActive()),
        5, 24, 170);
    popupLabel(dialog, Component.translatable("mmr.core.number", core.getCore()),
        5, 38, 170);
    if (core.isActive()) {
      MMRMachineUI.syncedLabel(dialog.contentContainer, () -> {
        MutableComponent status = Component.translatable("gui.controller.status");
        if (core.hasActiveRecipe()) {
          String perc = Utils.formatWithPercentage(
              (int) Math.round(Math.min(1f, core.getRecipeProgressTime()) * 100F));
          status.append(Component.translatable("gui.controller.status.crafting.progress", perc));
        } else {
          status.append(CraftingStatus.NO_RECIPE.getUnlocMessage());
        }
        return status;
      }, 5, 52).lss("width", 170);
    }
    // All children are in place now — showing the dialog re-enables its containers.
    dialog.show(uiRoot);
  }
}
