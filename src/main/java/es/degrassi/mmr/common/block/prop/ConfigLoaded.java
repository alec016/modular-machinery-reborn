package es.degrassi.mmr.common.block.prop;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.Map;

public interface ConfigLoaded {
  Map<Class<? extends Enum<?>>, ValueSetter<?>> setters = Maps.newHashMap();
  Map<Class<? extends Enum<?>>, IConfigSpec> specs = Maps.newHashMap();

  static <T extends Enum<T> & ConfigLoaded> void add(Class<T> clazz, ValueSetter<T> consumer) {
    setters.put(clazz, consumer);
    specs.putIfAbsent(clazz, clazz.getEnumConstants()[0].get());
  }

  static void load() {
    setters.forEach(ConfigLoaded::loadFromConfig);
  }

  static void load(IConfigSpec spec) {
    specs.entrySet()
        .stream()
        .filter(entry -> entry.getValue() == spec)
        .forEach(entry -> {
          setters.entrySet().stream()
              .filter(en -> en.getKey() == entry.getKey())
              .forEach(en -> loadFromConfig(en.getKey(), en.getValue()));
        });
  }

  IConfigSpec get();

  @SuppressWarnings("unchecked")
  static <T extends Enum<T> & ConfigLoaded> void loadFromConfig(Class<?> clazz, ValueSetter<T> consumer) {
    for (T value : ((Class<T>) clazz).getEnumConstants()) {
      consumer.set(value);
    }
  }

  interface ValueSetter<T extends Enum<T> & ConfigLoaded> {
    void set(T value);
  }
}
