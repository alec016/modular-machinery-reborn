package es.degrassi.mmr.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CycleTimer {
  /* the amount of time in ms to display one thing before cycling to the next one */
  @Getter
  private final Supplier<Integer> cycleTime;
  private final boolean ignoreShift;

  private long startTime;
  private long drawTime;
  private long pausedDuration = 0;

  public CycleTimer(Supplier<Integer> cycleTime, boolean ignoreShift) {
    this.cycleTime = cycleTime;
    long time = System.currentTimeMillis();
    this.startTime = time - cycleTime.get();
    this.drawTime = time;
    this.ignoreShift = ignoreShift;
  }

  @Nullable
  public <T> T get(List<T> list) {
    if (list.isEmpty()) {
      return null;
    }
    long index = ((drawTime - startTime) / cycleTime.get()) % list.size();
    return list.get(Math.toIntExact(index));
  }

  public <T> T getOrDefault(List<T> list, T defaultObject) {
    return Optional.ofNullable(get(list)).orElse(defaultObject);
  }

  public void onDraw() {
    if (ignoreShift) {
      if (pausedDuration > 0) {
        startTime += pausedDuration;
        pausedDuration = 0;
      }
      drawTime = System.currentTimeMillis();
    } else {
      if (!hasShiftDown()) {
        if (pausedDuration > 0) {
          startTime += pausedDuration;
          pausedDuration = 0;
        }
        drawTime = System.currentTimeMillis();
      } else {
        pausedDuration = System.currentTimeMillis() - drawTime;
      }
    }
  }

  private boolean hasShiftDown() {
    return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344);
  }
}
