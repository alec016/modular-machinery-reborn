package es.degrassi.mmr.common.registration;

import es.degrassi.mmr.ModularMachineryReborn;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegistration {
  public static final DeferredRegister<SoundEvent> SOUND_REG = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ModularMachineryReborn.MODID);
  public static DeferredHolder<SoundEvent, SoundEvent> WRENCH_FAIL = SOUND_REG.register("wrench_fail", () -> makeSound("wrench_fail"));
  public static DeferredHolder<SoundEvent, SoundEvent> WRENCH_SUCCESS = SOUND_REG.register("wrench_success", () -> makeSound("wrench_success"));

  static SoundEvent makeSound(String name) {
    return SoundEvent.createVariableRangeEvent(ModularMachineryReborn.rl(name));
  }
}
