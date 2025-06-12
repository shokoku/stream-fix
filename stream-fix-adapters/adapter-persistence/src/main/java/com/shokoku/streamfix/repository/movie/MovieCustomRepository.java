package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.MovieEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieCustomRepository {

  Optional<MovieEntity> findByMovieName(String name);

  Page<MovieEntity> search(Pageable pageable);
}
