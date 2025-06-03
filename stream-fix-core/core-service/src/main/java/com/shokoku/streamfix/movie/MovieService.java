package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService implements FetchMovieUseCase{

  private final TmdbMoviePort tmdbMoviePort;

  @Override
  public PageableMovieResponse fetchFromClient(int page) {
    TmdbPageableMovies tmdbPageableMovies = tmdbMoviePort.fetchPageable(page);
    return new PageableMovieResponse(
        tmdbPageableMovies.tmdbMovies().stream()
            .map(movie -> new MovieResponse(
                movie.movieName(),
                movie.isAdult(),
                movie.genre(),
                movie.overview(),
                movie.releaseAt()
            )).collect(Collectors.toList()), tmdbPageableMovies.page(),
        tmdbPageableMovies.hasNext());
  }

}
