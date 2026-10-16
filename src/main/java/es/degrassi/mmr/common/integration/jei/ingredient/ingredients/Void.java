package es.degrassi.mmr.common.integration.jei.ingredient.ingredients;

import es.degrassi.experiencelib.api.codec.NamedCodec;

public record Void(java.lang.Void value) {
  public static final NamedCodec<Void> CODEC = NamedCodec.record(instance -> instance.group(
     NamedCodec.VOID.fieldOf("value").forGetter(Void::value)
  ).apply(instance, Void::new), "Void");
}
