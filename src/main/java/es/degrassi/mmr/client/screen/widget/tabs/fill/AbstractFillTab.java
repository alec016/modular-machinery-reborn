package es.degrassi.mmr.client.screen.widget.tabs.fill;

import com.lowdragmc.lowdraglib2.client.font.LDFonts;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollDisplay;
import com.lowdragmc.lowdraglib2.gui.ui.data.ScrollerMode;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Slider;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import com.lowdragmc.lowdraglib2.gui.util.UISoundUtils;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.client.Icon;
import es.degrassi.mmr.api.entity.IPickResultEntity;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.util.NumberFormats;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.screen.widget.buttons.SpriteButton;
import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;
import es.degrassi.mmr.client.screen.widget.tabs.fill.kind.Kind;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.Pick;
import es.degrassi.mmr.common.network.client.CFillHatchRequestPacket;
import es.degrassi.mmr.common.util.TextureSizeHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractFillTab<
    HANDLER,
    FILTER,
    PICK extends Pick
> extends UIElement {
  public static final Identifier TEX_TAB_LEFT = ModularMachineryReborn.rl("textures/gui/widget/base_tab_left.png");
  public static final Identifier TEX_TAB_LEFT_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_left.png");
  public static final int FILL_OPEN_WIDTH = 132;
  public static final int FILL_OPEN_HEIGHT = 170;
  public static final int FILL_SIMPLE_OPEN_HEIGHT = 90;
  public static final int FILL_DURABILITY_OPEN_HEIGHT = 200;
  public static final int LEFT_TAB_WIDTH = TextureSizeHelper.getWidth(TEX_TAB_LEFT); // should be 27
  public static final int LEFT_TAB_HEIGHT = TextureSizeHelper.getHeight(TEX_TAB_LEFT); // should be 28
  public static final int LEFT_TAB_X = -LEFT_TAB_WIDTH;
  protected static final int BODY_LEFT = -FILL_OPEN_WIDTH;
  protected static final int CX = BODY_LEFT + LEFT_TAB_WIDTH + 4;
  protected static final int BUTTON_RIGHT_X = -64 + LEFT_TAB_WIDTH;
  protected static final int CLOSE_X = 15;
  protected static final int COLS = 6;
  protected static final int VISIBLE_ROWS = 3;
  protected static final int GRID_W = COLS * MMRMachineUI.SLOT + 4 + 7;
  protected static final Icon TAB_ICON = Icon.WHITE_ARROW_DOWN;
  protected static final int SIMPLE_BUTTON_Y = 66;
  protected static final int PICKER_LABEL_Y = 60;
  protected static final int PICKER_FIELD_Y = 70;
  protected static final int PICKER_GRID_Y = 88;
  protected static final int PICKER_BUTTON_Y = 144;
  protected static final int BLOCKED_WASH = 0x900C0C0C;
  protected static final int BLOCKED_TEXT = 0xFFFF5555;
  protected static final long OPEN_MS = 240L;
  protected static final int SCROLL_BORDER_LR = 3;
  protected static final int SCROLL_BORDER_TB = 4;
  protected static final int SELECTED_TINT = 0x60FFFFFF;

  @Nullable
  protected final Player player;
  protected final BlockEntity entity;
  protected final Kind<PICK, FILTER> kind;
  protected final Supplier<HANDLER> handler;
  protected final LongSupplier space;
  @Nullable
  protected final Predicate<FILTER> filter;

  private boolean opened = false;
  private long animStart = -1L;
  private boolean contentBuilt = false;
  protected boolean syncing = false;
  private long amount = 1;

  @Nullable
  protected Component blockReason;
  @Nullable
  private TextField amountField;
  @Nullable
  private Slider slider;
  @Nullable
  private Button confirmButton;
  @Nullable
  protected BlockOverlay overlay;
  @Nullable
  protected ScrollerView grid;
  @Nullable
  protected PICK selected;

  protected AbstractFillTab(
      @Nullable Player player,
      BlockEntity entity,
      Kind<PICK, FILTER> kind,
      @Nullable Supplier<HANDLER> handler,
      @Nullable LongSupplier simpleSpace,
      @Nullable Predicate<FILTER> filter
  ) {
    this.player = player;
    this.entity = entity;
    this.kind = kind;
    this.handler = handler;
    this.space = simpleSpace;
    this.filter = filter;
    addEventListener(UIEvents.MOUSE_DOWN, event -> {
      if (event.target == this && !opened) {
        UISoundUtils.playButtonClickSound();
        open();
      }
    });
    addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      if (!opened && event.target == this) {
        event.hoverTooltips = HoverTooltips.create(TabTexts.tooltipOpenFill.text());
      }
    });
    lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
  }

  @OverridingMethodsMustInvokeSuper
  protected void open() {
    opened = true;
    amount = 1;
    animStart = System.currentTimeMillis();
  }

  protected final void close() {
    if (!opened) return;
    opened = false;
    animStart = System.currentTimeMillis();
    if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
      clearAllChildren();
      amountField = null;
      slider = null;
      confirmButton = null;
      overlay = null;
      grid = null;
      kind.displayCache = null;
      blockReason = null;
      additionalClear();
    }
    contentBuilt = false;
  }

  protected void additionalClear() {}

  protected float currentProgress() {
    if (animStart < 0L) return opened ? 1 : 0;
    float t = (System.currentTimeMillis() - animStart) / (float) OPEN_MS;
    return opened ? Math.min(1f, t) : 1f - Math.min(1f, t);
  }

  protected int openHeight() {
    return kind.height();
  }

  @Override
  protected void drawBackgroundAdditional(IGUIContext context) {
    if (!(context instanceof GUIContext gui)) {
      return;
    }
    int x = (int) getContentX();
    int y = (int) getContentY();
    float progress = currentProgress();
    if (animStart >= 0L) {
      if (opened && progress >= 1f) {
        animStart = -1L;
        progress = 1f;
        buildContent();
      } else if (!opened && progress <= 0f) {
        animStart = -1L;
        progress = 0f;
      }
    }
    if (opened && animStart < 0L) {
      refreshBlocked();
    }
    if (!opened && animStart < 0L) {
      if (isHover()) {
        gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_LEFT_HOVERED, 0, 0, 29, LEFT_TAB_HEIGHT),
            x, y, 29, LEFT_TAB_HEIGHT);
      } else {
        gui.drawTexture(MMRMachineUI.sprite(TEX_TAB_LEFT, 0, 0, LEFT_TAB_WIDTH, LEFT_TAB_HEIGHT),
            x, y, LEFT_TAB_WIDTH, LEFT_TAB_HEIGHT);
      }
      gui.drawTexture(MMRMachineUI.sprite(TAB_ICON.TEXTURE, TAB_ICON.x, TAB_ICON.y,
          TAB_ICON.width, TAB_ICON.height), x + 5, y + 5, TAB_ICON.width, TAB_ICON.height);
      return;
    }
    int openHeight = openHeight();
    int width = Math.round((FILL_OPEN_WIDTH) * progress);
    int height = Math.round(LEFT_TAB_HEIGHT + (openHeight - LEFT_TAB_HEIGHT) * progress);
    int left = x - (width - LEFT_TAB_WIDTH);
    gui.drawTexture(MMRMachineUI.nineSliceTexture(MMRMachineUI.TEX_BACKGROUND), left, y, width, height);
  }

  protected final void buildContent() {
    contentBuilt = true;
    if (entity.getLevel() == null || !entity.getLevel().isClientSide()) return;
    boolean picker = kind.hasPick();
    IconButton.smallIconButton(this, Icon.CLEAR, CLOSE_X, 3, event -> {
      event.stopPropagation();
      close();
    }, () -> List.of(TabTexts.tooltipCloseFill.text())).style(style -> style.zIndex(20));
    MMRMachineUI.captionLabel(this, TabTexts.fillAmount.text(), CX, 13);
    amountField = new TextField();
    amountField.setNumbersOnlyLong(1, 1);
    amountField.setTextResponder(this::onAmountTyped);
    amountField.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      if (event.target == amountField) {
        event.hoverTooltips = amountTooltips();
      }
    });
    MMRMachineUI.add(this, amountField.lss("width", 124).lss("height", 14), CX, 23);
    slider = MMRMachineUI.withScrollerThumb(new Slider.Horizontal());
    slider.setRange(1f, 1f);
    slider.setOnValueChanged(this::onSliderMoved);
    slider.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
      if (event.target == slider) {
        event.hoverTooltips = amountTooltips();
      }
    });
    MMRMachineUI.add(this, slider.lss("width", 120).lss("height", 16), CX + 2, 40);
    additionalFields();
    if (picker) {
      addPicker();
    } else {
      long max = Math.max(1, currentMax());
      amount = 1;
      amountField.setNumbersOnlyLong(1, max);
      amountField.setText("1");
      slider.setRange(1f, (float) max);
      slider.setValue(1f);
      confirmButton = SpriteButton.spriteButton(this,
          TabTexts.fillConfirm.text(), CX, SIMPLE_BUTTON_Y - 2, 60, 20,
          event -> {
            event.stopPropagation();
            confirm();
          });
      SpriteButton.spriteButton(this, TabTexts.fillCancel.text(),
          BUTTON_RIGHT_X, SIMPLE_BUTTON_Y - 2, 60, 20,
          event -> {
            event.stopPropagation();
            close();
          });
    }
    overlay = new BlockOverlay();
  }

  // ------------------------------------------------------------------
  // picking + amount plumbing
  // ------------------------------------------------------------------

  private void onAmountTyped(String text) {
    if (syncing) {
      return;
    }
    syncing = true;
    amount = parseAmount(text);
    if (slider != null) {
      slider.setValue((float) amount);
    }
    syncing = false;
  }

  private void onSliderMoved(float value) {
    if (syncing) {
      return;
    }
    syncing = true;
    amount = Math.max(1, (long) value);
    if (amountField != null && !String.valueOf(amount).equals(amountField.getValue())) {
      amountField.setText(String.valueOf(amount));
    }
    syncing = false;
  }

  private long parseAmount(String text) {
    long parsed;
    try {
      parsed = Long.parseLong(text.trim());
    } catch (NumberFormatException exception) {
      parsed = 1;
    }
    return Mth.clamp(parsed, 1, Math.max(1, currentMax()));
  }

  protected abstract long currentMax();

  private @Nullable HoverTooltips amountTooltips() {
    if (amountField == null) {
      return null;
    }
    String raw = amountField.getValue();
    if (raw.isBlank() || amount <= 0) {
      return null;
    }
    var lines = new ArrayList<>(3);
    lines.add(Component.literal(NumberFormats.grouped(amount)));
    lines.add(Component.literal("≈ " + NumberFormats.compact(amount)));
    long max = Math.max(1, currentMax());
    lines.add(TabTexts.fillMax.text(
        NumberFormats.grouped(max) + " (≈ " + NumberFormats.compact(max) + ")"));
    return HoverTooltips.create(lines.toArray());
  }

  protected abstract void additionalFields();

  protected void addPicker() {
    defaultPicket(PICKER_LABEL_Y, PICKER_FIELD_Y, PICKER_GRID_Y, PICKER_BUTTON_Y);
  }

  protected void defaultPicket(int labelY, int fieldY, int gridY, int buttonY) {
    MMRMachineUI.captionLabel(this, TabTexts.fillSearch.text(), CX, labelY);
    TextField searchField = new TextField();
    searchField.setAnyString();
    searchField.setTextResponder(this::onSearchTyped);
    searchField.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> event.hoverTooltips =
        HoverTooltips.create(TabTexts.fillSearchHintMod.text(), TabTexts.fillSearchHintTag.text()));
    MMRMachineUI.add(this, searchField.lss("width", 124).lss("height", 14), CX, fieldY);
    grid = new ScrollerView();
    grid.lss("width", GRID_W).lss("height", VISIBLE_ROWS * MMRMachineUI.SLOT);
    MMRMachineUI.add(this, grid, CX, gridY);
    grid.getScrollerViewStyle().mode(ScrollerMode.VERTICAL).verticalScrollDisplay(ScrollDisplay.AUTO);
    grid.viewPort.layout(layout -> layout.paddingAll(0));
    grid.viewPort.style(style -> style.background(IGuiTexture.EMPTY));
    grid.verticalScroller.lss("margin-left", 4).lss("width", 7);
    grid.verticalScroller.scrollContainer.style(style ->
        style.background(MMRMachineUI.spriteNineSlice(
            MMRMachineUI.TEX_SMALL_SCROLLER_DISABLED, SCROLL_BORDER_LR, SCROLL_BORDER_TB,
            SCROLL_BORDER_LR, SCROLL_BORDER_TB)));
    grid.verticalScroller.scrollBar.buttonStyle(style -> {
      var thumb = MMRMachineUI.spriteNineSlice(
          MMRMachineUI.TEX_SMALL_SCROLLER, SCROLL_BORDER_LR, SCROLL_BORDER_TB,
          SCROLL_BORDER_LR, SCROLL_BORDER_TB);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    confirmButton = SpriteButton.spriteButton(this,
        TabTexts.fillConfirm.text(), CX, buttonY, 60, 20,
        event -> {
          event.stopPropagation();
          confirm();
        });
    SpriteButton.spriteButton(this, TabTexts.fillCancel.text(),
        BUTTON_RIGHT_X, buttonY, 60, 20,
        event -> {
          event.stopPropagation();
          close();
        });
    rebuild("");
  }

  // ------------------------------------------------------------------
  // candidate list
  // ------------------------------------------------------------------

  private void onSearchTyped(String query) {
    rebuild(query);
  }

  private void rebuild(String query) {
    if (grid == null) {
      return;
    }
    List<PICK> shown = candidates(query);
    grid.clearAllScrollViewChildren();
    if (shown.isEmpty()) {
      return;
    }
    int rows = (shown.size() + COLS - 1) / COLS;
    grid.viewContainer.lss("width", COLS * MMRMachineUI.SLOT)
        .lss("height", rows * MMRMachineUI.SLOT);
    for (int i = 0; i < shown.size(); i++) {
      grid.addScrollViewChild(MMRMachineUI.at(new PickEntry(shown.get(i)),
          (i % COLS) * MMRMachineUI.SLOT, (i / COLS) * MMRMachineUI.SLOT));
    }
  }

  private List<PICK> candidates(String query) {
    String trimmed = query.trim();
    boolean byMod = trimmed.startsWith("@");
    boolean byTag = trimmed.startsWith("#");
    String needle = (byMod || byTag ? trimmed.substring(1) : trimmed).trim().toLowerCase(Locale.ROOT);
    List<PICK> result = new ArrayList<>();
    kind.candidates(result, byMod, byTag, needle, entity, player);
    return result;
  }

  protected void confirm() {
    long max = currentMax();
    if (max > 0) {
      if (entity instanceof IPickResultEntity) ClientPacketDistributor.sendToServer(new CFillHatchRequestPacket(entity.getBlockPos(), encodePick()));
    }
    close();
  }

  @OverridingMethodsMustInvokeSuper
  protected CompoundTag encodePick() {
    CompoundTag tag = new CompoundTag();
    tag.putLong("amount", parseAmount(amountField == null ? "1" : amountField.getValue()));
    return tag;
  }

  protected final void refreshBlocked() {
    if (entity.getLevel() == null || !entity.getLevel().isClientSide()) return;
    boolean picker = kind.hasPick();
    boolean unselected = picker && selected == null;
    boolean hatchFull = isFull();
    boolean full = unselected ? hatchFull : currentMax() <= 0;
    boolean compiling = isCompiling();
    boolean blocked = shouldBlock(compiling, full);
    blockReason = blocked ? (compiling ? TabTexts.fillReasonCompiling.text() : fullReason()) : null;
    if (overlay != null) {
      if (blocked && overlay.getParent() == null) {
        MMRMachineUI.add(this, overlay, BODY_LEFT + LEFT_TAB_WIDTH, 0);
      } else if (!blocked) {
        overlay.removeSelf();
      }
      overlay.setActive(blocked);
    }

    boolean disable = unselected || blocked;
    if (amountField != null) amountField.setActive(!disable);
    if (slider != null) slider.setActive(!disable);
    if (confirmButton != null) confirmButton.setActive(!disable);
    updateActive(disable);
  }

  protected abstract void updateActive(boolean disable);

  @OverridingMethodsMustInvokeSuper
  protected boolean shouldBlock(boolean compiling, boolean full) {
    return compiling || full;
  }

  protected boolean isFull() {
    return false;
  }

  protected boolean isCompiling() {
    return false;
  }

  private Component fullReason() {
    return kind.text().text();
  }

  private static List<String> wrap(String text, int maxWidth) {
    Font font = LDFonts.font();
    List<String> lines = new ArrayList<>();
    StringBuilder line = new StringBuilder();
    for (String word : text.split(" ")) {
      String candidate = line.isEmpty() ? word : line + " " + word;
      if (!line.isEmpty() && font.width(candidate) > maxWidth) {
        lines.add(line.toString());
        line = new StringBuilder(word);
      } else {
        line.setLength(0);
        line.append(candidate);
      }
    }
    if (!line.isEmpty()) {
      lines.add(line.toString());
    }
    return lines;
  }

  @OverridingMethodsMustInvokeSuper
  protected void choose(PICK pick) {
    selected = pick;
    amount = 1;
    long max = Math.max(1, currentMax());
    if (amountField != null) {
      amountField.setNumbersOnlyLong(1, max);
      amountField.setText("1");
    }
    if (slider != null) {
      slider.setRange(1f, (float) max);
      slider.setValue(1f);
    }
  }

  protected final class BlockOverlay extends UIElement {
    BlockOverlay() {
      lss("width", FILL_OPEN_WIDTH).lss("height", openHeight());
      style(style -> style.zIndex(16).background(IGuiTexture.EMPTY));
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        if (blockReason != null) {
          event.hoverTooltips = HoverTooltips.create(blockReason);
        }
      });
    }

    @Override
    protected void drawBackgroundAdditional(IGUIContext context) {
      if (!(context instanceof GUIContext gui) || !isActive() || blockReason == null) {
        return;
      }
      int x = (int) getContentX();
      int y = (int) getContentY();
      int width = (int) getSizeWidth();
      int height = (int) getSizeHeight();
      DrawerHelperClient.drawSolidRect(gui, x, y, width, height, BLOCKED_WASH);
      Font font = LDFonts.font();
      List<String> lines = wrap(blockReason.getString(), width - 16);
      int lineHeight = font.lineHeight + 1;
      int top = y + height / 2 - lines.size() * lineHeight / 2;
      for (String line : lines) {
        DrawerHelperClient.drawStringSized(gui, line, x + width / 2f, top, BLOCKED_TEXT, true, 1f, true);
        top += lineHeight;
      }
    }
  }

  private final class PickEntry extends UIElement {
    private final PICK pick;

    PickEntry(PICK pick) {
      this.pick = pick;
      lss("width", MMRMachineUI.SLOT).lss("height", MMRMachineUI.SLOT);
      style(style -> style.background(MMRMachineUI.slotTexture()));
      addEventListener(UIEvents.MOUSE_DOWN, event -> {
        if (event.button == 0) {
          event.stopPropagation();
          UISoundUtils.playButtonClickSound();
          choose(pick);
        }
      });
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        List<Component> lines = new ArrayList<>();
        pick.addLines(lines);
        event.hoverTooltips = HoverTooltips.create(lines.toArray());
      });
    }

    @Override
    protected void drawBackgroundAdditional(IGUIContext context) {
      if (!(context instanceof GUIContext gui)) {
        return;
      }
      int x = (int) getContentX();
      int y = (int) getContentY();
      pick.draw(gui, x + 1, y + 1);
      if (pick.equals(selected)) {
        DrawerHelperClient.drawSolidRect(gui, x, y, MMRMachineUI.SLOT, MMRMachineUI.SLOT, SELECTED_TINT);
      }
    }
  }
}