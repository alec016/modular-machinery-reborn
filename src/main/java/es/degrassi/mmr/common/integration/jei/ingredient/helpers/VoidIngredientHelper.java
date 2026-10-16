package es.degrassi.mmr.common.integration.jei.ingredient.helpers;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Void;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class VoidIngredientHelper implements IIngredientHelper<Void> {

  @Override
  public IIngredientType<Void> getIngredientType() {
    return CustomIngredientTypes.VOID;
  }

  @Override
  public String getDisplayName(Void value) {
    return Component.translatable("modular_machinery_reborn.jei.ingredient.void", value).getString();
  }

  @Override
  public Object getUid(Void value, UidContext context) {
    return value.toString();
  }

  @Override
  public Void copyIngredient(Void value) {
    return new Void(value.value());
  }

  @Override
  public String getErrorInfo(@Nullable Void value) {
    return "";
  }

  @Override
  public Identifier getIdentifier(Void ingredient) {
    return ModularMachineryReborn.rl("void");
  }
}
