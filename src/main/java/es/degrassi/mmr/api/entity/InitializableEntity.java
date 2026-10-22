package es.degrassi.mmr.api.entity;

public interface InitializableEntity {
  boolean isInitialized();
  void setInitialized(boolean value);
  void init();
}
