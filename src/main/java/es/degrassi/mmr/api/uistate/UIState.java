package es.degrassi.mmr.api.uistate;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.experiencelib.api.codec.NamedMapCodec;
import es.degrassi.mmr.api.codec.RegistrarCodec;

import java.util.Optional;
import java.util.function.Function;

public interface UIState {
  NamedMapCodec<UIState> CODEC = RegistrarCodec.UI_STATE.dispatch(UIState::getType, UIStateType::getCodec, "UI State");
  NamedCodec<Optional<UIState>> OPTIONAL_CODEC = CODEC.optionalFieldOf("optional");

  UIStateType<?> getType();
}
