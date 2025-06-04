package com.shokoku.streamfix.sample;

import com.shokoku.streamfix.controller.StreamFixApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SampleController {

  private final SearchSampleUseCase searchSampleUseCase;

  @GetMapping("/api/v1/sample")
  public StreamFixApiResponse<SampleResponse> getSample() {
    SampleResponse response = searchSampleUseCase.getSample();
    return StreamFixApiResponse.ok(response);
  }
}
