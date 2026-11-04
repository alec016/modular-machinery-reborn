package es.degrassi.mmr.client.screen.widget;

import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.ui.rendering.IGUIContext;
import es.degrassi.mmr.client.screen.util.NumberFormats;
import es.degrassi.mmr.client.screen.widget.gauges.GaugesTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Item slot that renders its amount the way AE2 does.
 *
 * <p>The vanilla item stack cannot hold more than 99 items in its data form and the vanilla count
 * decoration only fits a handful of digits, while MMR handlers can be configured with far more items
 * per slot. This widget therefore draws the item decorations itself (the durability/chemical bar
 * and the cooldown overlay) and the amount with its own text: scaled down so it always fits into the
 * slot, the compact form for bigger amounts and the exact amount in the tooltip.</p>
 */
public class CountItemSlot extends ItemSlot {
  public CountItemSlot() {
    super();
  }

  public CountItemSlot(Slot slot) {
    super(slot);
  }

  @Override
  protected void drawItemStack(IGUIContext context, ItemStack itemStack) {
    if (!(context instanceof GUIContext guiContext)) {
      return;
    }
    var graphics = guiContext.graphics;
    graphics.item(itemStack, 0, 0);
    drawBar(guiContext, itemStack);
    drawCooldown(guiContext, itemStack);
    int amount = amount();
    if (amount <= 1) {
      return;
    }
    String text = countText(amount);
    var font = guiContext.mc.font;
    float width = font.width(text);
    // Scale the number down until it fits the slot.
    float scale = Math.clamp(getContentWidth() / Math.max(width, 1F), 0f, 1f);
    float x = getContentWidth() - width * scale;
    float y = getContentHeight() + 1 - font.lineHeight * scale;
    guiContext.pose.pushPose();
    guiContext.pose.translate(x, y);
    guiContext.pose.scale(scale, scale);
    graphics.text(font, text, 0, 0, -1, true);
    guiContext.pose.popPose();
  }

  /** The item's bar (durability, chemical tank color, ...), like the vanilla decoration. */
  private void drawBar(GUIContext guiContext, ItemStack itemStack) {
    if (!itemStack.isBarVisible()) {
      return;
    }
    var graphics = guiContext.graphics;
    int left = 2;
    int top = 13;
    graphics.fill(RenderPipelines.GUI, left, top, left + 13, top + 2, -16777216);
    graphics.fill(RenderPipelines.GUI, left, top, left + itemStack.getBarWidth(), top + 1, ARGB.opaque(itemStack.getBarColor()));
  }

  /** The cooldown overlay, like the vanilla decoration. */
  private void drawCooldown(GUIContext guiContext, ItemStack itemStack) {
    var player = guiContext.mc.player;
    float cooldown = player == null
        ? 0.0F
        : player.getCooldowns().getCooldownPercent(itemStack, guiContext.mc.getDeltaTracker().getGameTimeDeltaPartialTick(true));
    if (cooldown <= 0.0F) {
      return;
    }
    int top = Mth.floor(16.0F * (1.0F - cooldown));
    int bottom = top + Mth.ceil(16.0F * cooldown);
    guiContext.graphics.fill(RenderPipelines.GUI, 0, top, 16, bottom, Integer.MAX_VALUE);
  }

  /** The amount drawn on the slot: exact while it fits, compact for bigger amounts. */
  public static String countText(int amount) {
    if (amount < 1000) {
      return String.valueOf(amount);
    }
    return NumberFormats.compact(amount);
  }

  /** The amount the slot really holds. */
  protected int amount() {
    var slot = getSlot();
    var stack = slot.getItem();
    return stack.isEmpty() ? 0 : stack.getCount();
  }

  @Override
  public List<Component> getFullTooltipTexts(boolean withItemTooltips) {
    var texts = new ArrayList<>(super.getFullTooltipTexts(withItemTooltips));
    int amount = amount();
    if (amount > 999) {
      texts.add(GaugesTexts.experienceTooltip.text(
          Component.literal(NumberFormats.grouped(amount)).withStyle(ChatFormatting.GOLD),
          Component.literal(NumberFormats.grouped(getSlot().getMaxStackSize())).withStyle(ChatFormatting.GOLD)
      ).withStyle(ChatFormatting.GRAY));
    }
    return texts;
  }
}
