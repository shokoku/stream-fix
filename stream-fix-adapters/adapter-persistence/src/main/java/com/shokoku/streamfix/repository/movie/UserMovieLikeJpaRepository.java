package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.UserMovieLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMovieLikeJpaRepository extends JpaRepository<UserMovieLikeEntity, String> {

  Optional<UserMovieLikeEntity> findByUserIdAndMovieId(String userId, String movieId);
}
