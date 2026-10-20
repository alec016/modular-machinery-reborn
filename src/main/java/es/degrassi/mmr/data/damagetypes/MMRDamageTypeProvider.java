package es.degrassi.mmr.data.damagetypes;

import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class MMRDamageTypeProvider extends DamageTypeProvider {
  public MMRDamageTypeProvider(PackOutput output) {
    super(output);
  }

  @Override
  public void registerDamageTypes() {
    register("modular_machinery_damage", new DamageType("modular_machine", DamageScaling.NEVER, 0.1f));
  }
}
