package es.degrassi.mmr.client.screen.widget.tabs.fill;

import es.degrassi.mmr.client.screen.widget.tabs.fill.kind.FluidKind;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.FluidPick;
import es.degrassi.mmr.common.manager.handler.FluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class FluidFillTab extends AbstractFillTab<FluidHandler, FluidStack, FluidPick>{
  protected FluidFillTab(@Nullable Player player, BlockEntity entity, @Nullable Supplier<FluidHandler> handler) {
    super(player, entity, new FluidKind(FILL_OPEN_HEIGHT, s -> {
      if (handler == null || handler.get() == null) return true;
      return handler.get().isValid(0, FluidResource.of(s));
    }), handler, null, null);
  }

  @Override
  protected long currentMax() {
    return selected == null || handler == null || handler.get() == null
        ? 0L
        : handler.get().getSpaceForFluid(selected.stack());
  }

  @Override
  protected void additionalFields() {}

  @Override
  protected void updateActive(boolean disable) {}

  @Override
  protected boolean isFull() {
    return handler != null && handler.get() != null && handler.get().isFull();
  }

  @Override
  protected CompoundTag encodePick() {
    var tag = super.encodePick();
    if (selected != null) {
      var fluid = selected.stack().copy();
      tag.store("fluid", FluidStack.OPTIONAL_CODEC, fluid);
    }
    return tag;
  }
}
