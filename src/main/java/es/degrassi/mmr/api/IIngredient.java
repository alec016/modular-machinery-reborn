package es.degrassi.mmr.api;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Rotation;

import java.util.List;
import java.util.function.Predicate;

public interface IIngredient<O, P> extends Predicate<P> {
  List<O> getAll();

  IIngredient<O, P> copy();

  IIngredient<O, P> copyWithRotation(Rotation rotation);

  JsonObject asJson();
}
