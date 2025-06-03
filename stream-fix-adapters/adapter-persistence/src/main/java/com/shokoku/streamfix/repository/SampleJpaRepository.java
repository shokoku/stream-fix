package com.shokoku.streamfix.repository;

import com.shokoku.streamfix.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleJpaRepository extends JpaRepository<SampleEntity, String>, SampleCustomRepository {

}
