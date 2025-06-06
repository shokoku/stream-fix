package com.shokoku.streamfix.movie;

public interface DownloadMovieUseCase {

  String download(String userId, String role, String movieId);
}
