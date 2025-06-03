package com.shokoku.streamfix.repository;

import com.shokoku.streamfix.entity.SampleEntity;

import java.util.List;

public interface SampleCustomRepository {

  List<SampleEntity> findAllByQuery();
}
