package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import es.degrassi.mmr.common.block.base.SizedBlockHatch;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

@Getter
public class BlockCasing extends BlockMachineComponent implements SizedBlockHatch<BlockCasing.CasingType> {
  private final CasingType size;
  public BlockCasing(Properties id, CasingType size) {
    super(id);
    this.size = size;
  }

  @Override
  protected MapCodec<BlockCasing> codec() {
    return BlockCodecs.CASING.mapCodec();
  }

  @Override
  protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
    if (adjacentState.getBlock() instanceof BlockCasing bc && state.getBlock() instanceof BlockCasing bc1 && adjacentState.getRenderShape() == state.getRenderShape()) {
      return bc.getSize() == bc1.getSize();
    }
    return super.skipRendering(state, adjacentState, direction);
  }

  public enum CasingType implements StringRepresentable {
    PLAIN,
    VENT,
    FIREBOX,
    GEARBOX,
    REINFORCED,
    CIRCUITRY;

    @Override
    public String getSerializedName() {
      return name().toLowerCase();
    }

    public static CasingType value(String name) {
      return switch(name.toLowerCase(Locale.ROOT)) {
        case "vent" -> VENT;
        case "firebox" -> FIREBOX;
        case "gearbox" -> GEARBOX;
        case "reinforced" -> REINFORCED;
        case "circuitry" -> CIRCUITRY;
        default -> PLAIN;
      };
    }
  }

}
