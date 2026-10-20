package es.degrassi.mmr.data.damagetypes;

import com.google.common.base.Preconditions;
import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class DamageTypeProvider implements DataProvider {
  private final PackOutput.PathProvider pathOutput;
  private boolean wasSaved = false;
  private final Map<Identifier, DamageType> damageTypes = new HashMap<>();
  public DamageTypeProvider(PackOutput output) {
    pathOutput = output.createPathProvider(PackOutput.Target.DATA_PACK, "damage_type");
  }

  public abstract void registerDamageTypes();

  public final void register(String id, DamageType type) {
    Preconditions.checkState(!wasSaved, "Cannot add more damage types after they were already saved");
    register(ModularMachineryReborn.rl(id), type);
  }

  public final void register(Identifier id, DamageType type) {
    Preconditions.checkState(!wasSaved, "Cannot add more damage types after they were already saved");
    if (damageTypes.put(id, type) != null) {
      throw new IllegalStateException("Duplicated damage type key " + id);
    }
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cachedOutput) {
    registerDamageTypes();
    wasSaved = true;
    return CompletableFuture.allOf(
        DataProvider.saveAll(
            cachedOutput,
            DamageType.DIRECT_CODEC,
            pathOutput::json,
            damageTypes
        )
    );
  }

  @Override
  public String getName() {
    return "DamageType";
  }
}
