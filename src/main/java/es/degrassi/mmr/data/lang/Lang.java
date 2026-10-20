package es.degrassi.mmr.data.lang;

import net.minecraft.data.DataGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public enum Lang {
  en_us(EnUsLocalization::new),
  es_es(EsEsLocalization::new),
  pt_bt(PtBrLocalization::new),
  ru_ru(RuRuLocalization::new),
  uk_ua(UkUaLocalization::new),
  zh_cn(ZhCnLocalization::new)
  ;
  private final Function<DataGenerator, LocalizationProvider> constructor;

  Lang(Function<DataGenerator, LocalizationProvider> constructor) {
    this.constructor = constructor;
  }

  private LocalizationProvider construct(DataGenerator generator) {
    return constructor.apply(generator);
  }

  public static List<? extends LocalizationProvider> getAll(DataGenerator generator) {
    return Arrays.stream(values()).map(l -> l.construct(generator)).toList();
  }

  public String getFile() {
    return "%s.json".formatted(name().toLowerCase(Locale.ENGLISH));
  }
}
