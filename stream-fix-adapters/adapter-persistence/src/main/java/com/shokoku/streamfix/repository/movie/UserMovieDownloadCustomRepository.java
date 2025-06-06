package com.shokoku.streamfix.repository.movie;

public interface UserMovieDownloadCustomRepository {

  long countDownloadToday(String userId);
}
