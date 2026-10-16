package es.degrassi.mmr.common.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import es.degrassi.mmr.ModularMachineryReborn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = ModularMachineryReborn.MODID)
public class TaskDelayer {
  private TaskDelayer() {}

  private static final List<Pair<AtomicInteger, Runnable>> tasks = Lists.newArrayList();

  @SubscribeEvent
  public static void serverTick(final ServerTickEvent.Post event) {
    Iterator<Pair<AtomicInteger, Runnable>> iterator = tasks.iterator();
    while (iterator.hasNext()) {
      Pair<AtomicInteger, Runnable> pair = iterator.next();
      if (pair.getFirst().addAndGet(-1) < 0) {
        pair.getSecond().run();
        iterator.remove();
      }
    }
  }

  public static void enqueue(int ticks, Runnable task) {
    tasks.add(Pair.of(new AtomicInteger(ticks), task));
  }
}
