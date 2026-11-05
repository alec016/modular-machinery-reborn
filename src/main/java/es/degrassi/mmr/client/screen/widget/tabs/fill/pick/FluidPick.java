package es.degrassi.mmr.client.screen.widget.tabs.fill.pick;

import com.lowdragmc.lowdraglib2.gui.ui.rendering.GUIContext;
import com.lowdragmc.lowdraglib2.gui.util.DrawerHelperClient;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.List;

public record FluidPick(FluidStack stack, FluidResource resource) implements Pick {
  public static FluidPick of(FluidStack stack) {
    return new FluidPick(stack, FluidResource.of(stack));
  }

  @Override
  public void addLines(List<Component> lines) {
    lines.add(stack.getHoverName());
  }

  @Override
  public void draw(GUIContext gui, int x, int y) {
    DrawerHelperClient.drawFluidForGui(gui, stack, x, y, 16, 16, -1);
  }
}
