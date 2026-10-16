package es.degrassi.mmr.api.entity;

public interface IClientTickEntity extends ITickEntity {
  default void doClientTick() {}
}
