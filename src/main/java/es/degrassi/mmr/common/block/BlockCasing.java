package es.degrassi.mmr.common.block;

import com.mojang.serialization.MapCodec;
import es.degrassi.mmr.common.block.base.BlockMachineComponent;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@Getter
public class BlockCasing extends BlockMachineComponent {
  private final CasingType casingType;
  public static final MapCodec<BlockMachineComponent> CODEC = simpleCodec((props) -> new BlockMachineComponent(props) {});
  public BlockCasing(CasingType type) {
    this.casingType = type;
  }

  @Override
  protected MapCodec<? extends Block> codec() {
    return CODEC;
  }

  @Override
  protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
    if (adjacentState.getBlock() instanceof BlockCasing bc && state.getBlock() instanceof BlockCasing bc1 && adjacentState.getRenderShape() == state.getRenderShape()) {
      return bc.getCasingType() == bc1.getCasingType();
    }
    return super.skipRendering(state, adjacentState, direction);
  }

  protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
    return Shapes.empty();
  }

  public enum CasingType implements StringRepresentable {
    PLAIN,
    VENT,
    FIREBOX,
    GEARBOX,
    REINFORCED,
    CIRCUITRY;

    @Override
    public @NotNull String getSerializedName() {
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
