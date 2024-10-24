package es.degrassi.mmreborn.common.crafting.helper;

import es.degrassi.mmreborn.common.integration.ingredient.HybridFluid;
import net.minecraft.world.item.ItemStack;

public abstract class ComponentOutputRestrictor {

  public static class RestrictionTank extends ComponentOutputRestrictor {
    public final HybridFluid inserted;
    public final ProcessingComponent<?> exactComponent;

    public RestrictionTank(HybridFluid inserted, ProcessingComponent<?> exactComponent) {
      this.inserted = inserted;
      this.exactComponent = exactComponent;
    }
  }

  public static class RestrictionInventory extends ComponentOutputRestrictor {
    public final ItemStack inserted;
    public final ProcessingComponent<?> exactComponent;

    public RestrictionInventory(ItemStack inserted, ProcessingComponent<?> exactComponent) {
      this.inserted = inserted;
      this.exactComponent = exactComponent;
    }
  }

}
