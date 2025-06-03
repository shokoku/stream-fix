package com.shokoku.streamfix.controller.movie;

import com.shokoku.streamfix.movie.FetchMovieUseCase;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieController {

  private final FetchMovieUseCase fetchMovieUseCase;

  @GetMapping("/api/v1/movie/client/{page}")
  public String fetchMoviePageable(@PathVariable int page) {
    PageableMovieResponse pageableMovieResponse = fetchMovieUseCase.fetchFromClient(page);
    return "Fetched from client page";
  }

}
