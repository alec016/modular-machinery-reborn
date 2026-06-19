package es.degrassi.mmreborn.mixin.emi;

import appeng.api.stacks.AEKey;
import appeng.menu.AEBaseMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import es.degrassi.mmreborn.common.crafting.requirement.emi.EmiComponent;
import es.degrassi.mmreborn.common.crafting.requirement.emi.IngredientHolder;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;

@Mixin(targets = "appeng.integration.modules.emi.AbstractRecipeHandler$Result$EncodeWithCraftables", remap = false)
public abstract class Ae2AbstractRecipeHandlerMixin {
  @Shadow
  private static boolean isCraftable(Set<AEKey> craftableKeys, EmiIngredient ingredient) {
    throw new UnsupportedOperationException("Implemented via mixin");
  }

  @Shadow
  @Final
  private Set<AEKey> craftableKeys;

  @Inject(
      at = @At(value = "HEAD"),
      method = "render"
  )
  private void onRender(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                               GuiGraphics guiGraphics, CallbackInfo ci) {
    for (var widget : widgets) {
      if (widget instanceof EmiComponent<?,?> emic && emic instanceof IngredientHolder rh) {
        if (isCraftable(craftableKeys, rh.getIngredient())) {
          var poseStack = guiGraphics.pose();
          poseStack.pushPose();
          poseStack.translate(0, 0, 400);
          var bounds = mmr$getInnerBounds(emic);
          guiGraphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(),
              BLUE_SLOT_HIGHLIGHT_COLOR);
          poseStack.popPose();
        }
      }
    }
  }

  @Unique
  private static Bounds mmr$getInnerBounds(EmiComponent<?, ?> slot) {
    var bounds = slot.getBounds();
    return new Bounds(
        bounds.x() + 1,
        bounds.y() + 1,
        bounds.width(),
        bounds.height());
  }
}
