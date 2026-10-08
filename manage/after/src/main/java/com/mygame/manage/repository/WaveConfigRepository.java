package com.mygame.manage.repository;

import com.mygame.manage.entity.WaveConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaveConfigRepository extends JpaRepository<WaveConfigEntity, Long> {

    List<WaveConfigEntity> findByLevelIdOrderByWaveNumberAsc(Long levelId);

    Optional<WaveConfigEntity> findByLevelIdAndWaveNumber(Long levelId, Integer waveNumber);
}
