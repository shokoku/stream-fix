package com.shokoku.streamfix.repository.sample;

import com.shokoku.streamfix.entity.sample.SampleEntity;
import java.util.List;

public interface SampleCustomRepository {

  List<SampleEntity> findAllByQuery();
}
