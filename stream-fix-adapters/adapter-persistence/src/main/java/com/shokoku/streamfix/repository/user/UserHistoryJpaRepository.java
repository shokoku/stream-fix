package com.shokoku.streamfix.repository.user;

import com.shokoku.streamfix.entity.user.UserHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryJpaRepository extends JpaRepository<UserHistoryEntity, Long> {



}
