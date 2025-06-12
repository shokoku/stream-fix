package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.UserMovieLikeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMovieLikeJpaRepository extends JpaRepository<UserMovieLikeEntity, String> {

  Optional<UserMovieLikeEntity> findByUserIdAndMovieId(String userId, String movieId);
}
