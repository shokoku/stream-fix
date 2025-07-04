package com.shokoku.streamfix.tmdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shokoku.streamfix.client.TmdbHttpClient;
import com.shokoku.streamfix.movie.TmdbMovie;
import com.shokoku.streamfix.movie.TmdbMoviePort;
import com.shokoku.streamfix.movie.TmdbPageableMovies;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class TmdbMovieListHttpClient implements TmdbMoviePort {

  @Value("${tmdb.api.movie-lists.now-playing}")
  private String nowPlayingUrl;

  private final TmdbHttpClient tmdbHttpClient;

  @Override
  public TmdbPageableMovies fetchPageable(int page) {
    String url = nowPlayingUrl + "?language=ko-KR&page=" + page;
    String request =
        tmdbHttpClient.request(
            url, HttpMethod.GET, CollectionUtils.toMultiValueMap(Map.of()), Map.of());

    TmdbMovieNowPlayingResponse response;
    try {
      response = new ObjectMapper().readValue(request, TmdbMovieNowPlayingResponse.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return new TmdbPageableMovies(
        response.results().stream()
            .map(
                movie ->
                    new TmdbMovie(
                        movie.title(),
                        movie.adult(),
                        movie.genreIds(),
                        movie.overview(),
                        movie.releaseDate()))
            .toList(),
        page,
        response.totalPages() - page != 0);
  }
}
