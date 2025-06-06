package com.shokoku.streamfix.repository.movie;

import com.shokoku.streamfix.entity.movie.UserMovieLikeEntity;
import com.shokoku.streamfix.movie.LikeMoviePort;
import com.shokoku.streamfix.movie.UserMovieLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserMovieLikeRepository implements LikeMoviePort {

  private final UserMovieLikeJpaRepository userMovieLikeJpaRepository;

  @Override
  public void save(UserMovieLike domain) {
    userMovieLikeJpaRepository.save(UserMovieLikeEntity.toEntity(domain));
  }

  @Override
  public Optional<UserMovieLike> findByUserIdAndMovieId(String userId, String movieId) {
    return userMovieLikeJpaRepository
        .findByUserIdAndMovieId(userId, movieId)
        .map(UserMovieLikeEntity::toDomain);
  }
}
