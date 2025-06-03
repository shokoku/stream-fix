package com.shokoku.streamfix.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shokoku.streamfix.entity.QSampleEntity;
import com.shokoku.streamfix.entity.SampleEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SampleCustomRepositoryImpl implements SampleCustomRepository {

  private final JPAQueryFactory jpqQueryFactory;

  @Override
  public List<SampleEntity> findAll() {

    return jpqQueryFactory.selectFrom(QSampleEntity.sampleEntity)
        .fetch();
  }
}
