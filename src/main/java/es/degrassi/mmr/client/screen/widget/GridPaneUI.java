package es.degrassi.mmr.client.screen.widget;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerScreen;
import com.lowdragmc.lowdraglib2.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUIClientAccess;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollDisplay;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollerMode;
import com.lowdragmc.lowdraglib2.gui.ui.data.Vertical;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import es.degrassi.mmr.api.uistate.IBlockUIState;
import es.degrassi.mmr.client.container.MMRBusSlot;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.VisibleRows;
import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;
import es.degrassi.mmr.client.screen.widget.tabs.fill.AbstractFillTab;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.network.client.CGridChunkPacket;
import es.degrassi.mmr.common.util.GridChunkRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GridPaneUI {
  public static final int SCROLL_THRESHOLD = 36;
  public static final int GRID_BAR_GAP = 4;
  public static final int BAR_TAB_PAD = 6;
  public static final int TAB_INSET = 7;
  public static final int MAX_DEFAULT_ROWS = 5;
  private static final int SCROLL_BORDER_LR = 3;
  private static final int SCROLL_BORDER_TB = 4;
  private static final int FULL_SCREEN_MARGIN = AbstractFillTab.LEFT_TAB_HEIGHT;

  public static int defaultRows(int slots, int cols) {
    int realCols = Math.max(1, cols);
    int rows = (slots + realCols - 1) / realCols;
    return slots > SCROLL_THRESHOLD
        ? Math.clamp(SCROLL_THRESHOLD / realCols, 2, MAX_DEFAULT_ROWS)
        : rows;
  }

  public static IBlockUIState<VisibleRows> rowsStateOf(BlockUIMenuType.BlockUIHolder holder) {
    var block = holder.blockState.getBlock();
    if (!(block instanceof IBlockUIState<?> state)) {
      throw new IllegalStateException(
          "Block " + block + " must implement IBlockUIState<VisibleRows> to open its grid UI");
    }
    @SuppressWarnings("unchecked")
    var typed = (IBlockUIState<VisibleRows>) state;
    return typed;
  }

  public static int playerYAt(int gridY, int visibleRows) {
    return gridY + visibleRows * MMRMachineUI.SLOT + 10;
  }

  public static int panelHeightAt(int gridY, int visibleRows) {
    return gridY + visibleRows * MMRMachineUI.SLOT + 119;
  }

  public static void recenter(UIElement panel, int panelWidth, int panelHeight) {
    var screen = Minecraft.getInstance().screen;
    if (screen instanceof ModularUIContainerScreen containerScreen) {
      var mui = containerScreen.getMenu().getModularUI();
      ModularUIClientAccess.setScreenAndInit(mui, screen);
      containerScreen.init();
    }
  }

  public static int maxScreenRows(int gridY) {
    int windowHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
    return Math.max(4, (windowHeight - 2 * FULL_SCREEN_MARGIN - gridY - 119) / MMRMachineUI.SLOT);
  }

  public static IntSupplier screenMax(int gridY) {
    return () -> maxScreenRows(gridY);
  }

  public static int scrolledWidth(int gridX, int cols) {
    return gridX - 1 // scroller origin
        + cols * MMRMachineUI.SLOT // scroll port
        + GRID_BAR_GAP + 7 // scroller bar
        + BAR_TAB_PAD + TAB_INSET;
  }

  public static ItemSlot styledSlot(ItemHandler handler, int index) {
    ItemSlot slot = new CountItemSlot().bind(new MMRBusSlot(handler, index));
    slot.layout(layout -> {
      layout.width(MMRMachineUI.SLOT);
      layout.height(MMRMachineUI.SLOT);
      layout.paddingAll(1);
      layout.marginAll(0);
    });
    slot.style(style -> style.background(MMRMachineUI.slotTexture()));
    slot.slotStyle(s -> s.hoverOverlay(MMRMachineUI.hoverOverlayTexture()));
    return slot;
  }

  public static void addPlainGrid(
      UIElement panel, ItemHandler handler, int slots, int cols, int gridX, int gridY) {
    for (int i = 0; i < slots; i++) {
      MMRMachineUI.add(panel, styledSlot(handler, i),
          gridX - 1 + (i % cols) * MMRMachineUI.SLOT,
          gridY - 1 + (i / cols) * MMRMachineUI.SLOT);
    }
  }


  public static ScrolledGrid addScrolledGrid(
      UIElement panel, ItemHandler handler, int slots, int cols, int visibleRows,
      int gridX, int gridY, BlockPos pos, Player player) {
    ScrollerView scroller = buildScroller(panel, cols, visibleRows, gridX, gridY);
    int firstChunk = Math.max(6, cols * visibleRows);
    if (slots <= firstChunk) {
      fillScroller(scroller, handler, slots, cols);
      return new ScrolledGrid(scroller, null);
    }
    var compiler = new GridCompiler(panel, scroller, handler, slots, cols, firstChunk,
        gridX - 1, gridY - 1,
        cols * MMRMachineUI.SLOT + GRID_BAR_GAP + 7, visibleRows * MMRMachineUI.SLOT,
        pos, player);
    return new ScrolledGrid(scroller, compiler);
  }

  public record ScrolledGrid(ScrollerView scroller, @Nullable GridCompiler compiler) {
    public void resize(int rows, int height) {
      scroller.lss("height", height);
      if (compiler != null) {
        compiler.resize(rows, height);
      }
    }
  }

  public static ScrollerView buildScroller(
      UIElement panel, int cols, int visibleRows, int gridX, int gridY) {
    ScrollerView scroller = new ScrollerView();
    scroller.lss("width", cols * MMRMachineUI.SLOT + GRID_BAR_GAP + 7)
        .lss("height", visibleRows * MMRMachineUI.SLOT);
    MMRMachineUI.add(panel, scroller, gridX - 1, gridY - 1);
    scroller.getScrollerViewStyle().mode(ScrollerMode.VERTICAL).verticalScrollDisplay(ScrollDisplay.AUTO);
    scroller.viewPort.layout(layout -> layout.paddingAll(0));
    scroller.viewPort.style(style -> style.background(IGuiTexture.EMPTY));
    scroller.verticalScroller.lss("margin-left", GRID_BAR_GAP).lss("width", 7);
    scroller.verticalScroller.scrollContainer.style(style ->
        style.background(MMRMachineUI.spriteNineSlice(
            MMRMachineUI.TEX_SMALL_SCROLLER_DISABLED, SCROLL_BORDER_LR, SCROLL_BORDER_TB,
            SCROLL_BORDER_LR, SCROLL_BORDER_TB)));
    scroller.verticalScroller.scrollBar.buttonStyle(style -> {
      var thumb = MMRMachineUI.spriteNineSlice(
          MMRMachineUI.TEX_SMALL_SCROLLER, SCROLL_BORDER_LR, SCROLL_BORDER_TB,
          SCROLL_BORDER_LR, SCROLL_BORDER_TB);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    return scroller;
  }

  public static void fillScroller(ScrollerView scroller, ItemHandler handler, int slots, int cols) {
    for (int i = 0; i < slots; i++) {
      scroller.viewContainer.addChild(MMRMachineUI.at(styledSlot(handler, i),
          (i % cols) * MMRMachineUI.SLOT,
          (i / cols) * MMRMachineUI.SLOT));
    }
  }

  public static final class GridCompiler extends UIElement implements GridChunkRegistry.Sink {
    private final ItemHandler handler;
    private final ScrollerView scroller;
    private final int slots;
    private final int cols;
    private final int chunkSize;
    private final BlockPos blockPos;
    private final Label progress;
    private final List<MMRBusSlot> busSlots = new ArrayList<>();
    private int placeholderRows;
    private int placeholderHeight;
    private final List<UIElement> placeholders = new ArrayList<>();
    private int placed;
    private int nextChunk = 1;
    private boolean finished;

    private GridCompiler(UIElement panel, ScrollerView scroller, ItemHandler handler,
                         int slots, int cols, int firstChunk,
                         int x, int y, int width, int height,
                         BlockPos blockPos, Player player) {
      this.scroller = scroller;
      this.handler = handler;
      this.slots = slots;
      this.cols = cols;
      this.blockPos = blockPos;
      this.chunkSize = Math.max(Math.max(6, firstChunk), (slots + 47) / 48);
      this.placed = compileChunk(0);
      lss("position", "absolute")
          .lss("left", x).lss("top", y)
          .lss("width", width).lss("height", height);
      this.placeholderRows = height / MMRMachineUI.SLOT;
      this.placeholderHeight = height;
      this.progress = new Label();
      progress.setText(TabTexts.compileHud.text(
          TabTexts.compileHudTitle.text(), placed, slots));
      progress.layout(layout -> {
        layout.width(width - 7 - GRID_BAR_GAP);
        layout.height(height);
      });
      progress.textStyle(ts -> ts
          .textAlignHorizontal(Horizontal.CENTER)
          .textAlignVertical(Vertical.CENTER)
          .textColor(0xFFFFFFFF)
          .textShadow(true));
      progress.style(style -> style.background(new ColorRectTexture(0x60000000)));
      buildPlaceholders();

      MMRMachineUI.add(panel, this, x, y);
      if (placed < slots) {
        addEventListener(UIEvents.TICK, event -> clientTick());
        handler.setCompiling(true);
      } else {
        finished = true;
        unlockSlots();
        removeSelf();
      }
      if (player instanceof ServerPlayer serverPlayer) {
        GridChunkRegistry.register(serverPlayer.getUUID(), this);
      }
    }

    private void buildPlaceholders() {
      placeholders.forEach(UIElement::removeSelf);
      placeholders.clear();
      for (int row = 0; row < placeholderRows; row++) {
        for (int column = 0; column < cols; column++) {
          var placeholder = new UIElement();
          placeholder.lss("position", "absolute")
              .lss("left", column * MMRMachineUI.SLOT)
              .lss("top", row * MMRMachineUI.SLOT)
              .lss("width", MMRMachineUI.SLOT)
              .lss("height", MMRMachineUI.SLOT);
          placeholder.style(style -> style.background(IGuiTexture.dynamic(() ->
              SpriteTexture.of(MMRMachineUI.TEX_BACKGROUND.toString()).setBorder(4))));
          var frame = new UIElement();
          frame.lss("position", "absolute")
              .lss("left", 0).lss("top", 0)
              .lss("width", MMRMachineUI.SLOT)
              .lss("height", MMRMachineUI.SLOT);
          frame.style(style -> style.background(MMRMachineUI.slotTexture()));
          placeholder.addChild(frame);
          addChild(placeholder);
          placeholders.add(placeholder);
        }
      }
      addChild(progress);
      placeholders.add(progress);
    }

    public void resize(int rows, int height) {
      this.placeholderRows = Math.max(1, rows);
      this.placeholderHeight = height;
      lss("height", height);
      progress.layout(layout -> {
        layout.width(getContentWidth() - 7 - GRID_BAR_GAP);
        layout.height(height);
      });
      buildPlaceholders();
    }

    private void clientTick() {
      if (finished) {
        return;
      }
      int chunk = nextChunk;
      placed = compileChunk(chunk);
      nextChunk++;
      ClientPacketDistributor.sendToServer(new CGridChunkPacket(blockPos, chunk));
      if (placed >= slots) {
        finished = true;
        unlockSlots();
        removeSelf();
      } else {
        progress.setText(TabTexts.compileHud.text(
            TabTexts.compileHudTitle.text(), placed, slots));
      }
    }

    private int compileChunk(int chunk) {
      int start = chunk * chunkSize;
      if (start >= slots) {
        return placed;
      }
      int end = Math.min(slots, start + chunkSize);
      for (int i = start; i < end; i++) {
        var widget = styledSlot(handler, i);
        var slot = widget.getSlot();
        if (slot instanceof MMRBusSlot busSlot) {
          busSlot.setLocked(true);
          busSlots.add(busSlot);
        }
        scroller.viewContainer.addChild(MMRMachineUI.at(widget,
            (i % cols) * MMRMachineUI.SLOT,
            (i / cols) * MMRMachineUI.SLOT));
      }
      return end;
    }

    private void unlockSlots() {
      busSlots.forEach(MMRBusSlot::unlock);
      busSlots.clear();
      handler.setCompiling(false);
    }

    @Override
    public BlockPos pos() {
      return blockPos;
    }

    @Override
    public boolean acceptChunk(int chunk) {
      if (chunk != nextChunk || finished) {
        return false;
      }
      placed = compileChunk(chunk);
      nextChunk++;
      if (placed >= slots) {
        finished = true;
        unlockSlots();
        removeSelf();
      }
      return true;
    }
  }
}
