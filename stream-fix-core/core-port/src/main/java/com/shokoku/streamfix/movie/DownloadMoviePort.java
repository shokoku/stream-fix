package com.shokoku.streamfix.movie;

public interface DownloadMoviePort {

  void save(UserMovieDownload domain);

  long downloadCntToday(String userId);
}
