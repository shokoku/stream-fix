package com.shokoku.streamfix.movie;

import java.util.List;

public interface PersistenceMoviePort {

  List<StreamFixMovie> fetchBy(int page, int size);

  StreamFixMovie findBy(String movieName);

  void insert(StreamFixMovie streamFixMovie);
}
