package com.shokoku.streamfix.controller.movie;

import com.shokoku.streamfix.controller.user.StreamFixApiResponse;
import com.shokoku.streamfix.filter.JwtTokenProvider;
import com.shokoku.streamfix.movie.DownloadMovieUseCase;
import com.shokoku.streamfix.movie.FetchMovieUseCase;
import com.shokoku.streamfix.movie.LikeMovieUseCase;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MovieController {

  private final FetchMovieUseCase fetchMovieUseCase;
  private final DownloadMovieUseCase downloadMovieUseCase;
  private final LikeMovieUseCase likeMovieUseCase;
  private final JwtTokenProvider jwtTokenProvider;

  @GetMapping("/api/v1/movie/client/{page}")
  public StreamFixApiResponse<PageableMovieResponse> fetchMoviePageable(@PathVariable int page) {
    PageableMovieResponse pageableMovieResponse = fetchMovieUseCase.fetchFromClient(page);
    return StreamFixApiResponse.ok(pageableMovieResponse);
  }

  @PostMapping("/api/v1/movie/search")
  public StreamFixApiResponse<PageableMovieResponse> search(@RequestParam int page) {
    PageableMovieResponse pageableMovieResponse = fetchMovieUseCase.fetchFromDb(page);
    return StreamFixApiResponse.ok(pageableMovieResponse);
  }

  @PostMapping("/api/v1/movie/{movieId}/download")
  @PreAuthorize("hasAnyRole('ROLE_BRONZE', 'ROLE_SILVER', 'ROLE_GOLD')")
  public StreamFixApiResponse<String> download(@PathVariable String movieId) {
    String download =
        downloadMovieUseCase.download(
            jwtTokenProvider.getUserId(), jwtTokenProvider.getRole(), movieId);
    return StreamFixApiResponse.ok(download);
  }

  @PostMapping("/api/v1/movie/{movieId}/like")
  @PreAuthorize("hasAnyRole('ROLE_FREE', 'ROLE_BRONZE', 'ROLE_SILVER', 'ROLE_GOLD')")
  public StreamFixApiResponse<String> like(@PathVariable String movieId) {
    String userId = jwtTokenProvider.getUserId();
    likeMovieUseCase.like(movieId, userId);
    return StreamFixApiResponse.ok("");
  }
}
