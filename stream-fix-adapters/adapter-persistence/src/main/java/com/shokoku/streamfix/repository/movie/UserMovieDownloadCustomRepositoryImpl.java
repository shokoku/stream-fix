package com.shokoku.streamfix.repository.movie;

import static com.shokoku.streamfix.entity.movie.QUserMovieDownloadEntity.userMovieDownloadEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserMovieDownloadCustomRepositoryImpl implements UserMovieDownloadCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public long countDownloadToday(String userId) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startTime = now.truncatedTo(ChronoUnit.DAYS);
    LocalDateTime endTime = now.plusDays(1).truncatedTo(ChronoUnit.DAYS);

    return jpaQueryFactory
        .selectFrom(userMovieDownloadEntity)
        .where(
            userMovieDownloadEntity
                .userId
                .eq(userId)
                .and(userMovieDownloadEntity.createdAt.goe(startTime))
                .and(userMovieDownloadEntity.createdAt.lt(endTime)))
        .fetch()
        .size();
  }
}
