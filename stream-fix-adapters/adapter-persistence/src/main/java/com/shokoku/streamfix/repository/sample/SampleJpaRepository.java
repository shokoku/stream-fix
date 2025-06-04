package com.shokoku.streamfix.repository.sample;

import com.shokoku.streamfix.entity.sample.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleJpaRepository
    extends JpaRepository<SampleEntity, String>, SampleCustomRepository {}
