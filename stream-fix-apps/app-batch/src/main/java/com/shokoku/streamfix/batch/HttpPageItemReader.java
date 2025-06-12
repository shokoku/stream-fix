package com.shokoku.streamfix.batch;

import com.shokoku.streamfix.movie.FetchMovieUseCase;
import com.shokoku.streamfix.movie.response.MovieResponse;
import com.shokoku.streamfix.movie.response.PageableMovieResponse;
import java.util.LinkedList;
import java.util.List;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;

public class HttpPageItemReader extends AbstractItemCountingItemStreamItemReader<MovieResponse> {

  private int page;
  private final List<MovieResponse> contents = new LinkedList<>();
  private final FetchMovieUseCase fetchMovieUseCase;

  public HttpPageItemReader(int page, FetchMovieUseCase fetchMovieUseCase) {
    this.page = page;
    this.fetchMovieUseCase = fetchMovieUseCase;
  }

  @Override
  protected MovieResponse doRead() throws Exception {
    if (this.contents.isEmpty()) {
      readRow();
    }

    int size = contents.size();
    int index = size - 1;

    if (index < 0) {
      return null;
    }

    return contents.remove(contents.size() - 1);
  }

  @Override
  protected void doOpen() throws Exception {
    setName(HttpPageItemReader.class.getSimpleName());
  }

  @Override
  protected void doClose() throws Exception {}

  private void readRow() {
    PageableMovieResponse pageableMovieResponse = fetchMovieUseCase.fetchFromClient(page);
    contents.addAll(pageableMovieResponse.movieResponses());
    page++;
  }
}
