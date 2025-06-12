package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.MovieEntity;
import com.shokoku.streamfix.movie.PersistenceMoviePort;
import com.shokoku.streamfix.movie.StreamFixMovie;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MovieRepository implements PersistenceMoviePort {

  private final MovieJpaRepository movieJpaRepository;

  @Override
  @Transactional
  public List<StreamFixMovie> fetchBy(int page, int size) {
    return movieJpaRepository.search(PageRequest.of(page, size)).stream()
        .map(MovieEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public StreamFixMovie findBy(String movieName) {
    return movieJpaRepository.findByMovieName(movieName).map(MovieEntity::toDomain).orElseThrow();
  }

  @Override
  @Transactional
  public void insert(StreamFixMovie streamFixMovie) {
    Optional<MovieEntity> byMovieName =
        movieJpaRepository.findByMovieName(streamFixMovie.movieName());

    if (byMovieName.isPresent()) {
      return;
    }

    MovieEntity movieEntity =
        MovieEntity.newEntity(
            streamFixMovie.movieName(),
            streamFixMovie.isAdult(),
            streamFixMovie.genre(),
            streamFixMovie.overview(),
            streamFixMovie.releasedAt());
    movieJpaRepository.save(movieEntity);
  }
}
