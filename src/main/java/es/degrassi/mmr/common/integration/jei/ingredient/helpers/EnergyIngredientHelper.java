package es.degrassi.mmr.common.integration.jei.ingredient.helpers;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.integration.jei.ingredient.CustomIngredientTypes;
import es.degrassi.mmr.common.integration.jei.ingredient.ingredients.Energy;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class EnergyIngredientHelper implements IIngredientHelper<Energy> {

  @Override
  public IIngredientType<Energy> getIngredientType() {
    return CustomIngredientTypes.ENERGY;
  }

  @Override
  public String getDisplayName(Energy value) {
    return Component.translatable("modular_machinery_reborn.jei.ingredient.energy", value).getString();
  }

  @Override
  public Object getUid(Energy value, UidContext context) {
    return value.toString();
  }

  @Override
  public Energy copyIngredient(Energy value) {
    return new Energy(value.amount());
  }

  @Override
  public String getErrorInfo(@Nullable Energy value) {
    return "";
  }

  @Override
  public Identifier getIdentifier(Energy value) {
    return ModularMachineryReborn.rl("energy");
  }
}
