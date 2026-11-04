package es.degrassi.mmr.api.uistate;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.ModularMachineryReborn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UIStateType<S extends UIState> {
  public static final ResourceKey<Registry<UIStateType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ModularMachineryReborn.rl("ui_state_type"));

  private final NamedCodec<S> codec;

  public static <S extends UIState> UIStateType<S> create(NamedCodec<S> codec) {
    return new UIStateType<>(codec);
  }

  public Identifier getId() {
    return ModularMachineryReborn.getUISateRegistrar().getKey(this);
  }
}
