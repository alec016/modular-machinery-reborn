package es.degrassi.mmr.client.screen;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib2.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.data.TextWrap;
import com.lowdragmc.lowdraglib2.gui.ui.data.Vertical;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Slider;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/**
 * Base helpers for the LDLib2 ({@code ModularUI}) machine screens.
 *
 * <p>All machine UIs are plain {@link UIElement} trees, positioned with absolute LSS coordinates so the
 * classic Modular Machinery pixel layout (slots over the machine texture) is preserved. LSS strings are
 * used instead of the taffy enums because the taffy types are not on this project's compile classpath
 * ({@code ldlib2} is declared with {@code transitive = false}).
 *
 * <p>Every method here only uses common-safe types, so the same code runs on both sides, which is what
 * {@code BlockUIMenuType} requires (the UI is created on the server too, to drive data binding).
 */
public final class MMRMachineUI {

  /** Vanilla slot pitch. */
  public static final int SLOT = 18;
  /** Standard machine panel width used by every Modular Machinery GUI. */
  public static final int PANEL_WIDTH = 176;
  /**
   * Standard panel height: the vanilla inventory grid (label at 72, main rows from 84, hotbar at
   * 142) ends 159px down, leaving only the classic tight gap above the panel's bottom border.
   */
  public static final int PANEL_HEIGHT = 166;
  /** Standard vanilla inventory spot on the panel (label y, main-grid x/y). */
  public static final int INVENTORY_LABEL_Y = 72;
  public static final int INVENTORY_X = 8;
  public static final int INVENTORY_Y = 84;

  /** The legacy dynamic panel sprite: a 16x16 nine-slice (4px borders, sprites/background.png.mcmeta). */
  public static final Identifier TEX_BACKGROUND = ModularMachineryReborn.rl("textures/gui/sprites/background.png");
  public static final Identifier TEX_BASE_SLOT = ModularMachineryReborn.rl("textures/gui/base_slot.png");
  public static final Identifier TEX_BASE_SLOT_HOVERED = ModularMachineryReborn.rl("textures/gui/base_slot_hovered.png");
  public static final Identifier TEX_STATES = ModularMachineryReborn.rl("textures/gui/widget/states.png");
  public static final Identifier TEX_GUIBAR = ModularMachineryReborn.rl("textures/gui/guibar.png");
  public static final Identifier TEX_GUIEXPERIENCE = ModularMachineryReborn.rl("textures/gui/guiexperience.png");
  public static final Identifier TEX_GUITANK = ModularMachineryReborn.rl("textures/gui/guitank.png");
  public static final Identifier TEX_GUICONTROLLER = ModularMachineryReborn.rl("textures/gui/guicontroller.png");
  public static final Identifier TEX_SMALL_SCROLLER = ModularMachineryReborn.rl("textures/gui/sprites/small_scroller.png");
  public static final Identifier TEX_SMALL_SCROLLER_DISABLED = ModularMachineryReborn.rl("textures/gui/sprites/small_scroller_disabled.png");
  /** Standalone 20x61 black box backing the fluid/energy gauge fills (was the guibar.png crop). */
  public static final Identifier TEX_TANK_BACKING = ModularMachineryReborn.rl("textures/gui/tank_backing.png");

  private MMRMachineUI() {}

  /**
   * Left coordinate that horizontally centers a block of {@code contentWidth} pixels in the standard
   * panel. The layout recipe shared by the machine screens: compute the group width from the legacy
   * relative spacings, then place every member of the group at {@code left + legacyOffset}.
   */
  public static int centeredLeft(int contentWidth) {
    return (PANEL_WIDTH - contentWidth) / 2;
  }

  /** Wraps a machine UI as a menu-backed {@link ModularUI} bound to the player (required for slot syncing). */
  public static ModularUI menu(UIElement root, Player player) {
    return ModularUI.of(UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP)), player);
  }

  /** A raw sprite region 1:1 cropped for panels whose PNG carries the GUI in its top-left corner. */
  public static IGuiTexture sprite(Identifier texture, int x, int y, int width, int height) {
    return SpriteTexture.of(texture.toString()).setSprite(x, y, width, height);
  }

  /**
   * The nine-slice version of a texture (4px borders from its {@code .mcmeta}), stretched to
   * whatever size the caller paints, whatever the element size.
   */
  public static IGuiTexture nineSliceTexture(Identifier texture) {
    return IGuiTexture.dynamic(() -> SpriteTexture.of(texture.toString()).setBorder(4));
  }

  /** Same as {@link #nineSliceTexture(Identifier)} with explicit slice borders. */
  public static IGuiTexture spriteNineSlice(Identifier texture, int left, int top, int right, int bottom) {
    return IGuiTexture.dynamic(() ->
        SpriteTexture.of(texture.toString()).setBorder(left, top, right, bottom));
  }

  /**
   * A panel with the given background texture and size.
   *
   * <p>The legacy GUI textures ({@code guibar.png}, {@code guiexperience.png}, {@code guitank.png}) are
   * 256x256 files whose GUI is drawn 1:1 into the top-left corner, so the sprite region is cropped to
   * the panel size instead of stretching the whole image.
   */
  public static UIElement panel(Identifier background, int width, int height) {
    return new UIElement()
        .lss("width", width)
        .lss("height", height)
        .style(style -> style.background(IGuiTexture.dynamic(() ->
            SpriteTexture.of(background.toString()).setSprite(0, 0, width, height))));
  }

  /**
   * A panel backed by the legacy 16x16 nine-slice background sprite ({@code sprites/background.png},
   * 4px borders per its .mcmeta), which the old screens blitted at any size.
   */
  public static UIElement panelNineSlice(Identifier background, int width, int height) {
    return new UIElement()
        .lss("width", width)
        .lss("height", height)
        .style(style -> style.background(IGuiTexture.dynamic(() ->
            SpriteTexture.of(background.toString()).setBorder(4))));
  }

  /** Places a child at an absolute pixel position inside its parent. */
  @SuppressWarnings("unchecked")
  public static <T extends UIElement> T at(T child, int x, int y) {
    return (T) child.lss("position", "absolute").lss("left", x).lss("top", y);
  }

  /** Adds a child at an absolute pixel position. */
  public static <T extends UIElement> T add(UIElement parent, T child, int x, int y) {
    parent.addChild(at(child, x, y));
    return child;
  }

  /**
   * One machine, capability or filter slot in the legacy pixel style.
   *
   * <p>{@code x}/{@code y} are the coordinates of the 16x16 item area (vanilla {@code Slot} semantics);
   * the slot element itself is an 18x18 box whose background is the 1:1 {@code base_slot.png} frame, so
   * the frame lands exactly on {@code (x-1, y-1)} and the item on {@code (x, y)}. Hovering overlays the
   * {@code base_slot_hovered.png} frame plus the classic translucent white highlight.
   */
  public static ItemSlot slotAt(UIElement panel, int x, int y) {
    ItemSlot slot = new ItemSlot();
    slot.layout(layout -> {
      layout.width(SLOT);
      layout.height(SLOT);
      layout.paddingAll(1);
      layout.marginAll(0);
    });
    slot.style(style -> style.background(slotTexture()));
    slot.slotStyle(s -> s.hoverOverlay(hoverOverlayTexture()));
    return add(panel, slot, x - 1, y - 1);
  }

  /** The 1:1 {@code base_slot.png} slot frame. */
  public static IGuiTexture slotTexture() {
    return SpriteTexture.of(TEX_BASE_SLOT.toString());
  }

  /** Hovered slot overlay: the {@code base_slot_hovered.png} frame plus the translucent white foil. */
  public static IGuiTexture hoverOverlayTexture() {
    return new GuiTextureGroup(
        SpriteTexture.of(TEX_BASE_SLOT_HOVERED.toString()),
        new ColorRectTexture(0x80FFFFFF));
  }

  /** Adds the standard 9x3 + 9 player inventory at the given item-area origin. */
  public static InventorySlots playerInventory(UIElement panel, int x, int y) {
    var invSlots = new InventorySlots();
    for (InventorySlots.Row row : invSlots.rows) {
      styleSlotRow(row);
    }
    styleSlotRow(invSlots.hotbar);
    invSlots.hotbar.layout(layout -> layout.marginTop(4));
    return add(panel, invSlots, x - 1, y - 1);
  }

  private static void styleSlotRow(InventorySlots.Row row) {
    for (ItemSlot slot : row.slots) {
      slot.slotStyle(style -> style.slotOverlay(IGuiTexture.EMPTY));
      slot.style(style -> style.background(slotTexture()));
      slot.slotStyle(s -> s.hoverOverlay(hoverOverlayTexture()));
    }
  }

  /** Adds a plain text label. */
  public static void label(UIElement panel, Component text, int x, int y) {
    add(panel, new Label().setText(text), x, y);
  }

  /**
   * The vanilla screen caption the legacy screens drew with {@code drawString(font, text, x, y,
   * 4210752, false)}: plain dark text, no shadow.
   */
  public static Label captionLabel(UIElement panel, Component text, int x, int y) {
    Label label = new Label();
    label.setText(text);
    label.textStyle(style -> {
      style.textColor(0xFF404040);
      // The legacy drawString(...) drew captions without the shadow (the ldlib theme keeps the
      // vanilla text shadow on, which doubles the dark glyphs and looks blurry over the panel).
      style.textShadow(false);
      // Shrink-wrap so the caption renders exactly at its absolute (x, y) anchor.
      style.adaptiveWidth(true);
      style.adaptiveHeight(true);
    });
    return add(panel, label, x, y);
  }

  /** The vanilla {@code Inventory} caption the legacy harness screens drew above the player grid. */
  public static void inventoryLabel(UIElement panel, int x, int y) {
    captionLabel(panel, Component.translatable("container.inventory"), x, y);
  }

  /** Adds a read-only (server to client) text label, e.g. an amount read from the machine. */
  public static Label syncedLabel(UIElement panel, Supplier<Component> text, int x, int y) {
    Label label = new Label();
    label.bind(DataBindingBuilder.componentS2C(text).build());
    return add(panel, label, x, y);
  }

  /** Adds a static caption drawn on a {@code SpriteButton#spriteButton}-compatible row. */
  public static Label spriteCaption(UIElement parent, Component text, int x, int y, int width, int height) {
    Label label = new Label();
    label.setText(text);
    label.layout(layout -> {
      layout.width(width);
      layout.height(height);
    });
    label.textStyle(style -> style.textAlignHorizontal(Horizontal.CENTER).textAlignVertical(Vertical.CENTER));
    return add(parent, label, x, y);
  }

  /**
   * Dress a slider's handle with the small-scroller thumb — the same nine-slice the fill
   * drawer's scrollbar grabber uses — so the slider reads as a scrollbar. The slider's built-in
   * track and fill keep their default styling: the thumb carries no scrollbar background.
   */
  public static <T extends Slider> T withScrollerThumb(T slider) {
    slider.handle.buttonStyle(style -> {
      // Identical nine-slice borders to the drawer scroller thumb (LR 3 px, TB 4 px).
      var thumb = spriteNineSlice(TEX_SMALL_SCROLLER, 3, 4, 3, 4);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    return slider;
  }
}
