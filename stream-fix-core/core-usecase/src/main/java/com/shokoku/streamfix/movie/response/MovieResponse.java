package com.shokoku.streamfix.movie.response;

import java.util.List;

public record MovieResponse(String movieName, boolean isAdult, List<String> genre, String overview, String releaseAt) {

}
