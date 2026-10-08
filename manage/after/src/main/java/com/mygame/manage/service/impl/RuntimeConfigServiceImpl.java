package com.mygame.manage.service.impl;

import com.mygame.manage.entity.EnemyTypeEntity;
import com.mygame.manage.entity.LevelConfigEntity;
import com.mygame.manage.entity.WaveConfigEntity;
import com.mygame.manage.entity.WaveSpawnConfigEntity;
import com.mygame.manage.repository.EnemyTypeRepository;
import com.mygame.manage.repository.LevelConfigRepository;
import com.mygame.manage.repository.WaveConfigRepository;
import com.mygame.manage.repository.WaveSpawnConfigRepository;
import com.mygame.manage.service.GameConfigService;
import com.mygame.manage.service.RuntimeConfigService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RuntimeConfigServiceImpl implements RuntimeConfigService {

    private final GameConfigService gameConfigService;
    private final EnemyTypeRepository enemyTypeRepository;
    private final LevelConfigRepository levelConfigRepository;
    private final WaveConfigRepository waveConfigRepository;
    private final WaveSpawnConfigRepository waveSpawnConfigRepository;

    public RuntimeConfigServiceImpl(GameConfigService gameConfigService,
                                    EnemyTypeRepository enemyTypeRepository,
                                    LevelConfigRepository levelConfigRepository,
                                    WaveConfigRepository waveConfigRepository,
                                    WaveSpawnConfigRepository waveSpawnConfigRepository) {
        this.gameConfigService = gameConfigService;
        this.enemyTypeRepository = enemyTypeRepository;
        this.levelConfigRepository = levelConfigRepository;
        this.waveConfigRepository = waveConfigRepository;
        this.waveSpawnConfigRepository = waveSpawnConfigRepository;
    }

    @Override
    public Object runtimeGameConfig() {
        return gameConfigService.getConfig();
    }

    @Override
    public List<EnemyTypeEntity> runtimeEnemyTypes() {
        return enemyTypeRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getEnabled()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LevelConfigEntity> runtimeLevels() {
        return levelConfigRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getEnabled()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> runtimeLevelWaves(String levelCode) {
        LevelConfigEntity level = levelConfigRepository.findByLevelCode(levelCode).orElse(null);
        if (level == null) {
            return new LinkedHashMap<String, Object>();
        }
        List<WaveConfigEntity> waves = waveConfigRepository.findByLevelIdOrderByWaveNumberAsc(level.getId());
        Map<Long, EnemyTypeEntity> enemyTypeMap = enemyTypeRepository.findAll().stream()
                .collect(Collectors.toMap(EnemyTypeEntity::getId, item -> item));

        List<Map<String, Object>> waveList = new ArrayList<Map<String, Object>>();
        for (WaveConfigEntity wave : waves) {
            Map<String, Object> waveMap = new LinkedHashMap<String, Object>();
            waveMap.put("id", wave.getId());
            waveMap.put("waveNumber", wave.getWaveNumber());
            waveMap.put("breakTime", wave.getBreakTime());
            List<WaveSpawnConfigEntity> spawns = waveSpawnConfigRepository.findByWaveIdOrderBySpawnOrderAscIdAsc(wave.getId());
            List<Map<String, Object>> spawnList = new ArrayList<Map<String, Object>>();
            for (WaveSpawnConfigEntity spawn : spawns) {
                Map<String, Object> spawnMap = new LinkedHashMap<String, Object>();
                EnemyTypeEntity enemyType = enemyTypeMap.get(spawn.getEnemyTypeId());
                spawnMap.put("id", spawn.getId());
                spawnMap.put("enemyTypeId", spawn.getEnemyTypeId());
                spawnMap.put("enemyCode", enemyType == null ? null : enemyType.getEnemyCode());
                spawnMap.put("spawnCount", spawn.getSpawnCount());
                spawnMap.put("spawnInterval", spawn.getSpawnInterval());
                spawnMap.put("pathIndex", spawn.getPathIndex());
                spawnMap.put("spawnOrder", spawn.getSpawnOrder());
                spawnList.add(spawnMap);
            }
            waveMap.put("spawns", spawnList);
            waveList.add(waveMap);
        }

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("levelId", level.getId());
        result.put("levelCode", level.getLevelCode());
        result.put("levelName", level.getLevelName());
        result.put("mapPath", level.getMapPath());
        result.put("maxWave", level.getMaxWave());
        result.put("waves", waveList);
        return result;
    }
}
