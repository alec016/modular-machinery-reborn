package es.degrassi.mmr.common.integration.kubejs.components;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;
import es.degrassi.experiencelib.api.codec.DefaultCodecs;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record IdentifierComponent(ResourceKey<RecipeComponentType<?>> type) implements RecipeComponent<Identifier> {
  public static final IdentifierComponent IDENTIFIER = new IdentifierComponent(RecipeComponentType.key(ModularMachineryReborn.rl("rl")));

  @Override
  public Codec<Identifier> codec() {
    return DefaultCodecs.RESOURCE_LOCATION.codec();
  }

  @Override
  public TypeInfo typeInfo() {
    return TypeInfo.of(Identifier.class);
  }
}
