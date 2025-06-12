package com.shokoku.streamfix.repository.sample;

import com.shokoku.streamfix.entity.sample.SampleEntity;
import com.shokoku.streamfix.sample.SamplePersistencePort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class SampleRepository implements SamplePersistencePort {

  private final SampleJpaRepository sampleJpaRepository;

  @Override
  @Transactional
  public String getSampleName(String id) {
    Optional<SampleEntity> byId = sampleJpaRepository.findById(id);
    return byId.map(SampleEntity::getSampleName).orElseThrow();
  }
}
