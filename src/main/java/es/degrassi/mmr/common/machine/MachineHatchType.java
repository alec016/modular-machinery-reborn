package es.degrassi.mmr.common.machine;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public class MachineHatchType {
  public static final ResourceKey<Registry<MachineHatchType>> REGISTRY_KEY =
      ResourceKey.createRegistryKey(ModularMachineryReborn.rl("hatch_type"));

  protected MachineHatchType() {}

  public static MachineHatchType create() {
    return new MachineHatchType();
  }

  public Identifier getId() {
    return ModularMachineryReborn.getMachineHatchTypeRegistrar().getKey(this);
  }
}
