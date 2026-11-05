package es.degrassi.mmr.client.screen.widget.tabs.fill;

import com.lowdragmc.lowdraglib2.gui.ui.elements.Slider;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import es.degrassi.mmr.api.codec.BigStackCodecs;
import es.degrassi.mmr.client.screen.util.MMRMachineUI;
import es.degrassi.mmr.client.screen.widget.tabs.TabTexts;
import es.degrassi.mmr.client.screen.widget.tabs.fill.kind.DurabilityKind;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.ItemPick;
import es.degrassi.mmr.common.entity.base.TileInventory;
import es.degrassi.mmr.common.manager.handler.ItemHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class DurabilityFillTab extends AbstractFillTab<ItemHandler, ItemStack, ItemPick> {
  private static final int DURAB_LABEL_Y = 60;
  private static final int DURAB_ROW_Y = 70;
  private static final int DURAB_FIELD_W = 56;
  private static final int DURAB_SLIDER_X = CX + DURAB_FIELD_W + 4;
  private static final int DURAB_SLIDER_W = 64;
  private static final int DURAB_PICK_LABEL_Y = 90;
  private static final int DURAB_PICK_FIELD_Y = 100;
  private static final int DURAB_PICK_GRID_Y = 118;
  private static final int DURAB_PICK_BUTTON_Y = 174;

  private int durability = -1;
  @Nullable
  private TextField durabField;
  @Nullable
  private Slider durabSlider;

  DurabilityFillTab(
      @Nullable Player player,
      BlockEntity entity,
      @Nullable Supplier<ItemHandler> handler,
      @Nullable Predicate<ItemStack> filter
  ) {
    super(player, entity, new DurabilityKind(FILL_DURABILITY_OPEN_HEIGHT, filter), handler, null, filter);
  }

  @Override
  protected void updateActive(boolean disable) {
    if (durabField != null) durabField.setActive(!disable);
    if (durabSlider != null) durabSlider.setActive(!disable);
  }

  @Override
  protected boolean isFull() {
    return selected != null && currentMax() <= 0;
  }

  @Override
  protected long currentMax() {
    if (selected == null) {
      return 0L;
    }
    return handler == null || handler.get() == null ? 0L : handler.get().getSpaceForItem(selected.stack());
  }

  @Override
  protected void additionalFields() {
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

  @Override
  protected void additionalClear() {
    durabField = null;
    durabSlider = null;
  }

  @Override
  protected void addPicker() {
    defaultPicket(DURAB_PICK_LABEL_Y, DURAB_PICK_FIELD_Y, DURAB_PICK_GRID_Y, DURAB_PICK_BUTTON_Y);
  }

  @Override
  protected CompoundTag encodePick() {
    var tag = super.encodePick();
    if (selected != null) {
      var item = selected.stack().copy();
      int remaining = durability < 0 ? item.getMaxDamage() : Mth.clamp(durability, 0, item.getMaxDamage());
      item.setDamageValue(item.getMaxDamage() - remaining);
      tag.store("item", BigStackCodecs.OPTIONAL.codec(), item);
      tag.putInt("durability_sent", remaining);
    }
    return tag;
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

  /**
   * Whether the grid of this hatch is still compiling: its slots do not exist yet, so filling it
   * is not possible.
   */
  protected boolean isCompiling() {
    if (handler != null && handler.get() != null && handler.get().isCompiling()) {
      return true;
    }
    return entity instanceof TileInventory inventory && inventory.getInventory().isCompiling();
  }

  @Override
  protected void choose(ItemPick pick) {
    super.choose(pick);
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
