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
import es.degrassi.mmr.client.screen.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.buttons.IconButton;
import es.degrassi.mmr.client.screen.widget.buttons.SpriteButton;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    FillTab tab = new FillTab(entity, Kind.ITEM, handler, null, null, itemFilter);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillDurabilityTab(UIElement parent, Player player, BlockEntity entity,
                                       int x, int y, @Nullable Supplier<ItemHandler> handler) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    Predicate<ItemStack> damageable = stack -> stack.isDamageableItem() && stack.getMaxDamage() > 0;
    FillTab tab = new FillTab(entity, Kind.DURABILITY, handler, null, null, damageable);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillFluidTab(UIElement parent, Player player, BlockEntity entity,
                                  int x, int y, @Nullable Supplier<FluidHandler> handler) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(entity, Kind.FLUID, null, handler, null, null);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillEnergyTab(UIElement parent, Player player, BlockEntity entity,
                                   int x, int y, IntSupplier spaceSupplier) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(entity, Kind.ENERGY, null, null, spaceSupplier, null);
    tab.lss("width", LEFT_TAB_WIDTH).lss("height", LEFT_TAB_HEIGHT);
    MMRMachineUI.add(parent, tab, x, y);
  }

  public static void fillExperienceTab(UIElement parent, Player player, BlockEntity entity,
                                       int x, int y, IntSupplier spaceSupplier) {
    if (!MMRPermissions.canFillHatch(player)) {
      return;
    }
    FillTab tab = new FillTab(entity, Kind.EXPERIENCE, null, null, spaceSupplier, null);
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

  private enum Kind {
    ITEM, FLUID, ENERGY, EXPERIENCE, DURABILITY
  }

  private sealed interface Pick permits ItemPick, FluidPick {}
  private record ItemPick(ItemStack stack) implements Pick {}
  private record FluidPick(FluidStack stack) implements Pick {}

  private static final Map<Item, String> ITEM_NAMES = new HashMap<>();
  private static final Map<Fluid, String> FLUID_NAMES = new HashMap<>();

  private final BlockEntity entity;
  private final Kind kind;
  @Nullable
  private final Supplier<ItemHandler> itemHandler;
  @Nullable
  private final Supplier<FluidHandler> fluidHandler;
  @Nullable
  private final IntSupplier simpleSpace;
  @Nullable
  private final Predicate<ItemStack> itemFilter;

  private boolean opened = false;
  private long animStart = -1L;
  private boolean contentBuilt = false;
  private boolean syncing = false;
  private int amount = 1;
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
  private ScrollerView grid;
  @Nullable
  private Pick selected;

  FillTab(BlockEntity entity, Kind kind, @Nullable Supplier<ItemHandler> itemHandler,
          @Nullable Supplier<FluidHandler> fluidHandler, @Nullable IntSupplier simpleSpace,
          @Nullable Predicate<ItemStack> itemFilter) {
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
        event.hoverTooltips = HoverTooltips.create(Component.translatable("mmr.tooltip.open_fill"));
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
    if (kind == Kind.ENERGY || kind == Kind.EXPERIENCE) {
      return FILL_SIMPLE_OPEN_HEIGHT;
    }
    if (kind == Kind.DURABILITY) {
      return FILL_DURABILITY_OPEN_HEIGHT;
    }
    return FILL_OPEN_HEIGHT;
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
    }, () -> List.of(Component.translatable("mmr.gui.tooltip.close_fill")))
        .style(style -> style.zIndex(20));
    MMRMachineUI.captionLabel(this, Component.translatable("mmr.gui.fill.amount"), CX, 13);
    amountField = new TextField();
    amountField.setNumbersOnlyInt(1, 1);
    amountField.setTextResponder(this::onAmountTyped);
    MMRMachineUI.add(this, amountField.lss("width", 124).lss("height", 14), CX, 23);
    slider = MMRMachineUI.withScrollerThumb(new Slider.Horizontal());
    slider.setRange(1f, 1f);
    slider.setOnValueChanged(this::onSliderMoved);
    MMRMachineUI.add(this, slider.lss("width", 120).lss("height", 16), CX + 2, 40);
    if (kind == Kind.DURABILITY) {
      MMRMachineUI.captionLabel(this, Component.translatable("mmr.gui.fill.durability"), CX, DURAB_LABEL_Y);
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
      MMRMachineUI.captionLabel(this, Component.translatable("mmr.gui.fill.search"), CX, labelY);
      TextField searchField = new TextField();
      searchField.setAnyString();
      searchField.setTextResponder(this::onSearchTyped);
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
          Component.translatable("mmr.gui.fill.confirm"), CX, buttonY, 60, 20,
          event -> {
            event.stopPropagation();
            confirm();
          });
      SpriteButton.spriteButton(this, Component.translatable("mmr.gui.fill.cancel"),
          BUTTON_RIGHT_X, buttonY, 60, 20,
          event -> {
            event.stopPropagation();
            close();
          });
      rebuild("");
    } else {
      int max = Math.max(1, currentMax());
      amount = 1;
      amountField.setNumbersOnlyInt(1, max);
      amountField.setText("1");
      slider.setRange(1f, max);
      slider.setValue(1f);
      confirmButton = SpriteButton.spriteButton(this,
          Component.translatable("mmr.gui.fill.confirm"), CX, SIMPLE_BUTTON_Y - 2, 60, 20,
          event -> {
            event.stopPropagation();
            confirm();
          });
      SpriteButton.spriteButton(this, Component.translatable("mmr.gui.fill.cancel"),
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
    amount = Math.max(1, Math.round(value));
    if (amountField != null && !String.valueOf(amount).equals(amountField.getValue())) {
      amountField.setText(String.valueOf(amount));
    }
    syncing = false;
  }

  private int parseAmount(String text) {
    int parsed;
    try {
      parsed = Integer.parseInt(text.trim());
    } catch (NumberFormatException exception) {
      parsed = 1;
    }
    return Mth.clamp(parsed, 1, Math.max(1, currentMax()));
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
    return selected instanceof ItemPick(ItemStack stack) ? stack.getMaxDamage() : 0;
  }

  private int currentMax() {
    if (kind == Kind.ENERGY || kind == Kind.EXPERIENCE) {
      return simpleSpace == null ? 0 : Math.max(0, simpleSpace.getAsInt());
    }
    if (selected == null) {
      return 0;
    }
    if (kind == Kind.ITEM || kind == Kind.DURABILITY) {
      return itemHandler == null || itemHandler.get() == null ? 0
          : itemHandler.get().getSpaceForItem(((ItemPick) selected).stack());
    }
    return fluidHandler == null || fluidHandler.get() == null ? 0
        : fluidHandler.get().getSpaceForFluid(((FluidPick) selected).stack());
  }

  private void choose(Pick pick) {
    selected = pick;
    amount = 1;
    int max = Math.max(1, currentMax());
    if (amountField != null) {
      amountField.setNumbersOnlyInt(1, max);
      amountField.setText("1");
    }
    if (slider != null) {
      slider.setRange(1f, max);
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
    int max = currentMax();
    if (max > 0) {
      int fill = Mth.clamp(parseAmount(amountField == null ? "1" : amountField.getValue()),
          1, Integer.MAX_VALUE);
      byte packetKind;
      ItemStack item = ItemStack.EMPTY;
      FluidStack fluid = FluidStack.EMPTY;
      int durabilitySent = CFillHatchRequestPacket.DURABILITY_UNUSED;
      if (kind == Kind.ENERGY) {
        packetKind = CFillHatchRequestPacket.KIND_ENERGY;
      } else if (kind == Kind.EXPERIENCE) {
        packetKind = CFillHatchRequestPacket.KIND_EXPERIENCE;
      } else if (selected instanceof ItemPick(ItemStack stack)) {
        packetKind = CFillHatchRequestPacket.KIND_ITEM;
        item = stack;
        if (kind == Kind.DURABILITY) {
          item = stack.copy();
          int remaining = durability < 0 ? item.getMaxDamage()
              : Mth.clamp(durability, 0, item.getMaxDamage());
          item.setDamageValue(item.getMaxDamage() - remaining);
          durabilitySent = remaining;
        }
      } else {
        packetKind = CFillHatchRequestPacket.KIND_FLUID;
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
    String needle = query.trim().toLowerCase(Locale.ROOT);
    List<Pick> result = new ArrayList<>();
    if (kind == Kind.ITEM || kind == Kind.DURABILITY) {
      for (Item item : BuiltInRegistries.ITEM) {
        if (result.size() >= MAX_CANDIDATES) {
          break;
        }
        ItemStack template = new ItemStack(item);
        if (template.isEmpty()) {
          continue;
        }
        if (itemFilter != null && !itemFilter.test(template)) {
          continue;
        }
        if (matches(ITEM_NAMES.computeIfAbsent(item,
            key -> template.getHoverName().getString()), BuiltInRegistries.ITEM.getKey(item), needle)) {
          result.add(new ItemPick(template));
        }
      }
    } else {
      for (Fluid fluid : BuiltInRegistries.FLUID) {
        if (result.size() >= MAX_CANDIDATES) {
          break;
        }
        if (fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()) {
          continue;
        }
        FluidStack template = new FluidStack(fluid, 1);
        if (matches(FLUID_NAMES.computeIfAbsent(fluid,
            key -> template.getHoverName().getString()), BuiltInRegistries.FLUID.getKey(fluid), needle)) {
          result.add(new FluidPick(template));
        }
      }
    }
    return result;
  }

  private static boolean matches(String name, Identifier id, String needle) {
    return needle.isEmpty()
        || name.toLowerCase(Locale.ROOT).contains(needle)
        || id.getPath().contains(needle);
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
    boolean blocked = unselected ? hatchFull : full;
    blockedReason = blocked ? fullReason() : null;
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

  private Component fullReason() {
    return switch (kind) {
      case ENERGY -> Component.translatable("mmr.gui.fill.reason.energy");
      case EXPERIENCE -> Component.translatable("mmr.gui.fill.reason.experience");
      default -> Component.translatable("mmr.gui.fill.reason.full");
    };
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
        if (pick instanceof ItemPick(ItemStack stack)) {
          lines.add(stack.getHoverName());
        } else if (pick instanceof FluidPick(FluidStack stack)) {
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
      if (pick instanceof ItemPick(ItemStack stack)) {
        DrawerHelperClient.drawItemStack(gui, stack, x + 1, y + 1, 0);
      } else if (pick instanceof FluidPick(FluidStack stack)) {
        DrawerHelperClient.drawFluidForGui(gui, stack, x + 1, y + 1, 16, 16, -1);
      }
      if (pick.equals(selected)) {
        DrawerHelperClient.drawSolidRect(gui, x, y, MMRMachineUI.SLOT, MMRMachineUI.SLOT, SELECTED_TINT);
      }
    }
  }
}
