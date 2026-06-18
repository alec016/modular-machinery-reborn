package es.degrassi.mmreborn.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.api.handler.FilterRendererRegistry;
import es.degrassi.mmreborn.client.container.ContainerBase;
import es.degrassi.mmreborn.client.container.FilterSlotComponent;
import es.degrassi.mmreborn.client.screen.widget.GuiElement;
import es.degrassi.mmreborn.client.screen.widget.IGuiWrapper;
import es.degrassi.mmreborn.client.screen.widget.tabs.TabGroupWidget;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineComponentEntity;
import es.degrassi.mmreborn.common.util.TextureSizeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class BaseScreen<T extends ContainerBase<E>, E extends ColorableMachineComponentEntity> extends AbstractContainerScreen<T> implements IGuiWrapper {
  public static final ResourceLocation BASE_SLOT = ModularMachineryReborn.rl("textures/gui/base_slot.png");
  public static final ResourceLocation BASE_SLOT_HOVERED = ModularMachineryReborn.rl("textures/gui/base_slot_hovered.png");
  public static final ResourceLocation TAB = ModularMachineryReborn.rl("textures/gui/widget/base_tab_top.png");
  public static final ResourceLocation TAB_HOVERED = ModularMachineryReborn.rl("textures/gui/widget/base_tab_hovered_top.png");
  public static final ResourceLocation SCROLLBAR_BACKGROUND = ModularMachineryReborn.rl("small_scroller_disabled");
  public static final ResourceLocation SCROLLBAR_THUMB = ModularMachineryReborn.rl("small_scroller");

  public static final int SLOT_SIZE = 18;
  protected boolean hasClicked = false;

  protected final E entity;
  protected final boolean shouldRenderLabels;
  protected BaseScreen(T menu, Inventory playerInventory, Component title, boolean renderLabels) {
    super(menu, playerInventory, title);
    this.entity = menu.getEntity();
    this.shouldRenderLabels = renderLabels;
  }

  @Override
  public ItemStack getCarriedItem() {
    return getMenu().getCarried();
  }

  @Override
  public @Nullable BaseScreen<?, ?> getWindowHovering(double mouseX, double mouseY) {
    if (mouseX >= getGuiLeft() && mouseX <= getGuiLeft() + this.imageWidth && mouseY >= getGuiTop() && mouseY <= getGuiTop() + this.imageHeight
        || this.children().stream().anyMatch(child -> child.isMouseOver(mouseX, mouseY)))
      return this;
    return null;
  }

  public void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
    renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick, 0);
  }

  @Nullable
  public ResourceLocation getTexture() {
    return ModularMachineryReborn.rl("background");
  }

  public static ResourceLocation getScrollbarBackground() {
    return SCROLLBAR_BACKGROUND;
  }

  public static ResourceLocation getScrollbarThumb() {
    return SCROLLBAR_THUMB;
  }

  public static int getScrollbarBackgroundWidth() {
    return TextureSizeHelper.getWidth(ModularMachineryReborn.rl("textures/gui/sprites/" + getScrollbarBackground().getPath()));
  }

  public static int getScrollbarBackgroundHeight() {
    return TextureSizeHelper.getHeight(ModularMachineryReborn.rl("textures/gui/sprites/" + getScrollbarBackground().getPath()));
  }

  public static int getScrollbarWidth() {
    return TextureSizeHelper.getWidth(ModularMachineryReborn.rl("textures/gui/sprites/" + getScrollbarThumb().getPath()));
  }

  public static int getScrollbarHeight() {
    return TextureSizeHelper.getHeight(ModularMachineryReborn.rl("textures/gui/sprites/" + getScrollbarThumb().getPath()));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    int i = this.leftPos;
    int j = this.topPos;
    // Neo: replicate the super method's implementation to insert the event between background and widgets
    this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Background(this, guiGraphics, mouseX, mouseY));
    for (Renderable renderable : this.renderables) {
      renderable.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    RenderSystem.disableDepthTest();
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate((float)i, (float)j, 0.0F);
    this.hoveredSlot = null;

    for (int k = 0; k < this.menu.slots.size(); k++) {
      Slot slot = this.menu.slots.get(k);
      if (slot.isActive()) {
        this.renderSlot(guiGraphics, slot);
        if (this.isHovering(slot, mouseX, mouseY)) {
          this.hoveredSlot = slot;
          this.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
        }
      }
    }

    this.renderLabels(guiGraphics, mouseX, mouseY);
    NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(this, guiGraphics, mouseX, mouseY));
    ItemStack itemstack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
    if (!itemstack.isEmpty()) {
      int l1 = 8;
      int i2 = this.draggingItem.isEmpty() ? 8 : 16;
      String s = null;
      if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
        itemstack = itemstack.copyWithCount(Mth.ceil((float)itemstack.getCount() / 2.0F));
      } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
        itemstack = itemstack.copyWithCount(this.quickCraftingRemainder);
        if (itemstack.isEmpty()) {
          s = ChatFormatting.YELLOW + "0";
        }
      }

      this.renderFloatingItem(guiGraphics, itemstack, mouseX - i - 8, mouseY - j - i2, s);
    }

    if (!this.snapbackItem.isEmpty()) {
      float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
      if (f >= 1.0F) {
        f = 1.0F;
        this.snapbackItem = ItemStack.EMPTY;
      }

      int j2 = this.snapbackEnd.x - this.snapbackStartX;
      int k2 = this.snapbackEnd.y - this.snapbackStartY;
      int j1 = this.snapbackStartX + (int)((float)j2 * f);
      int k1 = this.snapbackStartY + (int)((float)k2 * f);
      this.renderFloatingItem(guiGraphics, this.snapbackItem, j1, k1, null);
    }

    guiGraphics.pose().popPose();
    RenderSystem.enableDepthTest();
    renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
    guiGraphics.blit(BaseScreen.BASE_SLOT, slot.x - 1, slot.y - 1, 0, 0, BaseScreen.SLOT_SIZE,
        BaseScreen.SLOT_SIZE,
        TextureSizeHelper.getWidth(BaseScreen.BASE_SLOT), TextureSizeHelper.getHeight(BaseScreen.BASE_SLOT));
    super.renderSlot(guiGraphics, slot);

  }

  @Override
  protected void renderSlotContents(GuiGraphics guiGraphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
    if (slot instanceof FilterSlotComponent<?, ?> fs) {
      if (FilterRendererRegistry.hasRenderer(fs.toFilterRender())) {
        FilterRendererRegistry.render(fs.toFilterRender(), guiGraphics, slot.x, slot.y);
        return;
      }
    }
    super.renderSlotContents(guiGraphics, itemstack, slot, countString);
    if (slot.isFake() && slot instanceof FilterSlotComponent<?, ?> && hoveredSlot != slot) {
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(0,0, 255);
      guiGraphics.fill(
          slot.x,
          slot.y,
          slot.x + 16,
          slot.y + 16,
          FastColor.ARGB32.color(255/2, 255, 255, 255)
      );
      guiGraphics.pose().popPose();
    }
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    if (this.shouldRenderLabels) super.renderLabels(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
    if (hoveredSlot instanceof FilterSlotComponent<?, ?> fs) {
      var comp = fs.getFilter();
      if (comp == null) return;
      guiGraphics.renderTooltip(Minecraft.getInstance().font, comp, x, y);
      return;
    }
    super.renderTooltip(guiGraphics, x, y);

    for (var element : children()) {
      if (element instanceof TabGroupWidget widget) {
        widget.renderTooltip(guiGraphics, x, y);
      }
    }
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    if (getTexture() != null) {
      guiGraphics.pose().pushPose();
      guiGraphics.setColor(1f, 1f, 1f, 1f);
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      guiGraphics.blit(getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight);
      guiGraphics.pose().popPose();
    }
  }

  protected void renderBgWithSlotSize(GuiGraphics guiGraphics, int cols, int slots) {
    if (getTexture() != null) {
      guiGraphics.pose().pushPose();
      guiGraphics.setColor(1f, 1f, 1f, 1f);
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      int slotsWidth = cols * 18 + 16;
      int invWidth = 18 * 9 + 16;
      int height =
          (int) Math.ceil(slots * 1D / cols) * 18 + 16
              + 18 * 4 + 3 + font.wordWrapHeight(title, Math.max(slotsWidth, invWidth) - 16) + titleLabelY;

      guiGraphics.blitSprite(getTexture(), leftPos, topPos, Math.max(slotsWidth, invWidth), height);
      guiGraphics.pose().popPose();
    }
  }

  protected void renderSlots(GuiGraphics guiGraphics) {
    for (Slot slot : getMenu().slots) {
      guiGraphics.blit(BASE_SLOT, slot.x + getGuiLeft() - 1, slot.y + getGuiTop() - 1, 0, 0,
          TextureSizeHelper.getWidth(BASE_SLOT),
          TextureSizeHelper.getHeight(BASE_SLOT),
          TextureSizeHelper.getWidth(BASE_SLOT),
          TextureSizeHelper.getHeight(BASE_SLOT));
    }
  }

  public static void renderSlotHighlight(GuiGraphics guiGraphics, int x, int y, int color, int z) {
    guiGraphics.pose().pushPose();
    int width = TextureSizeHelper.getWidth(BASE_SLOT_HOVERED), height = TextureSizeHelper.getHeight(BASE_SLOT_HOVERED);
    guiGraphics.blit(BASE_SLOT_HOVERED, x - 1, y - 1, 0, 0, width, height, width, height);
    guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, color, color, z);
    guiGraphics.pose().popPose();
  }

  public void setHoveredSlot(Slot slot) {
    this.hoveredSlot = slot;
  }

  public boolean childrenContainsElement(Predicate<GuiElement> predicate) {
    return children().stream()
        .filter(el -> el instanceof GuiElement)
        .map(el -> (GuiElement) el)
        .anyMatch(predicate);
  }

  @Override
  public int getSlotColor() {
    return this.slotColor;
  }
}
