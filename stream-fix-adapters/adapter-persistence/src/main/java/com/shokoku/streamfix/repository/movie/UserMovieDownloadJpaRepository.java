package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.UserMovieDownloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMovieDownloadJpaRepository
    extends JpaRepository<UserMovieDownloadEntity, String>, UserMovieDownloadCustomRepository {}
