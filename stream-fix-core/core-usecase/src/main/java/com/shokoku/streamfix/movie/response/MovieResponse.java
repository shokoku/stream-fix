package com.shokoku.streamfix.movie.response;

public record MovieResponse(String movieName, boolean isAdult, String genre, String overview, String releaseAt) {

}
