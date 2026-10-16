package es.degrassi.mmr.common.integration.jei.ingredient.ingredients;

import es.degrassi.experiencelib.api.codec.NamedCodec;

public record Fuel(long amount) {
  public static final NamedCodec<Fuel> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(Fuel::amount)
  ).apply(instance, Fuel::new), "Fuel");
}
