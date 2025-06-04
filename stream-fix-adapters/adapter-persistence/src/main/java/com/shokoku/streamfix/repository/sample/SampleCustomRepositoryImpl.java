package com.shokoku.streamfix.repository.sample;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shokoku.streamfix.entity.sample.QSampleEntity;
import com.shokoku.streamfix.entity.sample.SampleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SampleCustomRepositoryImpl implements SampleCustomRepository {

  private final JPAQueryFactory jpqQueryFactory;

  @Override
  public List<SampleEntity> findAllByQuery() {

    return jpqQueryFactory.selectFrom(QSampleEntity.sampleEntity).fetch();
  }
}
