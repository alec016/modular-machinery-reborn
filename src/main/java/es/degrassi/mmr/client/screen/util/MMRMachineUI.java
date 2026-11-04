package es.degrassi.mmr.client.screen.util;

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
import com.lowdragmc.lowdraglib2.integration.xei.IngredientIO;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.api.entity.FiltereableEntity;
import es.degrassi.mmr.client.container.FilterSlotComponent;
import es.degrassi.mmr.client.screen.widget.FilterSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public final class MMRMachineUI {
  public static final int SLOT = 18;
  public static final int PANEL_WIDTH = 176;
  public static final int PANEL_HEIGHT = 166;
  public static final int INVENTORY_LABEL_Y = 72;
  public static final int INVENTORY_X = 8;
  public static final int INVENTORY_Y = 84;

  public static final Identifier TEX_BACKGROUND = ModularMachineryReborn.rl("textures/gui/sprites/background.png");
  public static final Identifier TEX_BASE_SLOT = ModularMachineryReborn.rl("textures/gui/base_slot.png");
  public static final Identifier TEX_BASE_SLOT_HOVERED = ModularMachineryReborn.rl("textures/gui/base_slot_hovered.png");
  public static final Identifier TEX_SLOT_BORDER_INPUT = ModularMachineryReborn.rl("textures/gui/slot_border_input.png");
  public static final Identifier TEX_SLOT_BORDER_OUTPUT = ModularMachineryReborn.rl("textures/gui/slot_border_output.png");
  public static final Identifier TEX_STATES = ModularMachineryReborn.rl("textures/gui/widget/states.png");
  public static final Identifier TEX_GUIBAR = ModularMachineryReborn.rl("textures/gui/guibar.png");
  public static final Identifier TEX_GUIEXPERIENCE = ModularMachineryReborn.rl("textures/gui/guiexperience.png");
  public static final Identifier TEX_GUITANK = ModularMachineryReborn.rl("textures/gui/guitank.png");
  public static final Identifier TEX_GUICONTROLLER = ModularMachineryReborn.rl("textures/gui/guicontroller.png");
  public static final Identifier TEX_SMALL_SCROLLER = ModularMachineryReborn.rl("textures/gui/sprites/small_scroller.png");
  public static final Identifier TEX_SMALL_SCROLLER_DISABLED = ModularMachineryReborn.rl("textures/gui/sprites/small_scroller_disabled.png");
  public static final Identifier TEX_TANK_BACKING = ModularMachineryReborn.rl("textures/gui/tank_backing.png");

  private MMRMachineUI() {}

  public static int centeredLeft(int contentWidth) {
    return (PANEL_WIDTH - contentWidth) / 2;
  }

  public static ModularUI menu(UIElement root, Player player) {
    return ModularUI.of(UI.of(root, StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.GDP)), player);
  }

  public static IGuiTexture sprite(Identifier texture, int x, int y, int width, int height) {
    return SpriteTexture.of(texture.toString()).setSprite(x, y, width, height);
  }

  public static IGuiTexture nineSliceTexture(Identifier texture) {
    return IGuiTexture.dynamic(() -> SpriteTexture.of(texture.toString()).setBorder(4));
  }

  public static IGuiTexture spriteNineSlice(Identifier texture, int left, int top, int right, int bottom) {
    return IGuiTexture.dynamic(() ->
        SpriteTexture.of(texture.toString()).setBorder(left, top, right, bottom));
  }

  public static UIElement panel(Identifier background, int width, int height) {
    return new UIElement()
        .lss("width", width)
        .lss("height", height)
        .style(style -> style.background(IGuiTexture.dynamic(() ->
            SpriteTexture.of(background.toString()).setSprite(0, 0, width, height))));
  }

  public static UIElement panelNineSlice(Identifier background, int width, int height) {
    return new UIElement()
        .lss("width", width)
        .lss("height", height)
        .style(style -> style.background(IGuiTexture.dynamic(() ->
            SpriteTexture.of(background.toString()).setBorder(4))));
  }

  @SuppressWarnings("unchecked")
  public static <T extends UIElement> T at(T child, int x, int y) {
    return (T) child.lss("position", "absolute").lss("left", x).lss("top", y);
  }

  public static <T extends UIElement> T add(UIElement parent, T child, int x, int y) {
    parent.addChild(at(child, x, y));
    return child;
  }

  public static ItemSlot slotAt(UIElement panel, int x, int y) {
    ItemSlot slot = new ItemSlot();
    styleSlot(slot);
    return add(panel, slot, x - 1, y - 1);
  }

  public static <T, E> FilterSlot<T, E> filterSlotAt(FiltereableEntity<T, E> entity, UIElement panel, int x, int y) {
    var slot = new FilterSlot<>(entity);
    styleSlot(slot);
    slot.slotStyle(s -> s.acceptQuickMove(false));
    slot.bind(new FilterSlotComponent<>(entity, 0, x - 1, y -1));
    return add(panel, slot, x - 1, y - 1);
  }

  private static void styleSlot(ItemSlot slot) {
    slot.layout(layout -> layout.width(SLOT).height(SLOT).paddingAll(1).marginAll(0));
    slot.style(style -> style.background(slotTexture()));
    slot.slotStyle(s -> s.hoverOverlay(hoverOverlayTexture()));
  }

  public static IGuiTexture slotTexture() {
    return SpriteTexture.of(TEX_BASE_SLOT.toString());
  }

  public static ItemSlot slotWithBorder(UIElement panel, int x, int y, Identifier border) {
    return slotAt(panel, x, y).slotStyle(s ->
        s.slotOverlay(SpriteTexture.of(border.toString())).showSlotOverlayOnlyEmpty(false));
  }

  public static ItemSlot inputSlotAt(UIElement panel, int x, int y) {
    return slotWithBorder(panel, x, y, TEX_SLOT_BORDER_INPUT)
        .xeiRecipeIngredient(IngredientIO.INPUT);
  }

  public static ItemSlot outputSlotAt(UIElement panel, int x, int y) {
    return slotWithBorder(panel, x, y, TEX_SLOT_BORDER_OUTPUT)
        .xeiRecipeIngredient(IngredientIO.OUTPUT);
  }

  public static IGuiTexture hoverOverlayTexture() {
    return new GuiTextureGroup(
        SpriteTexture.of(TEX_BASE_SLOT_HOVERED.toString()),
        new ColorRectTexture(0x80FFFFFF));
  }

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

  public static UIElement label(UIElement panel, Component text, int x, int y) {
    return add(
        panel,
        new Label()
            .setText(text)
            .textStyle(style -> style
                .textWrap(TextWrap.WRAP)
                .adaptiveHeight(true)
                .adaptiveWidth(true)
            ),
        x,
        y
    );
  }

  public static Label captionLabel(UIElement panel, Component text, int x, int y) {
    Label label = new Label();
    label.setText(text);
    label.textStyle(style -> {
      style.textColor(0xFF404040);
      style.textShadow(false);
      style.adaptiveWidth(true);
      style.adaptiveHeight(true);
    });
    return add(panel, label, x, y);
  }

  public static Label inventoryLabel(UIElement panel, int x, int y) {
    return captionLabel(panel, Component.translatable("container.inventory"), x, y);
  }

  public static Label syncedLabel(UIElement panel, Supplier<Component> text, int x, int y) {
    Label label = new Label();
    label.bind(DataBindingBuilder.componentS2C(text).build());
    return add(panel, label, x, y);
  }

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

  public static <T extends Slider> T withScrollerThumb(T slider) {
    slider.handle.buttonStyle(style -> {
      var thumb = spriteNineSlice(TEX_SMALL_SCROLLER, 3, 4, 3, 4);
      style.baseTexture(thumb);
      style.hoverTexture(thumb);
      style.pressedTexture(thumb);
    });
    return slider;
  }
}
