package com.shokoku.streamfix.movie;

import com.shokoku.streamfix.movie.response.PageableMovieResponse;

public interface FetchMovieUseCase {

  PageableMovieResponse fetchFromClient(int page);

}
