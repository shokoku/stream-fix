package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService implements FetchMovieUseCase, InsertMovieUseCase {

  private final TmdbMoviePort tmdbMoviePort;
  private final PersistenceMoviePort persistenceMoviePort;

  @Override
  public PageableMovieResponse fetchFromClient(int page) {
    TmdbPageableMovies tmdbPageableMovies = tmdbMoviePort.fetchPageable(page);
    return new PageableMovieResponse(
        tmdbPageableMovies.tmdbMovies().stream()
            .map(
                movie ->
                    new MovieResponse(
                        movie.movieName(),
                        movie.isAdult(),
                        movie.genre(),
                        movie.overview(),
                        movie.releaseAt()))
            .collect(Collectors.toList()),
        tmdbPageableMovies.page(),
        tmdbPageableMovies.hasNext());
  }

  @Override
  public void insert(List<MovieResponse> items) {
    items.forEach(
        it -> {
          StreamFixMovie streamFixMovie =
              StreamFixMovie.builder()
                  .movieName(it.movieName())
                  .isAdult(it.isAdult())
                  .overview(it.overview())
                  .genre("genre")
                  .build();
          persistenceMoviePort.insert(streamFixMovie);
        });
  }
}
