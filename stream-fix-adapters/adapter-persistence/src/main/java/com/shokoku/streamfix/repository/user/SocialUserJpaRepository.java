package com.shokoku.streamfix.repository.user;

import com.shokoku.streamfix.entity.user.SocialUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserJpaRepository extends JpaRepository<SocialUserEntity, String> {

  Optional<SocialUserEntity> findByProviderId(String providerId);
}
