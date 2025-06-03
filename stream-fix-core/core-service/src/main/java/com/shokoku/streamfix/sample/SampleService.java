package com.shokoku.streamfix.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleService implements SearchSampleUseCase{

  private final SamplePort samplePort;

  @Override
  public SampleResponse getSample() {
    SamplePortResponse sample = samplePort.getSample();
    return new SampleResponse(sample.name());
  }
}
