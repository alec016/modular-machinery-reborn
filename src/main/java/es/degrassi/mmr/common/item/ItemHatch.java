package es.degrassi.mmr.common.item;

import es.degrassi.mmr.common.registration.DataComponentRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ItemHatch {
  Identifier getDefaultBaseTexture();
  Identifier getDefaultOverlayTexture();

  static @Nullable Identifier getBaseTexture(ItemStack stack) {
    if (!stack.has(DataComponentRegistration.BASE_TEXTURE)) return null;
    return stack.get(DataComponentRegistration.BASE_TEXTURE);
  }

  static @Nullable Identifier getOverlayTexture(ItemStack stack) {
    if (!stack.has(DataComponentRegistration.OVERLAY_TEXTURE)) return null;
    return stack.get(DataComponentRegistration.OVERLAY_TEXTURE);
  }

  static void resetToDefault(ItemStack stack) {
    resetBaseTexture(stack);
    resetOverlayTexture(stack);
  }

  static void resetBaseTexture(ItemStack stack) {
    if (stack.getItem() instanceof ItemHatch item)
      stack.set(DataComponentRegistration.BASE_TEXTURE, item.getDefaultBaseTexture());
  }

  static void resetOverlayTexture(ItemStack stack) {
    if (stack.getItem() instanceof ItemHatch item)
      stack.set(DataComponentRegistration.OVERLAY_TEXTURE, item.getDefaultOverlayTexture());
  }

  static void setBaseTexture(ItemStack stack, Identifier texture) {
    stack.set(DataComponentRegistration.BASE_TEXTURE, texture);
  }

  static void setOverlayTexture(ItemStack stack, Identifier texture) {
    stack.set(DataComponentRegistration.OVERLAY_TEXTURE, texture);
  }
}
