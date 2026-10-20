package es.degrassi.mmreborn.common.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmreborn.common.entity.EffectDispenserEntity;
import es.degrassi.mmreborn.common.entity.FuelTankEntity;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.entity.RedstonePortEntity;
import es.degrassi.mmreborn.common.entity.base.ColorableMachineEntity;
import es.degrassi.mmreborn.common.entity.base.EnergyHatchEntity;
import es.degrassi.mmreborn.common.entity.base.ExperienceHatchEntity;
import es.degrassi.mmreborn.common.entity.base.FluidTankEntity;
import es.degrassi.mmreborn.common.entity.base.TileInventory;
import es.degrassi.mmreborn.common.manager.handler.FluidHandler;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Arrays;

public class RedstoneHelper {
  private RedstoneHelper() {}
  public static int getRedstoneLevel(@Nullable BlockEntity sync) {
    if (sync == null) return 0;
    return switch (sync) {
      case MachineControllerEntity entity -> {
        if (entity.getStatus().isCrafting()) yield 15;
        if (!entity.getStatus().isMissingStructure()) yield 1;
        yield 0;
      }
      case FluidTankEntity ft -> {
        FluidHandler tank = ft.getTank();
        float cap = tank.getCapacity();
        float cur = tank.getFluidAmount();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      case EnergyHatchEntity entity -> {
        float cap = entity.getMaxEnergy();
        float cur = entity.getCurrentEnergy();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      case ExperienceHatchEntity entity -> {
        float cap = entity.getTank().getExperienceCapacity();
        float cur = entity.getTank().getExperience();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      case FuelTankEntity entity -> {
        float cap = entity.getFuelHandler().getMaxFuel();
        float cur = entity.getFuelHandler().getFuel();
        yield Mth.clamp(Math.round(15F * (cur / cap)), 0, 15);
      }
      case EffectDispenserEntity entity -> entity.isApplyingEffect() ? 15 : 0;
      case TileInventory entity -> entity.getInventory().calcRedstoneFromInventory();
      default -> 0;
    };
  }

  public static int getReceivingRedstone(@Nullable BlockEntity sync) {
    if (sync == null || sync.getLevel() == null) return 0;
    return switch (sync) {
      case MachineControllerEntity entity ->
          Arrays.stream(Direction.values())
              .map(dir -> Pair.of(dir, entity.getBlockPos().relative(dir)))
              .filter(pair ->
                  Arrays.stream(Direction.values())
                    .map(pair.getSecond()::relative)
                    .map(pos -> entity.getLevel().getBlockEntity(pos))
                    .noneMatch(o -> o instanceof RedstonePortEntity)
              )
              .mapToInt(pair -> entity.getLevel().getSignal(pair.getSecond(), pair.getFirst()))
              .max()
              .orElse(0);
      case FluidTankEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case EnergyHatchEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case ExperienceHatchEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case FuelTankEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case EffectDispenserEntity entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case TileInventory entity -> entity.getLevel().getBestNeighborSignal(entity.getBlockPos());
      case RedstonePortEntity entity -> Arrays.stream(Direction.values())
          .map(dir -> Pair.of(dir, entity.getBlockPos().relative(dir)))
          .map(pair -> {
            var filtered = Lists.newArrayList(Arrays.stream(Direction.values())
                .map(pair.getSecond()::relative)
                .filter(pos -> !(entity.getLevel().getBlockEntity(pos) instanceof ColorableMachineEntity))
                .iterator());
            filtered.add(pair.getSecond());
            return Pair.of(pair.getFirst(), Lists.newArrayList(filtered.iterator()));
          })
          .mapToInt(pair ->
            pair.getSecond().stream()
                .mapToInt(pos -> entity.getLevel().getSignal(pos, pair.getFirst()))
                .max()
                .orElse(0)
          )
          .max()
          .orElse(0);
      default -> 0;
    };
  }
}
