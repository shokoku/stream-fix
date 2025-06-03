package com.shokoku.streamfix.movie.response;

import java.util.List;

public record PageableMovieResponse(List<MovieResponse> movieResponses, int page, boolean hasNext) {

}
