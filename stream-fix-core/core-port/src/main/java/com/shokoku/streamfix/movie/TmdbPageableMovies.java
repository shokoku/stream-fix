package com.shokoku.streamfix.movie;

import java.util.List;

public record TmdbPageableMovies(List<TmdbMovie> tmdbMovies, int page, boolean hasNext) {}
