package es.degrassi.mmr.api.entity;


import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ColorableMachineEntity {

  int getMachineColor();

  void setMachineColor(int newColor);

  void setRequestModelUpdate(boolean request);

  boolean isRequestModelUpdate();
}
