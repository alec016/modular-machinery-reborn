package es.degrassi.mmr.client.screen.widget.buttons;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.tabs.FillTab;
import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public final class VisibleRowsButton extends UIElement {
  public static final int TAB_SPOT_BOTTOM = FillTab.LEFT_TAB_HEIGHT + 1;

  private final Consumer<VisibleRows> onChange;
  private final IntSupplier screenMaxRows;
  private VisibleRows state;

  public VisibleRowsButton(UIElement parent, int x, int y, Consumer<VisibleRows> onChange, IntSupplier screenMaxRows) {
    this(parent, x, y, onChange, screenMaxRows, null);
  }

  public VisibleRowsButton(UIElement parent, int x, int y, Consumer<VisibleRows> onChange, IntSupplier screenMaxRows, VisibleRows initialState) {
    this.onChange = onChange;
    this.screenMaxRows = screenMaxRows;
    this.state = initialState == null ? VisibleRows.SMALL : initialState;
    lss("width", FillTab.LEFT_TAB_WIDTH).lss("height", FillTab.LEFT_TAB_HEIGHT);
    addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (event.button == 0) {
        event.stopPropagation();
        UISoundUtils.playButtonClickSound();
        state = state.next();
        if (this.onChange != null) {
          this.onChange.accept(state);
        }
      }
    });
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      event.hoverTooltips = HoverTooltips.create(tooltips().toArray());
    });
    MMRMachineUI.add(parent, this, x, y);
  }

  public VisibleRows current() {
    return state;
  }

  public int resolved() {
    return state.resolve(screenMaxRows);
  }

  private List<Object> tooltips() {
    var lines = new ArrayList<>(2);
    lines.add(state.isFullScreen()
        ? ButtonsTexts.rowsScreenMax.text(resolved())
        : ButtonsTexts.rowsVisible.text(state.fixedRows()));
    lines.add(TabTexts.rowsCycleHint.text());
    return lines;
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    if (!(context instanceof GUIContext gui)) {
      return;
    }
    int x = (int) getContentX();
    int y = (int) getContentY();
    if (isHover()) {
      gui.drawTexture(MMRMachineUI.sprite(FillTab.TEX_TAB_LEFT_HOVERED, 0, 0, 29, FillTab.LEFT_TAB_HEIGHT),
          x, y, 29, FillTab.LEFT_TAB_HEIGHT);
    } else {
      gui.drawTexture(MMRMachineUI.sprite(FillTab.TEX_TAB_LEFT, 0, 0, FillTab.LEFT_TAB_WIDTH, FillTab.LEFT_TAB_HEIGHT),
          x, y, FillTab.LEFT_TAB_WIDTH, FillTab.LEFT_TAB_HEIGHT);
    }
    Icon icon = byState();
    if (icon == null) return;
    gui.drawTexture(MMRMachineUI.sprite(icon.TEXTURE, icon.x, icon.y, icon.width, icon.height),
        x + 5, y + 5, icon.width, icon.height);
  }

  private Icon byState() {
    if (state == VisibleRows.SMALL) return Icon.TERMINAL_STYLE_SMALL;
    else if (state == VisibleRows.MEDIUM) return Icon.TERMINAL_STYLE_MEDIUM;
    else if (state == VisibleRows.TALL) return Icon.TERMINAL_STYLE_TALL;
    else if (state == VisibleRows.FULL) return Icon.TERMINAL_STYLE_FULL;
    else return null;
  }
}
