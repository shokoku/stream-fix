package com.shokoku.streamfix.movie;

public record TmdbMovie(String movieName, boolean isAdult, String genre, String overview, String releaseAt) {

}
