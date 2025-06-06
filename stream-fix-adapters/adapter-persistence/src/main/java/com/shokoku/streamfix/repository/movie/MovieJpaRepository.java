package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieJpaRepository
    extends JpaRepository<MovieEntity, String>, MovieCustomRepository {}
