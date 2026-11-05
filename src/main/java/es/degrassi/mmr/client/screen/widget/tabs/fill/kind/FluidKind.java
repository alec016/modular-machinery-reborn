package es.degrassi.mmr.client.screen.widget.tabs.fill.kind;

import es.degrassi.mmr.client.screen.widget.tabs.fill.pick.FluidPick;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class FluidKind extends Kind<FluidPick, FluidStack> {
  private final Predicate<FluidStack> filter;
  public FluidKind(int height, Predicate<FluidStack> filter) {
    super("fluid", height, true);
    this.filter = filter;
  }

  protected List<FluidStack> displayFluidStacks(BlockEntity entity) {
    if (displayCache != null) return displayCache;
    var stacks = new ArrayList<FluidStack>();
    var level = entity.getLevel();
    if (level != null) {
      var fluids = BuiltInRegistries.FLUID.stream()
          .filter(f -> f.isSource(f.defaultFluidState()))
          .map(f -> new FluidStack(f, 1))
          .toList();
      var seen = new HashSet<FluidResource>();
      for (var fluid : fluids) {
        if (fluid.isEmpty() || !seen.add(FluidResource.of(fluid))) continue;
        stacks.add(fluid.copyWithAmount(1));
      }
    }
    displayCache = List.copyOf(stacks);
    return displayCache;
  }

  @Override
  public void candidates(List<FluidPick> result, boolean byMod, boolean byTag, String needle, BlockEntity entity, @Nullable Player player) {
    List<FluidStack> display = displayFluidStacks(entity);
    if (byTag) {
      collectTagCandidates(needle, display, result);
    } else if (display.isEmpty()) {
      for (Fluid fluid : BuiltInRegistries.FLUID) {
        if (!fluid.isSource(fluid.defaultFluidState())) continue;
        if (result.size() >= MAX_CANDIDATES) break;
        FluidStack template = new FluidStack(fluid, 1);
        if (!template.isEmpty()
            && (filter == null || filter.test(template))
        && matches(byMod, template.getHoverName().getString(),
        BuiltInRegistries.FLUID.getKey(fluid), needle)) {
          result.add(FluidPick.of(template));
        }
      }
    } else {
      for (FluidStack template : display) {
        if (result.size() >= MAX_CANDIDATES) break;
        if ((filter == null || filter.test(template))
        && matches(byMod, template.getHoverName().getString(),
            BuiltInRegistries.FLUID.getKey(template.getFluid()), needle)) {
          result.add(FluidPick.of(template));
        }
      }
    }
  }

  protected void collectTagCandidates(String needle, List<FluidStack> display, List<FluidPick> result) {
    Map<Fluid, FluidStack> preferred = new HashMap<>();
    display.forEach(s -> preferred.putIfAbsent(s.getFluid(), s));
    Set<Fluid> seen = new LinkedHashSet<>();
    BuiltInRegistries.FLUID.getTags().forEach(named -> {
      if (result.size() >= MAX_CANDIDATES || !tagMatches(named.key().location(), needle)) return;
      for (var holder : named.stream().toList()) {
        if (result.size() >= MAX_CANDIDATES) return;
        var fluid = holder.value();
        if (fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource() || !seen.add(fluid)) continue;
        FluidStack template = preferred.getOrDefault(fluid, FluidStack.EMPTY);
        if (template.isEmpty() || (filter != null && !filter.test(template))) continue;
        result.add(FluidPick.of(new FluidStack(fluid, 1)));
      }
    });
  }
}
