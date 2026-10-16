package es.degrassi.mmr.api.crafting;

import es.degrassi.experiencelib.api.codec.NamedCodec;
import es.degrassi.mmr.common.entity.MachineControllerEntity;
import es.degrassi.mmr.common.manager.crafting.ProcessorType;
import es.degrassi.mmr.common.registration.ProcessorTypeRegistration;

public interface IProcessorTemplate<T extends IProcessor> {
  NamedCodec<IProcessorTemplate<? extends IProcessor>> CODEC = ProcessorTypeRegistration.PROCESSOR.dispatch(
      IProcessorTemplate::getType,
      ProcessorType::getCodec,
      "Processor"
  );

  ProcessorType<T> getType();

  T build(MachineControllerEntity tile);
}
