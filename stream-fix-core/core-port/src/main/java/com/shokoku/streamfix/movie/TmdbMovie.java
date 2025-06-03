package com.shokoku.streamfix.movie;

import java.util.List;

public record TmdbMovie(String movieName, boolean isAdult, List<String> genre, String overview, String releaseAt) {

}
