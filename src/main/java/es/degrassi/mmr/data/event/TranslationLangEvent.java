package es.degrassi.mmr.data.event;

import es.degrassi.mmr.data.lang.Lang;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.NoArgsConstructor;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.EnumMap;
import java.util.Map;

@NoArgsConstructor
public class TranslationLangEvent extends Event implements IModBusEvent {
  private final Map<Lang, Map<String, String>> translations = new EnumMap<>(Lang.class);

  public void add(Lang lang, String key, String value) {
    translations.computeIfAbsent(lang, _ -> new Object2ObjectArrayMap<>()).put(key, value);
  }

  public Map<String, String> get(Lang lang) {
    return translations.get(lang);
  }
}
