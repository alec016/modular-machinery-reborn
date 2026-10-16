package es.degrassi.mmr.common.integration.jei.ingredient.ingredients;

import es.degrassi.experiencelib.api.codec.NamedCodec;

public record Energy(long amount) {
  public static final NamedCodec<Energy> CODEC = NamedCodec.record(instance -> instance.group(
      NamedCodec.longRange(0, Long.MAX_VALUE).fieldOf("amount").forGetter(Energy::amount)
  ).apply(instance, Energy::new), "Energy");
}
