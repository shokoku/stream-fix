package com.shokoku.streamfix.movie;

public interface TmdbMoviePort {
  TmdbPageableMovies fetchPageable(int page);

}
