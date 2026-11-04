package es.degrassi.mmr.client.screen.widget.tabs;

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
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.util.NumberFormats;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.screen.widget.buttons.SpriteButton;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import es.degrassi.mmr.common.network.client.CFillHatchRequestPacket;
import es.degrassi.mmr.common.util.MMRPermissions;
import net.minecraft.client.gui.Font;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

// TODO: refactor to be abstract af, this is awful but works
public final class FillTab extends UIElement {

  public static final int LEFT_TAB_X = -27;
  public static final int LEFT_TAB_WIDTH = 27;
  public static final int LEFT_TAB_HEIGHT = 28;
  public static final int FILL_OPEN_WIDTH = 132;
  public static final int FILL_OPEN_HEIGHT = 170;
  public static final int FILL_SIMPLE_OPEN_HEIGHT = 90;
  public static final int FILL_DURABILITY_OPEN_HEIGHT = 200;

  public static final Identifier TEX_TAB_LEFT = ModularMachineryReborn.rl("textures/gui/widget/base_tab_left.png");
  public static final Identifier TEX_TAB_LEFT_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_left.png");

  public static void fillItemTab(UIElement parent, Player player, BlockEntity entity,
                                 int x, int y, @Nullable Supplier<ItemHandler> handler,
                                 @Nullable Predicate<ItemStack> itemFilter) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(player, entity, Kind.ITEM, handler, null, null, itemFilter);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillDurabilityTab(UIElement parent, Player player, BlockEntity entity,
                                       int x, int y, @Nullable Supplier<ItemHandler> handler) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    Predicate<ItemStack> damageable = stack -> stack.isDamageableItem() && stack.getMaxDamage() > 0;
    FillTab tab = new FillTab(player, entity, Kind.DURABILITY, handler, null, null, damageable);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillFluidTab(UIElement parent, Player player, BlockEntity entity,
                                  int x, int y, @Nullable Supplier<FluidHandler> handler) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(player, entity, Kind.FLUID, null, handler, null, null);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillEnergyTab(UIElement parent, Player player, BlockEntity entity,
                                   int x, int y, LongSupplier spaceSupplier) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(player, entity, Kind.ENERGY, null, null, spaceSupplier, null);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillExperienceTab(UIElement parent, Player player, BlockEntity entity,
                                       int x, int y, LongSupplier spaceSupplier) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(player, entity, Kind.EXPERIENCE, null, null, spaceSupplier, null);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  private static final int BODY_LEFT = -FILL_OPEN_WIDTH;
  private static final int CX = BODY_LEFT + LEFT_TAB_WIDTH + 4;
  private static final int BUTTON_RIGHT_X = -64 + LEFT_TAB_WIDTH;
  private static final int CLOSE_X = 15;
  private static final int COLS = 6;
  private static final int VISIBLE_ROWS = 3;
  private static final int GRID_W = COLS * MMRMachineUI.SLOT + 4 + 7;
  private static final Icon TAB_ICON = Icon.WHITE_ARROW_DOWN;
  private static final int SIMPLE_BUTTON_Y = 66;
  private static final int PICKER_LABEL_Y = 60;
  private static final int PICKER_FIELD_Y = 70;
  private static final int PICKER_GRID_Y = 88;
  private static final int PICKER_BUTTON_Y = 144;
  private static final int DURAB_LABEL_Y = 60;
  private static final int DURAB_ROW_Y = 70;
  private static final int DURAB_FIELD_W = 56;
  private static final int DURAB_SLIDER_X = CX + DURAB_FIELD_W + 4;
  private static final int DURAB_SLIDER_W = 64;
  private static final int DURAB_PICK_LABEL_Y = 90;
  private static final int DURAB_PICK_FIELD_Y = 100;
  private static final int DURAB_PICK_GRID_Y = 118;
  private static final int DURAB_PICK_BUTTON_Y = 174;
  private static final int BLOCKED_WASH = 0x900C0C0C;
  private static final int BLOCKED_TEXT = 0xFFFF5555;
  private static final long OPEN_MS = 240L;
  private static final int SCROLL_BORDER_LR = 3;
  private static final int SCROLL_BORDER_TB = 4;
  private static final int MAX_CANDIDATES = 90;
  private static final int SELECTED_TINT = 0x60FFFFFF;

  public record Kind(Identifier id, TabTexts text, int height) {
    Kind(String path, TabTexts text, int height) {
      this(mmr(path), text, height);
    }
    Kind(String path) {
      this(mmr(path), TabTexts.fillReasonFull, FILL_OPEN_HEIGHT);
    }
    Kind(String path, int height) {
      this(mmr(path), TabTexts.fillReasonFull, height);
    }
    public static final Kind ITEM = new Kind("item");
    public static final Kind FLUID = new Kind("fluid");
    public static final Kind ENERGY = new Kind("energy", TabTexts.fillReasonEnergy, FILL_SIMPLE_OPEN_HEIGHT);
    public static final Kind EXPERIENCE = new Kind("experience", TabTexts.fillReasonExperience, FILL_SIMPLE_OPEN_HEIGHT);
    public static final Kind DURABILITY = new Kind("durability", FILL_DURABILITY_OPEN_HEIGHT);

    public static Identifier mmr(String path) {
      return ModularMachineryReborn.rl(path);
    }
  }

  private sealed interface Pick permits ItemPick, FluidPick {}

  private record ItemPick(ItemStack stack, ItemResource key) implements Pick {
    static ItemPick of(ItemStack stack) {
      return new ItemPick(stack, ItemResource.of(stack));
    }
  }

  private record FluidPick(FluidStack stack, FluidResource key) implements Pick {
    static FluidPick of(FluidStack stack) {
      return new FluidPick(stack, FluidResource.of(stack));
    }
  }

  @Nullable
  private final Player player;
  private final BlockEntity entity;
  private final Kind kind;
  @Nullable
  private final Supplier<ItemHandler> itemHandler;
  @Nullable
  private final Supplier<FluidHandler> fluidHandler;
  @Nullable
  private final LongSupplier simpleSpace;
  @Nullable
  private final Predicate<ItemStack> itemFilter;

  private boolean opened = false;
  private long animStart = -1L;
  private boolean contentBuilt = false;
  private boolean syncing = false;
  private long amount = 1;
  private int durability = -1;
  @Nullable
  private Component blockedReason;

  @Nullable
  private TextField amountField;
  @Nullable
  private Slider slider;
  @Nullable
  private TextField durabField;
  @Nullable
  private Slider durabSlider;
  @Nullable
  private Button confirmButton;
  @Nullable
  private BlockOverlay overlay;
  @Nullable
  private List<ItemStack> displayCache;
  @Nullable
  private ScrollerView grid;
  @Nullable
  private Pick selected;

  FillTab(@Nullable Player player, BlockEntity entity, Kind kind, @Nullable Supplier<ItemHandler> itemHandler,
          @Nullable Supplier<FluidHandler> fluidHandler, @Nullable LongSupplier simpleSpace,
          @Nullable Predicate<ItemStack> itemFilter) {
    this.player = player;
    this.entity = entity;
    this.kind = kind;
    this.itemHandler = itemHandler;
    this.fluidHandler = fluidHandler;
    this.simpleSpace = simpleSpace;
    this.itemFilter = itemFilter;
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
  }

  private void open() {
    opened = true;
    amount = 1;
    durability = -1;
    animStart = System.currentTimeMillis();
  }

  private void close() {
    if (!opened) {
      return;
    }
    opened = false;
    animStart = System.currentTimeMillis();
    if (entity.getLevel() != null && entity.getLevel().isClientSide()) {
      clearAllChildren();
      amountField = null;
      slider = null;
      durabField = null;
      durabSlider = null;
      confirmButton = null;
      overlay = null;
      grid = null;
      displayCache = null;
      blockedReason = null;
    }
    contentBuilt = false;
  }

  private float currentProgress() {
    if (animStart < 0L) {
      return opened ? 1f : 0f;
    }
    float t = (System.currentTimeMillis() - animStart) / (float) OPEN_MS;
    return opened ? Math.min(1f, t) : 1f - Math.min(1f, t);
  }

  private int openHeight() {
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

  private void buildContent() {
    contentBuilt = true;
    if (entity.getLevel() == null || !entity.getLevel().isClientSide()) {
      return;
    }
    boolean picker = kind == Kind.ITEM || kind == Kind.FLUID || kind == Kind.DURABILITY;
    IconButton.smallIconButton(this, Icon.CLEAR, CLOSE_X, 3, event -> {
      event.stopPropagation();
      close();
    }, () -> List.of(TabTexts.tooltipCloseFill.text()))
        .style(style -> style.zIndex(20));
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
    if (kind == Kind.DURABILITY) {
      MMRMachineUI.captionLabel(this, TabTexts.fillDurability.text(), CX, DURAB_LABEL_Y);
      durabField = new TextField();
      durabField.setNumbersOnlyInt(0, 0);
      durabField.setTextResponder(this::onDurabTyped);
      MMRMachineUI.add(this, durabField.lss("width", DURAB_FIELD_W).lss("height", 14), CX, DURAB_ROW_Y);
      durabSlider = MMRMachineUI.withScrollerThumb(new Slider.Horizontal());
      durabSlider.setRange(0f, 0f);
      durabSlider.setOnValueChanged(this::onDurabMoved);
      MMRMachineUI.add(this, durabSlider.lss("width", DURAB_SLIDER_W - 2).lss("height", 16), DURAB_SLIDER_X, DURAB_ROW_Y);
    }
    if (picker) {
      boolean durab = kind == Kind.DURABILITY;
      int labelY = durab ? DURAB_PICK_LABEL_Y : PICKER_LABEL_Y;
      int fieldY = durab ? DURAB_PICK_FIELD_Y : PICKER_FIELD_Y;
      int gridY = durab ? DURAB_PICK_GRID_Y : PICKER_GRID_Y;
      int buttonY = durab ? DURAB_PICK_BUTTON_Y : PICKER_BUTTON_Y;
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

  private void onDurabTyped(String text) {
    if (syncing) {
      return;
    }
    syncing = true;
    durability = parseDurability(text);
    if (durabSlider != null) {
      durabSlider.setValue((float) durability);
    }
    syncing = false;
  }

  private void onDurabMoved(float value) {
    if (syncing) {
      return;
    }
    syncing = true;
    durability = Math.max(0, Math.round(value));
    if (durabField != null && !String.valueOf(durability).equals(durabField.getValue())) {
      durabField.setText(String.valueOf(durability));
    }
    syncing = false;
  }

  private int parseDurability(String text) {
    int parsed;
    try {
      parsed = Integer.parseInt(text.trim());
    } catch (NumberFormatException exception) {
      parsed = 0;
    }
    return Mth.clamp(parsed, 0, selectedMaxDurability());
  }

  private int selectedMaxDurability() {
    return selected instanceof ItemPick(ItemStack stack, ItemResource ignore) ? stack.getMaxDamage() : 0;
  }

  private long currentMax() {
    if (kind == Kind.ENERGY || kind == Kind.EXPERIENCE) {
      return simpleSpace == null ? 0L : Math.max(0L, simpleSpace.getAsLong());
    }
    if (selected == null) {
      return 0L;
    }
    if (kind == Kind.ITEM || kind == Kind.DURABILITY) {
      return itemHandler == null || itemHandler.get() == null ? 0L
          : itemHandler.get().getSpaceForItem(((ItemPick) selected).stack());
    }
    return fluidHandler == null || fluidHandler.get() == null ? 0L
        : fluidHandler.get().getSpaceForFluid(((FluidPick) selected).stack());
  }

  private void choose(Pick pick) {
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
    if (kind == Kind.DURABILITY) {
      durability = selectedMaxDurability();
      int clamped = Math.max(0, durability);
      if (durabField != null) {
        durabField.setNumbersOnlyInt(0, clamped);
        durabField.setText(String.valueOf(durability));
      }
      if (durabSlider != null) {
        durabSlider.setRange(0f, clamped);
        durabSlider.setValue((float) durability);
      }
    }
  }

  private void confirm() {
    long max = currentMax();
    if (max > 0) {
      long fill = parseAmount(amountField == null ? "1" : amountField.getValue());
      Identifier packetKind = kind.id();
      ItemStack item = ItemStack.EMPTY;
      FluidStack fluid = FluidStack.EMPTY;
      int durabilitySent = CFillHatchRequestPacket.DURABILITY_UNUSED;
      if (selected instanceof ItemPick(ItemStack stack, ItemResource ignore)) {
        item = stack;
        if (kind == Kind.DURABILITY) {
          item = stack.copy();
          int remaining = durability < 0 ? item.getMaxDamage()
              : Mth.clamp(durability, 0, item.getMaxDamage());
          item.setDamageValue(item.getMaxDamage() - remaining);
          durabilitySent = remaining;
        }
      } else if (kind == Kind.FLUID) {
        fluid = ((FluidPick) selected).stack();
      }
      ClientPacketDistributor.sendToServer(new CFillHatchRequestPacket(
          entity.getBlockPos(), packetKind, fill, item, fluid, durabilitySent));
    }
    close();
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
    List<Pick> shown = candidates(query);
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

  private List<Pick> candidates(String query) {
    String trimmed = query.trim();
    boolean byMod = trimmed.startsWith("@");
    boolean byTag = trimmed.startsWith("#");
    String needle = (byMod || byTag ? trimmed.substring(1) : trimmed).trim().toLowerCase(Locale.ROOT);
    List<Pick> result = new ArrayList<>();
    if (kind == Kind.ITEM || kind == Kind.DURABILITY) {
      List<ItemStack> display = displayItemStacks();
      if (byTag) {
        collectItemTagCandidates(needle, display, result);
      } else if (display.isEmpty()) {
        for (Item item : BuiltInRegistries.ITEM) {
          if (result.size() >= MAX_CANDIDATES) {
            break;
          }
          ItemStack template = new ItemStack(item);
          if (!template.isEmpty()
              && (itemFilter == null || itemFilter.test(template))
              && matches(byMod, template.getHoverName().getString(),
                  BuiltInRegistries.ITEM.getKey(item), needle)) {
            result.add(ItemPick.of(template));
          }
        }
      } else {
        for (ItemStack template : display) {
          if (result.size() >= MAX_CANDIDATES) {
            break;
          }
          if ((itemFilter == null || itemFilter.test(template))
              && matches(byMod, template.getHoverName().getString(),
                  BuiltInRegistries.ITEM.getKey(template.getItem()), needle)) {
            result.add(ItemPick.of(template));
          }
        }
      }
    } else if (byTag) {
      var seen = new LinkedHashSet<Fluid>();
      BuiltInRegistries.FLUID.getTags().forEach(named -> {
        if (result.size() >= MAX_CANDIDATES || !tagMatches(named.key().location(), needle)) {
          return;
        }
        for (var holder : named.stream().toList()) {
          if (result.size() >= MAX_CANDIDATES) {
            return;
          }
          Fluid fluid = holder.value();
          if (fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource() || !seen.add(fluid)) {
            continue;
          }
          if (!acceptsFluid(fluid)) {
            continue;
          }
          result.add(FluidPick.of(new FluidStack(fluid, 1)));
        }
      });
    } else {
      for (Fluid fluid : BuiltInRegistries.FLUID) {
        if (result.size() >= MAX_CANDIDATES) {
          break;
        }
        if (fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()) {
          continue;
        }
        if (!acceptsFluid(fluid)) {
          continue;
        }
        FluidStack template = new FluidStack(fluid, 1);
        if (matches(byMod, template.getHoverName().getString(),
            BuiltInRegistries.FLUID.getKey(fluid), needle)) {
          result.add(FluidPick.of(template));
        }
      }
    }
    return result;
  }

  /**
   * Whether the hatch accepts this fluid at all: the handler carries the hatch's filter, so a
   * filtered hatch only offers (and accepts) the fluids that pass it.
   */
  private boolean acceptsFluid(Fluid fluid) {
    if (fluidHandler == null || fluidHandler.get() == null) {
      return true;
    }
    return fluidHandler.get().isValid(0, FluidResource.of(fluid));
  }

  private List<ItemStack> displayItemStacks() {
    if (displayCache != null) {
      return displayCache;
    }
    var stacks = new ArrayList<ItemStack>();
    var level = entity.getLevel();
    if (level != null) {
      CreativeModeTabs.tryRebuildTabContents(
          level.enabledFeatures(), player != null && player.canUseGameMasterBlocks(),
          level.registryAccess());
      var seen = new HashSet<ItemResource>();
      for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
        if (tab.getType() != CreativeModeTab.Type.CATEGORY) {
          continue;
        }
        for (ItemStack stack : tab.getDisplayItems()) {
          if (stack.isEmpty() || !seen.add(ItemResource.of(stack))) {
            continue;
          }
          stacks.add(stack.copyWithCount(1));
        }
      }
    }
    displayCache = List.copyOf(stacks);
    return displayCache;
  }

  private static boolean matches(boolean byMod, String name, Identifier id, String needle) {
    if (byMod) {
      return id.getNamespace().contains(needle);
    }
    return needle.isEmpty()
        || name.toLowerCase(Locale.ROOT).contains(needle)
        || id.getPath().contains(needle);
  }

  private static boolean tagMatches(Identifier location, String needle) {
    return needle.isEmpty()
        || location.getPath().contains(needle)
        || location.getNamespace().contains(needle);
  }

  private void collectItemTagCandidates(String needle, List<ItemStack> display, List<Pick> result) {
    Map<Item, ItemStack> preferred = new HashMap<>();
    for (ItemStack stack : display) {
      preferred.putIfAbsent(stack.getItem(), stack);
    }
    var seen = new LinkedHashSet<Item>();
    BuiltInRegistries.ITEM.getTags().forEach(named -> {
      if (result.size() >= MAX_CANDIDATES || !tagMatches(named.key().location(), needle)) {
        return;
      }
      for (var holder : named.stream().toList()) {
        if (result.size() >= MAX_CANDIDATES) {
          return;
        }
        Item item = holder.value();
        if (!seen.add(item)) {
          continue;
        }
        ItemStack template = preferred.getOrDefault(item, ItemStack.EMPTY);
        if (template.isEmpty() || (itemFilter != null && !itemFilter.test(template))) {
          continue;
        }
        result.add(ItemPick.of(template));
      }
    });
    BuiltInRegistries.BLOCK.getTags().forEach(named -> {
      if (result.size() >= MAX_CANDIDATES || !tagMatches(named.key().location(), needle)) {
        return;
      }
      for (var holder : named.stream().toList()) {
        if (result.size() >= MAX_CANDIDATES) {
          return;
        }
        ItemStack template = new ItemStack(holder.value().asItem());
        if (template.isEmpty()
            || !seen.add(template.getItem())
            || (itemFilter != null && !itemFilter.test(template))) {
          continue;
        }
        result.add(ItemPick.of(template));
      }
    });
  }

  // ------------------------------------------------------------------
  // blocked state
  // ------------------------------------------------------------------

  private void refreshBlocked() {
    if (entity.getLevel() == null || !entity.getLevel().isClientSide()) {
      return;
    }
    boolean picker = kind == Kind.ITEM || kind == Kind.FLUID || kind == Kind.DURABILITY;
    boolean unselected = picker && selected == null;
    boolean hatchFull = kind == Kind.FLUID
        && (fluidHandler == null || fluidHandler.get() == null
        || fluidHandler.get().getTotalFreeSpace() <= 0);
    boolean full = unselected ? hatchFull : currentMax() <= 0;
    // While the grid of the hatch is still compiling its slots do not exist yet, so nothing can
    // be filled into it.
    boolean compiling = isCompiling();
    boolean blocked = compiling || (unselected ? hatchFull : full);
    blockedReason = blocked ? (compiling ? TabTexts.fillReasonCompiling.text() : fullReason()) : null;
    if (overlay != null) {
      if (blocked && overlay.getParent() == null) {
        MMRMachineUI.add(this, overlay, BODY_LEFT + LEFT_TAB_WIDTH, 0);
      } else if (!blocked) {
        overlay.removeSelf();
      }
      overlay.setActive(blocked);
    }
    boolean disable = unselected || blocked;
    if (amountField != null) {
      amountField.setActive(!disable);
    }
    if (slider != null) {
      slider.setActive(!disable);
    }
    if (confirmButton != null) {
      confirmButton.setActive(!disable);
    }
    if (kind == Kind.DURABILITY) {
      if (durabField != null) {
        durabField.setActive(!disable);
      }
      if (durabSlider != null) {
        durabSlider.setActive(!disable);
      }
    }
  }

  /**
   * Whether the grid of this hatch is still compiling: its slots do not exist yet, so filling it
   * is not possible.
   */
  private boolean isCompiling() {
    if (itemHandler != null && itemHandler.get() != null && itemHandler.get().isCompiling()) {
      return true;
    }
    return entity instanceof TileInventory inventory && inventory.getInventory().isCompiling();
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

  private final class BlockOverlay extends UIElement {
    BlockOverlay() {
      lss("width", FILL_OPEN_WIDTH).lss("height", openHeight());
      style(style -> style.zIndex(16).background(IGuiTexture.EMPTY));
      addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
        if (blockedReason != null) {
          event.hoverTooltips = HoverTooltips.create(blockedReason);
        }
      });
    }

    @Override
    protected void drawBackgroundAdditional(IGUIContext context) {
      if (!(context instanceof GUIContext gui) || !isActive() || blockedReason == null) {
        return;
      }
      int x = (int) getContentX();
      int y = (int) getContentY();
      int width = (int) getSizeWidth();
      int height = (int) getSizeHeight();
      DrawerHelperClient.drawSolidRect(gui, x, y, width, height, BLOCKED_WASH);
      Font font = LDFonts.font();
      List<String> lines = wrap(blockedReason.getString(), width - 16);
      int lineHeight = font.lineHeight + 1;
      int top = y + height / 2 - lines.size() * lineHeight / 2;
      for (String line : lines) {
        DrawerHelperClient.drawStringSized(gui, line, x + width / 2f, top, BLOCKED_TEXT, true, 1f, true);
        top += lineHeight;
      }
    }
  }

  private final class PickEntry extends UIElement {
    private final Pick pick;

    PickEntry(Pick pick) {
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
        if (pick instanceof ItemPick(ItemStack stack, ItemResource ignore)) {
          lines.add(stack.getHoverName());
        } else if (pick instanceof FluidPick(FluidStack stack, FluidResource ignore)) {
          lines.add(stack.getHoverName());
        }
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
      if (pick instanceof ItemPick(ItemStack stack, ItemResource ignore)) {
        DrawerHelperClient.drawItemStack(gui, stack, x + 1, y + 1, 0);
      } else if (pick instanceof FluidPick(FluidStack stack, FluidResource ignore)) {
        DrawerHelperClient.drawFluidForGui(gui, stack, x + 1, y + 1, 16, 16, -1);
      }
      if (pick.equals(selected)) {
        DrawerHelperClient.drawSolidRect(gui, x, y, MMRMachineUI.SLOT, MMRMachineUI.SLOT, SELECTED_TINT);
      }
    }
  }
}
