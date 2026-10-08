package com.mygame.manage.repository;

import com.mygame.manage.entity.WaveSpawnConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaveSpawnConfigRepository extends JpaRepository<WaveSpawnConfigEntity, Long> {

    List<WaveSpawnConfigEntity> findByWaveIdOrderBySpawnOrderAscIdAsc(Long waveId);

    void deleteByWaveId(Long waveId);
}
