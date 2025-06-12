package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.MovieResponse;
import java.util.List;

public interface InsertMovieUseCase {

  void insert(List<MovieResponse> items);
}
