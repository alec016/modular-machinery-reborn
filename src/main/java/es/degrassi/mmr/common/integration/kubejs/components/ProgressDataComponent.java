package es.degrassi.mmr.common.integration.kubejs.components;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;
import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.crafting.helper.ProgressData;
import net.minecraft.resources.ResourceKey;

public record ProgressDataComponent(ResourceKey<RecipeComponentType<?>> type) implements RecipeComponent<ProgressData> {
  public static final ProgressDataComponent PROGRESS_DATA = new ProgressDataComponent(RecipeComponentType.key(ModularMachineryReborn.rl("progress_data")));

  @Override
  public Codec<ProgressData> codec() {
    return ProgressData.CODEC.codec();
  }

  @Override
  public TypeInfo typeInfo() {
    return TypeInfo.of(ProgressData.class);
  }
}
