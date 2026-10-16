package es.degrassi.mmr.client;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Iterator;
import java.util.Map;

public class ClientScheduler {

  @Getter
  private static long clientTick = 0;
  private static final Object lock = new Object();

  private boolean inTick = false;
  private final Map<Runnable, Counter> queuedRunnables = Maps.newHashMap();
  private final Map<Runnable, Integer> waitingRunnables = Maps.newHashMap();

  @SubscribeEvent
  public void tick(ClientTickEvent.Pre event) {
    clientTick++;

    inTick = true;
    synchronized (lock) {
      inTick = true;
      Iterator<Runnable> iterator = queuedRunnables.keySet().iterator();
      while (iterator.hasNext()) {
        Runnable r = iterator.next();
        Counter delay = queuedRunnables.get(r);
        delay.decrement();
        if (delay.value <= 0) {
          r.run();
          iterator.remove();
        }
      }
      inTick = false;
      for (Map.Entry<Runnable, Integer> waiting : waitingRunnables.entrySet()) {
        queuedRunnables.put(waiting.getKey(), new Counter(waiting.getValue()));
      }
    }
    waitingRunnables.clear();
  }

  public void addRunnable(Runnable r, int tickDelay) {
    synchronized (lock) {
      if (inTick) {
        waitingRunnables.put(r, tickDelay);
      } else {
        queuedRunnables.put(r, new Counter(tickDelay));
      }
    }
  }

  public static class Counter {
    public int value;

    public Counter(int value) {
      this.value = value;
    }

    public void decrement() {
      value--;
    }

    public void increment() {
      value++;
    }

  }
}
