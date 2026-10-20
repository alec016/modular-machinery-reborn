package es.degrassi.mmreborn.common.block.prop;

import com.google.common.collect.Maps;

import java.util.Map;

public interface ConfigLoaded {
  Map<Class<? extends Enum<?>>, ValueSetter<?>> setters = Maps.newHashMap();

  static <T extends Enum<T> & ConfigLoaded> void add(Class<T> clazz, ValueSetter<T> consumer) {
    setters.put(clazz, consumer);
  }

  static void load() {
    setters.forEach(ConfigLoaded::loadFromConfig);
  }

  @SuppressWarnings("unchecked")
  static <T extends Enum<T> & ConfigLoaded> void loadFromConfig(Class<?> clazz, ValueSetter<T> consumer) {
    for (T value : ((Class<T>) clazz).getEnumConstants()) {
      consumer.set(value);
    }
  }

  @FunctionalInterface
  interface ValueSetter<T extends Enum<T> & ConfigLoaded> {
    void set(T value);
  }
}
