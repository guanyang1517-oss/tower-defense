package com.mygame.manage.service.impl;

import com.mygame.manage.common.ApiException;
import com.mygame.manage.common.ValidationMessage;
import com.mygame.manage.entity.EnemyTypeEntity;
import com.mygame.manage.entity.WaveConfigEntity;
import com.mygame.manage.entity.WaveSpawnConfigEntity;
import com.mygame.manage.repository.EnemyTypeRepository;
import com.mygame.manage.repository.LevelConfigRepository;
import com.mygame.manage.repository.WaveConfigRepository;
import com.mygame.manage.repository.WaveSpawnConfigRepository;
import com.mygame.manage.service.WaveConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WaveConfigServiceImpl implements WaveConfigService {

    private final WaveConfigRepository waveConfigRepository;
    private final WaveSpawnConfigRepository waveSpawnConfigRepository;
    private final LevelConfigRepository levelConfigRepository;
    private final EnemyTypeRepository enemyTypeRepository;

    public WaveConfigServiceImpl(WaveConfigRepository waveConfigRepository,
                                 WaveSpawnConfigRepository waveSpawnConfigRepository,
                                 LevelConfigRepository levelConfigRepository,
                                 EnemyTypeRepository enemyTypeRepository) {
        this.waveConfigRepository = waveConfigRepository;
        this.waveSpawnConfigRepository = waveSpawnConfigRepository;
        this.levelConfigRepository = levelConfigRepository;
        this.enemyTypeRepository = enemyTypeRepository;
    }

    @Override
    public List<WaveConfigEntity> listByLevelId(Long levelId) {
        levelConfigRepository.findById(levelId).orElseThrow(() -> new ApiException("关卡不存在"));
        return waveConfigRepository.findByLevelIdOrderByWaveNumberAsc(levelId);
    }

    @Override
    public WaveConfigEntity create(Long levelId, WaveConfigEntity request) {
        levelConfigRepository.findById(levelId).orElseThrow(() -> new ApiException("关卡不存在"));
        validateWave(request, levelId, null);
        request.setId(null);
        request.setLevelId(levelId);
        return waveConfigRepository.save(request);
    }

    @Override
    public WaveConfigEntity update(Long id, WaveConfigEntity request) {
        WaveConfigEntity entity = waveConfigRepository.findById(id).orElseThrow(() -> new ApiException("波次不存在"));
        validateWave(request, entity.getLevelId(), id);
        entity.setWaveNumber(request.getWaveNumber());
        entity.setBreakTime(request.getBreakTime());
        return waveConfigRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        waveSpawnConfigRepository.deleteByWaveId(id);
        waveConfigRepository.deleteById(id);
    }

    @Override
    public List<WaveSpawnConfigEntity> listSpawns(Long waveId) {
        waveConfigRepository.findById(waveId).orElseThrow(() -> new ApiException("波次不存在"));
        return waveSpawnConfigRepository.findByWaveIdOrderBySpawnOrderAscIdAsc(waveId);
    }

    @Override
    public WaveSpawnConfigEntity createSpawn(Long waveId, WaveSpawnConfigEntity request) {
        waveConfigRepository.findById(waveId).orElseThrow(() -> new ApiException("波次不存在"));
        validateSpawn(request);
        request.setId(null);
        request.setWaveId(waveId);
        return waveSpawnConfigRepository.save(request);
    }

    @Override
    public WaveSpawnConfigEntity updateSpawn(Long id, WaveSpawnConfigEntity request) {
        WaveSpawnConfigEntity entity = waveSpawnConfigRepository.findById(id).orElseThrow(() -> new ApiException("刷怪配置不存在"));
        validateSpawn(request);
        entity.setEnemyTypeId(request.getEnemyTypeId());
        entity.setSpawnCount(request.getSpawnCount());
        entity.setSpawnInterval(request.getSpawnInterval());
        entity.setPathIndex(request.getPathIndex());
        entity.setSpawnOrder(request.getSpawnOrder());
        return waveSpawnConfigRepository.save(entity);
    }

    @Override
    public void deleteSpawn(Long id) {
        waveSpawnConfigRepository.deleteById(id);
    }

    private void validateWave(WaveConfigEntity request, Long levelId, Long currentId) {
        requirePositive(request.getWaveNumber(), "waveNumber");
        requireNonNegative(request.getBreakTime(), "breakTime");
        waveConfigRepository.findByLevelIdAndWaveNumber(levelId, request.getWaveNumber()).ifPresent(found -> {
            if (currentId == null || !currentId.equals(found.getId())) {
                throw new ApiException("当前关卡下该波次编号已存在");
            }
        });
    }

    private void validateSpawn(WaveSpawnConfigEntity request) {
        requirePositive(request.getSpawnCount(), "spawnCount");
        requirePositive(request.getSpawnInterval(), "spawnInterval");
        requireNonNegative(request.getPathIndex(), "pathIndex");
        requireNonNegative(request.getSpawnOrder(), "spawnOrder");
        EnemyTypeEntity enemyType = enemyTypeRepository.findById(request.getEnemyTypeId())
                .orElseThrow(() -> new ApiException("怪物类型不存在"));
        if (!Boolean.TRUE.equals(enemyType.getEnabled())) {
            throw new ApiException("该怪物类型已停用");
        }
    }

    private void requireNonNegative(Number value, String fieldName) {
        if (value == null || value.doubleValue() < 0) {
            throw new ApiException(ValidationMessage.nonNegative(fieldName));
        }
    }

    private void requirePositive(Number value, String fieldName) {
        if (value == null || value.doubleValue() <= 0) {
            throw new ApiException(ValidationMessage.positive(fieldName));
        }
    }
}
