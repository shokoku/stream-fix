package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService
    implements FetchMovieUseCase, InsertMovieUseCase, DownloadMovieUseCase, LikeMovieUseCase {

  private final TmdbMoviePort tmdbMoviePort;
  private final PersistenceMoviePort persistenceMoviePort;
  private final DownloadMoviePort downloadMoviePort;
  private final LikeMoviePort likeMoviePort;
  private final List<UserDownloadMovieRoleValidator> validators;

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
  public PageableMovieResponse fetchFromDb(int page) {
    List<StreamFixMovie> streamFixMovies = persistenceMoviePort.fetchBy(page, 10);
    return new PageableMovieResponse(
        streamFixMovies.stream()
            .map(
                it ->
                    new MovieResponse(
                        it.movieName(), it.isAdult(), List.of(), it.overview(), it.releasedAt()))
            .toList(),
        page,
        true);
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

  @Override
  public String download(String userId, String role, String movieId) {
    long cnt = downloadMoviePort.downloadCntToday(userId);
    boolean validate =
        validators.stream()
            .filter(validator -> validator.isTarget(role))
            .findAny()
            .orElseThrow()
            .validate(cnt);

    if (!validate) {
      throw new RuntimeException("더 이상 다운로드를 할 수 없습니다.");
    }

    StreamFixMovie by = persistenceMoviePort.findBy(movieId);

    downloadMoviePort.save(UserMovieDownload.newDownload(userId, movieId));

    return by.movieName();
  }

  @Override
  public void like(String userId, String movieId) {
    Optional<UserMovieLike> byUserIdAndMovieId =
        likeMoviePort.findByUserIdAndMovieId(userId, movieId);

    if (byUserIdAndMovieId.isEmpty()) {
      likeMoviePort.save(UserMovieLike.newLike(userId, movieId));
    }

    UserMovieLike userMovieLike = byUserIdAndMovieId.get();
    userMovieLike.like();
    likeMoviePort.save(userMovieLike);
  }
}
