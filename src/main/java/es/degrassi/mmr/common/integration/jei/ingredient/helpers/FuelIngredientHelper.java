package es.degrassi.mmr.common.integration.jei.ingredient.helpers;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Energy;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Fuel;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class FuelIngredientHelper implements IIngredientHelper<Fuel> {

  @Override
  public IIngredientType<Fuel> getIngredientType() {
    return CustomIngredientTypes.FUEL;
  }

  @Override
  public String getDisplayName(Fuel value) {
    return Component.translatable("modular_machinery_reborn.jei.ingredient.fuel", value).getString();
  }

  @Override
  public Object getUid(Fuel value, UidContext context) {
    return value.toString();
  }

  @Override
  public Fuel copyIngredient(Fuel value) {
    return new Fuel(value.amount());
  }

  @Override
  public String getErrorInfo(@Nullable Fuel value) {
    return "";
  }

  @Override
  public Identifier getIdentifier(Fuel value) {
    return ModularMachineryReborn.rl("fuel");
  }
}
