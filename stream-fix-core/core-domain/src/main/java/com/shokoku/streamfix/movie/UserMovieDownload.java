package com.shokoku.streamfix.movie;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserMovieDownload(String userMovieDownloadId, String userId, String movieId) {

  public static UserMovieDownload newDownload(String userId, String movieId) {
    return UserMovieDownload.builder()
        .userMovieDownloadId(UUID.randomUUID().toString())
        .userId(userId)
        .movieId(movieId)
        .build();
  }
}
