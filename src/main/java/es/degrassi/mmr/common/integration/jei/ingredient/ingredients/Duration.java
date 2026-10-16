package es.degrassi.mmr.common.integration.jei.ingredient.ingredients;

import es.degrassi.experiencelib.api.codec.NamedCodec;

public record Duration(int amount) {
  public static final NamedCodec<Duration> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.intRange(0, Integer.MAX_VALUE).fieldOf("amount").forGetter(Duration::amount)
  ).apply(instance, Duration::new), "Duration");
}
