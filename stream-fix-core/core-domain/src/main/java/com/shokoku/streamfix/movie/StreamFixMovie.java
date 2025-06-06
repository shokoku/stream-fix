package com.shokoku.streamfix.movie;

import lombok.Builder;

@Builder
public record StreamFixMovie(
    String movieName, Boolean isAdult, String genre, String overview, String releasedAt) {}
