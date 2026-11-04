package es.degrassi.mmr.mixin;

import es.degrassi.mmr.client.container.FilterSlotComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The ghost filter slot is fully client-managed: vanilla must never touch it (its menu slot is a
 * placeholder). Without this, {@code AbstractContainerScreen#mouseClicked} consumes the click in
 * its hovered-slot logic before the LD2 widget tree sees it, so the ghost placement could never
 * trigger. Here a click on the ghost asks the server through the filter packet and the vanilla
 * handling is cancelled entirely; the server-side slot rejects pickup/quick-move as a backstop.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class FilterSlotClickMixin {

  @Shadow
  @Nullable
  protected Slot hoveredSlot;

  @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
  private void mmr$filterSlotClick(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
    if (!(this.hoveredSlot instanceof FilterSlotComponent<?, ?> filterSlot)) {
      return;
    }
    var menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();
    ItemStack carried = menu.getCarried();
    if (!carried.isEmpty()) {
      filterSlot.setFromClient(carried);
    } else if (!filterSlot.getItem().isEmpty()) {
      filterSlot.setFromClient(ItemStack.EMPTY);
    }
    cir.setReturnValue(true);
  }
}
