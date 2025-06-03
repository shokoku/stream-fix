package com.shokoku.streamfix.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TmdbMovieNowPlayingResponse(
    TmdbDataResponse dates,
    String page,
    @JsonProperty("total_pages") int totalPages,
    @JsonProperty("total_results") int totalResults,
    List<TmdbMovieNowPlaying> results) {}
