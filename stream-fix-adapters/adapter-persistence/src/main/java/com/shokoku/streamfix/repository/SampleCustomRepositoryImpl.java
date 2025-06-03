package com.shokoku.streamfix.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shokoku.streamfix.entity.QSampleEntity;
import com.shokoku.streamfix.entity.SampleEntity;
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
