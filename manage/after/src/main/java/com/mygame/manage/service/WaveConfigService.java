package com.mygame.manage.service;

import com.mygame.manage.entity.WaveConfigEntity;
import com.mygame.manage.entity.WaveSpawnConfigEntity;

import java.util.List;

public interface WaveConfigService {

    List<WaveConfigEntity> listByLevelId(Long levelId);

    WaveConfigEntity create(Long levelId, WaveConfigEntity request);

    WaveConfigEntity update(Long id, WaveConfigEntity request);

    void delete(Long id);

    List<WaveSpawnConfigEntity> listSpawns(Long waveId);

    WaveSpawnConfigEntity createSpawn(Long waveId, WaveSpawnConfigEntity request);

    WaveSpawnConfigEntity updateSpawn(Long id, WaveSpawnConfigEntity request);

    void deleteSpawn(Long id);
}
