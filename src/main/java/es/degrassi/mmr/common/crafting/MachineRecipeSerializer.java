package es.degrassi.mmr.common.crafting;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MachineRecipeSerializer {
  public static final RecipeSerializer<MachineRecipe> INSTANCE = new RecipeSerializer<>(
      MachineRecipe.CODEC.mapCodec().xmap(MachineRecipe.MachineRecipeBuilder::build, MachineRecipe.MachineRecipeBuilder::new),
      ByteBufCodecs.fromCodecWithRegistries(
          MachineRecipe.CODEC.mapCodec().xmap(MachineRecipe.MachineRecipeBuilder::build, MachineRecipe.MachineRecipeBuilder::new).codec()
      )
  );
}
