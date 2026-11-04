package es.degrassi.mmr.api.uistate;

public interface IBlockUIState<S extends UIState> {
  S fallback();
}
