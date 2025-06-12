package com.shokoku.streamfix.repository.movie;

import static com.shokoku.streamfix.entity.movie.QMovieEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shokoku.streamfix.entity.movie.MovieEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MovieCustomRepositoryImpl implements MovieCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<MovieEntity> findByMovieName(String name) {
    return jpaQueryFactory
        .selectFrom(movieEntity)
        .where(movieEntity.movieName.eq(name))
        .fetch()
        .stream()
        .findFirst();
  }

  @Override
  public Page<MovieEntity> search(Pageable pageable) {
    List<MovieEntity> fetch =
        jpaQueryFactory
            .selectFrom(movieEntity)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    long count = jpaQueryFactory.selectFrom(movieEntity).fetch().size();

    return new PageImpl<>(fetch, pageable, count);
  }
}
