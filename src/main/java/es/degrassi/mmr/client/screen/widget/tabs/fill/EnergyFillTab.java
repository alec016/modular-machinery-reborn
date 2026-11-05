package es.degrassi.mmr.client.screen.widget.tabs.fill;

import es.degrassi.mmr.client.screen.widget.tabs.fill.kind.EnergyKind;
import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.Pick;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.function.LongSupplier;

public class EnergyFillTab extends AbstractFillTab<Object, Object, Pick> {
  protected EnergyFillTab(@Nullable Player player, BlockEntity entity, @Nullable LongSupplier simpleSpace) {
    super(player, entity, new EnergyKind(), null, simpleSpace, null);
  }

  @Override
  protected long currentMax() {
    return space == null ? 0 : Math.max(0, space.getAsLong());
  }

  @Override
  protected void additionalFields() {}

  @Override
  protected void updateActive(boolean disable) {}

  @Override
  protected boolean isFull() {
    return currentMax() <= 0;
  }
}
